package com.example.buttleships

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.buttleships.ui.theme.ButtleshipsTheme

data class player(
    var name: String = "",
    var ready: Boolean = false,
)

data class game(
    var player1ships: List<String> = listOf(""),
    var player2ships: List<String> = listOf(""),
    var gameState: String = "invite",
    var player1Id: String = "",
    var player2Id: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ButtleshipsTheme {
                val dataBase = dataBase()
                dataBase.listentoPlayer()
                dataBase.listentoGame()
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = nav.firstScreen , builder = {
                    composable(nav.firstScreen) {
                        firstScreen(navController = navController)
                    }
                    composable(nav.secondScreen) {
                        secondScreen(navController = navController, dataBase)
                    }
                    composable(nav.lobby) {
                        lobby(navController = navController, dataBase)
                    }
                    composable ("mainGame/{gameId}"){ backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId")
                        MainGame2(navController = navController, dataBase, gameId)
                    }
                })



            }
        }
    }
}







@Preview
@Composable
fun GreetingPreview() {
    ButtleshipsTheme {
       //lobby( navController = rememberNavController())
    }
}

