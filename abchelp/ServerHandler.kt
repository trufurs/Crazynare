package com.example.crazynare.Utlis

import com.example.crazynare.Connections.ServerConnectionThread
import java.net.Socket

class ServerHandler :  {
    /*override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val messageData = msg.data
        val gameObject: Any? = messageData.getSerializable(Constants.DATA_KEY)

        if (gameObject is Players) {
            val playerInfo: PlayerInfo? = gameObject as PlayerInfo?
            PlayerListFragment.deviceList.add(playerInfo.username)
            PlayerListFragment.mAdapter.notifyItemInserted(PlayerListFragment.deviceList.size() - 1)
        }
        if (gameObject is Game) {
            if (GameFragment.gameObject != null) {
                GameFragment.gameObject = gameObject as Game?
                GameFragment.updatePlayerStatus()
                GameFragment.updateTable()
                sendToAll(gameObject)
            } else {
                PlayerListFragment.gameObject = gameObject as Game?
            }
        }
    }
*/
    companion object {
        fun sendToAll(gameObject: Any?) {
            val socketIterator: MutableIterator<Socket?> =
                ServerConnectionThread.socketUserMap.keys.iterator()
            var socket: Socket
            while (socketIterator.hasNext()) {
                socket = socketIterator.next()
                if (!ServerConnectionThread.socketUserMap.get(socket)
                        .equals((gameObject as Game?).senderUsername)
                ) {
                    val sendGameName: ServerSenderThread = ServerSenderThread(socket, gameObject)
                    sendGameName.start()
                }
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
