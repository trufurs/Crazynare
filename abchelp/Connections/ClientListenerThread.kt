package com.example.crazynare.Connections


import android.os.Bundle
import android.os.Message
import srk.syracuse.gameofcards.Fragments.MainFragment
import srk.syracuse.gameofcards.Model.Game
import srk.syracuse.gameofcards.Utils.Constants
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.net.Socket

class ClientListenerThread internal constructor(var socket: Socket) : Thread() {
    override fun run() {
        try {
            while (true) {
                var objectInputStream: ObjectInputStream
                var inputStream: InputStream? = null
                inputStream = socket.getInputStream()
                objectInputStream = ObjectInputStream(inputStream)
                val data = Bundle()
                val serverObject = objectInputStream.readObject() as Any
                if (serverObject != null) {
                    if (serverObject is String) {
                        data.putSerializable(Constants.DATA_KEY, serverObject)
                        data.putInt(Constants.ACTION_KEY, Constants.UPDATE_GAME_NAME)
                    }
                    if (serverObject is Game) {
                        data.putSerializable(Constants.DATA_KEY, serverObject as Game)
                    }
                    val msg = Message()
                    msg.data = data
                    MainFragment.clientHandler.sendMessage(msg)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
}
