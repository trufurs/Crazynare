package com.example.crazynare.gameofcards.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.ListView

class DeckCustomizeDialog : DialogFragment() {
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (cardList == null) {
            cardList = ArrayList<Any?>()
            exclusionCardList = ArrayList<Any?>()
            initCardList()
        }
    }

    fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builderSingle = AlertDialog.Builder(
            getActivity()
        )
        builderSingle.setIcon(R.drawable.card_game)
        builderSingle.setTitle("Customize Deck")
        val rootView: View =
            getActivity().getLayoutInflater().inflate(R.layout.dialog_list_fragment, null)
        builderSingle.setView(rootView)
        val cardListView = rootView.findViewById<View>(R.id.cardListView) as ListView
        val customizeDeck: DeckCustomizeAdapter = DeckCustomizeAdapter(cardList, getActivity())
        cardListView.adapter = customizeDeck
        builderSingle.setPositiveButton("OK",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.dismiss()
                }
            })
        builderSingle.setNegativeButton("CANCEL",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    cardList = ArrayList<Any?>()
                    exclusionCardList = ArrayList<Any?>()
                    initCardList()
                    dialog.dismiss()
                }
            })

        cardListView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val cardCustom: CardCustomize? = cardList!![i]
                val checkBox: CheckBox = view.findViewById<View>(R.id.cardSelect) as CheckBox
                cardCustom.setIsSelected(!checkBox.isChecked())
                checkBox.setChecked(!checkBox.isChecked())
                if (!cardCustom.isSelected() && !exclusionCardList!!.contains(cardCustom)) {
                    exclusionCardList!!.add(cardCustom)
                } else {
                    exclusionCardList!!.remove(cardCustom)
                }
                customizeDeck.notifyDataSetChanged()
            }
        })
        return builderSingle.create()
    }

    private fun initCardList() {
        var cards: CardCustomize = CardCustomize()
        cards.setCardImage(R.drawable.joker_zero)
        cards.setIsSelected(true)
        cards.setCardTitle("joker")
        cardList!!.add(cards)
        val cardNames: Array<String> =
            getActivity().getResources().getStringArray(R.array.card_array)
        for (j in cardNames.indices) {
            cards = CardCustomize()
            val resID: Int = getResources()
                .getIdentifier("hearts_" + cardNames[j], "drawable", getActivity().getPackageName())
            cards.setCardImage(resID)
            cards.setIsSelected(true)
            cards.setCardTitle(cardNames[j])
            cardList!!.add(cards)
        }
    }

    companion object {
        var cardList: MutableList<CardCustomize?>? = null
        var exclusionCardList: MutableList<CardCustomize?>? = null
    }
}
