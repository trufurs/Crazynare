package com.example.crazynare.gameofcards.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View

class CardDealDialog : DialogFragment() {
    var dealToPlayers: RadioGroup? = null

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builderSingle = AlertDialog.Builder(
            getActivity()
        )
        builderSingle.setIcon(R.drawable.deck_icon)
        builderSingle.setTitle("Deal Deck")
        val rootView: View =
            getActivity().getLayoutInflater().inflate(R.layout.card_deal_layout, null)
        toPlayer = rootView.findViewById<View>(R.id.toPlayersRadio) as RadioButton
        ontable = rootView.findViewById<View>(R.id.onTableRadio) as RadioButton
        numberCards = rootView.findViewById<View>(R.id.dealCards) as MaterialEditText
        dealToPlayers = rootView.findViewById<View>(R.id.dealToPlayers) as RadioGroup
        var playerCount = 1
        for (i in 0 until GameFragment.Companion.gameObject.players.size()) {
            if (!MainFragment.Companion.userName.getText().toString()
                    .equals(GameFragment.Companion.gameObject.players.get(i).username)
            ) {
                val radio: RadioButton = rootView.findViewById(
                                    getActivity().getResources().getIdentifier(
                                        "player$playerCount", "id", getActivity().getPackageName()
                                    )
                                )
                radio.setText(GameFragment.Companion.gameObject.players.get(i).username)
                radio.setVisibility(View.VISIBLE)
                playerCount++
            }
        }
        toPlayer.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
                if (b) {
                    dealToPlayers.setVisibility(View.VISIBLE)
                } else {
                    dealToPlayers.setVisibility(View.GONE)
                }
            }
        })

        builderSingle.setView(rootView)

        builderSingle.setPositiveButton("DEAL",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    val checked: Boolean = (toPlayer as RadioButton?).isChecked()
                    if (numberCards.getText() != null && numberCards.getText().toString().trim()
                            .length() > 0
                    ) {
                        val numCards: Int = numberCards.getText().toString().toInt()
                        if (checked) {
                            val radioButton: RadioButton =
                                rootView.findViewById<View>(dealToPlayers.getCheckedRadioButtonId()) as RadioButton
                            val selectedText: String = radioButton.getText().toString()
                            if (selectedText == "All") {
                                if (GameFragment.Companion.gameObject.deckCards.size() >= (numCards * GameFragment.Companion.gameObject.getNumberOfPlayer())) {
                                    GameFragment.Companion.gameObject.getHand(numCards)
                                    GameFragment.Companion.mCardHandAdapter.notifyDataSetChanged()
                                    ServerHandler.sendToAll(GameFragment.Companion.gameObject)
                                } else {
                                    Toast.makeText(
                                        getActivity(),
                                        "Not enough cards to DEAL!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                if (GameFragment.Companion.gameObject.deckCards.size() >= numCards) {
                                    if (selectedText == "self") {
                                        GameFragment.Companion.gameObject.setHandPlayer(
                                            numCards,
                                            MainFragment.Companion.userName.getText().toString()
                                        )
                                        GameFragment.Companion.mCardHandAdapter.notifyDataSetChanged()
                                    } else {
                                        GameFragment.Companion.gameObject.setHandPlayer(
                                            numCards,
                                            selectedText
                                        )
                                    }
                                    ServerHandler.sendToAll(GameFragment.Companion.gameObject)
                                } else {
                                    Toast.makeText(
                                        getActivity(),
                                        "Not enough cards to DEAL!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            if (GameFragment.Companion.gameObject.deckCards.size() >= numCards) {
                                GameFragment.Companion.gameObject.mTable.putCardsOnTable(
                                    GameFragment.Companion.gameObject.getCardsFromDeck(numCards)
                                )
                                GameFragment.Companion.mTableViewAdapter.notifyDataSetChanged()
                                ServerHandler.sendToAll(GameFragment.Companion.gameObject)
                            } else {
                                Toast.makeText(
                                    getActivity(),
                                    "Not enough cards to DEAL!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    dialog.dismiss()
                }
            }

        )
        builderSingle.setNegativeButton("CANCEL",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.dismiss()
                }
            }

        )

        return builderSingle.create()
    }

    fun onRadioButtonClicked(view: View) {
        // Is the button now checked?
        val checked: Boolean = (view as RadioButton).isChecked()

        when (view.id) {
            R.id.toPlayersRadio -> {
                if (checked) // Pirates are the best
                    break
                if (checked) // Ninjas rule
                    break
            }

            R.id.onTableRadio -> if (checked)
                break
        }
    }

    companion object {
        var toPlayer: RadioButton? = null
        var ontable: RadioButton? = null
        var numberCards: MaterialEditText? = null
    }
}
