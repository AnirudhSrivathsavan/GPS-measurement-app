package com.anirudh.gps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val dbHelper = DatabaseHelper(this)
        val database = dbHelper.writableDatabase

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

// Create and launch sign-in intent
        if(dbHelper.login==false) {
            dbHelper.login=true
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            startActivity(signInIntent)
        }


        var newmes = findViewById<Button>(R.id.newmes)
        var loadmes=findViewById<Button>(R.id.loadmes)

        newmes.setOnClickListener {
            val intent=Intent(this,LocationActivity::class.java)
            startActivity(intent)
        }

        loadmes.setOnClickListener {
            val intent=Intent(this,LoadMeasurement::class.java)
            startActivity(intent)
        }
    }
}
