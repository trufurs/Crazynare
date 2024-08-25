package com.example.crazynare.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.crazynare.R

class WelcomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val whew = inflater.inflate(R.layout.fragment_welcome, container, false)
        val CreateButton = whew.findViewById<Button>(R.id.create_main)
        val JoinButton = whew.findViewById<Button>(R.id.join_main)
        JoinButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main, PlayerFragment())
                ?.addToBackStack(PlayerFragment::class.java.name)
                ?.commit()
        }
        return whew
    }


}