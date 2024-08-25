package com.example.crazynare.gameofcards.Adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import srk.syracuse.gameofcards.Model.CardCustomize
import java.lang.String
import kotlin.Any
import kotlin.Int
import kotlin.Long

class DeckCustomizeAdapter(cardList: List<CardCustomize>?, con: Context?) : BaseAdapter() {
    var cardList: List<CardCustomize>? = null
    var context: Context? = null

    init {
        if (cardList != null) {
            this.cardList = cardList
            context = con
        }
    }

    val count: Int
        get() = cardList!!.size

    override fun getItem(position: Int): Any {
        if (position >= 0 && position < cardList!!.size) {
            return cardList!![position]
        }
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    class PlaceHolder {
        var cardTitle: TextView? = null
        var cardImage: ImageView? = null
        var selected: CheckBox? = null
        var cardCustom: CardCustomize? = null
    }


    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val adaptView: View
        if (convertView == null) {
            val inflater: LayoutInflater =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            adaptView = inflater.inflate(R.layout.customize_deck_item, parent, false)
            holder = PlaceHolder()
            holder!!.cardTitle = adaptView.findViewById<View>(R.id.cardTitle) as TextView
            holder!!.selected = adaptView.findViewById<View>(R.id.cardSelect) as CheckBox
            holder!!.cardImage = adaptView.findViewById<View>(R.id.cardImage) as ImageView
            adaptView.tag = holder
            holder!!.cardCustom = cardList!![position]
        } else {
            adaptView = convertView
            holder = convertView.tag as PlaceHolder
            holder!!.cardCustom = cardList!![position]
        }
        holder!!.cardTitle.setText(
            String.valueOf(
                holder!!.cardCustom.getCardTitle()
            )
        )
        holder!!.cardImage!!.setImageResource(holder!!.cardCustom.getCardImage())
        holder!!.selected.setChecked(holder!!.cardCustom.isSelected())
        return adaptView
    }

    companion object {
        var holder: PlaceHolder? = null
    }
}
