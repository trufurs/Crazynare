package com.example.crazynare.gameofcards.Utils

import android.os.Bundle
import android.os.Handler
import android.os.Message
import srk.syracuse.gameofcards.Connections.ClientConnectionThread

class ClientHandler : Handler() {
    var messageData: Bundle? = null

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        messageData = msg.data
        val value = messageData.getInt(Constants.ACTION_KEY)
        val clientObject: Any? = messageData.getSerializable(Constants.DATA_KEY)
        if (value == Constants.UPDATE_GAME_NAME) {
            val gameName = clientObject as String?
            JoinGameFragment.gameName.setText(gameName)
        }
        if (clientObject is Game) {
            if (GameFragment.gameObject != null) {
                if ((clientObject as Game?).senderUsername.equals(Constants.NEW_GAME.toString())) {
                    ClientSenderThread.isActive = true
                    //                    ((Game) clientObject).senderUsername = "";
                }
                GameFragment.gameObject = clientObject as Game?
                GameFragment.updatePlayerStatus()
                GameFragment.updateTable()
                GameFragment.updateHand()
            } else {
                JoinGameFragment.gameobject = clientObject as Game?
            }
        }
    }

    companion object {
        fun sendToServer(gameObject: Any?) {
            val sendGameChange: ClientSenderThread =
                ClientSenderThread(ClientConnectionThread.socket, gameObject)
            sendGameChange.start()
        }
    }
}
