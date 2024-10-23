package com.example.crazynare.Chat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket

class HostServer(private val port: Int) {

    private var serverSocket: ServerSocket? = null
    private val clientSockets = mutableListOf<Socket>()

    suspend fun startServer(onClientConnected: (Socket) -> Unit) {
        withContext(Dispatchers.IO) {
            serverSocket = ServerSocket(port)
            while (true) {
                val clientSocket = serverSocket?.accept()
                clientSocket?.let {
                    clientSockets.add(it)
                    onClientConnected(it)
                }
            }
        }
    }

    fun getClients(): List<Socket> = clientSockets

    fun closeServer() {
        serverSocket?.close()
        clientSockets.forEach { it.close() }
    }
}
