package com.example.parcelpin.models

interface PlacemarkStore {
    fun findAll(): List<PlacemarkModel>
    fun create(placemark: PlacemarkModel)
    fun update(placemark: PlacemarkModel): Boolean
    fun delete(id: Long): Boolean
    fun findOne(id: Long): PlacemarkModel?

}