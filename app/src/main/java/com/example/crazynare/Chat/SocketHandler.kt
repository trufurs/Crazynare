package com.example.crazynare.Chat

import kotlinx.coroutines.*
import java.io.*
import java.net.Socket

object SocketHandler {
    private var socket: Socket? = null
    private var outputStream: PrintWriter? = null
    private var inputStream: BufferedReader? = null

    fun setSocket(newSocket: Socket) {
        socket = newSocket
        outputStream = PrintWriter(BufferedWriter(OutputStreamWriter(socket?.getOutputStream())), true)
        inputStream = BufferedReader(InputStreamReader(socket?.getInputStream()))
    }

    fun getSocket(): Socket? = socket

    suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO) {
            outputStream?.println(message)
        }
    }

    suspend fun receiveMessages(onMessageReceived: (String) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                while (true) {
                    val message = inputStream?.readLine()
                    message?.let {
                        onMessageReceived(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun closeConnection() {
        socket?.close()
    }
}
