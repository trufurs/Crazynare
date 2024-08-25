package com.example.crazynare.gameofcards.Connections


import srk.syracuse.gameofcards.Model.PlayerInfo
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException

class ClientConnectionThread(var userName: String) : Thread() {
    var dstAddress: String? = null
    var dstPort: Int = 8080

    override fun run() {
        if (socket == null) {
            try {
                val deviceList: ArrayList<String> = WifiHelper.getDeviceList()
                if (deviceList.size > 0) {
                    dstAddress = deviceList[0]
                    if (dstAddress != null) {
                        socket = Socket(dstAddress, dstPort)
                        if (socket!!.isConnected) {
                            serverStarted = true
                            val clientListener = ClientListenerThread(socket)
                            clientListener.start()
                            val playerInfo: PlayerInfo = PlayerInfo(userName)
                            val sendUserName = ClientSenderThread(socket, playerInfo)
                            sendUserName.start()
                        }
                    }
                }
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var socket: Socket? = null
        var serverStarted: Boolean = false
    }
}
