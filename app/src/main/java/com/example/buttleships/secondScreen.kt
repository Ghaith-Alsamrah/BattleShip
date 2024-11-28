package com.example.buttleships

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.asStateFlow


@Composable
fun secondScreen (navController: NavController, dataBase: Database, isProcessing: IsProcessing) {
    //The offline information from storage with an ID of "BattleShipPrefs"
    val sharedPreferences = LocalContext.current.getSharedPreferences("BattleShipPrefs", Context.MODE_PRIVATE)
    val players by dataBase.playerList.asStateFlow().collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf("") }

    // Check for playerId in SharedPreferences
    LaunchedEffect(Unit) {
        //Retrieves the name from the shared preferences
        dataBase.localPlayerId.value = sharedPreferences.getString("playerId", null)


        if (dataBase.localPlayerId.value != null) {
            navController.navigate("lobby")
        }


    }
    if (dataBase.localPlayerId.value == null) {
        var name by remember{ mutableStateOf("") }
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            Box(modifier = Modifier.padding(innerPadding)) {// the box is allow you to have multiple things over each other
                Image(
                    painter = painterResource(R.drawable.background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize(),

                    verticalArrangement = Arrangement.Bottom, // putt the button in the middil of the page
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Enter Your Name",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(5f, 5f), // Controls thickness; adjust as needed
                            )
                        )
                    )
                    TextField(modifier = Modifier.padding(20.dp),
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name") }

                    )
                    Button(
                        onClick = {
                            if(name.isNotBlank() && !isProcessing.isProcessing2.value ) {
                                isProcessing.isProcessing2.value = true
                                val newPlayer = player(name = name)
                                //Accesses the database collection that's called "players"
                                dataBase.db.collection("players")
                                    .add(newPlayer)
                                    //Document Ref = Document references which is the new data that has been created into the database
                                    .addOnSuccessListener { documentRef ->
                                        val playerId = documentRef.id

                                        sharedPreferences.edit().putString("playerId", playerId).apply()

                                        dataBase.localPlayerId.value = playerId

                                        navController.navigate(nav.lobby)
                                        isProcessing.isProcessing2.value = false
                                    }
                            }
                                  },
                        colors = buttonColors( // this will make the button it self red
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Enter", color = Color.White)
                    }
                    Row(modifier = Modifier.padding(100.dp)) {}
                }
            }
        }
    }
    else {
        var itExists = false
        players.forEach{ player ->
            if (dataBase.localPlayerId.value == player.value.name){
                itExists = true
            }
        }
        if (!itExists){
            val newPlayer = player (name = dataBase.localPlayerId.value!!)
            dataBase.db.collection("players")
                .add(newPlayer)
            Log.d ("test", "Player " + newPlayer + " is created")
            itExists = true
        }
    }
}



