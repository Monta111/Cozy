package com.monta.cozy.di.module

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.monta.cozy.data.*
import com.monta.cozy.data.repository.*
import com.monta.cozy.utils.consts.CACHE_FILENAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideCacheRepository(context: Context): CacheRepository {
        return CacheRepositoryImpl(
            context.getSharedPreferences(CACHE_FILENAME, Context.MODE_PRIVATE),
            Gson()
        )
    }

    @Provides
    @Singleton
    fun providePlaceRepository(impl: PlaceRepositoryImpl): PlaceRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository {
        return impl
    }


    @Provides
    @Singleton
    fun provideRoomRepository(impl: RoomRepositoryImpl): RoomRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideMessageRepository(impl: MessageRepositoryImpl): MessageRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
}