package com.example.crazynare.gameofcards.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class TableViewAdapter(var context: Context, cards: ArrayList<Cards>, cardBack: Int) :
    RecyclerView.Adapter<TableViewAdapter.ViewHolder?>() {
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
                .inflate(R.layout.table_design_layout, parent, false)
        )
    }

    fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currCard: Cards = cards[position]
        if (currCard.cardFaceUp) {
            holder.imageView.setImageResource(
                context.resources.getIdentifier(
                    currCard.imageID, "drawable",
                    context.packageName
                )
            )
        } else {
            holder.imageView.setImageResource(this.cardBack)
        }
    }

    val itemCount: Int
        get() = cards.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var imageView: ImageView

        init {
            v.setOnClickListener { }
            imageView = v.findViewById<View>(R.id.cardDesign) as ImageView
            imageView.setOnClickListener { v ->
                if (mItemClickListener != null) {
                    mItemClickListener!!.OnItemClick(v, getPosition())
                }
            }
            imageView.setOnLongClickListener(object : OnLongClickListener {
                override fun onLongClick(view: View): Boolean {
                    if (mItemClickListener != null) {
                        mItemClickListener!!.OnItemLongClick(view, getPosition())
                        return true
                    }
                    return false
                }
            })
        }
    }

    interface OnItemClickListener {
        fun OnItemClick(v: View?, position: Int)

        fun OnItemLongClick(v: View?, position: Int)
    }

    fun setOnItemCLickListener(onItemClickListener: OnItemClickListener?) {
        mItemClickListener = onItemClickListener
    }

    companion object {
        var mItemClickListener: OnItemClickListener? = null
    }
}
