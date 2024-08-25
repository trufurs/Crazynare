package com.example.crazynare.gameofcards.Model

import java.io.Serializable
import java.util.Random

class Game(
    usernames: ArrayList<String>,
    private var numberOfDeck: Int,
    private var numberOfCardsDraw: Int,
    private var isDrawEqual: Boolean,
    restrictedCards: ArrayList<Cards?>,
    var gameName: String
) : Serializable {
    private var numberOfPlayer: Int = usernames.size
    private var decks: ArrayList<Deck?> = ArrayList<Deck?>()
    var players: ArrayList<Player?> = ArrayList<Player?>()
    var cardBackImage: Int = 0
    var senderUsername: String? = null
    private var mTable: Table
    var actionKey: Int = 0
    var gameBackground: Int = 0
    private var deckCards: ArrayList<Cards?>

    init {
        mTable = Table()
        if (usernames.size > 6) {
            usernames.removeAt(usernames.size - 1)
            throw IllegalArgumentException("Number of players above the allowed limit (6)")
        }
        if (isDrawEqual && numberOfCardsDraw > ((numberOfDeck * (52 - restrictedCards.size)) / numberOfPlayer)) {
            usernames.removeAt(usernames.size - 1)
            throw IllegalArgumentException("Cards to be drawn per person not a valid number")
        }

        for (i in 0 until numberOfDeck) {
            if (restrictedCards.size > 0) decks.add(Deck(restrictedCards))
            else decks.add(Deck())
        }
        this.deckCards = populateDecks()
        for (i in 0 until numberOfPlayer) {
            players.add(Player(i + 1, usernames[i], Hand(), true))
        }
        if (!isDrawEqual) {
            hand
        } else {
            getHand(this.numberOfCardsDraw)
        }
        usernames.removeAt(usernames.size - 1)
    }

    fun populateDecks(): ArrayList<Cards?> {
        val allCards: ArrayList<Cards?> = ArrayList<Cards?>()
        for (i in 0 until numberOfDeck) {
            val temp = decks[i]
            for (card in temp!!.cards) {
                allCards.add(card)
            }
        }
        return allCards
    }

    fun getHand(number: Int) {
        for (k in 0 until numberOfPlayer) {
            for (i in 0 until number) {
                shuffleDeck(this.deckCards)
                players[k]!!.hand.addCard(deckCards[0])
                deckCards.removeAt(0)
            }
        }
    }

    fun setHandPlayer(number: Int, userName: String) {
        for (k in 0 until numberOfPlayer) {
            if (players[k]!!.username == userName) {
                for (i in 0 until number) {
                    shuffleDeck(this.deckCards)
                    players[k]!!.hand.addCard(deckCards[0])
                    deckCards.removeAt(0)
                }
                break
            }
        }
    }

    fun getCardsFromDeck(number: Int): ArrayList<Cards?> {
        val cardList: ArrayList<Cards?> = ArrayList<Cards?>()
        shuffleDeck(this.deckCards)
        for (i in 0 until number) {
            val card = deckCards[0]
            card!!.cardFaceUp = true
            cardList.add(card)
            deckCards.removeAt(0)
        }
        return cardList
    }

    val hand: Unit
        get() {
            while (deckCards.size > 0) {
                var playernum = 0
                while (playernum < numberOfPlayer && deckCards.size > 0) {
                    if (deckCards.size != 1) shuffleDeck(this.deckCards)
                    players[playernum]!!.hand.addCard(deckCards[0])
                    deckCards.removeAt(0)
                    playernum++
                }
            }
        }

    fun shuffleDeck(allCards: ArrayList<Cards?>): ArrayList<Cards?> {
        var random: Int
        for (i in allCards.indices) {
            random = getRandomCard(i)
            val temp = allCards[i]
            allCards[i] = allCards[random]
            allCards[random] = temp
        }
        return allCards
    }

    fun articulate() {
        for (player in players) {
            println(player!!.playerID.toString() + " " + player.username)
            player.hand.printHand()
        }
    }


    fun getmTable(): Table {
        return mTable
    }

    fun setmTable(mTable: Table) {
        this.mTable = mTable
    }

    companion object {
        fun getRandomCard(cardNumber: Int): Int {
            return (Math.random() * cardNumber + 1).toInt()
        }

        fun randInt(min: Int, max: Int): Int {
            val rand = Random()
            return rand.nextInt((max - min) + 1) + min
        }
    }
}

