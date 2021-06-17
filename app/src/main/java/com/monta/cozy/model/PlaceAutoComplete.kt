package com.monta.cozy.model

data class PlaceAutoComplete(
    var placeId: String,
    var description: String,
    var structuredFormatting : Map<String, String>
) {

    fun getMainText() : String {
        return structuredFormatting["main_text"] ?: ""
    }

    fun getSecondaryText() : String {
        return structuredFormatting["secondary_text"] ?: ""
    }
}