package com.example.crazynare.gameofcards.Connections

import android.os.Bundle
import android.os.Message
import srk.syracuse.gameofcards.Fragments.HostFragment
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.net.Socket

class ServerListenerThread internal constructor(private val hostThreadSocket: Socket) : Thread() {
    override fun run() {
        while (true) {
            var objectInputStream: ObjectInputStream
            try {
                var inputStream: InputStream? = null
                inputStream = hostThreadSocket.getInputStream()
                objectInputStream = ObjectInputStream(inputStream)
                val data = Bundle()
                var gameObject = objectInputStream.readObject()
                if (gameObject != null) {
                    if (gameObject is PlayerInfo) {
                        data.putSerializable(Constants.DATA_KEY, gameObject as PlayerInfo)
                        data.putInt(Constants.ACTION_KEY, Constants.PLAYER_LIST_UPDATE)
                        ServerConnectionThread.Companion.socketUserMap.put(
                            hostThreadSocket,
                            (gameObject as PlayerInfo).username
                        )
                    } else {
                        data.putSerializable(Constants.DATA_KEY, gameObject as Game)
                    }
                    val msg = Message()
                    msg.data = data
                    HostFragment.serverHandler.sendMessage(msg)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}
