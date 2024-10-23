package com.example.crazynare.Adapter

import android.net.wifi.ScanResult
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.crazynare.R
/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MywifisearchRecyclerViewAdapter(
    private val onItemClick:(ScanResult) -> Unit
) : RecyclerView.Adapter<MywifisearchRecyclerViewAdapter.ViewHolder>() {

    private var values : MutableList<ScanResult> = mutableListOf()
    fun setData(newData: MutableList<ScanResult>) {
        values = newData
        notifyDataSetChanged()
    }
    fun clearData() {
        values.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_wifiseachlist,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.SSID
        holder.contentView.text = item.BSSID
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.content1)
        val contentView: TextView = view.findViewById(R.id.content2)
        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}