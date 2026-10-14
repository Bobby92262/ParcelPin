package com.example.parcelpin

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parcelpin.models.PlacemarkModel

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private lateinit var logoDisplay: ImageView

    private lateinit var logoChoice: Spinner


    private var editingId: Long? = null

    // Default logo set
    private var selectedLogo: String = "an_post"

    //Image file names for logos
    private val availableLogos = linkedMapOf(
        "an_post" to R.drawable.an_post,
        "dhl" to R.drawable.dhl,
        "dpd" to R.drawable.dpd,
        "drop2shop" to R.drawable.drop2shop,
        "fedex" to R.drawable.fedex,
        "vinted" to R.drawable.vinted
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit)

        titleInput =
            findViewById(R.id.titleInput)

        descriptionInput =
            findViewById(R.id.descriptionInput)

        xInput =
            findViewById(R.id.xInput)

        yInput =
            findViewById(R.id.yInput)

        // Bind Logo Display and Choice
        logoDisplay = findViewById(R.id.logoDisplay)
        logoChoice = findViewById(R.id.logoChoice)

        val logonames = availableLogos.keys.toList()
        //val dummynames = listOf("test1", "test2","test3", "test4", "test5")

        val saveButton =
            findViewById<Button>(R.id.saveButton)

        val cancelButton =
            findViewById<Button>(R.id.cancelButton)

        // Set up Logo Choice Spinner
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            logonames
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        logoChoice.adapter = spinnerAdapter

        //Listener for logo choice from spinner
        logoChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedLogo = logonames[position]
                    updateLogoDisplay(selectedLogo)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case where nothing is selected
                }
        }

        editingId =
            intent.getLongExtra("id", -1L)
                .takeIf { it != -1L }

        if (editingId != null) {

            findViewById<TextView>(R.id.formTitle)
                .text = "Edit Mark"

            loadExistingMark(editingId!!)
        }

        saveButton.setOnClickListener {
            saveMark()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun loadExistingMark(id: Long) {

        val mark =
            AppData.placedMarks.findOne(id)

        if (mark == null) {

            Toast.makeText(
                this,
                "Mark not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        titleInput.setText(mark.title)
        descriptionInput.setText(mark.description)
        xInput.setText(mark.latitude.toString())
        yInput.setText(mark.longitude.toString())

        if (mark.image.isNotEmpty()) {
            selectedLogo = mark.image
            val logonames = availableLogos.keys.toList()
            val index = logonames.indexOf(selectedLogo)
            if (index >= 0) {
                logoChoice.setSelection(index)
            }
            val resId = availableLogos[selectedLogo] ?: R.drawable.an_post
            logoDisplay.setImageResource(resId)
        }
    }

    // Helper functions to update logo display
    private fun updateLogoDisplay(logoName: String) {
        val resourceId = availableLogos[logoName] ?: R.drawable.an_post
        logoDisplay.setImageResource(resourceId)
    }
    private fun saveMark() {

        val title =
            titleInput.text.toString().trim()

        val description =
            descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        val x =
            xInput.text.toString().toDoubleOrNull()

        if (x == null) {
            xInput.error = "Enter a valid number"
            return
        }

        val y =
            yInput.text.toString().toDoubleOrNull()

        if (y == null) {
            yInput.error = "Enter a valid number"
            return
        }

        if (editingId == null) {

            val mark = PlacemarkModel(
                title = title,
                description = description,
                latitude = x,
                longitude = y,
                image = selectedLogo
            )

            AppData.placedMarks.create(mark)

            Toast.makeText(
                this,
                "Mark created",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            val mark = PlacemarkModel(
                id = editingId!!,
                title = title,
                description = description,
                latitude = x,
                longitude = y,
                image = selectedLogo
            )

            AppData.placedMarks.update(mark)

            Toast.makeText(
                this,
                "Mark updated",
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }
}
