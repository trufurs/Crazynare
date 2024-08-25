package com.example.crazynare.gameofcards.Model

import java.io.Serializable

class Hand : Serializable {
    var gameHand: ArrayList<Cards?>
    var numberOfCards: Int = 0
    var handFaceUp: Boolean

    internal constructor() {
        this.handFaceUp = false
        this.gameHand = ArrayList()
    }

    internal constructor(cards: ArrayList<Cards?>) {
        this.gameHand = cards
        this.numberOfCards = cards.size
        this.handFaceUp = false
    }

    fun addCard(card: Cards?) {
        gameHand.add(card)
    }

    val card: Cards?
        get() {
            val ret = gameHand[numberOfCards - 1]
            gameHand.removeAt(--numberOfCards)
            return ret
        }

    fun getCard(position: Int): Cards? {
        val ret = gameHand[position]
        return ret
    }

    fun playCards(cards: ArrayList<Cards?>?): Boolean {
        return false
    }

    fun printHand() {
        for (card in gameHand) {
            println(card.toString())
        }
    }

    fun isHandFaceUp() {
        for (i in gameHand.indices) if (!getCard(i)!!.cardFaceUp) {
            this.handFaceUp = false
            return
        }
        this.handFaceUp = true
    }
}