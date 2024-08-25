package com.example.crazynare.gameofcards.Model

import java.io.Serializable

class Cards : Serializable {
    private var suit = 0
    private var rank = 0
    var imageID: String? = null
    var cardFaceUp: Boolean = false

    constructor()

    constructor(_suit: Int, _rank: Int) {
        require(!(_suit != SPADES && _suit != HEARTS && _suit != DIAMONDS && _suit != CLUBS && _suit != JOKER)) { "Illegal playing card suit" }
        require(!(_suit != JOKER && (_rank < 1 || _rank > 13))) { "Illegal playing card value" }

        this.suit = _suit
        this.rank = _rank
        this.cardFaceUp = false
        this.imageID = this.suitAsString + "_" + this.valueAsString
    }

    fun getSuit(): Int {
        return suit
    }

    fun setSuit(suit: Int) {
        this.suit = suit
    }

    fun getRank(): Int {
        return rank
    }

    fun setRank(rank: Int) {
        this.rank = rank
    }

    val suitAsString: String
        get() = when (suit) {
            SPADES -> "spades"
            HEARTS -> "hearts"
            DIAMONDS -> "diamonds"
            CLUBS -> "clubs"
            else -> "joker"
        }

    val valueAsString: String
        get() = when (rank) {
            0 -> "zero"
            1 -> "ace"
            2 -> "two"
            3 -> "three"
            4 -> "four"
            5 -> "five"
            6 -> "six"
            7 -> "seven"
            8 -> "eight"
            9 -> "nine"
            10 -> "ten"
            11 -> "jack"
            12 -> "queen"
            else -> "king"
        }

    fun getCopyForAll(cardRank: String): ArrayList<Cards?> {
        val tempCardSuitList: ArrayList<Cards?> = ArrayList<Any?>()
        if (cardRank.equals("joker", ignoreCase = true)) {
            val card = Cards()
            card.rank = 0
            card.suit = JOKER
            card.imageID = "joker_zero"
            tempCardSuitList.add(card)
        } else {
            var card = Cards()

            card.imageID = "diamonds_$cardRank"
            val meta =
                card.imageID!!.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            card.rank = getRankFromString(meta[1])
            card.suit = getSuitFromString(meta[0])
            tempCardSuitList.add(card)

            card = Cards()
            card.imageID = "hearts_$cardRank"
            val meta1 =
                card.imageID!!.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            card.rank = getRankFromString(meta1[1])
            card.suit = getSuitFromString(meta1[0])
            tempCardSuitList.add(card)

            card = Cards()
            card.imageID = "clubs_$cardRank"
            val meta2 =
                card.imageID!!.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            card.rank = getRankFromString(meta2[1])
            card.suit = getSuitFromString(meta2[0])
            tempCardSuitList.add(card)

            card = Cards()
            card.imageID = "spades_$cardRank"
            val meta3 =
                card.imageID!!.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            card.rank = getRankFromString(meta3[1])
            card.suit = getSuitFromString(meta3[0])
            tempCardSuitList.add(card)
        }
        return tempCardSuitList
    }

    fun getSuitFromString(suit: String): Int {
        if (suit == "diamonds") return DIAMONDS
        else if (suit == "hearts") return HEARTS
        else if (suit == "clubs") return CLUBS
        else if (suit == "spades") return SPADES

        return JOKER
    }

    fun getRankFromString(rank: String?): Int {
        if (suit == JOKER) return 0
        return when (rank) {
            "ace" -> 1
            "two" -> 2
            "three" -> 3
            "four" -> 4
            "five" -> 5
            "six" -> 6
            "seven" -> 7
            "eight" -> 8
            "nine" -> 9
            "ten" -> 10
            "jack" -> JACK
            "queen" -> QUEEN
            "joker" -> JOKER
            else -> KING
        }
    }

    fun isEqual(card: Cards): Boolean {
        return suit == card.suit && rank == card.rank
    }

    companion object {
        const val SPADES: Int = 0
        const val HEARTS: Int = 1
        const val DIAMONDS: Int = 2
        const val CLUBS: Int = 3
        const val JOKER: Int = 4

        const val ACE: Int = 1
        const val JACK: Int = 11
        const val QUEEN: Int = 12
        const val KING: Int = 13
    }
}
