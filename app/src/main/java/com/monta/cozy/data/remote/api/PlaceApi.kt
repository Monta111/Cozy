package com.monta.cozy.data.remote.api

import com.monta.cozy.data.remote.dto.DistanceResult
import com.monta.cozy.data.remote.dto.PlaceAutoCompleteResult
import com.monta.cozy.data.remote.dto.PlaceDetailResult
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceApi {

    @GET("Place/Autocomplete")
    suspend fun searchPlaceAutoComplete(@Query("input") input: String): PlaceAutoCompleteResult

    @GET("Place/Detail")
    suspend fun searchPlaceDetail(@Query("place_id") placeId: String): PlaceDetailResult

    @GET("Direction")
    suspend fun calculateRoute(
        @Query("origin") originLatLng: String,
        @Query("destination") desLatLng: String,
        @Query("vehicle") vehicle: String = "car"
    ): DistanceResult
}