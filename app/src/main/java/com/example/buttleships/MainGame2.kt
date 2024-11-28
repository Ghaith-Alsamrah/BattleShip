package com.example.buttleships

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.asStateFlow


data class Position (val row: Int, val column: Int)

class Ships (name: String, length: Int, shipImage: Int, shipRotatedImage: Int) {
    val shipName: String = name
    val shipLength : Int = length
    var isRotated : Boolean = false
    var isSelected: Boolean = false
    var position: Position = Position(0,0)
    var startPosition2: MutableState<Position> =  (mutableStateOf(Position(0,0)) )
    //var startPosition: Position = Position(0,0)
    val image: Int =  shipImage
    var imageRotation : MutableState<Float> = (mutableStateOf(90f))
}

val battleShips = arrayOf(
    Ships ("1x1", 1, shipImage = R.drawable.ship1x1copy,
        shipRotatedImage = R.drawable.ship1x1rotated),
    Ships ("1x2", 2, R.drawable.ship2x1copy,
            shipRotatedImage = R.drawable.ship2x1rotated),
    Ships ("1x3", 3, R.drawable.ship3x1copy,
        shipRotatedImage = R.drawable.ship3x1rotated),
    Ships ("1x4", 4, R.drawable.ship4x1copy,
        shipRotatedImage = R.drawable.ship4x1rotated),
    Ships ("1x5", 5, R.drawable.ship3x1copy,
        shipRotatedImage = R.drawable.ship3x1rotated),
)

class Cell () {
    var ships: MutableList<Ships> = mutableListOf()
    var currentColor by mutableStateOf(selectColor())
    var isReady2 : Boolean = false


    fun selectColor () :Color {
        val result = !(isReady2?: false)
        if (result) {
            if (ships.isEmpty()) {
                return Color.Transparent
            }
            else if (ships.size > 1) {
                return Color.Red
            }
            else {
                return Color.Green
            }
        }else{
            return Color.Transparent
        }

    }

    fun assignShip (theShip: Ships, x : Int, y : Int ) {
        theShip.position = Position(x,y)
        this.ships.add(theShip)

    }


    fun reassignColor() {
        this.currentColor = selectColor()
    }


    fun removeShip () {
        this.ships.remove(this.ships.last())
        reassignColor()
    }


    @Composable
    fun DrawBox (onClick: () -> Unit = {}) {
        Box (modifier = Modifier
            .background(currentColor)
            .border(width = 1.dp, color = Color.White)
            .size(34.dp)
            .clickable {
                onClick()
            } ) {

        }
    }
//new it company name
    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun DrawImage(ship: Ships?){
    var currentShip = ships.last()
        if (ship != null ) {
            currentShip = ship
        }

        var offsetXAdjustment = 0

        if (currentShip.imageRotation.value<1){
            offsetXAdjustment = 17 * (currentShip.shipLength - 1 )
        }

        //currentShip.isRotated = true
        //Log.d("test", currentShip.startPosition.toString())
        Box (modifier = Modifier.fillMaxSize()
            .padding(20.dp)
            .padding(bottom = 40.dp)){
            Image(
                painter = painterResource(id = ships.last().image),
                contentDescription = "${currentShip.shipName}",
                contentScale = ContentScale.Fit,
                modifier = Modifier

                    //
                    //.width(34.dp)
                    .height((34*currentShip.shipLength).dp)
                    .absoluteOffset((34 * currentShip.startPosition2.value.column+
                            (17*(currentShip.shipLength))-5 - offsetXAdjustment).dp,
                                    (currentShip.startPosition2.value.row* 34
                                            - (17*(currentShip.shipLength-1)) + offsetXAdjustment)
                                        .dp
                    )
                    .rotate(currentShip.imageRotation.value)

                //.width(34.dp)
            )
        }

    }

}

