package com.example.parcelpin.models

class PlacemarkMemStore : PlacemarkStore {

    private val placemarks = ArrayList<PlacemarkModel>()
    private var lastId = 0L

    private fun getId(): Long = lastId++

    override fun findAll(): List<PlacemarkModel> {
        return placemarks
    }

    override fun create(placemark: PlacemarkModel) {
        placemark.id = getId()
        placemarks.add(placemark)
    }

    override fun update (placemark: PlacemarkModel): Boolean {
        val foundPlacemark = findOne(placemark.id)
        if (foundPlacemark != null) {
            foundPlacemark.title = placemark.title
            foundPlacemark.description = placemark.description
            foundPlacemark.image = placemark.image
            foundPlacemark.latitude = placemark.latitude
            foundPlacemark.longitude = placemark.longitude
            foundPlacemark.zoom = placemark.zoom
            return true
        }
        return false
    }

    override fun delete(id: Long): Boolean {
        return placemarks.removeIf { it.id == id }
    }

    override fun findOne(id: Long): PlacemarkModel? {
        return placemarks.find { it.id == id }
    }

}