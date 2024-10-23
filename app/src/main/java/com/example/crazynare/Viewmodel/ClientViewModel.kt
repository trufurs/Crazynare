package com.example.crazynare.Viewmodel

import android.app.Application
import android.util.Log
import androidx.appcompat.view.ActionMode.Callback
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.crazynare.Model.Players
import com.example.crazynare.Utlis.SocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ClientViewModel(application: Application) : AndroidViewModel(application) {

    private val socketManager: SocketManager = SocketManager(application)

    fun connectToServer(
        hostIp: String,
        messageViewModel: MessageViewModel,
        playerViewModel: PlayerViewModel,
        onResult: (Boolean) -> Unit  // Callback to return success or failure as Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main){ playerViewModel.clearPlayers() }
                socketManager.connectToServer(hostIp, 8008){ Result->
                    onResult(Result)
                    socketManager.isHost = false
                }

                // Collect incoming socket messages
                socketManager.messages.collect { jsonMessage ->
                    when (jsonMessage.type) {
                        "message" -> {
                            withContext(Dispatchers.Main) {
                                messageViewModel.addMessage(jsonMessage.content)
                            }
                        }
                        "player" -> {
                            withContext(Dispatchers.Main) {
                                if (jsonMessage.originIp == socketManager.clientSocket?.localAddress?.hostAddress) {
                                    // Handle player data sent by the client
                                    playerViewModel.playerId = jsonMessage.playerID ?: 2
                                }
                                val newPlayer = Players(
                                    playerName = jsonMessage.playerName ?: "Unknown",
                                    playerType = jsonMessage.playerType ?: "Unknown",
                                    playerID = jsonMessage.playerID ?: 0,
                                    playerPenalty = jsonMessage.playerPenalty ?: 0,
                                    playerStatus = jsonMessage.playerStatus?.toBoolean() ?: false,
                                    originIp = jsonMessage.originIp
                                )
                                withContext(Dispatchers.Main){ playerViewModel.addPlayer(newPlayer)}
                            }
                        }
                        "game" -> {
                            if(jsonMessage.playerID == playerViewModel.playerId){
                                withContext(Dispatchers.Main) {
                                    messageViewModel.addQuestion(jsonMessage.content)
                                }
                            }
                            else{
                                withContext(Dispatchers.Main){
                                    messageViewModel.addQuestion(jsonMessage.playerName!!)
                                }
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("SocketManager", "Client connection error: ${e.message}")
                // Invoke the callback with false in case of an error
                withContext(Dispatchers.Main) {
                    onResult(false)  // Connection failed
                }
            }
        }
    }


    fun sendMessage(message: String , messageViewModel: MessageViewModel) {
        //messageViewModel.addMessage(message)
        viewModelScope.launch(Dispatchers.IO) {
            socketManager.sendMessage(message)
            // Optionally, add the sent message to the MessageViewModel
        }
    }
    fun disconnectclient(){
        socketManager.disconnect()
    }

    fun sendPlayerData(player: Players ,playerViewModel: PlayerViewModel) {
        viewModelScope.launch(Dispatchers.IO) {
            socketManager.sendPlayerData(player)
            // Optionally, add the sent player to the PlayerViewModel
        }
    }

    fun startGame(question1: String, question2: String, playerViewModel: PlayerViewModel) {
        socketManager.sendGamedata(question1,question2,playerViewModel)
    }
}