data class Grid (val dataBase: Database, val players : Map<String, player>, val games : Map<String, game>) {
    val gridArray: Array<Array<Cell>> = Array(10) { Array(10) { Cell() } }
    var selectedShip: MutableState <Ships?> = mutableStateOf(null)
    var isReady : MutableState <Boolean> = mutableStateOf(false)
    var shipLocation : MutableList<String> = mutableListOf("")
    var shipLocation1 : MutableList<String> = mutableListOf("")



    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun DrawGrid(gridArray: Array<Array<Cell>>) {
        Box(modifier = Modifier
            .fillMaxSize()) {
            Box(
                modifier = Modifier
                    //.size(380.dp)
                    .align(Alignment.TopCenter)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .padding(bottom = 40.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    for (i in 0 until 10) {
                        Row {
                            for (j in 0 until 10) {
                                val currentCell: Cell = gridArray[i][j]
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                )
                                {
                                    currentCell.DrawBox(onClick = {
                                        if (!isReady.value) {
                                            if (selectedShip.value != null) {
                                                if (selectedShip.value!!.imageRotation.value > 0) {
                                                    replaceShip(selectedShip.value!!, j, i)
                                                } else {
                                                    replaceShipRotated(selectedShip.value!!, j, i)
                                                }
                                                selectedShip.value = null
                                            } else if (!currentCell.ships.isEmpty()) {
                                                selectedShip.value = currentCell.ships.last()
                                            }
                                        }

                                    })
                                }


                            }

                        }

                    }
                }
                for (ship in battleShips) {
                    val currentCell =
                        gridArray[ship.startPosition2.value.row][ship.startPosition2.value.column]
                    currentCell.DrawImage(ship)
                }
            }
            if (selectedShip.value != null) {
                Button(
                    onClick = {
                        rotateImage()
                        selectedShip.value = null

                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(top = 50.dp)
                ) { Text("Rotate") }
            }
            if (!isReady.value) {
                Button(
                    onClick = {
                        var ready = true

                        for (i in 0 until 9) {
                            for (j in 0 until 9) {
                                if (gridArray[i][j].ships.size > 1) {
                                    ready = false
                                }
                            }
                        }
                        if (ready) {
                        games.forEach { (gameId, game) ->

                                if (game.player1Id == dataBase.localPlayerId.value) {
                                    dataBase.db.collection("players")
                                        .document(game.player1Id)
                                        .update("ready", ready)
                                }
                                else if(game.player2Id == dataBase.localPlayerId.value) {
                                    dataBase.db.collection("players")
                                        .document(game.player2Id)
                                        .update("ready", ready)
                                }
                            }

                        }
                        isReady2(ready)

                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(top = 50.dp)
                )
                { Text("Confirm") }
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    fun rotateImage() {
        val currentShip = selectedShip.value!!
        currentShip.imageRotation.value = (currentShip.imageRotation.value + 90f)%180
            rotateimage2(
                currentShip,
                currentShip.startPosition2.value.row,
                currentShip.startPosition2.value.column
            )

    }

    fun rotateimage2(ship:Ships, x:Int, y:Int) {
        var adjustmentX = 0
        var adjustmentY = 0
        if (y + ship.shipLength > 9){
            adjustmentY = y + ship.shipLength - 10

        }
        if (x + ship.shipLength > 9) {
            adjustmentX = x + ship.shipLength - 10
        }
        for (i in 0 until ship.shipLength) {
            if (ship.imageRotation.value < 1) {
                gridArray[x][y+i].removeShip()
                gridArray[x + i - adjustmentX][y].assignShip(ship, x + i - adjustmentX, (y))
                gridArray[x + i - adjustmentX][y].reassignColor()
            }
            else{
                gridArray[x+i][y].removeShip()
                gridArray[x][y+i - adjustmentY].assignShip(ship, x , (y+i - adjustmentY))
                gridArray[x][y + i - adjustmentY].reassignColor()
            }
        }
        gridArray[x - adjustmentX][y - adjustmentY].ships.last().startPosition2.value = Position(x - adjustmentX,y - adjustmentY )
    }

    fun startingPosition() {
        for (i in 0 until 5) {
            gridArray[0][i].assignShip(battleShips[4], 0, i)
            gridArray[0][i].reassignColor()

        }
        gridArray[0][0].ships.last().startPosition2.value = Position(0,0)
        for (i in 6 until 10) {
            gridArray[0][i].assignShip(battleShips[3], 0, i)
            gridArray[0][i].reassignColor()
        }
        gridArray[0][6].ships.last().startPosition2.value = Position(0,6)
        for (i in 0 until 3) {
            gridArray[1][i].assignShip(battleShips[2], 1, i)
            gridArray[1][i].reassignColor()
        }
        gridArray[1][0].ships.last().startPosition2.value = Position(1,0)
        for (i in 4 until 6) {
            gridArray[1][i].assignShip(battleShips[1], 1, i)
            gridArray[1][i].reassignColor()
        }
        gridArray[1][4].ships.last().startPosition2.value = Position(1,4)

        gridArray[1][7].assignShip(battleShips[0], 1, 7)
        gridArray[1][7].ships.last().startPosition2.value = Position(1,7)
        gridArray[1][7].reassignColor()


    }


    fun replaceShip( ship: Ships, newX: Int, newY: Int) {
        Log.d ("test", "The old starting position of the ship is " + ship.startPosition2.value)

        var adjustment = 0
        if (newX + ship.shipLength > 10){
            adjustment = newX + ship.shipLength - 10
        }
        for (i in 0 until ship.shipLength) {

            Log.d ("test", "Replacing the " + i + " part of the ship")
            gridArray[ship.startPosition2.value.row][ship.startPosition2.value.column+i].removeShip()
            gridArray[newY][newX+i- adjustment].assignShip(ship, newX+i- adjustment, newY)
            gridArray[newY][newX+i- adjustment].reassignColor()
        }
        gridArray[newY][newX].ships.last().startPosition2.value = Position(newY,newX- adjustment)
        Log.d("test", "Reassigning the starting position of ${ship.shipName}" )
    }


    fun replaceShipRotated(ship: Ships, newX: Int, newY: Int) {
        Log.d ("test", "The old starting position of the ship is " + ship.startPosition2.value)
        var currentShip = selectedShip.value!!
        var adjustment = 0
        if (newY + ship.shipLength > 10){
            adjustment = newY + ship.shipLength - 10
        }
        for (i in 0 until ship.shipLength) {

            Log.d ("test", "Replacing the " + i + " part of the ship")
            gridArray[currentShip.startPosition2.value.row + i][currentShip.startPosition2.value.column].removeShip()
            gridArray[newY + i - adjustment][newX]
                .assignShip(selectedShip.value!!, newX , (newY +i - adjustment))
            gridArray[newY + i - adjustment][newX].reassignColor()
            Log.d ("test", "The new position is " + currentShip.startPosition2.value.row + i + " " +  currentShip.startPosition2.value.column)
        }
        gridArray[newY][newX].ships.last().startPosition2.value = Position(newY - adjustment,newX)
        Log.d("test", "Reassigning the starting position of ${ship.shipName}" )
    }

    fun isReady2 (ready : Boolean) {


        if (ready) {
            for (i in 0 until 10) {
                for (j in 0 until 10) {
                    gridArray[i][j].isReady2 = true
                    gridArray[i][j].reassignColor()
                }
            }
            this.isReady.value = ready
        }

        games.forEach { (gameId, game) ->
                Log.d("sss", "${ready}")

                Log.d("sss", "${this.isReady.value}")
                if (players[games[gameId]!!.player1Id]!!.ready) {
                    for (i in 0 until 10) {
                        for (j in 0 until 10) {
                            if (gridArray[i][j].ships.isNotEmpty()) {
                                shipLocation.add(i.toString() + j.toString())
                                Log.d("shipp", "1")


                        }
                    }
                }
            }
                if (players[games[gameId]!!.player2Id]!!.ready){
                    for (i in 0 until 10) {
                        for (j in 0 until 10) {
                            if (gridArray[i][j].ships.isNotEmpty()) {
                                shipLocation1.add(i.toString() + j.toString())
                                Log.d("shipp", "2")


                        }
                    }
                }
            }
            if (players[games[gameId]!!.player1Id]!!.ready) {
                if (game.player1Id == dataBase.localPlayerId.value) {
                    dataBase.db.collection("games")
                        .document(gameId)
                        .update("player1ships", shipLocation)
                }
            }
            if (players[games[gameId]!!.player2Id]!!.ready){
                if (game.player1Id == dataBase.localPlayerId.value) {
                    dataBase.db.collection("games")
                        .document(gameId)
                        .update("player2ships", shipLocation1)
                }
            }
        }


    }



}

@Composable
fun MainGame2(navController: NavController, dataBase: Database, gameId: String?) {
    val players by dataBase.playerList.asStateFlow().collectAsStateWithLifecycle()
    val games by dataBase.gameMap.asStateFlow().collectAsStateWithLifecycle()

    if (gameId != null && games.containsKey(gameId)) {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFF4287F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween // Space elements apart
                ) {
                    // Top box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        games.forEach { (gameId, game) ->
                            if (game.player1Id == dataBase.localPlayerId.value) {
                                Text(
                                    text = "Name: player1 ${players[games[gameId]!!.player1Id]!!.name} ",
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp),
                                    color = Color.White
                                )
                                Text(
                                    text = "Score: 0-0",
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 20.dp),
                                    color = Color.White
                                )
                            }
                            else {
                                Text(
                                    text = "Name: palyer2 ${players[games[gameId]!!.player2Id]!!.name} ",
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp),
                                    color = Color.White
                                )
                                Text(
                                    text = "Score: 0-0",
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 20.dp),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Bottom box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val grid = Grid(dataBase ,players, games)
                        grid.startingPosition()
                        grid.DrawGrid(grid.gridArray)
                    }
                }
            }
        }
    }
}
