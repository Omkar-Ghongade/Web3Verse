package com.ritorno.webverse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ritorno.webverse.avatar.AvatarActivity
import com.ritorno.webverse.dashboard.DashboardFragment
import com.ritorno.webverse.nearbychat.NearbyFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomnav = this.findViewById<BottomNavigationView>(R.id.bottomnav)
        replacefragment(DashboardFragment())



        bottomnav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboardnavigate -> {
                    replacefragment(DashboardFragment())
                }

                R.id.nearbynavigate -> {
                    replacefragment(NearbyFragment())
                }
            }
            return@setOnItemSelectedListener true
        }
    }
        private fun replacefragment(fragment : Fragment) {
            val fragmentmanager = supportFragmentManager
            val fragmentTransaction = fragmentmanager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentcontainer, fragment)
            fragmentTransaction.commit()
        }
}