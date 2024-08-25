package com.example.crazynare.gameofcards.Connections

import srk.syracuse.gameofcards.Fragments.MainFragment
import java.io.IOException
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.net.Socket

class ClientSenderThread(private val hostThreadSocket: Socket?, var message: Any) : Thread() {
    override fun run() {
        val outputStream: OutputStream
        val objectOutputStream: ObjectOutputStream
        if (hostThreadSocket!!.isConnected) {
            try {
                if (isActive) {
                    if (message is Game && !Constants.isPlayerActive(
                            MainFragment.userName.getText().toString(), message as Game
                        )
                    ) {
                        isActive = false
                    }
                    outputStream = hostThreadSocket.getOutputStream()
                    objectOutputStream = ObjectOutputStream(outputStream)
                    objectOutputStream.writeObject(message)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    companion object {
        var isActive: Boolean = true
    }
}
