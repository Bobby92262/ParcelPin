package com.example.parcelpin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parcelpin.models.PlacemarkModel

class MarkListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacedMarkAdapter

    // Filter state
    private var currentSearchQuery: String = ""
    private var selectedProximity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_list)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        recyclerView =
            findViewById(R.id.marksRecyclerView)

        adapter = PlacedMarkAdapter(
            marks = AppData.placedMarks.findAll(),

            onEdit = { mark ->
                editMark(mark)
            },

            onDelete = { mark ->
                deleteMark(mark)
            }
        )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        val returnButton =
            findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {

            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
        setupFilterControls()
    }

    private fun setupFilterControls() {
        val searchInput = findViewById<EditText>(R.id.searchInput)
        val filterSpinner = findViewById<Spinner>(R.id.filterSpinner)

        val filterOptions = arrayOf(
            "All Zones",
            "Zone 1 (Nearest)",
            "Zone 2 ",
            "Zone 3 ",
            "Zone 4 ",
            "Zone 5 (Furthest)"
        )

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            filterOptions
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = spinnerAdapter

        //Proximity filter
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedProximity = position
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //Search bar listener
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString()?.trim()?: ""
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
            })
        }

            private fun applyFilters() {
                val allMarks = AppData.placedMarks.findAll()

                val filteredMarks = allMarks.filter { mark ->
                    val matchesSearch = mark.image.contains(currentSearchQuery, ignoreCase = true)
                    val matchesProximity =
                        (selectedProximity == 0 || mark.proximity == selectedProximity)

                    matchesSearch && matchesProximity
                }
                adapter.updateMarks(filteredMarks)
            }


    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            applyFilters()
        }
    }

    private fun editMark(mark: PlacemarkModel) {
        val intent =
            Intent(this, AddEditActivity::class.java)
        intent.putExtra("id", mark.id)
        startActivity(intent)
    }

    private fun deleteMark(mark: PlacemarkModel) {
        AppData.placedMarks.delete(mark.id)
        applyFilters()
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
}
