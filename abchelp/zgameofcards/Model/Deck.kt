package com.example.crazynare.gameofcards.Model

import java.io.Serializable

class Deck : Serializable {
    var cards: Array<Cards?>
    var numberOfCards: Int = 0

    internal constructor() {
        cards = arrayOfNulls(53)
        initDeck()
    }

    internal constructor(restrictedCards: ArrayList<Cards>) {
        cards = arrayOfNulls(53 - restrictedCards.size)
        initDeckWithRestrictedCards(restrictedCards)
    }

    private fun initDeck() {
        var cardIndex = 0
        for (rank in 1..13) {
            for (suit in 0..3) {
                cards[cardIndex] = Cards(suit, rank)
                cardIndex++
            }
        }
        cards[52] = Cards(Cards.Companion.JOKER, 0)
        numberOfCards = cardIndex
    }

    private fun initDeckWithRestrictedCards(restrictedCards: ArrayList<Cards>) {
        var cardIndex = 0
        var contains: Boolean
        var isJoker = false
        for (rank in 1..13) {
            for (suit in 0..3) {
                val temp = Cards(suit, rank)
                contains = false
                for (crd in restrictedCards) {
                    if (temp.isEqual(crd)) {
                        contains = true
                    } else if (crd.rank == 0) {
                        isJoker = true
                    }
                }

                if (!contains) {
                    cards[cardIndex] = Cards(suit, rank)
                    cardIndex++
                }
                if (!isJoker) {
                    isJoker = true
                    cards[cardIndex] = Cards(4, 0)
                    cardIndex++
                }
            }
        }
        numberOfCards = cardIndex
    }

    fun deal(): Cards? {
        if (numberOfCards == 0) return null

        numberOfCards--
        return cards[numberOfCards]
    }

    fun shuffleDeck() {
        var random: Int
        for (i in 0 until numberOfCards) {
            random = getRandomCard(i)
            val temp = cards[i]
            cards[i] = cards[random]
            cards[random] = temp
        }
    }

    fun removeCard(): Cards? {
        val crd = cards[numberOfCards - 1]
        numberOfCards--
        return crd
    }

    fun articulate() {
        for (card in cards) {
            println(card.toString())
        }

        println("Number of cards:$numberOfCards")
    }

    companion object {
        fun getRandomCard(cardNumber: Int): Int {
            return (Math.random() * cardNumber + 1).toInt()
        }
    }
}