package com.example.parcelpin.models

data class PlacemarkModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var image: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var zoom: Float = 15f
)