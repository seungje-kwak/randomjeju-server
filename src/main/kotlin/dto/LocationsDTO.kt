package com.sjcompany.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocationDTO(
    val locationId: Int,
    val lat: Double,
    val lng: Double,
    val address: String?,
    val createdAt: String
)