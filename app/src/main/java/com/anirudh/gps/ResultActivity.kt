package com.anirudh.gps

//java topography suite
//import net.sf.geographiclib.*;
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import java.text.DecimalFormat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class ResultActivity : AppCompatActivity() {
    val API_KEY = "89ecb7326c6581db812216b9af847e5f"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        var count =0
        val decimalFormat = DecimalFormat("#." + "#".repeat(2))
        var perival=0.0
        val counttext =findViewById<TextView>(R.id.id)
        val latitudetext=findViewById<TextView>(R.id.latitude)
        val longitudetext=findViewById<TextView>(R.id.longitude)
        var side = findViewById<TextView>(R.id.side)
        var sidemeter = findViewById<TextView>(R.id.sidemeter)
        var sidefeet = findViewById<TextView>(R.id.sidefeet)
        val receivedString = intent.getStringExtra("id")
        findViewById<TextView>(R.id.plotname).text=receivedString
        val latlonglist = receivedString?.let { dbHelper.getLatLong(it) }
        if (latlonglist != null) {
            for (pair in latlonglist) {
                ++count
                //Toast.makeText(this, pair.toString(), Toast.LENGTH_SHORT).show()
                var lat = pair.first
                var long = pair.second
                counttext.text = counttext.text.toString() + count.toString() + "\n "
                latitudetext.text = latitudetext.text.toString() + lat.toString() + "\n "
                longitudetext.text = longitudetext.text.toString() + long.toString() + "\n "
            }
            for(index in latlonglist.indices){
                val pair1=latlonglist[index]
                val pair2=latlonglist[(index+1)%count]
                val sidedou = calculateDistance(pair1.first,pair1.second,pair2.first,pair2.second)
                val sideval = decimalFormat.format(sidedou)
                perival = perival + sidedou
                val sidevalft = decimalFormat.format(sideval.toDouble()*3.28084)
                side.text=side.text.toString()+(index+1)+"-"+((index+1)%count+1).toString()+"\n"
                sidemeter.text = sidemeter.text.toString()+sideval.toString()+"m\n"
                sidefeet.text=sidefeet.text.toString()+sidevalft.toString()+"ft\n"
            }
                    findViewById<TextView>(R.id.areakm).text= buildString {
                append(decimalFormat.format(calculateAreaInMeters(latlonglist.toList())))
                append(" m²")
            }
                    findViewById<TextView>(R.id.areaft).text= buildString {
                append(decimalFormat.format(calculateAreaInFeets(latlonglist.toList())))
                append(" ft²")
            }
            findViewById<TextView>(R.id.perim).text=decimalFormat.format(perival)+" m "
            findViewById<TextView>(R.id.perift).text=decimalFormat.format(perival*3.28084)+" f "

        }
         var backPressedOnce = false
         val doubleBackPressInterval = 2000 // Interval in milliseconds
        findViewById<Button>(R.id.delete).setOnClickListener {
            if (backPressedOnce) {
                db.delete("Points","id = ?", arrayOf(receivedString))
                Toast.makeText(this, "Measurement is deleted", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,LoadMeasurement::class.java)
                startActivity(intent)
            }
            backPressedOnce = true
            Toast.makeText(this, "Press again to delete", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                backPressedOnce = false
            }, doubleBackPressInterval.toLong())
        }


    }


    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double{
        val earthRadius = 6378137.0 // Earth's radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun calculateAreaInMeters(vertices: List<Pair<Double, Double>>): Double {
        var vert = mutableListOf<Pair<Double,Double>>()
        vert = vertices as MutableList<Pair<Double, Double>>;
        vert.add(vert.first())
        val geometryFactory = GeometryFactory()
        val coordinates = vertices.map { Coordinate(it.first, it.second) }.toTypedArray()
        val polygon = geometryFactory.createPolygon(coordinates)
        return polygon.area*100000000000
    }

    private fun calculateAreaInFeets(vertices: List<Pair<Double, Double>>): Double {
        return calculateAreaInMeters(vertices)*10.7639
    }

}
