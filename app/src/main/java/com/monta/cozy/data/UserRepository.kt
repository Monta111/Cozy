package com.monta.cozy.data

import com.monta.cozy.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun signUp(user: User, password: String): Flow<Boolean>

    fun signIn(email: String, password: String): Flow<Boolean>

    fun signOut()

    fun validateEmailForSignUp(email: String): Flow<Boolean>

    fun validateEmailForSignIn(email: String): Flow<Boolean>

    fun isSignedIn() : Boolean

    fun fetchUser() : Flow<User>

    fun fetchUser(userId: String) : Flow<User>

}