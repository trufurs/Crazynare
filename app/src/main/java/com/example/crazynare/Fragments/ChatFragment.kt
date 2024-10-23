package com.example.crazynare.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crazynare.Adapter.ChatAdapter
import com.example.crazynare.R
import com.example.crazynare.Viewmodel.ClientViewModel
import com.example.crazynare.Viewmodel.HostViewModel
import com.example.crazynare.Viewmodel.MessageViewModel
import com.google.android.material.drawable.DrawableUtils

class ChatFragment : Fragment() {
    private val messageViewModel: MessageViewModel by activityViewModels() // Using activityViewModels
    private val clientViewModel: ClientViewModel by activityViewModels() // Access ClientViewModel
    private val hostViewModel: HostViewModel by activityViewModels() // Access HostViewModel

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chat_fragment, container, false)

        // Initialize RecyclerView and adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.message_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        chatAdapter = ChatAdapter(messages)
        recyclerView.adapter = chatAdapter

        val sendButton = view.findViewById<Button>(R.id.send_button)
        val messageField = view.findViewById<EditText>(R.id.message_input)

        sendButton.setOnClickListener {
            if (messageField.text.isBlank()) {
                return@setOnClickListener
            }
            val message = messageField.text.toString()
            messageField.text.clear() // Clear the input field

            // Send the message via the appropriate ViewModel
            if (hostViewModel.isHost()) {
                Log.i("MEssage Sent Host",message)
                //messageViewModel.addMessage(message)
                hostViewModel.sendMessage(message,messageViewModel) // Send message using HostViewModel
            } else {
                Log.i("MEssage Sent Client",message)
                clientViewModel.sendMessage(message,messageViewModel) // Send message using ClientViewModel
            }
        }

        val game_question = view.findViewById<TextView>(R.id.game_question)
        game_question.setTextColor(AppCompatResources.getColorStateList(requireContext(),R.color.black))
        //game_question.background = AppCompatResources.getDrawable(requireContext(),R.color.yellow_overlay)

        messageViewModel.question.observe(viewLifecycleOwner) { question ->
            game_question.text = question
        }
        // Observe chat messages
        // In ChatFragment, inside the observer
        messageViewModel.messages.observe(viewLifecycleOwner) { updatedMessages ->
            messages.clear()
            messages.addAll(updatedMessages)
            chatAdapter.notifyDataSetChanged()
            Log.d("ChatFragment", "Messages updated: $updatedMessages")
            if (messages.isNotEmpty()) {
                recyclerView.scrollToPosition(messages.size - 1)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.main, PlayerFragment())
            ?.addToBackStack(null)
            ?.commit()
    }
}
