package com.monta.cozy.data.mapping

import com.monta.cozy.data.remote.dto.PlaceAutoCompleteDto
import com.monta.cozy.data.remote.dto.PlaceDetailDto
import com.monta.cozy.data.remote.dto.RouteDto
import com.monta.cozy.model.PlaceAutoComplete
import com.monta.cozy.model.PlaceDetail
import com.monta.cozy.model.Route

fun PlaceDetailDto.toPOJO(): PlaceDetail {
    return PlaceDetail(placeId, name, formatedAddress, geometry.location.lat, geometry.location.lng)
}

fun PlaceAutoCompleteDto.toPOJO(): PlaceAutoComplete {
    return PlaceAutoComplete(placeId, description, structuredFormatting)
}

fun RouteDto.toPOJO(): Route {
    if (legDtos.isNotEmpty()) {
        return Route(
            legDtos[0].distanceDto.text, legDtos[0].distanceDto.value,
            legDtos[0].durationDto.text, legDtos[0].durationDto.value
        )
    }
    return Route()
}