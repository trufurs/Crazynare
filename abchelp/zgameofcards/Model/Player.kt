package com.example.crazynare.gameofcards.Model

import java.io.Serializable

class Player internal constructor(
    var playerID: Int,
    var username: String,
    var hand: Hand,
    var isActive: Boolean
) : Serializable
