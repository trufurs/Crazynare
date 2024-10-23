package com.example.crazynare.Viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.crazynare.Model.Players

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData to store the list of players
    private val _players = MutableLiveData<MutableList<Players>>(mutableListOf())
    val players: LiveData<MutableList<Players>> get() = _players
    var maxid = 1
    var playerId = 1

    // Function to add or update a player
    fun addPlayer(newPlayer: Players) {
        val currentPlayers = _players.value ?: mutableListOf()

        // Check if the player already exists by matching originIp
        val existingPlayerIndex = currentPlayers.indexOfFirst { it.originIp == newPlayer.originIp }

        if (existingPlayerIndex == -1) {
            // Player doesn't exist, add it
            currentPlayers.add(newPlayer)
        } else {
            // Player exists, update only if the player data has changed
            val existingPlayer = currentPlayers[existingPlayerIndex]

            if (existingPlayer != newPlayer) {
                currentPlayers[existingPlayerIndex] = newPlayer
            }
        }

        _players.value = currentPlayers
    }

    // Function to remove a player by IP
    fun removePlayerByIp(originIP: String) {
        val currentPlayers = _players.value?.toMutableList() ?: mutableListOf()
        currentPlayers.removeAll { it.originIp == originIP }
        _players.value = currentPlayers
    }

    // Function to get a player by IP
    fun getPlayerByIp(originIP: String): Players? {
        return _players.value?.find { it.originIp == originIP }
    }

    // Function to remove a player by ID
    fun removePlayer(playerId: Int) {
        val currentPlayers = _players.value?.toMutableList() ?: mutableListOf()
        currentPlayers.removeAll { it.playerID == playerId }
        _players.value = currentPlayers
    }

    // Function to get a player by ID
    fun getPlayerById(playerId: Int): Players? {
        return _players.value?.find { it.playerID == playerId }
    }

    // Function to clear the player list
    fun clearPlayers() {
        _players.value = mutableListOf()
    }
}
