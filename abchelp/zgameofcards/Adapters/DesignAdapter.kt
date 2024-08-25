package com.example.crazynare.gameofcards.Adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.crazynare.R

class DesignAdapter(private val mDataSet: IntArray, private val isCardType: Boolean) :
    RecyclerView.Adapter<DesignAdapter.ViewHolder?>() {
    var mOnItemClickListner: OnItemClickListener? = null

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imageView: ImageView

        init {
            v.setOnClickListener { }
            imageView = v.findViewById<View>(R.id.cardDesign) as ImageView
            imageView.setOnClickListener {
                if (mOnItemClickListner != null) {
                    mOnItemClickListner!!.onItemClick(itemView, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = if (isCardType) {
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.table_design_layout, viewGroup, false)
        } else {
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.table_design_layout, viewGroup, false)
        }
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        mDataSet.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.imageView.setImageResource(mDataSet[position])
        viewHolder.imageView.tag = mDataSet[position]
    }

    val itemCount: Int
        get() = mDataSet.size

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    fun setOnItemClickListener(mOnItemClickListener: Any) {
        this.mOnItemClickListner = mOnItemClickListener
    }
}
