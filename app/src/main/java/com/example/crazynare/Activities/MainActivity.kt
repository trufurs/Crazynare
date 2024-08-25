package com.example.crazynare.Activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.crazynare.Fragments.PlayerFragment
import com.example.crazynare.Fragments.WelcomeFragment
import com.example.crazynare.R

class MainActivity : AppCompatActivity() {
    private val fragmentManager : FragmentManager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager.beginTransaction().replace(R.id.main, WelcomeFragment())
            .addToBackStack(null)
            .commit()
    }
}