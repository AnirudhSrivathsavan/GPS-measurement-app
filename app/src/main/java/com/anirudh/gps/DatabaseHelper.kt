package com.anirudh.gps

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    var countins=0
    var login:Boolean = false


    companion object {
        private const val DATABASE_NAME = "coordinates.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Points (id TEXT,ser_no INT, lat DOUBLE,long DOUBLE)");
    }
    /*
   |id|ser_no|lat|long
    -----------------------------
    |home|0|1,111|1.1111
    |home-------------------
    |home---------------
    |home----------------
    |office|0|
    */


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getEntryCountBySameId(id: String): Int {
        val selectQuery = "SELECT COUNT(*) FROM Points WHERE id = ? GROUP BY id"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(id))

        val entryCount = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            0
        }

        cursor.close()
        return entryCount
    }

    public fun insert(id:String,ser_no:Int,lat:Double,long:Double){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id",id)
        contentValues.put("ser_no",ser_no)
        contentValues.put("lat",lat)
        contentValues.put("long",long)
        db.insert("Points",null,contentValues)
        //countins=getEntryCountBySameId(id)
    }

    fun getExistingnames(): Array<String> {
        val db = readableDatabase
        val projection = arrayOf("DISTINCT id")
        val cursor = db.query("Points", projection, null, null, null, null, null)
        val columnIndex = cursor.getColumnIndex("id")
        val arrayList = ArrayList<String>()
        while (cursor.moveToNext()) {
            val value = cursor.getString(columnIndex)
            arrayList.add(value)
        }
        cursor.close()
        db.close()
        return arrayList.toTypedArray()
    }

    @SuppressLint("Range")
    fun getLatLong(id: String):  Array<Pair<Double,Double>> {
        val latLongList =  ArrayList<Pair<Double,Double>>()
        val selectQuery = "SELECT lat,long FROM Points WHERE id = ? ORDER BY ser_no"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(id))
        while (cursor.moveToNext()) {
            val lat = cursor.getDouble(cursor.getColumnIndex("lat"))
            val long = cursor.getDouble(cursor.getColumnIndex("long"))
            latLongList.add(Pair(lat,long))
        }
        cursor.close()
        return latLongList.toTypedArray()
    }

}