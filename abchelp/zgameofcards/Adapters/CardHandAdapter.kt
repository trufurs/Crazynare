package com.example.crazynare.gameofcards.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

/**
 * Created by kunalshrivastava on 4/21/15.
 */
class CardHandAdapter(var context: Context, cards: ArrayList<Cards>, cardBack: Int) :
    RecyclerView.Adapter<CardHandAdapter.ViewHolder?>() {
    fun getCards(): ArrayList<Cards> {
        return cards
    }

    fun setCards(cards: ArrayList<Cards>) {
        this.cards = cards
    }

    var cards: ArrayList<Cards>

    var cardBack: Int

    init {
        this.cards = cards
        this.cardBack = cardBack
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_back_layout, parent, false)
        )
    }

    fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currCard: Cards = cards[position]
        holder.cardFront.setImageResource(
            context.resources.getIdentifier(
                currCard.imageID, "drawable",
                context.packageName
            )
        )
        holder.cardBack.setImageResource(this.cardBack)
        if (currCard.cardFaceUp === true) {
            holder.cardBack.visibility = View.INVISIBLE
        } else {
            holder.cardBack.visibility = View.VISIBLE
        }
    }

    val itemCount: Int
        get() = cards.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var cardFront: ImageView
        var cardBack: ImageView

        init {
            v.setOnClickListener { }
            cardFront = v.findViewById<View>(R.id.cardDesign) as ImageView
            cardBack = v.findViewById<View>(R.id.cardDesignBack) as ImageView

            cardBack.setOnClickListener { v ->
                if (mItemClickListener != null) {
                    mItemClickListener!!.OnItemClick(v, getPosition())
                }
            }

            cardFront.setOnLongClickListener(object : OnLongClickListener {
                override fun onLongClick(view: View): Boolean {
                    if (mItemLongClickListener != null) {
                        mItemLongClickListener!!.OnItemLongClick(view, getPosition())
                    }
                    return true
                }
            })
        }
    }

    interface OnItemClickListener {
        fun OnItemClick(v: View?, position: Int)
    }

    interface OnItemLongClickListener {
        fun OnItemLongClick(v: View?, position: Int): Boolean
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener?) {
        mItemLongClickListener = onItemLongClickListener
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mItemClickListener = onItemClickListener
    }

    companion object {
        var mItemClickListener: OnItemClickListener? = null
        var mItemLongClickListener: OnItemLongClickListener? = null
    }
}
