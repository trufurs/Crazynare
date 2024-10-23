package com.example.crazynare.Connections


import com.example.crazynare.Model.Players
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ServerConnectionThread : Thread() {
    override fun run() {
        if (serverSocket == null) {
            try {
                serverSocket = ServerSocket(SocketServerPORT)
                serverStarted = true
                while (true) {
                    val socket = serverSocket!!.accept()
                    if (!allPlayersJoined) {
                        val socketListenThread = Thread(ServerListenerThread(socket))
                        socketListenThread.start()
                        val sendGameName =
                            ServerSenderThread(socket, "Crazynare")
                        sendGameName.start()
                        socketUserMap[socket] = null
                        if (socketUserMap.size > 3) {
                            allPlayersJoined = true
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val SocketServerPORT: Int = 8080
        @JvmField
        var socketUserMap: HashMap<Socket?, String?> = HashMap<Socket?, String?>()
        var serverStarted: Boolean = false
        var serverSocket: ServerSocket? = null
        var allPlayersJoined: Boolean = false
    }
}
