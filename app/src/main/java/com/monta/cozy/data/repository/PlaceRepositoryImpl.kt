package com.monta.cozy.data.repository

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.monta.cozy.data.PlaceRepository
import com.monta.cozy.data.mapping.toPOJO
import com.monta.cozy.data.remote.api.PlaceApi
import com.monta.cozy.model.PlaceAutoComplete
import com.monta.cozy.model.PlaceDetail
import com.monta.cozy.model.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api: PlaceApi
) : PlaceRepository {

    override suspend fun searchPlaceAutoComplete(input: String): Flow<List<PlaceAutoComplete>> {
        return flow {
            if (input.isBlank()) {
                emit(emptyList<PlaceAutoComplete>())
            } else {
                emit(api.searchPlaceAutoComplete(input).predictions.map {
                    it.toPOJO()
                })
            }
        }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun searchPlaceDetail(placeId: String): PlaceDetail {
        return withContext(Dispatchers.IO) {
            api.searchPlaceDetail(placeId).placeDetailDto.toPOJO()
        }
    }

    override suspend fun calculateRoute(origin: LatLng, destination: LatLng): Route {
        return withContext(Dispatchers.IO) {
            val originLatLng = origin.latitude.toString() + "," + origin.longitude.toString()
            val destinationLatLng = origin.latitude.toString() + "," + origin.longitude.toString()
            val routes = api.calculateRoute(originLatLng, destinationLatLng).routeDtos
            return@withContext if (routes.isNotEmpty()) {
                routes[0].toPOJO()
            } else {
                Route()
            }
        }
    }
}
