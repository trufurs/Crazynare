package com.example.crazynare.gameofcards.Model

import java.io.Serializable

class Table : Serializable {
    var TableCards: ArrayList<Cards?> = ArrayList<Any?>()

    val tableCardsCount: Int
        get() = TableCards.size

    fun removeCardsFromTable(count: Int) {
        for (i in 0 until count) TableCards.removeAt(TableCards.size - 1)
    }

    fun putCardsOnTable(cards: ArrayList<Cards?>) {
        for (i in cards.indices) TableCards.add(cards[i])
    }
}