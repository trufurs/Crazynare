package com.example.crazynare.gameofcards.Adapters


import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class PlayerAdapter(private val mDataSet: ArrayList<String>) :
    RecyclerView.Adapter<PlayerAdapter.ViewHolder?>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textView: TextView

        init {
            v.setOnClickListener { }
            textView = v.findViewById<View>(R.id.textView) as TextView
        }
    }

    fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.player_list_item, viewGroup, false)

        return ViewHolder(v)
    }

    fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = mDataSet[position]
    }

    val itemCount: Int
        get() = mDataSet.size
}
