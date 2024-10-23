package com.example.crazynare.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crazynare.R

class ChatAdapter(private val messages: MutableList<String>) :RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_message
                    , parent
                    , false
                )
        )
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        holder.content_message.text = messages[position]

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val content_message: TextView = view.findViewById<TextView>(R.id.message_message)
    }

}
