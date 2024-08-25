package com.example.crazynare.Fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.crazynare.Model.Players
import com.example.crazynare.R
import com.example.crazynare.databinding.FragmentItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private val values: List<Players>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
//        layoutParams.width = // Half the screen width
//        layoutParams.height = layoutParams.width // Set height equal to width to make it square
//        holder.itemView.layoutParams = layoutParams
        holder.itemView.layoutParams.height = (holder.itemView.resources.displayMetrics.widthPixels / 2 )-64
        val item = values[position]
        val color = if (item.playerStatus) R.color.light_blue_600 else R.color.red_overlay
        holder.itemView.setBackgroundColor( ContextCompat.getColor(holder.itemView.context, color))
        holder.playerName.text = item.playerName
        holder.playerType.text = item.playerType
        holder.playerButton.visibility=View.VISIBLE
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val playerName: TextView = binding.playerName
        val playerType: TextView = binding.playerType
        val playerButton: Button = binding.playerButton

        override fun toString(): String {
            return super.toString() + " '" + playerType.text + "'"
        }
    }

}