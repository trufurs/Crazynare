package com.example.crazynare.Chat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket

class ClientSocket(private val hostAddress: String, private val port: Int) {

    suspend fun connectToHost(onConnected: (Socket) -> Unit) {
        withContext(Dispatchers.IO) {
            val socket = Socket(hostAddress, port)
            onConnected(socket)
        }
    }
}
