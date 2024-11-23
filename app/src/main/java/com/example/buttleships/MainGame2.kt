package com.example.buttleships

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


data class Position (val row: Int, val column: Int)
class Ships (name: String, length: Int, shipImage: Int) {
    val shipName: String = name
    val shipLength : Int = length
    var isRotated : Boolean = false
    var isSelected: Boolean = false
    var position: Position = Position(0,0)
    var startPosition: Position = Position(0,0)
    val image: Int =  shipImage
}

val battleShips = arrayOf(
    Ships ("1x1", 1, shipImage = R.drawable.ship1x1copy ),
    Ships ("1x2", 2, R.drawable.ship2x1copy),
    Ships ("1x3", 3, R.drawable.ship3x1copy),
    Ships ("1x4", 4, R.drawable.ship4x1copy),
    Ships ("1x5", 5, R.drawable.ship3x1copy),
)
class Cell () {
    var ships: MutableList<Ships> = mutableListOf()
    var currentColor by mutableStateOf(selectColor())
    fun selectColor () :Color {
        if (ships.isEmpty()) {
            return Color.Transparent
        }
        else if (ships.size > 1) {
            return Color.Red
        }
        else {
            return Color.Green
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
        this.ships.remove(this.ships.first())
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

    @Composable
    fun DrawImage(){
        val currentShip = ships.last()
        Box (modifier = Modifier.fillMaxSize()
            .padding(20.dp)
            .padding(bottom = 40.dp)){
            Image(
                painter = painterResource(id = ships.last().image),
                contentDescription = "Description of your image",
                contentScale = ContentScale.Fit,
                modifier = Modifier

                    .rotate(90f)
                    //.width(34.dp)
                    .height((34*ships.last().shipLength).dp)
                    .absoluteOffset((currentShip.startPosition.row)    .dp,
                                    (currentShip.startPosition.column) .dp
                    )

                //.width(34.dp)
            )
        }

    }


}

class Grid () {
    val gridArray: Array<Array<Cell>> = Array(10) { Array(10) { Cell() } }
    var selectedShip: Ships? = null

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun DrawGrid(gridArray: Array<Array<Cell>>) {
        Box(modifier = Modifier
            .size(380.dp)) {
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
                            Box(modifier = Modifier
                                .size(34.dp)

                            )
                            {
                                currentCell.DrawBox(onClick = {
                                    if (selectedShip != null) {
                                        replaceShip(selectedShip!!, j,i)
                                        selectedShip = null
                                    }else if (!currentCell.ships.isEmpty()) {
                                        selectedShip = currentCell.ships.first()
                                    }
                                }
                                )
                                if (currentCell.ships.size >0){
                                    if (i == currentCell.ships.last().startPosition.row &&
                                        j == currentCell.ships.last().startPosition.column){
                                        Log.d("test", "Trying to print a ship msg")
                                        //currentCell.DrawImage()
                                    }
                                }
                            }



                        }

                    }

                }

            }
            for (i in 0 until 10){
                for(j in 0 until 10) {
                    val currentCell: Cell = gridArray[i][j]
                    if (currentCell.ships.size >0){
                        if (i == currentCell.ships.last().startPosition.row &&
                            j == currentCell.ships.last().startPosition.column){
                            Log.d("test", "Trying to print a ship msg")
                            currentCell.DrawImage()
                        }
                    }
                }
            }
        }


    }

    fun startingPosition() {
        for (i in 0 until 5) {
            gridArray[0][i].assignShip(battleShips[4], 0, i)
            gridArray[0][i].reassignColor()

        }
        gridArray[0][0].ships.first().startPosition = Position(0,0)
        for (i in 6 until 10) {
            gridArray[0][i].assignShip(battleShips[3], 0, i)
            gridArray[0][i].reassignColor()
        }
        gridArray[0][6].ships.first().startPosition = Position(0,6)
        for (i in 0 until 3) {
            gridArray[1][i].assignShip(battleShips[2], 1, i)
            gridArray[1][i].reassignColor()
        }
        gridArray[1][0].ships.first().startPosition = Position(1,0)
        for (i in 4 until 6) {
            gridArray[1][i].assignShip(battleShips[1], 1, i)
            gridArray[1][i].reassignColor()
        }
        gridArray[1][4].ships.first().startPosition = Position(1,4)

        gridArray[1][7].assignShip(battleShips[0], 1, 7)
        gridArray[1][7].ships.first().startPosition = Position(1,7)
        gridArray[1][7].reassignColor()


    }

    fun replaceShip( ship: Ships, newX: Int, newY: Int) {

        for (i in 0 until ship.shipLength) {
            gridArray[ship.startPosition.row][ship.startPosition.column+i].removeShip()
            gridArray[newY][newX+i].assignShip(ship, newX+i, (newY))
            gridArray[newY][newX+i].reassignColor()
            Log.d("test", (newX+i).toString())
        }
        gridArray[newY][newX].ships.first().startPosition = Position(newX,newY)
    }




}

@Composable
fun MainGame2(navController: NavController) {
    Scaffold  {innerPadding ->
        Box (modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color(0xFF4287F5))
        )
        {
            val grid = Grid()
            grid.startingPosition()
            grid.DrawGrid(grid.gridArray)
        }

    }
}