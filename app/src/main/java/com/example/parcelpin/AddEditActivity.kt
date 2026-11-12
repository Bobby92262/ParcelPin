package com.example.parcelpin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
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
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts


class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var proximitySpinner: Spinner
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private lateinit var logoDisplay: ImageView

    private lateinit var logoChoice: Spinner


    private var editingId: Long? = null

    private var selectedImageUri: Uri? = null

    private var previousLogoPos: Int = 0

    private var isPrgrammaticSelection: Boolean = false

    //Display labels for the dropdown options
    private val proximityOptions = arrayOf(
        "1 - Nearest",
        "2 - Close",
        "3 - Moderate",
        "4 - Far",
        "5 - Furthest"
    )

    // Default logo set
    private var selectedLogo: String = "an_post"

    //Image file names for logos
    private val availableLogos = linkedMapOf(
        "an_post" to R.drawable.an_post,
        "dhl" to R.drawable.dhl,
        "dpd" to R.drawable.dpd,
        "drop2shop" to R.drawable.drop2shop,
        "fedex" to R.drawable.fedex,
        "vinted" to R.drawable.vinted,
    )

    private val customOptionLabel = "+ Custom Logo..."
    val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                // Keep access to the document URI for later use, when supported.
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Some providers may not grant persistable access.
                }

                selectedImageUri = uri
                selectedLogo = uri.toString()
                logoDisplay.setImageURI(uri)
                previousLogoPos = logoChoice.selectedItemPosition
            } else {
                //User canceled the image selection - revert back to valid position
                isPrgrammaticSelection = true
                logoChoice.setSelection(previousLogoPos)
            }
        }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        // Enable the Up/Back arrow in action bar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        titleInput =
            findViewById(R.id.titleInput)

        proximitySpinner = findViewById(R.id.proximitySpinner)

        xInput =
            findViewById(R.id.xInput)

        yInput =
            findViewById(R.id.yInput)

        // Bind Logo Display and Choice
        logoDisplay = findViewById(R.id.logoDisplay)
        logoChoice = findViewById(R.id.logoChoice)

        val logonames = availableLogos.keys.toList() + customOptionLabel
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
                    //Quick return if user cancels selection
                    if (isPrgrammaticSelection) {
                        isPrgrammaticSelection = false
                        return
                    }

                    selectedLogo = logonames[position]
                    if (selectedLogo == customOptionLabel) {
                        imagePicker.launch(arrayOf("image/*"))
                    } else {
                        previousLogoPos = position
                        selectedImageUri = null
                        updateLogoDisplay(selectedLogo)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case where nothing is selected
                }
        }

        val proxAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            proximityOptions
        )
        proxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        proximitySpinner.adapter = proxAdapter

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

        val index = (mark.proximity - 1).coerceIn(0, 4)
        proximitySpinner.setSelection(index)

        xInput.setText(mark.latitude.toString())
        yInput.setText(mark.longitude.toString())

        if (mark.image.isNotEmpty()) {
            selectedLogo = mark.image
            val logonames = availableLogos.keys.toList() + customOptionLabel
            if (availableLogos.containsKey(selectedLogo)){
                val index = logonames.indexOf(selectedLogo)
                if (logonames != null){
                    isPrgrammaticSelection = true
                    logoChoice.setSelection(index)
                    previousLogoPos = index
                }
                updateLogoDisplay(selectedLogo)
            } else{
                val customIndex = logonames.indexOf(customOptionLabel)
                isPrgrammaticSelection = true
                logoChoice.setSelection(customIndex)
                previousLogoPos = customIndex

                selectedImageUri = Uri.parse(selectedLogo)
                try{
                    logoDisplay.setImageURI(selectedImageUri)
                } catch (_: Exception){
                    logoDisplay.setImageResource(R.drawable.an_post)
                }
            }
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

        val proximity =
            proximitySpinner.selectedItemPosition + 1

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
                proximity = proximity,
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
                proximity = proximity,
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
