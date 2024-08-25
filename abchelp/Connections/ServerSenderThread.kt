package com.example.crazynare.Connections

import srk.syracuse.gameofcards.Fragments.PlayerListFragment
import srk.syracuse.gameofcards.Model.Game
import java.io.IOException
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.net.Socket

class ServerSenderThread(private val hostThreadSocket: Socket, var message: Any) : Thread() {
    override fun run() {
        val outputStream: OutputStream
        val objectOutputStream: ObjectOutputStream

        try {
            outputStream = hostThreadSocket.getOutputStream()
            objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(message)
            if (message is Game) {
                PlayerListFragment.gameObject = message as Game
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
