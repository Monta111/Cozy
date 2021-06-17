package com.monta.cozy.data

import com.google.android.gms.maps.model.LatLngBounds
import com.monta.cozy.model.Rating
import com.monta.cozy.model.Room
import kotlinx.coroutines.flow.Flow

interface RoomRepository {

    fun postRoom(imageUriList: List<String>, room: Room): Flow<Boolean>

    fun searchNearbyRoom(query: Map<String, String>, bounds: LatLngBounds): Flow<List<Room>>

    fun fetchRoom(roomId: String): Flow<Room>

    fun ratingRoom(rating: Rating): Flow<Boolean>

    fun getRatingList(roomId: String): Flow<List<Rating>>
}