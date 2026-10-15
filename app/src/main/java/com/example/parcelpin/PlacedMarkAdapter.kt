package com.example.parcelpin


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parcelpin.models.PlacemarkModel

class PlacedMarkAdapter(
    private var marks: List<PlacemarkModel>,
    private val onEdit: (PlacemarkModel) -> Unit,
    private val onDelete: (PlacemarkModel) -> Unit
) : RecyclerView.Adapter<PlacedMarkAdapter.MarkViewHolder>() {

    class MarkViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val titleText: TextView =
            itemView.findViewById(R.id.titleText)

        val proximityText: TextView =
            itemView.findViewById<TextView>(R.id.proximityText)

        val coordinatesText: TextView =
            itemView.findViewById(R.id.coordinatesText)

        val editButton: Button =
            itemView.findViewById(R.id.editButton)

        val deleteButton: Button =
            itemView.findViewById(R.id.deleteButton)

        // Binding the Imageview from item_placed_mark.xml layout
        val logoView: ImageView = itemView.findViewById(R.id.logoView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarkViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_placed_mark,
                parent,
                false
            )

        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MarkViewHolder,
        position: Int
    ) {

        val mark = marks[position]

        holder.titleText.text =
            "${mark.id}: ${mark.title}"

        holder.proximityText.text =
            "Proximity Zone: ${mark.proximity}"

        holder.coordinatesText.text =
            "X: ${mark.latitude}, Y: ${mark.longitude}"

        // Map saved string to R.drawable constant
        val logoResId = when (mark.image) {
            "an_post" -> R.drawable.an_post
            "dhl" -> R.drawable.dhl
            "dpd" -> R.drawable.dpd
            "drop2shop" -> R.drawable.drop2shop
            "fedex" -> R.drawable.fedex
            "vinted" -> R.drawable.vinted
            else -> R.drawable.an_post
        }

        // Set the image resource
        holder.logoView.setImageResource(logoResId)

        holder.editButton.setOnClickListener {
            onEdit(mark)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(mark)
        }
    }

    override fun getItemCount(): Int {
        return marks.size
    }

    fun updateMarks(newMarks: List<PlacemarkModel>) {
        marks = newMarks
        notifyDataSetChanged()
    }
}
