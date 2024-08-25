package com.example.crazynare.gameofcards.Fragments

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.crazynare.R
import com.example.crazynare.gameofcards.Connections.ClientConnectionThread
import com.example.crazynare.gameofcards.Utils.ClientHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.main_screen_layout, container, false)
        val hostGame = rootView.findViewById<View>(R.id.hostGame) as Button
        val joinGame = rootView.findViewById<View>(R.id.joinGame) as Button
        val infoButton = rootView.findViewById<View>(R.id.infoButton) as Button
        infoButton.setOnClickListener {
            activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, InformationFragment())
                .addToBackStack(InformationFragment::class.java.name)
                .commit()
        }
        val wifi: WifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wmMethods: Array<Method> = wifi.javaClass.getDeclaredMethods()
        userName = rootView.findViewById<View>(R.id.userName) as MaterialEditText
        for (method in wmMethods) {
            if (method.name == "isWifiApEnabled") {
                try {
                    val isWifiAPEnabled = method.invoke(wifi) as Boolean
                    val fragmentManager: FragmentManager = activity.getSupportFragmentManager()
                    if (isWifiAPEnabled) {
                        joinGame.visibility = View.GONE
                        hostGame.setOnClickListener {
                            if (userName.getText() != null && userName.getText().toString().trim()
                                    .length() > 0
                            ) {
                                fragmentManager.beginTransaction()
                                    .replace(R.id.container, HostFragment()).addToBackStack(
                                        HostFragment::class.java.name
                                    )
                                    .commit()
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Please enter a UserName",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        if (clientHandler == null) {
                            clientHandler = ClientHandler()
                        }
                        hostGame.visibility = View.GONE
                        joinGame.setOnClickListener {
                            val clientConnect: ClientConnectionThread = ClientConnectionThread(
                                userName.getText().toString()
                            )
                            clientConnect.start()
                            if (userName.getText() != null && userName.getText().toString().trim()
                                    .length() > 0
                            ) {
//                                    if (ClientConnectionThread.serverStarted) {
                                fragmentManager.beginTransaction()
                                    .replace(R.id.container, JoinGameFragment()).addToBackStack(
                                        JoinGameFragment::class.java.name
                                    )
                                    .commit()
                                //                                    } else {
//                                        Toast.makeText(getActivity(), "Game yet to be hosted", Toast.LENGTH_SHORT).show();
//                                    }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Please enter a UserName",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
        return rootView
    }


    companion object {
        var clientHandler: ClientHandler? = null
        var userName: MaterialEditText? = null
    }
}
