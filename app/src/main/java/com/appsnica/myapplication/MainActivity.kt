package com.appsnica.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.appsnica.myapplication.ui.theme.ConsumoAPIComposeTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConsumoAPIComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ConsumeApiWithFuel(

                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}



@Composable
fun ConsumeApiWithFuel(modifier: Modifier = Modifier) {
    var displayText by remember { mutableStateOf("Loading...") }
    var personajes by remember { mutableStateOf<List<Personaje>?>(null) }
    // Fetch data from the API asynchronously
    LaunchedEffect(Unit) {
        val (request, response, result) = "https://bobsburgers-api.herokuapp.com/characters/".httpGet().awaitStringResponseResult()

        // Handle the result
        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                // Handle error
                val error = result.getException()
                displayText = "Error: $error"
            }
            is com.github.kittinunf.result.Result.Success -> {
                // Parse and display the data
                val data = result.get()
                //displayText = "Data: $data"
                val gson = Gson()
                personajes = gson.fromJson(data, Array<Personaje>::class.java).toList()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (personajes != null) {
            Text(text = "Personajes:")
            for (personaje in personajes!!) {
                Text(text = personaje.name)
                Image(
                    painter = rememberAsyncImagePainter(model = personaje.url),
                    contentDescription = "Character Image",
                    modifier = Modifier
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        } else
        Text(text = displayText, style = MaterialTheme.typography.displayMedium)
    }
}
data class Personaje(val id: Int, val name: String, val image: String, val url: String)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    ConsumoAPIComposeTheme {
        ConsumeApiWithFuel()
    }
}