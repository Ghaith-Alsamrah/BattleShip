package com.example.buttleships

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlin.math.roundToInt

// Todo --> Connect to the database

//On second tap, place boat from the center of the boat to the right squars

data class ShipType (
    val length: Int,
    val shipName: String,
    var rotationState : Float = 0f,
    var isShipSelectedState: Boolean = false,
    var offsetState: MutableState<Offset> = mutableStateOf(Offset(0f, 0f)),
    var centerOfImageState: Offset = Offset (0f,0f)
)

@Composable
fun shipWithValueRemember (length: Int, name: String, rotation: Float = 0f,
                           isShipSelected: Boolean = false, offset: Offset = Offset(0f,0f),
                           centerOfImageState : Offset = Offset (0f, 0f)) : ShipType {
    val rotation = remember { mutableStateOf(rotation) }
    val isShipSelected = remember { mutableStateOf(isShipSelected) }
    val offset = remember { mutableStateOf(offset) }
    val centerOfImage = remember { mutableStateOf(centerOfImageState) }
    return ShipType(
        length = length,
        shipName = name,
        rotationState = rotation.value,
        isShipSelectedState = isShipSelected.value,
        offsetState = offset,
        centerOfImageState = centerOfImage.value
    ).apply {
        this.rotationState = rotation.value
        this.isShipSelectedState = isShipSelected.value
        this.offsetState = offset
        this.centerOfImageState = centerOfImage.value
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun MainGame (navController: NavController) {
    val context = LocalContext.current

    //Todo --> Create an array for the cells with a boolean if occupied or nots
    //Todo --> Create an array of object "ships" that contains their sizes and if they are rotated or not

    val ships = arrayOf(
        shipWithValueRemember(5,"5x1"),
        shipWithValueRemember(4,"4x1"),
        shipWithValueRemember(3,"3x1"),
        shipWithValueRemember(2,"2x1"),
        shipWithValueRemember(1,"1x1")
    )



    val cells = Array(10) { row ->
        Array(10) {col ->
            val isEmpty : Boolean = true
        }
    }

    var imageRotation by remember { mutableStateOf(0f) }
    val rotationAnimation by animateFloatAsState( targetValue = imageRotation)
    val imagePosition = remember { mutableStateOf(Offset(0f, 0f)) } //starting position of the ship
    val isVectorSelected = remember { mutableStateOf(false) } //check if the ship is selected
    val touchOffset = remember { mutableStateOf(Offset.Zero) } //The ship placement from the center of the tap instead
    val imageSize = remember { mutableStateOf(Offset.Zero) } //The ship image size
    val density = LocalDensity.current //Allocated the variable density, converting from the density points to pixels for the offest
    val currentDensity2 = with (density) {34.dp.toPx() } //Calculates the length of the cell for the offset
    val isRotated = remember { mutableStateOf(false) }




    Scaffold  { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color(0xFF4287F5))
            ) {

            Box(
                modifier = Modifier
                    .size(
                        width = 380.dp,
                        height = 400.dp
                    )
                    .padding(20.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .align(Alignment.TopCenter)
            ){
                Text(text = "Name: ",
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    color = Color.White
                )
                Text(text = "Score: 0-0",
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp ),
                    color = Color.White,

                )

            }
            Box (modifier = Modifier
                .align(Alignment.BottomCenter)
                .pointerInput (Unit) {
                    detectTapGestures (
                        onTap = {
                            tapOffset ->
                            Log.d("test","testt")
                            for (ship in ships){
                                if (ship.isShipSelectedState) {
                                    Log.d("test","test2")

                                    ship.offsetState.value = tapOffset - touchOffset.value
                                    Log.d("test",ship.shipName.toString())
                                    calculatePosition(
                                        ship,currentDensity2
                                    )
                                    ship.isShipSelectedState= false
                                }
                                /*
                                if (ship.isShipSelectedState) {

                                    //Checks if the ship is taped, if it is place it in the middle of where the other tap is

                                    //This code might need refactoring if we are dealing with many ships instead!!!
                                    //Todo --> Make the ship rotatable and re-rendering the image
                                    ship.offsetState = tapOffset - touchOffset.value
                                    calculatePosition(
                                        mutableStateOf(ship.offsetState) ,currentDensity2,
                                        imageRotation = mutableStateOf(imageRotation)
                                    )
                                    ship.isShipSelectedState = false
                                }

                                 */
                            }
                            
                        }
                    )


                }

            ) {
                //Rendering the cells
                Column (
                    modifier = Modifier
                        .size(
                            width = 380.dp,
                            height = 420.dp
                        )
                        .padding(20.dp)
                        .padding(bottom = 40.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .align(Alignment.BottomCenter)
                ) {
                    for (i in 0 until 10) {
                        Row {
                            for (j in 0 until 10) {
                                Box (
                                    modifier = Modifier
                                        .border(width = 1.dp, color = Color.White)
                                        .width(34.dp)
                                        .height(34.dp)

                                )
                            }
                        }
                    }

                }

                //Rendering the first ship
                for (ship in ships) {
                    DisplaySvg(context, ship,
                        offset = imagePosition , isVectorSelected = isVectorSelected,
                        touchOffset = touchOffset, imageSize = imageSize,
                        imageRotation = mutableFloatStateOf(imageRotation) , rotationAnimation = rotationAnimation, currentDensity2,
                        imagePosition, isRotated)
                    if (ship.isShipSelectedState){
                        Button(modifier = Modifier
                            .align(Alignment.BottomStart),

                            onClick = {
                                ship.rotationState += 90
                                isRotated.value = !isRotated.value

                            }
                        ) { Text    (text = "Rotate")
                        }
                    }
                }
/*
                if (isVectorSelected.value){
                    Button(modifier = Modifier
                        .align(Alignment.BottomStart),

                        onClick = {
                            imageRotation += 90
                            isRotated.value = !isRotated.value

                        }
                    ) { Text    (text = "Rotate")
                    }
                }

 */

            }

        }
    }

}


//Render the ship based off its name
@SuppressLint("SuspiciousIndentation")
@Composable
fun DisplaySvg(context: Context, ship : ShipType,
               offset: MutableState<Offset>, isVectorSelected: MutableState <Boolean>,
               touchOffset : MutableState <Offset>, imageSize : MutableState <Offset>
               , imageRotation : MutableState <Float>, rotationAnimation : Float, currentDensity2 : Float,
               imagePosition: MutableState <Offset>, isRotated : MutableState<Boolean>) {
    val size = 34 * ship.length
    val imageLoader = ImageLoader.Builder(context)

        .components {
            add(SvgDecoder.Factory())
        }
        .build()
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/${ship.shipName}.svg")
                .build(),
            contentDescription = "Small Ship",
            imageLoader = imageLoader,
            modifier = Modifier
                //.width((size*2).dp)
                .height((size*1.6).dp)
                .padding(10.dp)
                .offset{
                    IntOffset(
                        x = ship.offsetState.value.x.roundToInt(),
                        y = ship.offsetState.value.y.roundToInt()
                    )
                }
                //Assign the size of the current image
                .onGloballyPositioned { layoutCoordinates ->
                    imageSize.value = Offset (
                        x = layoutCoordinates.size.width.toFloat(),
                        y = layoutCoordinates.size.height.toFloat()
                    )

                }

                .graphicsLayer {
                    rotationZ =  imageRotation.value
                }
                .pointerInput(Unit) {
                    detectTapGestures(


                        //Sends the calculating variable of center of the image to be placed at the center later on as well
                        onTap = { tapOffset ->
                            Log.d ("test", ship.shipName)
                            whatever(
                                imagePosition = imagePosition,
                                tapOffset = tapOffset,
                                imageSize = imageSize,
                                imageRotation = imageRotation,
                                touchOffset = touchOffset,
                                shipType = ship
                            )
                            /*
                            if (ship.isShipSelectedState) {
                                if (ship.rotationState > 0) {
                                    ship.offsetState = Offset(
                                        (ship.offsetState.x - ( tapOffset.y -imageSize.value.y /2)),
                                        ship.offsetState.y
                                    )
                                }else{
                                    ship.offsetState = Offset(
                                        (ship.offsetState.x ),
                                        ship.offsetState.y + (tapOffset.y -imageSize.value.y /2)
                                    )
                                }
                                ship.isShipSelectedState = false
                            } else {
                                if ((ship.rotationState % 180) > 0) {
                                    touchOffset.value = Offset(
                                        x = tapOffset.x + imageSize.value.y / 2,
                                        y = tapOffset.x + imageSize.value.x / 2

                                    )
                                } else {
                                    touchOffset.value = Offset(
                                        x = imageSize.value.x / 2,
                                        y = imageSize.value.y / 2

                                    )
                                }
                                ship.isShipSelectedState = true
                            }
                            */
                        }
                    )
                }

        )
}

