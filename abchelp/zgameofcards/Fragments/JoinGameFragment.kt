package com.example.crazynare.gameofcards.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class JoinGameFragment : Fragment() {
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.join_select_layout, container, false)
        val joinGame = rootView.findViewById<View>(R.id.joinGame) as Button
        gameName = rootView.findViewById<View>(R.id.gameName) as TextView
        userName = rootView.findViewById<View>(R.id.userName) as TextView
        userName.setText(MainFragment.Companion.userName.getText())
        val fragmentManager: FragmentManager = getActivity().getSupportFragmentManager()
        joinGame.setOnClickListener {
            if (gameobject != null) {
                val gameFragment = GameFragment()
                gameFragment.setParameters(gameobject, ClientConnectionThread.socket)
                fragmentManager.beginTransaction()
                    .replace(R.id.container, gameFragment)
                    .addToBackStack(JoinGameFragment::class.java.name)
                    .commit()
            } else {
                Toast.makeText(
                    getActivity(),
                    "Game setup not complete. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        return rootView
    }


    companion object {
        var gameName: TextView? = null
        var userName: TextView? = null
        var gameobject: Game? = null
    }
}
