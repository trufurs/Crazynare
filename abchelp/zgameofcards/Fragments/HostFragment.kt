package com.example.crazynare.gameofcards.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.crazynare.Connections.ServerConnectionThread
import com.example.crazynare.R
import com.example.crazynare.gameofcards.Utils.ServerHandler

class HostFragment : Fragment() {
    var numberOfPlayers: MaterialEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverHandler = ServerHandler()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.host_game_layout, container, false)
        val startGame = rootView.findViewById<View>(R.id.startGame) as Button
        gameName = rootView.findViewById<View>(R.id.gameName) as MaterialEditText
        numberOfPlayers = rootView.findViewById<View>(R.id.numberOfPlayers) as MaterialEditText
        val fragmentManager: Any =
            activity?.supportFragmentManager ?: startGame.setOnClickListener {
                if (gameName.getText() != null && numberOfPlayers.getText() != null && numberOfPlayers.getText()
                        .toString().trim().length() > 0 && gameName.getText().toString().trim()
                        .length() > 0
                ) {
                    numberPlayers = Integer.valueOf(numberOfPlayers.getText().toString())
                    if (numberPlayers > 5 || numberPlayers < 1) {
                        Toast.makeText(
                            activity,
                            "Maximum 5 players allowed ",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        val startServerThread: ServerConnectionThread = ServerConnectionThread()
                        startServerThread.start()
                        fragmentManager?.beginTransaction()
                            .replace(R.id.container, PlayerListFragment()).addToBackStack(
                                PlayerListFragment::class.java.name
                            )
                            .commit()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Missing game name or number of players",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        return rootView
    }


    companion object {
        var serverHandler: ServerHandler? = null
        var gameName: MaterialEditText? = null
        var numberPlayers: Int = 0
    }
}