//align ship in cells After conversion from density point to normal pixels
fun calculatePosition (ship : ShipType, spacings : Float, ) {
    //Todo --> The padding logic might need to change depending on the ship!
    val spacingX = spacings.roundToInt()
    var offsetX = ship.offsetState.value.x.roundToInt()
    var offsetY = ship.offsetState.value.y.roundToInt()
    if (offsetX > (spacingX * 10)){
        offsetX = (spacingX * 9)
    }
    if (offsetY > (spacingX * 8)){
        offsetY = (spacingX * 7)
    }

    offsetX = offsetX / spacingX
    offsetY = offsetY / spacingX
    offsetX = offsetX * spacingX + (spacingX/15)
    offsetY = offsetY * spacingX
    if ((ship.rotationState % 180 ) > 0){
        offsetY -= spacingX  - (spacingX /2 )
    }
    ship.offsetState.value = Offset (
        x = offsetX.toFloat(), y = offsetY.toFloat()
    )
}


//fun changeShipToOffset


fun whatever ( imagePosition: MutableState<Offset>,
              tapOffset : Offset, imageSize: MutableState<Offset>, imageRotation: MutableState<Float>,
               touchOffset: MutableState<Offset>,
              shipType:ShipType) {

    val isRotated = ((shipType.rotationState % 180) > 0)
    if (shipType.isShipSelectedState) {
        if (isRotated) {
            imagePosition.value = Offset(
                (imagePosition.value.x - ( tapOffset.y -imageSize.value.y /2)),
                imagePosition.value.y
            )
        }else{
            imagePosition.value = Offset(
                (imagePosition.value.x ),
                imagePosition.value.y + (tapOffset.y -imageSize.value.y /2)
            )
        }
        shipType.isShipSelectedState = false
    } else {
        if ((imageRotation.value % 180) > 0) {
            touchOffset.value = Offset(
                x = tapOffset.x + imageSize.value.y / 2,
                y = tapOffset.x + imageSize.value.x / 2

            )
        } else {
            touchOffset.value = Offset(
                x = imageSize.value.x / 2,
                y = imageSize.value.y / 2

            )
        }
        shipType.isShipSelectedState = true
    }
}