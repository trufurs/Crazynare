package com.example.crazynare.gameofcards.Fragments


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crazynare.R
import com.example.crazynare.gameofcards.Adapters.DesignAdapter

class SettingsFragment : Fragment() {
    protected var mCardRecyclerView: RecyclerView? = null
    protected var mTableRecyclerView: RecyclerView? = null
    protected var mCardAdapter: DesignAdapter? = null
    protected var mTableAdapter: DesignAdapter? = null
    protected var mCardLayoutManager: RecyclerView.LayoutManager? = null
    protected var mTableLayoutManager: RecyclerView.LayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataset()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.game_setting_layout, container, false)
        val apply = rootView.findViewById<View>(R.id.applySettings) as Button
        val manageDeck = rootView.findViewById<View>(R.id.manageDeck) as Button
        val cancel = rootView.findViewById<View>(R.id.cancelChanges) as Button
        dealEven = rootView.findViewById<View>(R.id.radioEven) as RadioButton
        dealExact = rootView.findViewById<View>(R.id.radioExact) as RadioButton
        dealExactCards = rootView.findViewById<View>(R.id.cards_spinner) as Spinner
        deckNumber = rootView.findViewById<View>(R.id.deckNumber) as MaterialEditText
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            activity,
            R.array.deal_exactly, R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        dealExactCards.setAdapter(adapter)
        dealEven.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
                if (b) {
                    dealExactCards.setVisibility(View.GONE)
                } else {
                    dealExactCards.setVisibility(View.VISIBLE)
                }
            }
        })
        apply.setOnClickListener { activity.onBackPressed() }
        manageDeck.setOnClickListener {
            val cardException = DeckCustomizeDialog()
            cardException.setTargetFragment(this@SettingsFragment, 2)
            cardException.show(fragmentManager, "Remove Cards")
        }
        cancel.setOnClickListener {
            selectedCardImage = -1
            selectedTableImage = -1
            dealExact.setChecked(false)
            dealExactCards.setVisibility(View.GONE)
            activity.onBackPressed()
        }
        val alpha: AlphaAnimation = AlphaAnimation(0.5f, 0.5f)
        alpha.setDuration(0)
        alpha.fillAfter = true
        mCardRecyclerView = rootView.findViewById<View>(R.id.cardDesignList) as RecyclerView
        mCardLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mCardRecyclerView.setLayoutManager(mCardLayoutManager)
        mCardAdapter = DesignAdapter(mCardDataSet, true)
        mCardRecyclerView!!.setAdapter(mCardAdapter)
        mTableLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mTableRecyclerView = rootView.findViewById<View>(R.id.tableDesignList) as RecyclerView
        mTableRecyclerView!!.setLayoutManager(mTableLayoutManager)
        mTableAdapter = DesignAdapter(mTableDataSet, false)
        mTableRecyclerView!!.setAdapter(mTableAdapter)
        mCardAdapter!!.setOnItemClickListener(object : DesignAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val image = view?.findViewById<View>(R.id.cardDesign) as ImageView
                if (image.tag.toString().toInt() == mCardDataSet[position]) {
                    if (selectedCardImageView != null) {
                        selectedCardImageView!!.setBackgroundColor(Color.WHITE)
                        selectedCardImageView!!.tag = mCardDataSet[previousCardPosition]
                    }
                    previousCardPosition = position
                    selectedCardImageView = image
                    image.tag = Color.WHITE
                    image.setBackgroundColor(Color.RED)
                    selectedCardImage = mCardDataSet[position]
                } else {
                    image.setBackgroundColor(Color.WHITE)
                    image.tag = mCardDataSet[position]
                    selectedCardImage = R.drawable.cardback1
                    image.setImageResource(mCardDataSet[position])
                }
            }

        })

        mTableAdapter!!.setOnItemClickListener(object : OnItemClickListener() {
            fun onItemClick(view: View, position: Int) {
                val image = view.findViewById<View>(R.id.cardDesign) as ImageView
                if (image.tag.toString().toInt() == mTableDataSet[position]) {
                    if (selectedTableImageView != null) {
                        selectedTableImageView!!.setBackgroundColor(Color.WHITE)
                        selectedTableImageView!!.tag = mTableDataSet[previousTablePosition]
                    }
                    previousTablePosition = position
                    selectedTableImageView = image
                    image.tag = Color.WHITE
                    image.setBackgroundColor(Color.RED)
                    selectedTableImage = mTableDataSet[position]
                } else {
                    image.setBackgroundColor(Color.WHITE)
                    image.tag = mTableDataSet[position]
                    selectedTableImage = R.drawable.table_back1
                    image.setImageResource(mTableDataSet[position])
                }
            }
        })
        return rootView
    }

    fun onSaveInstanceState(savedInstanceState: Bundle?) {
        super.onSaveInstanceState(savedInstanceState)
    }

    private fun initDataset() {
        mCardDataSet = intArrayOf(
            R.drawable.cardback1,
            R.drawable.cardback2,
            R.drawable.cardback3,
            R.drawable.cardback4
        )
        mTableDataSet = intArrayOf(R.drawable.table_back1, R.drawable.table_back2)
        if (selectedTableImage == -1) {
            selectedTableImage = R.drawable.table_back1
        }
        if (selectedCardImage == -1) {
            selectedCardImage = R.drawable.cardback1
        }
    }

    companion object {
        var selectedCardImage: Int = -1
        var selectedTableImage: Int = -1
        var dealEven: RadioButton? = null
        var dealExact: RadioButton? = null
        private var mCardDataSet: IntArray
        private var mTableDataSet: IntArray
        var dealExactCards: Spinner? = null
        var deckNumber: MaterialEditText? = null
        private var previousTablePosition = -1
        private var selectedTableImageView: ImageView? = null
        private var previousCardPosition = -1
        private var selectedCardImageView: ImageView? = null
    }
}
