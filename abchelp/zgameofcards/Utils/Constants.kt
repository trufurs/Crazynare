package com.example.crazynare.gameofcards.Utils

import com.example.crazynare.gameofcards.Model.Game
import com.example.crazynare.gameofcards.Model.Player

object Constants {
    const val MOVE_FOLD: Int = 0
    const val UPDATE_GAME_NAME: Int = 1
    const val GAME_PLAY: Int = 2
    const val PLAYER_LIST_UPDATE: Int = 3
    const val NEW_GAME: Int = 4
    const val DEAL_CARD: Int = 5
    const val ACTION_KEY: String = "action"
    const val DATA_KEY: String = "data"

    fun isPlayerActive(userName: String?, gameObject: Game): Boolean {

        for (i in 0 until gameObject.players.size) {
            val play: Player? = gameObject.players[i]
            if (play != null) {
                if (play.username == userName && play.isActive) {
                    return true
                }
            }
        }
        return false
    }
}
