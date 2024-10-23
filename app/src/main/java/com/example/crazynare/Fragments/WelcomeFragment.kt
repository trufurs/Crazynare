package com.example.crazynare.Fragments

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.crazynare.R
import com.example.crazynare.Viewmodel.ClientViewModel
import com.example.crazynare.Viewmodel.HostViewModel
import com.example.crazynare.Viewmodel.MessageViewModel
import com.example.crazynare.Viewmodel.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Method

class WelcomeFragment : Fragment() {
    private val hostViewModel: HostViewModel by activityViewModels()
    private val clientViewModel: ClientViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()
    private val messageViewModel: MessageViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)
        val createButton = view.findViewById<Button>(R.id.create_main)
        val joinButton = view.findViewById<Button>(R.id.join_main)

        // Set listener for creating a host
        createButton.setOnClickListener {
            if (isHotspotEnabled(requireContext())) {
                hostViewModel.startServer(messageViewModel, playerViewModel) { connection ->
                    if (connection) {
                        // Navigate to PlayerFragment after starting the server
                        navigateToPlayerFragment()
                    } else {
                        showToast("Closed")
                        navigateToWelcomeFragment()
                    }
                }
            } else {
                showToast("Hotspot is not enabled")
            }
        }

        // Set listener for joining as a client
        joinButton.setOnClickListener {
            val hotspotIp = getHotspotIp() // Retrieve the hotspot IP
            if (hotspotIp.isNotEmpty()) {
                Log.i("network", "Hotspot IP: $hotspotIp")
                clientViewModel.connectToServer(hotspotIp, messageViewModel, playerViewModel) { connection ->
                    if (connection) {
                        Log.i("network", "Connected to server")
                        navigateToPlayerFragment()
                    } else {
                        showToast("No Connections")
                        navigateToWelcomeFragment()
                    }
                }
            } else {
                showToast("Wi-Fi is not Connected")
            }
        }

        // Handle back button press to close the app
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            (context as? Activity)?.finishAffinity()
        }

        return view
    }

    // Retrieves the hotspot IP (if connected)
    private fun getHotspotIp(): String {
        return try {
            val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            val isWifiConnected = wifiManager.isWifiEnabled &&
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                        ?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) == true

            if (isWifiConnected) {
                val dhcpInfo = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)?.dnsServers
                Log.i("network", "Connected to ${dhcpInfo.toString()}")
                dhcpInfo?.firstOrNull()?.hostAddress ?: ""
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e("HotspotIP", "Error getting Hotspot IP: ${e.message}", e)
            "" // Return a default value in case of error
        }
    }

    // Check if the Wi-Fi Hotspot is enabled using reflection
    private fun isHotspotEnabled(context: Context): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return try {
            val method: Method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            method.invoke(wifiManager) as Boolean
        } catch (e: Exception) {
            Log.e("hotspot", "Error checking hotspot status", e)
            false
        }
    }

    // Show a Toast message
    private fun showToast(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    // Navigate to the PlayerFragment
    private fun navigateToPlayerFragment() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.main, PlayerFragment())
            ?.addToBackStack(WelcomeFragment::class.java.name)
            ?.commit()
    }

    // Clear all fragments and navigate to WelcomeFragment
    private fun navigateToWelcomeFragment() {
        // Pop all fragments off the backstack
        activity?.supportFragmentManager?.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)

        // Replace the current fragment with WelcomeFragment
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.main, WelcomeFragment())
            ?.commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Optionally, stop the server if needed when leaving the fragment
        // hostViewModel.stopServer()
    }
}
