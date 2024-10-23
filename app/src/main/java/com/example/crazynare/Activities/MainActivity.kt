package com.example.crazynare.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.crazynare.Fragments.WelcomeFragment
import com.example.crazynare.R
import com.example.crazynare.Viewmodel.ClientViewModel
import com.example.crazynare.Viewmodel.HostViewModel
import com.example.crazynare.Viewmodel.MessageViewModel
import com.example.crazynare.Viewmodel.PlayerViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager

    // Initialize the ViewModels using viewModels() or ViewModelProvider, if needed for the activity scope
    private val playerViewModel: PlayerViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()

    // You don't need to manually pass the dependencies now.
    // Fragments will access these via activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.main, WelcomeFragment()).commit()
    }
}
/*
        chatList = findViewById(R.id.chat_list)
        messageInput = findViewById(R.id.message_input)
        sendButton = findViewById(R.id.send_button)

        chatAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)
        chatList.adapter = chatAdapter

        // Choose between host and client
        if (isHost()) {
            startHostServer()
        } else {
            connectToHost()
        }

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            messageInput.text.clear()

            // Send message to other players
            CoroutineScope(Dispatchers.IO).launch {
                SocketHandler.sendMessage(message)
            }
        }
    }

    private fun isHost(): Boolean {
        // Determine if this device is the host (you can use UI logic for this)
        return false // For demo purposes, set as host
    }

    private fun startHostServer() {
        val server = HostServer(port)

        CoroutineScope(Dispatchers.IO).launch {
            server.startServer { clientSocket ->
                SocketHandler.setSocket(clientSocket)
                receiveMessages()
            }
        }
    }

    private fun connectToHost() {
        val client = ClientSocket("192.168.249.209", port) // Replace with the host IP

        CoroutineScope(Dispatchers.IO).launch {
            client.connectToHost { socket ->
                SocketHandler.setSocket(socket)
                receiveMessages()
            }
        }
    }

    private fun receiveMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            SocketHandler.receiveMessages { message ->
                runOnUiThread {
                    messages.add(message)
                    chatAdapter.notifyDataSetChanged()
                }
            }
        }
    }*/