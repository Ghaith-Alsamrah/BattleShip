package com.example.buttleships

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.asStateFlow


@Composable
fun lobby(navController : NavController) {
    val players by dataBase.playerList.asStateFlow().collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {// This box allows you to have multiple things on top of each other
            Image(
                painter = painterResource(R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome to the Lobby",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 50.dp),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(5f, 5f), // Controls thickness; adjust as needed
                        )
                    )
                )
                // A black box that contains the names
                Box(
                    modifier = Modifier
                        .size(
                            width = 380.dp,
                            height = 550.dp
                        )
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    dataBase.deleteOfflinePlayers()
                    // names column
                    LazyColumn(
                        modifier = Modifier.fillMaxSize() .padding(top = 15.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp) // for spacing between items
                    ) {
                        items(players) { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 15.dp)
                                    .clickable {
                                        navController.navigate("mainGame")
                                    },
                                verticalAlignment = Alignment.CenterVertically,

                            ) {

                                    // Black circle/point
                                    Canvas(modifier = Modifier.size(13.dp)) {
                                        drawCircle(color = Color.White)
                                    }
                                Spacer(modifier = Modifier.width(15.dp)) // Space between circle and text
                                Text(
                                    text = player.name,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)

                                )
                                TextButton(
                                    onClick = { /* Handle button action */ },
                                    modifier = Modifier.padding(end = 10.dp)
                                ) {
                                    Text("Invite",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        navController.navigate(nav.mainGame)
                    },
                    colors = buttonColors(
                        // Changes the color of the button
                        containerColor = Color.Red,
                    ),
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text("Play", color = Color.White)
                }
            }
        }
    }

}
