package com.example.i347865.myapplication

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import com.example.i347865.myapplication.services.PlacesServices
import com.example.i347865.myapplication.services.UserServices
import org.jetbrains.anko.toast

class MenuAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        runServices()
        val userServices = UserServices()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val button1: Button = findViewById(R.id.button1)
        button1.setOnClickListener {
            val intent = Intent(this, MapsAct::class.java)
            startActivity(intent)
        }
        val button2: Button = findViewById(R.id.button2)
        button2.setOnClickListener {
            val intent = Intent(this, ShowPlacesActivity::class.java)
            startActivity(intent)
        }
        val button3: Button = findViewById(R.id.button3)
        button3.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        val button4: Button = findViewById(R.id.button4)
        button4.setOnClickListener {
            val intent = Intent(this, CreditsActivity::class.java)
            startActivity(intent)
        }
        val userButton: ImageButton = findViewById(R.id.userButton)
        userButton.setOnClickListener {
            if (userServices.isLogged()) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun runServices() {
        val placesServicesName = PlacesServices::class.java
        val placesIntent = Intent(applicationContext, placesServicesName)
        if (!isServiceRunning(placesServicesName)) {
            // Start the service
            startService(placesIntent)
        } else {
            toast("PlacesService already running.")
        }

    }

    // Custom method to determine whether a service is running
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Loop through the running services
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                // If the service is running then return true
                return true
            }
        }
        return false
    }

}
