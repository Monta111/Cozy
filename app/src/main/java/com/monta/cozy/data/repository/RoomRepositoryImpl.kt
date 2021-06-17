package com.monta.cozy.data.repository

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.android.SphericalUtil
import com.monta.cozy.data.RoomRepository
import com.monta.cozy.enumclass.RoomCategory
import com.monta.cozy.enumclass.RoomFeature
import com.monta.cozy.model.Rating
import com.monta.cozy.model.Room
import com.monta.cozy.utils.consts.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) :
    RoomRepository {

    @ExperimentalCoroutinesApi
    override fun postRoom(imageUriList: List<String>, room: Room): Flow<Boolean> {
        return callbackFlow {
            val roomDocumentId = UUID.randomUUID().toString().also { room.id = it }
            val roomDocumentRef = firestore.collection(ROOM_COLLECTION).document(roomDocumentId)
            roomDocumentRef.set(room)
                .addOnSuccessListener {
                    val imagesRef = storage.reference.child(STORAGE_ROOM_IMAGE_PATH)
                    Timber.e(imageUriList.size.toString())
                    imageUriList.forEach { uri ->
                        val imageRef = imagesRef.child(System.nanoTime().toString())
                        imageRef.putFile(Uri.parse(uri))
                            .continueWithTask { imageRef.downloadUrl }
                            .addOnSuccessListener { result ->
                                roomDocumentRef.update(
                                    ROOM_IMAGE_URLS_FIELD,
                                    FieldValue.arrayUnion(result.toString())
                                )
                                    .addOnFailureListener { Timber.e(it) }
                            }
                            .addOnFailureListener { Timber.e(it) }
                    }

                    trySend(true)
                    close()
                }
                .addOnFailureListener { close(it) }
            awaitClose { cancel() }
        }
    }

    override fun updateRoom(imageUriList: List<String>, room: Room): Flow<Boolean> {
        return callbackFlow {
            val roomDocumentId = room.id
            val roomDocumentRef = firestore.collection(ROOM_COLLECTION).document(roomDocumentId)
            roomDocumentRef.update(
                mapOf(
                    "isAvailable" to room.isAvailable,
                    "roomCategory" to room.roomCategory,
                    "area" to room.area,
                    "numberOfRooms" to room.numberOfRooms,
                    "capacity" to room.capacity,
                    "gender" to room.gender,
                    "rentCost" to room.rentCost,
                    "deposit" to room.deposit,
                    "timeDeposit" to room.timeDeposit,
                    "electricCost" to room.electricCost,
                    "waterCost" to room.waterCost,
                    "internetCost" to room.internetCost,
                    "features" to room.features,
                )
            )
                .addOnSuccessListener {
                    val imagesRef = storage.reference.child(STORAGE_ROOM_IMAGE_PATH)
                    Timber.e(imageUriList.size.toString())
                    imageUriList.forEach { uri ->
                        val imageRef = imagesRef.child(System.nanoTime().toString())
                        imageRef.putFile(Uri.parse(uri))
                            .continueWithTask { imageRef.downloadUrl }
                            .addOnSuccessListener { result ->
                                roomDocumentRef.update(
                                    ROOM_IMAGE_URLS_FIELD,
                                    FieldValue.arrayUnion(result.toString())
                                )
                                    .addOnFailureListener { Timber.e(it) }
                            }
                            .addOnFailureListener { Timber.e(it) }
                    }

                    trySend(true)
                    close()
                }
                .addOnFailureListener { close(it) }
            awaitClose { cancel() }
        }
    }

    @ExperimentalCoroutinesApi
    override fun searchNearbyRoom(
        query: Map<String, String>,
        bounds: LatLngBounds
    ): Flow<List<Room>> {
        val startLat = bounds.southwest.latitude
        val endLat = bounds.northeast.latitude
        val startLng = bounds.southwest.longitude
        val endLng = bounds.northeast.longitude
        val center = bounds.center

        val latRefQuery = firestore.collection(ROOM_COLLECTION)
            .whereGreaterThan(ROOM_LAT_FIELD, startLat)
            .whereLessThan(ROOM_LAT_FIELD, endLat)

        val lngRefQuery = firestore.collection(ROOM_COLLECTION)
            .whereGreaterThan(ROOM_LNG_FIELD, startLng)
            .whereLessThan(ROOM_LNG_FIELD, endLng)

        val mathScope = CoroutineScope(Dispatchers.Default)

        return callbackFlow {
            latRefQuery
                .get()
                .addOnSuccessListener { latBoundList ->
                    val latRooms = latBoundList.documents.map { it.toObject(Room::class.java) }

                    lngRefQuery
                        .get()
                        .addOnSuccessListener { lngBoundList ->
                            mathScope.launch {
                                val lngRooms =
                                    lngBoundList.documents.map { it.toObject(Room::class.java) }

                                val rooms = mutableListOf<Room>()
                                latRooms.forEach { latRoom ->
                                    lngRooms.forEach { lngRoom ->
                                        if (latRoom != null && lngRoom != null) {
                                            if (latRoom.placeId == lngRoom.placeId) {
                                                rooms.add(latRoom)
                                            }
                                        }
                                    }
                                }

                                val filtered = filterRoom(query, rooms)

                                filtered.forEach { room ->
                                    val distance = SphericalUtil.computeDistanceBetween(
                                        center,
                                        LatLng(room.lat, room.lng)
                                    )
                                    room.distanceToCenterLocation = distance
                                }

                                val sorted = filtered.sortedBy { it.distanceToCenterLocation }

                                trySend(sorted)
                                close()
                            }
                        }
                        .addOnFailureListener { e ->
                            close(e)
                        }
                }
                .addOnFailureListener { e ->
                    close(e)
                }
            awaitClose {
                mathScope.cancel()
                cancel()
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun fetchRoom(roomId: String): Flow<Room> {
        return callbackFlow {
            firestore.collection(ROOM_COLLECTION).document(roomId)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot != null && snapshot.exists()) {
                        snapshot.toObject(Room::class.java)?.let { trySend(it) }
                    }
                }
                .addOnFailureListener { close(it) }

            awaitClose {
                cancel()
            }
        }
    }

    private fun filterRoom(query: Map<String, String>, input: List<Room>): List<Room> {
        var result = input
        val filteredRoomCategory = query[ROOM_CATEGORY_FIELD]
        if (filteredRoomCategory != null) {
            result = result.filter {
                it.roomCategory == RoomCategory.valueOf(filteredRoomCategory)
            }
                .toMutableList()
        }

        val filteredRentCost = query[ROOM_RENT_COST_FIELD]
        if (filteredRentCost != null) {
            result = result.filter { it.rentCost <= filteredRentCost.toInt() }
                .toMutableList()
        }

        val filteredFeatures = query[ROOM_FEATURES_FIELD]
        if (filteredFeatures != null) {
            val features =
                filteredFeatures.split(",").map { RoomFeature.valueOf(it) }
            result = result.filter { it.features.containsAll(features) }
                .toMutableList()
        }

        return result
    }

    @ExperimentalCoroutinesApi
    override fun ratingRoom(rating: Rating): Flow<Boolean> {
        return callbackFlow {
            firestore.collection("reviews")
                .document(rating.roomId)
                .collection("contents")
                .document(rating.userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot == null || !snapshot.exists()) {
                        firestore.collection("reviews")
                            .document(rating.roomId)
                            .collection("contents")
                            .document(rating.userId)
                            .set(rating)
                            .addOnSuccessListener {
                                trySend(true)
                                close()
                            }
                            .addOnFailureListener {
                                close(it)
                            }
                    } else {
                        firestore.collection("reviews")
                            .document(rating.roomId)
                            .collection("contents")
                            .document(rating.userId)
                            .update(
                                mapOf(
                                    "content" to rating.content,
                                    "ratingScore" to rating.ratingScore,
                                    "time" to rating.time
                                )
                            )
                            .addOnSuccessListener {
                                trySend(true)
                                close()
                            }
                            .addOnFailureListener {
                                close(it)
                            }
                    }
                }
                .addOnFailureListener {
                    close(it)
                }

            awaitClose { cancel() }
        }
    }

    @ExperimentalCoroutinesApi
    override fun getRatingList(roomId: String): Flow<List<Rating>> {
        return callbackFlow {
            val listener = Firebase.firestore.collection("reviews")
                .document(roomId)
                .collection("contents")
                .addSnapshotListener { documents, error ->
                    if (error != null) {
                        Timber.e(error)
                        return@addSnapshotListener
                    }
                    if (documents == null || documents.isEmpty) {
                        trySend(emptyList<Rating>())
                    } else {
                        val ratings = mutableListOf<Rating>()
                        for (document in documents) {
                            val rating = document.toObject(Rating::class.java)
                            ratings.add(rating)
                        }
                        trySend(ratings)
                    }
                }

            awaitClose {
                listener.remove()
                cancel()
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun getUploadRoom(userId: String): Flow<List<Room>> {
        return callbackFlow {
            val listener = firestore.collection(ROOM_COLLECTION)
                .whereEqualTo("ownerId", userId)
                .addSnapshotListener { documents, error ->
                    if (error != null) {
                        Timber.e(error)
                        return@addSnapshotListener
                    }

                    if (documents != null) {
                        val rooms = mutableListOf<Room>()
                        documents.forEach {
                            val room = it.toObject(Room::class.java)
                            rooms.add(room)
                        }
                        trySend(rooms)
                    }
                }

            awaitClose {
                listener.remove()
                cancel()
            }
        }
    }
}