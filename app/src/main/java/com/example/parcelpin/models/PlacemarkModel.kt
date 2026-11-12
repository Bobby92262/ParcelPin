package com.example.parcelpin.models


data class PlacemarkModel(
    var id: Long = 0,
    var title: String = "",
    var proximity: Int = 1,
    var image: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var zoom: Float = 15f,
    var imageUri: String? = null
) {
    fun validate() {
        require(title.isNotBlank()) { "Title is required" }
        require(latitude.isFinite() && latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
        require(longitude.isFinite() && longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
    }
}