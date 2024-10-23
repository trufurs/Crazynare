package com.example.crazynare.Model

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
data class Players(
    var playerName : String = "Player",
    var playerType : String = "Host",
    var playerID : Int = 1,
    var playerPenalty : Int = 0,
    var playerStatus : Boolean = false,
    var originIp : String = "127.0.0.1"
)