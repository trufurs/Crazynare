package com.example.crazynare.gameofcards.Fragments

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.crazynare.R

class InformationFragment : Fragment() {

    fun onCreateView(
        inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View =
            activity?.layoutInflater.inflate(R.layout.information_dialog, null)
        val infoContent = rootView.findViewById<View>(R.id.infoContent) as TextView
        infoContent.linksClickable = true
        infoContent.autoLinkMask = Linkify.EMAIL_ADDRESSES
        return rootView
    }
}
