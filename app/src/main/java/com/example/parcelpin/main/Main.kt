package com.example.parcelpin.main

import com.example.parcelpin.models.PlacemarkMemStore
import com.example.parcelpin.models.PlacemarkModel
import java.util.Scanner

val store = PlacemarkMemStore()

fun main() {
    val scanner = Scanner(System.`in`)
    var input: Int

    do {
        input = menu()
        when (input) {
            1 -> addPlacemark()
            2 -> listAllPlacemarks()
            3 -> updatePlacemark()
            4 -> deletePlacemark()
            5 -> searchPlacemark()
            0 -> println("Exiting...")
            else -> println("Invalid option. Please try again.")
        }
        println()
    }while (input != 0)
}

fun menu(): Int {
    println("n=== ParcelPin CLI Manager ===")
    println("1. Add Placemark")
    println("2. List All Placemarks")
    println("3. Update Placemark")
    println("4. Delete Placemark")
    println("5. Search Placemark")
    println("0. Exit")
    println()
    print("\nEnter your option: ")
    return readlnOrNull()?.toIntOrNull()?: -1
}

fun addPlacemark() {
    println("\n--- Add Placemark ---")
    print("Enter Title: ")
    val title = readlnOrNull()?.trim().orEmpty()
    print("Enter Description: ")
    val description = readlnOrNull()?.trim().orEmpty()

    if (title.isNotEmpty()) {
        val placemark = PlacemarkModel(title = title, description = description)
        store.create(placemark)
        println("Placemark added successfully with ID: ${placemark.id}")
    } else {
        println("Title cannot be empty. Creation cancelled.")
    }
}
fun updatePlacemark() {
    println("\n--- Update Placemark ---")
    listAllPlacemarks()
    if (store.findAll().isEmpty()) return

    print("\nEnter ID of Placemark to update: ")
    val id = readlnOrNull()?.toLongOrNull()

    if (id != null && store.findOne(id) != null) {
        print("Enter New Title: ")
        val title = readlnOrNull()?.trim().orEmpty()
        print("Enter New Description: ")
        val description = readlnOrNull()?.trim().orEmpty()

        if (title.isNotEmpty()) {
            val updated = store.update(PlacemarkModel(id = id, title = title, description = description))
            if (updated) println("Placemark updated successfully.")
        } else {
            println("Title cannot be empty. Update cancelled.")
        }
    } else {
        println("Placemark with ID $id not found.")
    }
}

fun listAllPlacemarks() {
    println("\n--- All Placemarks ---")
    val placemarks = store.findAll()
    if (placemarks.isEmpty()) {
        println("No placemarks stored yet.")
    } else {
        placemarks.forEach { println("ID: ${it.id} | Title: ${it.title} | Description: ${it.description}") }
    }
}

fun deletePlacemark(){
    println("\n--- Delete Placemark ---")
    listAllPlacemarks()
    if (store.findAll().isEmpty()) return

    print("\nEnter ID of Placemark to delete: ")
    val id = readlnOrNull()?.toLongOrNull()

    if (id != null) {
        val deleted = store.delete(id)
        if (deleted) {
            println("Placemark with ID $id deleted successfully.")
        } else {
            println("Placemark with ID $id not found.")
        }
    } else {
        println("Invalid ID entered.")
    }
}
fun searchPlacemark() {
    println("\n--- Search Placemark ---")
    print("Enter ID: ")
    val id = readlnOrNull()?.toLongOrNull()

    if (id != null) {
        val placemark = store.findOne(id)
        if (placemark != null) {
            println("Found: ID: ${placemark.id} | Title: ${placemark.title} | Description: ${placemark.description}")
        } else {
            println("No placemark found with ID $id.")
        }
    } else {
        println("Invalid ID entered.")
    }
}