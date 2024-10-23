package com.example.crazynare.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.crazynare.Adapter.MyItemRecyclerViewAdapter
import com.example.crazynare.Model.Players
import com.example.crazynare.R
import com.example.crazynare.Viewmodel.ClientViewModel
import com.example.crazynare.Viewmodel.HostViewModel
import com.example.crazynare.Viewmodel.PlayerViewModel

class PlayerFragment : Fragment() {

    private var columnCount = 2
    private val playerViewModel: PlayerViewModel by activityViewModels()
    private val hostViewModel: HostViewModel by activityViewModels()
    private val clientViewModel : ClientViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        // Handle back press to show exit confirmation only when returning to WelcomeFragment
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Show exit confirmation when navigating back to WelcomeFragment
            showExitConfirmationDialog(requireContext()) {
                // Stop the server and navigate back to WelcomeFragment after confirming
                closeServerAndNavigateBack()
            }
        }
    }

    private fun closeServerAndNavigateBack() {
        if (hostViewModel.isHost()) {
            hostViewModel.disconnecthost()
        } else {
            clientViewModel.disconnectclient()
        }

        // Pop back to the WelcomeFragment
        requireActivity().supportFragmentManager.popBackStack("WelcomeFragment", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_list, container, false)

        val players = mutableListOf<Players>()

        // Set the RecyclerView adapter
        val adapter = view.findViewById<RecyclerView>(R.id.list_players)
        val playerAdapter = MyItemRecyclerViewAdapter(players, playerViewModel) {
            showChangeNameStatusDialog(requireContext()) { newName, newStatus ->
                it.playerName = newName
                it.playerStatus = newStatus
                if (hostViewModel.isHost()) {
                    hostViewModel.sendPlayerData(it, playerViewModel)
                } else {
                    clientViewModel.sendPlayerData(it, playerViewModel)
                }
            }
        }
        adapter.adapter = playerAdapter

        // Observe player data and update the list
        playerViewModel.players.observe(viewLifecycleOwner) { updatedPlayers ->
            players.clear()
            players.addAll(updatedPlayers)
            playerAdapter.notifyDataSetChanged()
        }

        // Handle "Chat" button click and navigate to ChatFragment without exit confirmation
        val chatButton = view.findViewById<Button>(R.id.button_chat)
        chatButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, ChatFragment())
                .addToBackStack(null)  // Don't add PlayerFragment to backstack to avoid destroying it
                .commit()
        }

        val closeButton = view.findViewById<Button>(R.id.button_close)
        closeButton.setOnClickListener {
            closeServerAndNavigateBack()
        }

        val buttonForm = view.findViewById<Button>(R.id.button_form)
        buttonForm.setOnClickListener {
            showStartGameDialog(requireContext()) { question1, question2 ->
                if (hostViewModel.isHost()) {
                    hostViewModel.startGame(question1, question2, playerViewModel)
                } else {
                    clientViewModel.startGame(question1, question2, playerViewModel)
                }
            }
        }
        return view
    }

    // Show exit confirmation dialog
    private fun showExitConfirmationDialog(context: Context, onConfirm: () -> Unit) {
        val dialog = Dialog(context, R.style.CustomDialogStyle)
        dialog.setContentView(R.layout.dialog_exit)

        dialog.findViewById<Button>(R.id.positiveButton).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.negativeButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }



    private fun showStartGameDialog(context: Context, onStartGame: (String, String) -> Unit) {
        val dialog = Dialog(context, R.style.CustomDialogStyle)
        dialog.setContentView(R.layout.dialog_start_game)

        val question1EditText = dialog.findViewById<EditText>(R.id.editTextQuestion1)
        val question2EditText = dialog.findViewById<EditText>(R.id.editTextQuestion2)
        val positiveButton = dialog.findViewById<Button>(R.id.positiveButton)
        val negativeButton = dialog.findViewById<Button>(R.id.negativeButton)

        positiveButton.setOnClickListener {
            val question1 = question1EditText.text.toString().trim()
            val question2 = question2EditText.text.toString().trim()

            if (question1.isNotEmpty() && question2.isNotEmpty()) {
                onStartGame(question1, question2)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please enter both questions.", Toast.LENGTH_SHORT).show()
            }
        }

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showChangeNameStatusDialog(context: Context, onSubmit: (String, Boolean) -> Unit) {
        val dialog = Dialog(context, R.style.CustomDialogStyle)
        dialog.setContentView(R.layout.dialog_change_name_status)

        val nameEditText = dialog.findViewById<EditText>(R.id.editTextName)
        val statusSpinner = dialog.findViewById<Spinner>(R.id.spinnerStatus)

        // Set up the spinner with descriptive statuses
        val statuses = arrayOf("Active", "Inactive")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = adapter

        dialog.findViewById<Button>(R.id.positiveButton).setOnClickListener {
            val newName = nameEditText.text.toString().trim()

            if (newName.isNotEmpty()) {
                val newStatus = statusSpinner.selectedItem.toString() == "Active"
                onSubmit(newName, newStatus)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please enter a valid name.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.findViewById<Button>(R.id.negativeButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
