package com.example.crazynare.gameofcards.Fragments


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.net.Socket

class GameFragment : Fragment() {
    var mCardHand: RecyclerView? = null
    var mTableView: RecyclerView? = null
    var foldButton: CircleButton? = null

    var playButton: CircleButton? = null
    var myImageViewText: TextView? = null
    var newGameButton: Button? = null
    var dealCardButton: Button? = null
    var hideButton: CircleButton? = null

    var dialog: AlertDialog? = null
    protected var mCardLayoutManager: RecyclerView.LayoutManager? = null
    protected var mTableLayoutManager: RecyclerView.LayoutManager? = null
    fun setParameters(gameObject: Game?, socket: Socket?) {
        Companion.gameObject = gameObject
        Companion.socket = socket
    }


    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.main_game_layout, container, false)
        currentUserImage = rootView!!.findViewById<View>(R.id.currentPlayerImage) as ImageView
        currentUserText = rootView!!.findViewById<View>(R.id.currentPlayerText) as TextView
        val relative: RelativeLayout =
            rootView!!.findViewById<View>(R.id.mainGameLayout) as RelativeLayout
        relative.setBackgroundResource(gameObject.gameBackground)
        context = getActivity()
        updatePlayers()
        updatePlayerStatus()
        updateHand()
        mCardHand = rootView!!.findViewById<View>(R.id.cardHand) as RecyclerView
        mTableView = rootView!!.findViewById<View>(R.id.tableView) as RecyclerView
        foldButton = rootView!!.findViewById<View>(R.id.foldCardsButton) as CircleButton
        newGameButton = rootView!!.findViewById<View>(R.id.newGameButton) as Button
        dealCardButton = rootView!!.findViewById<View>(R.id.dealCardsButton) as Button
        if (ServerConnectionThread.socketUserMap != null && ServerConnectionThread.socketUserMap.size() > 0) {
            val linearLayout3: LinearLayout =
                rootView!!.findViewById<View>(R.id.linearLayout3) as LinearLayout
            linearLayout3.setVisibility(View.VISIBLE)
        } else {
            val layoutParams: RelativeLayout.LayoutParams = mCardHand
                .getLayoutParams() as RelativeLayout.LayoutParams
            layoutParams.addRule(
                RelativeLayout.RIGHT_OF,
                0
            )
            mCardHand.setLayoutParams(layoutParams)
        }
        newGameButton!!.setOnClickListener {
            dialog = confirmMove(Constants.NEW_GAME).create()
            dialog.show()
        }
        dealCardButton!!.setOnClickListener {
            if (gameObject.deckCards.size() === 0) {
                Toast.makeText(getActivity(), "Not enough cards to DEAL!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val dealCards = CardDealDialog()
                dealCards.setTargetFragment(this@GameFragment, 2)
                dealCards.show(getFragmentManager(), "Deal Cards")
            }
        }
        playButton = rootView!!.findViewById<View>(R.id.playButton) as CircleButton
        myImageViewText = rootView!!.findViewById<View>(R.id.myImageViewText) as TextView
        hideButton = rootView!!.findViewById<View>(R.id.hideCardsButton) as CircleButton

        mCardLayoutManager =
            LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        mTableLayoutManager =
            LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        mCardHand.setLayoutManager(mCardLayoutManager)
        mCardHandAdapter =
            CardHandAdapter(context, thisPlayer.hand.gameHand, gameObject.cardBackImage)
        mCardHand.setAdapter(mCardHandAdapter)

        mCardHandAdapter.setOnItemClickListener(object : OnItemClickListener() {
            fun OnItemClick(v: View, position: Int) {
                val rootLayout = v.parent as View
                val cardBack = rootLayout.findViewById<View>(R.id.cardDesignBack)
                updateHand()
                cardBack.visibility = View.INVISIBLE
                setCardFaceUp(position, true)
            }
        })

        mCardHandAdapter.setOnItemLongClickListener(object : OnItemLongClickListener() {
            fun OnItemLongClick(v: View?, position: Int): Boolean {
                gameObject.mTable.TableCards.add(thisPlayer.hand.gameHand.get(position))
                tempHandCards.add(thisPlayer.hand.gameHand.get(position))
                if (tempHandCards.size > 0) {
                    playButton.setVisibility(View.VISIBLE)
                    myImageViewText!!.visibility = View.VISIBLE
                }
                thisPlayer.hand.gameHand.remove(position)
                mTableViewAdapter.notifyItemInserted(gameObject.mTable.TableCards.size() - 1)
                mCardHandAdapter.notifyItemRemoved(position)
                return true
            }
        })
        mTableViewAdapter =
            TableViewAdapter(context, gameObject.mTable.TableCards, gameObject.cardBackImage)
        mTableView.setLayoutManager(mTableLayoutManager)
        mTableView.setAdapter(mTableViewAdapter)

        mTableViewAdapter.setOnItemCLickListener(object : OnItemClickListener() {
            fun OnItemClick(v: View?, position: Int) {
            }

            fun OnItemLongClick(v: View?, position: Int) {
                tableChanged = true
                thisPlayer.hand.gameHand.add(gameObject.mTable.TableCards.get(position))
                if (tempHandCards.contains(gameObject.mTable.TableCards.get(position))) {
                    tempHandCards.remove(gameObject.mTable.TableCards.get(position))
                }
                if (tempHandCards.size == 0 && !tableChanged) {
                    playButton.setVisibility(View.INVISIBLE)
                    myImageViewText!!.visibility = View.INVISIBLE
                } else {
                    playButton.setVisibility(View.VISIBLE)
                    myImageViewText!!.visibility = View.VISIBLE
                }

                gameObject.mTable.TableCards.remove(position)
                mCardHandAdapter.notifyItemInserted(thisPlayer.hand.gameHand.size() - 1)
                mTableViewAdapter.notifyItemRemoved(position)
                gameObject.setActionKey(Constants.GAME_PLAY)
            }
        })

        foldButton.setOnClickListener(View.OnClickListener {
            dialog = confirmMove(Constants.MOVE_FOLD).create()
            dialog.show()
        })

        playButton.setOnClickListener(View.OnClickListener {
            if (tempHandCards.size > 0 || tableChanged) {
                tableChanged = false
                playButton.setVisibility(View.INVISIBLE)
                myImageViewText!!.visibility = View.INVISIBLE
                updateGameForAll(gameObject, Constants.GAME_PLAY)
            }
        })

        hideButton.setOnClickListener(View.OnClickListener {
            if (thisPlayer.hand.handFaceUp) for (i in 0 until thisPlayer.hand.gameHand.size()) {
                setCardFaceUp(i, false)
                mCardHandAdapter.setCards(thisPlayer.hand.gameHand)
            }
            else for (i in 0 until thisPlayer.hand.gameHand.size()) {
                setCardFaceUp(i, true)
                mCardHandAdapter.setCards(thisPlayer.hand.gameHand)
            }
            mCardHandAdapter.notifyDataSetChanged()
        })

        hideButton.setOnClickListener(View.OnClickListener {
            if (thisPlayer.hand.handFaceUp) for (i in 0 until thisPlayer.hand.gameHand.size()) {
                setCardFaceUp(i, false)
                mCardHandAdapter.setCards(thisPlayer.hand.gameHand)
                thisPlayer.hand.handFaceUp = false
            }
            else for (i in 0 until thisPlayer.hand.gameHand.size()) {
                setCardFaceUp(i, true)
                mCardHandAdapter.setCards(thisPlayer.hand.gameHand)
                thisPlayer.hand.handFaceUp = true
            }
            mCardHandAdapter.notifyDataSetChanged()
        })

        return rootView
    }

    fun setCardFaceUp(position: Int, isFaceUp: Boolean) {
        thisPlayer.hand.getCard(position).cardFaceUp = isFaceUp
        thisPlayer.hand.isHandFaceUp()
        val myImageViewText1 = rootView!!.findViewById<View>(R.id.myImageViewText1) as TextView
        if (thisPlayer.hand.handFaceUp) myImageViewText1.text = "Hide Cards"
        else myImageViewText1.text = "Show Cards"
    }

    fun updateGameForAll(gameObject: Game?, message: Int) {
        if (ClientConnectionThread.serverStarted) {
            onFold(message)
            ClientHandler.sendToServer(gameObject)
        } else {
            if (Constants.isPlayerActive(
                    MainFragment.Companion.userName.getText().toString(),
                    gameObject
                )
            ) {
                onFold(message)
                ServerHandler.sendToAll(gameObject)
            }
        }
    }

    private fun onFold(actionMessage: Int) {
        if (Constants.MOVE_FOLD === actionMessage) {
            thisPlayer.isActive = false
            for (i in 0 until gameObject.players.size()) {
                if (thisPlayer.username.equals(gameObject.players.get(i).username)) {
                    gameObject.players.set(i, thisPlayer)
                    break
                }
            }
        }
    }

    private fun confirmMove(message: Int): AlertDialog.Builder {
        val alertDialogBuilder = AlertDialog.Builder(
            context
        )
        var alertMessage = "Are you sure ?"
        when (message) {
            Constants.MOVE_FOLD -> alertMessage = "Do you want to FOLD ?"
            Constants.NEW_GAME -> alertMessage = "Do you want to start a New Game ?"
        }
        alertDialogBuilder.setTitle("Confirm")
        alertDialogBuilder
            .setMessage(alertMessage)
            .setCancelable(false)
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    actionTaken(message)
                    dialog.dismiss()
                }
            })
            .setNegativeButton("No", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.dismiss()
                }
            })

        return alertDialogBuilder
    }

    private fun actionTaken(keyword: Int) {
        when (keyword) {
            Constants.MOVE_FOLD -> {
                currentUserImage!!.setImageResource(R.drawable.inactive_icon)
                gameObject.setActionKey(Constants.MOVE_FOLD)
                updateGameForAll(gameObject, Constants.MOVE_FOLD)
            }

            Constants.NEW_GAME -> getActivity().getSupportFragmentManager().popBackStack()
        }
    }

    companion object {
        var rootView: View? = null
        var gameObject: Game? = null
        var context: Context? = null
        var mCardHandAdapter: CardHandAdapter? = null
        var mTableViewAdapter: TableViewAdapter? = null
        var thisPlayer: Player? = null
        var socket: Socket? = null
        var currentUserImage: ImageView? = null
        var currentUserText: TextView? = null
        var tableChanged: Boolean = false
        var tempHandCards: ArrayList<Cards> = ArrayList<Cards>()

        fun updatePlayers() {
            val playerList: List<Player> = gameObject.players
            var playerImage: ImageView
            var playerText: TextView
            val currentUser: String = MainFragment.Companion.userName.getText().toString()
            for (i in 1..playerList.size) {
                if (!playerList[i - 1].username.equals(currentUser)) {
                    playerImage = rootView!!.findViewById<View>(
                        context!!.resources.getIdentifier(
                            "player" + i + "Image", "id",
                            context!!.packageName
                        )
                    ) as ImageView
                    playerText = rootView!!.findViewById<View>(
                        context!!.resources.getIdentifier(
                            "player" + i + "Text", "id",
                            context!!.packageName
                        )
                    ) as TextView
                    playerText.visibility = View.VISIBLE
                    playerImage.visibility = View.VISIBLE
                    playerText.setText(playerList[i - 1].username)
                } else {
                    currentUserText.setText(playerList[i - 1].username)
                }
            }
        }

        fun updateTable() {
            val tableList: ArrayList<Cards> = gameObject.mTable.TableCards
            mTableViewAdapter.setCards(tableList)
            mTableViewAdapter.notifyDataSetChanged()
        }

        fun updatePlayerStatus() {
            val playerList: List<Player> = gameObject.players
            var playerImage: ImageView
            for (i in 1..playerList.size) {
                playerImage = rootView!!.findViewById<View>(
                    context!!.resources.getIdentifier(
                        "player" + i + "Image",
                        "id",
                        context!!.packageName
                    )
                ) as ImageView
                if (playerList[i - 1].username.equals(
                        MainFragment.Companion.userName.getText().toString()
                    )
                ) {
                    playerImage =
                        rootView!!.findViewById<View>(R.id.currentPlayerImage) as ImageView
                    if (playerList[i - 1].isActive) {
                        playerImage.setImageResource(R.drawable.active_icon)
                    } else {
                        playerImage.setImageResource(R.drawable.inactive_icon)
                    }
                } else if (playerList[i - 1].isActive) {
                    playerImage.setImageResource(R.drawable.active_icon)
                } else {
                    playerImage.setImageResource(R.drawable.inactive_icon)
                }
            }
        }

        fun updateHand() {
            val playerList: List<Player> = gameObject.players

            val currentUser: String = MainFragment.Companion.userName.getText().toString()
            for (player in playerList) {
                if (player.username.equals(currentUser)) {
                    thisPlayer = player
                }
            }
            if (mCardHandAdapter != null) {
                mCardHandAdapter.setCards(thisPlayer.hand.gameHand)
                mCardHandAdapter.notifyDataSetChanged()
            }
            if (gameObject.senderUsername.equals(java.lang.String.valueOf(Constants.NEW_GAME)) && mCardHandAdapter != null) {
                rootView!!.setBackgroundResource(gameObject.gameBackground)
                mCardHandAdapter.setCardBack(gameObject.cardBackImage)
                gameObject.senderUsername = ""
            }
        }
    }
}


