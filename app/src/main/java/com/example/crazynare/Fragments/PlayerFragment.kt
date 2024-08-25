package com.example.crazynare.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.crazynare.R
import com.example.crazynare.Model.Players

/**
 * A fragment representing a list of Items.
 */
class PlayerFragment : Fragment() {

    private var columnCount = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        val players = mutableListOf<Players>()

        players.add(Players("Player 1", "Host", "1", 0, false))
        players.add(Players("Player 2", "Guest", "2", 0, true))
        players.add(Players("Player 1", "Host", "1", 0, false))
        players.add(Players("Player 2", "Guest", "2", 0, true))
        players.add(Players("Player 1", "Host", "1", 0, false))
        players.add(Players("Player 2", "Guest", "2", 0, true))
        players.add(Players("Player 2", "Guest", "2", 0, true))
        players.add(Players("Player 1", "Host", "1", 0, false))
        players.add(Players("Player 2", "Guest", "2", 0, true))
        players.add(Players("Player 2", "Guest", "2", 0, true))
        players.add(Players("Player 1", "Host", "1", 0, false))
        players.add(Players("Player 2", "Guest", "2", 0, true))

        // Set the adapter
        val adapter = view.findViewById<RecyclerView>(R.id.list)
        adapter.adapter = MyItemRecyclerViewAdapter(players)
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}