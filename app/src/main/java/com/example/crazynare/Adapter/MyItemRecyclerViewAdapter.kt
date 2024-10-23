package com.example.crazynare.Adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.crazynare.Model.Players
import com.example.crazynare.R
import com.example.crazynare.Viewmodel.PlayerViewModel

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private val values: List<Players>,
    private val playerViewModel: PlayerViewModel,
    private val onCLick : (Players) -> Unit
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_players,
                parent,
                false)
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
        holder.playerButton.visibility = if(item.playerID == playerViewModel.playerId) View.VISIBLE else View.INVISIBLE
        holder.playerButton.setOnClickListener { onCLick(item) }
    }

    override fun getItemCount(): Int = values.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerName: TextView = view.findViewById(R.id.player_Name)
        val playerType: TextView = view.findViewById(R.id.player_type)
        val playerButton: Button = view.findViewById(R.id.player_button)

        override fun toString(): String {
            return super.toString() + " '" + playerType.text + "'"
        }
    }

}