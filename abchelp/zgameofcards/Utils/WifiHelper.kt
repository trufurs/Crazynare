package com.example.crazynare.gameofcards.Utils

import java.io.BufferedReader
import java.io.FileReader
import java.net.InetAddress


object WifiHelper {
    var deviceList: ArrayList<String?>? = null

    fun getDeviceList(): ArrayList<String?>? {
        if (deviceList == null) {
            deviceList = ArrayList<String?>()
        }
        val br: BufferedReader
        var isFirstLine = true

        try {
            br = BufferedReader(FileReader("/proc/net/arp"))
            var line: String
            while ((br.readLine().also { line = it }) != null) {
                if (isFirstLine) {
                    isFirstLine = false
                    continue
                }
                val splitted =
                    line.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (splitted.size >= 4) {
                    val ipAddress = splitted[0]
                    val isReachable = InetAddress.getByName(
                        splitted[0]
                    ).isReachable(500)
                    if (isReachable) {
                        deviceList!!.add(ipAddress)
                    }
                }
            }
            br.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return deviceList
    }
}
