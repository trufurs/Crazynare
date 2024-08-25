package com.example.crazynare.gameofcards.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crazynare.R
import com.example.crazynare.gameofcards.Adapters.PlayerAdapter
import com.example.crazynare.gameofcards.Connections.ServerConnectionThread
import com.example.crazynare.gameofcards.Model.Cards
import com.example.crazynare.gameofcards.Model.Game
import com.example.crazynare.gameofcards.Utils.Constants
import com.example.crazynare.gameofcards.Utils.ServerHandler

class PlayerListFragment : Fragment() {
    enum class LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected var mCurrentLayoutManagerType: LayoutManagerType? = null

    protected var mPlayerList: RecyclerView? = null
    protected var mLayoutManager: RecyclerView.LayoutManager? = null

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.player_list_layout, container, false)
        rootView.tag = TAG
        val gameSettings = rootView.findViewById<View>(R.id.gameSettings) as Button
        val playGame = rootView.findViewById<View>(R.id.playGame) as Button

        val fragmentManager: FragmentManager = activity.getSupportFragmentManager()
        gameSettings.setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.container, SettingsFragment())
                .addToBackStack(SettingsFragment::class.java.name)
                .commit()
        }
        playGame.setOnClickListener {
            if (ServerConnectionThread.allPlayersJoined) {
                try {
                    initializeGame()
                    val gameFragment = GameFragment()
                    gameFragment.setParameters(gameObject, null)
                    fragmentManager.beginTransaction()
                        .replace(R.id.container, gameFragment)
                        .addToBackStack(GameFragment::class.java.name)
                        .commit()
                } catch (exception: IllegalArgumentException) {
                    Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    activity,
                    "Waiting for all players to Join the game",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        mPlayerList = rootView.findViewById<View>(R.id.gameList) as RecyclerView


        mLayoutManager = LinearLayoutManager(activity)

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER

        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = savedInstanceState
                .getSerializable(KEY_LAYOUT_MANAGER) as LayoutManagerType?
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType)
        mAdapter = PlayerAdapter(deviceList)
        mPlayerList!!.setAdapter(mAdapter)
        return rootView
    }

    private fun setRecyclerViewLayoutManager(layoutManagerType: LayoutManagerType?) {
        var scrollPosition = 0

        if (mPlayerList.getLayoutManager() != null) {
            scrollPosition = (mPlayerList.getLayoutManager() as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition()
        }

        when (layoutManagerType) {
            LayoutManagerType.GRID_LAYOUT_MANAGER -> {
                mLayoutManager = GridLayoutManager(activity, SPAN_COUNT)
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER
            }

            LayoutManagerType.LINEAR_LAYOUT_MANAGER -> {
                mLayoutManager = LinearLayoutManager(activity)
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER
            }

            else -> {
                mLayoutManager = LinearLayoutManager(activity)
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER
            }
        }
        mPlayerList.setLayoutManager(mLayoutManager)
        mPlayerList.scrollToPosition(scrollPosition)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType)
        super.onSaveInstanceState(savedInstanceState)
    }

    private fun initializeGame() {
        val restrictedCards: ArrayList<Cards?> = ArrayList<Cards?>()
        deviceList.add(MainFragment.Companion.userName.getText().toString())
        var dealExact = false
        var numberOfDecks = 1
        var numberOfCardsDraw = 5

        if (SettingsFragment.Companion.dealExact != null && SettingsFragment.Companion.dealExact.isChecked()) {
            dealExact = true
            numberOfCardsDraw =
                SettingsFragment.Companion.dealExactCards.getSelectedItem().toString().toInt()
        }
        if (SettingsFragment.Companion.deckNumber != null && SettingsFragment.Companion.deckNumber.getText()
                .length() > 0
        ) {
            numberOfDecks =
                Integer.valueOf(SettingsFragment.Companion.deckNumber.getText().toString())
            if (!(numberOfDecks >= 1 && numberOfDecks <= 6)) {
                numberOfDecks = 1
                Toast.makeText(
                    activity,
                    "Only a total of 6 decks is allowed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (DeckCustomizeDialog.Companion.exclusionCardList != null) {
            val card: Cards = Cards()
            for (i in DeckCustomizeDialog.Companion.exclusionCardList!!.indices) {
                restrictedCards.addAll(
                    card.getCopyForAll(
                        DeckCustomizeDialog.Companion.exclusionCardList!!.get(
                            i
                        ).getCardTitle()
                    )
                )
            }
        }
        gameObject = Game(
            deviceList,
            numberOfDecks,
            numberOfCardsDraw,
            dealExact,
            restrictedCards,
            HostFragment.gameName.getText().toString()
        )

        if (SettingsFragment.Companion.selectedTableImage == -1) {
            gameObject!!.gameBackground = R.drawable.table_back1
        } else {
            gameObject!!.gameBackground = SettingsFragment.Companion.selectedTableImage
        }
        if (SettingsFragment.Companion.selectedCardImage == -1) {
            gameObject!!.cardBackImage = R.drawable.cardback1
        } else {
            gameObject!!.cardBackImage = SettingsFragment.Companion.selectedCardImage
        }
        gameObject!!.senderUsername = java.lang.String.valueOf(Constants.NEW_GAME)
        ServerHandler.sendToAll(gameObject)
    }

    companion object {
        private const val TAG = "RecyclerViewFragment"
        private const val KEY_LAYOUT_MANAGER = "layoutManager"
        private const val SPAN_COUNT = 2
        var gameObject: Game? = null
        var deviceList: ArrayList<String> = ArrayList<String>()

        var mAdapter: PlayerAdapter? = null
    }
}
