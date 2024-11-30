package com.example.buttleships

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.buttleships.ui.theme.ButtleshipsTheme

data class player(
    var name: String = "",
    var ready: Boolean = false,
    var playerShips: List<Int> = listOf(0),
    var guessedShips: MutableList<Int> = mutableListOf(),
    var enemyPlayer: String = ""
)

data class game(
    var gameState: String = "invite",
    var player1Id: String = "",
    var player2Id: String = ""
)

class IsProcessing : ViewModel() {
    var isProcessing2 = mutableStateOf(false )
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ButtleshipsTheme {
                val dataBase = Database()
                val isLoading = IsProcessing()
                dataBase.listentoPlayer()
                dataBase.listentoGame()
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = nav.firstScreen , builder = {
                    composable(nav.firstScreen) {
                        firstScreen(navController = navController)
                    }
                    composable(nav.secondScreen) {
                        secondScreen(navController = navController, dataBase, isProcessing = isLoading)
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

