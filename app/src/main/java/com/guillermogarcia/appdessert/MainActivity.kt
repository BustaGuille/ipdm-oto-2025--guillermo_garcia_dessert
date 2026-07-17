package com.guillermogarcia.appdessert

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guillermogarcia.appdessert.ui.theme.AppDessertTheme

private const val TAG = "MainActivity"

data class Dessert(val imageRes: Int, val price: Int, val startThreshold: Int)

object Datasource {
    val dessertList = listOf(
        Dessert(R.drawable.cupcake, 5, 0),
        Dessert(R.drawable.donut, 10, 5),
        Dessert(R.drawable.eclair, 15, 20),
        Dessert(R.drawable.froyo, 30, 50),
        Dessert(R.drawable.gingerbread, 50, 100),
        Dessert(R.drawable.honeycomb, 100, 200),
        Dessert(R.drawable.icecreamsandwich, 500, 500),
        Dessert(R.drawable.jellybean, 1000, 1000),
        Dessert(R.drawable.kitkat, 2000, 2000),
        Dessert(R.drawable.lollipop, 3000, 4000),
        Dessert(R.drawable.marshmallow, 4000, 8000),
        Dessert(R.drawable.nougat, 5000, 16000),
        Dessert(R.drawable.oreo, 6000, 20000)
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
        enableEdgeToEdge()
        setContent {
            AppDessertTheme {
                DessertClickerApp(Datasource.dessertList)
            }
        }
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart Called") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume Called") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart Called") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause Called") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop Called") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy Called") }
}

@Composable
fun DessertClickerApp(desserts: List<Dessert>) {
    var revenue by rememberSaveable { mutableStateOf(0) }
    var dessertsSold by rememberSaveable { mutableStateOf(0) }
    
    val currentDessertIndex = determineDessertToShow(desserts, dessertsSold)
    val currentDessert = desserts[currentDessertIndex]

    Scaffold(
        topBar = {
            val context = LocalContext.current
            DessertClickerAppBar(
                onShareButtonClicked = {
                    shareDessertsInformation(context, dessertsSold, revenue)
                }
            )
        }
    ) { innerPadding ->
        DessertClickerScreen(
            revenue = revenue,
            dessertsSold = dessertsSold,
            dessertImageRes = currentDessert.imageRes,
            onDessertClicked = {
                revenue += currentDessert.price
                dessertsSold++
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

private fun determineDessertToShow(desserts: List<Dessert>, dessertsSold: Int): Int {
    var dessertIndex = 0
    for (i in desserts.indices) {
        if (dessertsSold >= desserts[i].startThreshold) {
            dessertIndex = i
        } else {
            break
        }
    }
    return dessertIndex
}

private fun shareDessertsInformation(context: Context, dessertsSold: Int, revenue: Int) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "¡He vendido $dessertsSold postres y ganado $$revenue!")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    try {
        context.startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No hay app para compartir", Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun DessertClickerAppBar(
    onShareButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF5ED8FF))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dessert Clicker",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )
        IconButton(onClick = onShareButtonClicked) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Compartir",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun DessertClickerScreen(
    revenue: Int,
    dessertsSold: Int,
    @DrawableRes dessertImageRes: Int,
    onDessertClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Imagen de fondo (Bakery)
        Image(
            painter = painterResource(id = R.drawable.bakery_back),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Imagen del postre
                Image(
                    painter = painterResource(id = dessertImageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .width(175.dp)
                        .height(175.dp)
                        .clickable { onDessertClicked() },
                    contentScale = ContentScale.Fit
                )
            }
            
            // Panel inferior de estadísticas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2E3E4E))
                    .padding(24.dp)
            ) {
                StatRow(label = "Desserts sold", value = dessertsSold.toString())
                StatRow(label = "Total Revenue", value = "$$revenue", isTotal = true)
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = if (isTotal) 22.sp else 18.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = if (isTotal) 22.sp else 18.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}
