package com.monta.cozy.data.repository

import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.monta.cozy.data.UserRepository
import com.monta.cozy.data.remote.api.EmailValidationApi
import com.monta.cozy.model.User
import com.monta.cozy.utils.consts.USER_COLLECTION
import com.monta.cozy.utils.extensions.matchEmailRegex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val emailValidationApi: EmailValidationApi
) : UserRepository {

    @ExperimentalCoroutinesApi
    override fun signUp(user: User, password: String): Flow<Boolean> {
        return callbackFlow {
            auth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    val userId = it.user?.uid

                    if (userId == null) {
                        close(Exception())
                    } else {
                        user.id = userId
                        firestore.collection(USER_COLLECTION).document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                trySend(true)
                                close()
                            }
                            .addOnFailureListener { e ->
                                close(e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    close(e)
                }
            awaitClose { cancel() }
        }
    }

    @ExperimentalCoroutinesApi
    override fun signIn(email: String, password: String): Flow<Boolean> {
        return callbackFlow {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    trySend(true)
                    close()
                }
                .addOnFailureListener {
                    close(it)
                }
            awaitClose { cancel() }
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    @ExperimentalCoroutinesApi
    override fun validateEmailForSignUp(email: String): Flow<Boolean> {
        return callbackFlow {
            val isValid = validateEmail(email)
            if (isValid) {
                auth.fetchSignInMethodsForEmail(email)
                    .addOnSuccessListener {
                        val isNewUser = it.signInMethods.isNullOrEmpty()
                        if (isNewUser) {
                            trySend(true)
                            close()
                        } else {
                            close(FirebaseAuthUserCollisionException(".", "."))
                        }
                    }
                    .addOnFailureListener {
                        close(it)
                    }
            } else {
                close(FirebaseAuthInvalidCredentialsException(".", "."))
            }

            awaitClose { cancel() }
        }
            .flowOn(Dispatchers.IO)
    }

    @ExperimentalCoroutinesApi
    override fun validateEmailForSignIn(email: String): Flow<Boolean> {
        return callbackFlow {
            val isValid = validateEmail(email)
            if (isValid) {
                auth.fetchSignInMethodsForEmail(email)
                    .addOnSuccessListener {
                        val isNewUser = it.signInMethods.isNullOrEmpty()
                        if (!isNewUser) {
                            trySend(true)
                            close()
                        } else {
                            close(FirebaseAuthInvalidUserException(".", "."))
                        }
                    }
                    .addOnFailureListener {
                        close(it)
                    }
            } else {
                close(FirebaseAuthInvalidCredentialsException(".", "."))
            }

            awaitClose { cancel() }
        }
            .flowOn(Dispatchers.IO)
    }

    private suspend fun validateEmail(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (!email.matchEmailRegex()) {
                return@withContext false
            }

            val result = emailValidationApi.validateEmail(email)
            return@withContext result.valid
        }
    }

    override fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    @ExperimentalCoroutinesApi
    override fun fetchUser(): Flow<User> {
        return callbackFlow {
            var listener: ListenerRegistration? = null
            val userId = auth.currentUser?.uid
            if (userId == null) {
                close(Exception())
            } else {
                listener = firestore.collection(USER_COLLECTION).document(userId)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Timber.e(e)
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            snapshot.toObject(User::class.java)?.let { trySend(it) }
                        }
                    }
            }
            awaitClose {
                listener?.remove()
                cancel()
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun fetchUser(userId: String): Flow<User> {
        return callbackFlow {
            firestore.collection(USER_COLLECTION).document(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot != null && snapshot.exists()) {
                        snapshot.toObject(User::class.java)?.let { trySend(it) }
                        close()
                    }
                }
                .addOnFailureListener { e ->
                    close(e)
                }
            awaitClose {
                cancel()
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun updatePhoneNumber(userId: String, phoneNumber: String): Flow<Boolean> {
        return callbackFlow {
            firestore.collection(USER_COLLECTION)
                .document(userId)
                .update(mapOf("phoneNumber" to phoneNumber))
                .addOnSuccessListener {
                    trySend(true)
                    close()
                }
                .addOnFailureListener {
                    close(it)
                }
            awaitClose { cancel() }
        }
    }
}