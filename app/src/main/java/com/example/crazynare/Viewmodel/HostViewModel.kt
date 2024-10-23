package com.example.crazynare.Viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.crazynare.Model.Players
import com.example.crazynare.Utlis.SocketManager
import com.example.crazynare.Viewmodel.MessageViewModel
import com.example.crazynare.Viewmodel.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class HostViewModel(application: Application) : AndroidViewModel(application) {

    // Fetch MessageViewModel and PlayerViewModel using ViewModelProvider
    private val socketManager = SocketManager(application)
    fun startServer(
        messageViewModel: MessageViewModel,
        playerViewModel: PlayerViewModel,
        onResult: (Boolean) -> Unit  // Callback to return success or failure as Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                socketManager.startServer(8008,playerViewModel){result ->
                    onResult(result)
                    socketManager.isHost = true
                }


                // Collect incoming socket messages
                socketManager.messages.collect { jsonMessage ->
                    when (jsonMessage.type) {
                        "message" -> {
                            withContext(Dispatchers.Main) {
                                // Add message to MessageViewModel
                                messageViewModel.addMessage(jsonMessage.content)
                            }
                        }
                        "player" -> {
                            // Create a Players object from the received data
                            val newPlayer = Players(
                                playerName = jsonMessage.playerName ?: "Unknown",
                                playerType = jsonMessage.playerType ?: "Unknown",
                                playerID = jsonMessage.playerID ?: 0,
                                playerPenalty = jsonMessage.playerPenalty ?: 0,
                                playerStatus = jsonMessage.playerStatus?.toBoolean() ?: false,
                                originIp = jsonMessage.originIp
                            )
                            // Add player to PlayerViewModel
                            withContext(Dispatchers.Main){ playerViewModel.addPlayer(newPlayer)}
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
                Log.e("SocketManager", "Server start error: ${e.message}")
                // Invoke the callback with false in case of an error
                withContext(Dispatchers.Main) {
                    onResult(false)  // Server failed to start
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

    fun sendPlayerData(player: Players, playerViewModel: PlayerViewModel) {
        viewModelScope.launch(Dispatchers.IO) {
            socketManager.sendPlayerData(player)
            // Optionally, add the sent player to the PlayerViewModel
        }
    }

    fun disconnecthost(){
        socketManager.disconnect()
    }
    fun isHost(): Boolean {
        return socketManager.isHost // Assuming you have this method in SocketManager
    }

    fun startGame(question1: String, question2: String , playerViewModel: PlayerViewModel) {
        socketManager.sendGamedata(question1,question2, playerViewModel)
    }
}
