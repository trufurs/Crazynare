package com.example.crazynare.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pGroup
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.crazynare.Adapter.MywifisearchRecyclerViewAdapter
import com.example.crazynare.R

/**
 * A fragment representing a list of Items.
 */
class WifiSearchFragment : Fragment() {

    private lateinit var wifiManager: WifiManager
    private lateinit var networkList: RecyclerView
    private lateinit var adapter: MywifisearchRecyclerViewAdapter
    private val PERMISSION_REQUEST_CODE = 102



    private var columnCount = 1

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
        val view = inflater.inflate(R.layout.fragment_wifi_seach_list, container, false)

        wifiManager = requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager

        networkList = view?.findViewById<RecyclerView>(R.id.list_wifii)!!
        adapter = MywifisearchRecyclerViewAdapter{ selectedNetwork ->
            connectToNetwork(selectedNetwork)
        }
        networkList.adapter = adapter

        val refreshButton = view.findViewById<ImageButton>(R.id.refresh)
        refreshButton.setOnClickListener {
            refreshNetworks()
        }

        /*if (!wifiManager.isWifiEnabled) {
            val intent = Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)
            startActivityForResult(intent, 0)
        }*/
        checkAndRequestPermissions()
        displayNetworks()

        return view
    }

    private fun refreshNetworks() {
        wifiManager.startScan()
        displayNetworks()
    }


    private fun checkAndRequestPermissions(): Boolean {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        return if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
            false
        } else {
            true
        }
    }

    private fun displayNetworks() {
        val results : MutableList<ScanResult> = if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("scan", "failed perms")
            emptyList<ScanResult>().toMutableList()
        } else {
            Log.i("status" ,"gained permissions")
            wifiManager.scanResults

        }

        adapter.setData(results)
        Log.i("status","Found ${results.size} networks.")
        /*val networkNames = results.map { it.SSID }.filter { it.isNotEmpty() }
        if (networkNames.isEmpty()) {
            Log.i("status","No networks found.")
            return
        }

        Log.i("status","Select a network to connect:")*/

    }

    private fun connectToNetwork(network: ScanResult) {
        val wifiConfig = WifiConfiguration().apply {
            SSID = "\"${network.SSID}\""
            preSharedKey = "\"12345678\"" // Replace with actual password or handle input
        }

        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        wifiManager.reconnect()

        Log.i("status","Connecting to ${network.SSID}...")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //requireContext().unregisterReceiver(wifiScanReceiver)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            WifiSearchFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}