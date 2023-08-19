package com.anirudh.gps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.properties.Delegates


class LocationActivity : AppCompatActivity() {
    var nameset = false
    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener
    override fun onCreate(savedInstanceState: Bundle?) {
         var count=0;
         var dispcur=findViewById<TextView>(R.id.dispcur)
         var counter=findViewById<TextView>(R.id.counter)
         var latitude by Delegates.notNull<Double>()
         var longitude by Delegates.notNull<Double>()
         lateinit var name:String
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        var locname = findViewById<EditText>(R.id.locname)
        var latdis = findViewById<TextView>(R.id.latdis)
        var londis = findViewById<TextView>(R.id.londis)
        var addpt = findViewById<Button>(R.id.addpt)
        var next = findViewById<Button>(R.id.nextactivity)
        var setname = findViewById<Button>(R.id.setname)
        next.isEnabled=false
        addpt.isEnabled=false

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        val existingnames : Array<String> = dbHelper.getExistingnames()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Handle location updates here
                latitude = location.latitude
                longitude = location.longitude
                // Update your UI or perform any other desired action


                    addpt.isEnabled=true
                findViewById<TextView>(R.id.dispcur).text= "$latitude   $longitude"
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }

        addpt.setOnClickListener {
            if(++count<10) {
                latdis.text = latdis.text.toString() + latitude.toString() + "\n"
                londis.text = londis.text.toString() + longitude.toString() + "\n"
                findViewById<TextView>(R.id.counter).text="Count = $count"
                dbHelper.insert(name,count, latitude, longitude)
                //Toast.makeText(this, dbHelper.countins.toString(), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Maximum number of points reached", Toast.LENGTH_SHORT).show()
                addpt.isEnabled=false
                nameset=true
            }
        }

        setname.setOnClickListener {
            name =locname.text.toString()
            if(name==""){
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            if(existingnames.contains(name)){
                Toast.makeText(this, "Name already exists", Toast.LENGTH_SHORT).show()
            }
            if(name!="" && !existingnames.contains(name)){
                next.isEnabled=true
                locname.isClickable=false
                setname.isEnabled=false
            }
        }

        next.setOnClickListener {
            if(count<3)
                Toast.makeText(this, "At least 3 points are needed!", Toast.LENGTH_SHORT).show()
            else
            {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("id", name)
                startActivity(intent)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME_BETWEEN_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES,
            locationListener
        )
    }

    private fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 100 // 0.1 second
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 0.01f // 0.01 meter
    }
}
