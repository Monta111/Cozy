package com.monta.cozy.data

import com.google.android.gms.maps.model.LatLng
import com.monta.cozy.model.PlaceAutoComplete
import com.monta.cozy.model.PlaceDetail
import com.monta.cozy.model.Route
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {

    suspend fun searchPlaceAutoComplete(input: String): Flow<List<PlaceAutoComplete>>

    suspend fun searchPlaceDetail(placeId: String): PlaceDetail

    suspend fun calculateRoute(origin: LatLng, destination: LatLng): Route


}