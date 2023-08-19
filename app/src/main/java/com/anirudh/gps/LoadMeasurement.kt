package com.anirudh.gps

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale


class LoadMeasurement : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val dbHelper = DatabaseHelper(this)
            val database = dbHelper.writableDatabase

            var ids = mutableListOf<String>()
            ids= dbHelper.getExistingnames().toMutableList()
            val onItemClick: (String) -> Unit = { id ->
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }

            setContent {
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                        ){
                    Heading()
                    IdList(ids = ids, onItemClick = onItemClick)
                }
            }
        }
}

@Preview
@Composable
fun Heading(){
    Text(
        text = "Saved Measurements",
        color = Color(136, 0, 73, 255),
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(16.dp)
            .background(Color(242, 239, 230))
            .padding(8.dp)
            .fillMaxWidth(),
        style = MaterialTheme.typography.headlineLarge
    )

}

@Composable
fun IdList(ids: List<String>, onItemClick: (String) -> Unit) {
    LazyColumn {
            itemsIndexed(ids) { index, id ->
                val ind = index+1
                val dot="."
                val textWithIndex = "$ind$dot $id"

                Text(
                    text = textWithIndex.uppercase(Locale.ROOT),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .clickable { onItemClick(id) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .background(
                            color = Color(151, 7, 84),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
    }
}

/*
what is compose
jetpack compose
sdk
emulator
sqlite
AVD
 */