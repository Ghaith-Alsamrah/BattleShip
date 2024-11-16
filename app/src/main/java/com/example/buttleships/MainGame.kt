package com.example.buttleships

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ErrorResult
import coil.request.ImageRequest
import kotlin.math.roundToInt

// Todo --> Connect to the database

//On second tap, place boat from the center of the boat to the right squars

@Composable
fun MainGame (navController: NavController) {
    val context = LocalContext.current

    //Todo --> Create an array for the cells with a boolean if occupied or not
    //Todo --> Create an array of object "ships" that contains their sizes and if they are rotated or not
    val ships = Array(10) { row ->
        Array(10) { col ->
        }
    }

    val cells = Array(10) { row ->
        Array(10) {col ->
            val isEmpty : Boolean = true
        }
    }

    val imagePosition = remember { mutableStateOf(Offset(0f, 0f)) } //starting position of the ship
    val isVectorSelected = remember { mutableStateOf(false) } //check if the ship is selected
    val touchOffset = remember { mutableStateOf(Offset.Zero) } //The ship placement from the center of the tap instead
    val imageSize = remember { mutableStateOf(Offset.Zero) } //The ship image size
    val density = LocalDensity.current //Allocated the variable density, converting from the density points to pixels for the offest
    val currentDensity2 = with (density) {34.dp.toPx() } //Calculates the length of the cell for the offset
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
                            if (isVectorSelected.value) {
                                //Checks if the ship is taped, if it is place it in the middle of where the other tap is

                                //This code might need refactoring if we are dealing with many ships instead!!!
                                //Todo --> Make the ship rotatable and re-rendering the image
                                imagePosition.value = tapOffset - touchOffset.value
                                calculatePosition(imagePosition,currentDensity2)
                                isVectorSelected.value = false
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
                DisplaySvg(context, "test1",
                    offset = imagePosition , isVectorSelected = isVectorSelected,
                    touchOffset = touchOffset, imageSize = imageSize)
            }

        }
    }

}


//Render the ship based off its name
@Composable
fun DisplaySvg(context: Context, shipType : String,
               offset: MutableState<Offset>, isVectorSelected: MutableState <Boolean>,
               touchOffset : MutableState <Offset>, imageSize : MutableState <Offset>) {

    val imageLoader = ImageLoader.Builder(context)

        .components {
            add(SvgDecoder.Factory())
        }
        .build()

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/$shipType.svg")
                .build(),
            contentDescription = "Small Ship",
            imageLoader = imageLoader,
            modifier = Modifier
                //Todo --> Creating padding to be placed better
                .width(70.dp)
                .height(135.dp)
                .offset{
                    IntOffset(
                        x = offset.value.x.roundToInt(),
                        y = offset.value.y.roundToInt()
                    )
                }
                //Assign the size of the current image
                .onGloballyPositioned { layoutCoordinates ->
                    imageSize.value = Offset (
                        x = layoutCoordinates.size.width.toFloat(),
                        y = layoutCoordinates.size.height.toFloat()
                    )

                }
                .pointerInput(Unit) {
                    detectTapGestures (
                        //Sends the calculating variable of center of the image to be placed at the center later on as well
                        onTap = { tapOffset ->
                            touchOffset.value = Offset (
                                x = imageSize.value.x / 2 ,
                                y = imageSize.value.y / 2
                            )
                            isVectorSelected.value = true

                        }
                    )

                }
        )
}

//align ship in cells After conversion from density point to normal pixels
fun calculatePosition (offset : MutableState <Offset> , spacings : Float) {
    val spacingX = spacings.roundToInt()
    var offsetX = offset.value.x.roundToInt()
    var offsetY = offset.value.y.roundToInt()
    if ((offsetX % spacingX)> (spacingX/2)) {
        offsetX = offsetX + spacingX
        }
    if ((offsetY % spacingX)>(spacingX/2)) {
        offsetY = offsetY + spacingX
    }
    if (offsetX > (spacingX * 10)){
        offsetX = (spacingX * 9)
    }
    if (offsetY > (spacingX * 8)){
        offsetY = (spacingX * 7)
    }
    offsetX = offsetX / spacingX
    offsetY = offsetY / spacingX
    offsetX = offsetX * spacingX
    offsetY = offsetY * spacingX
    offset.value = Offset (
        x = offsetX.toFloat(), y = offsetY.toFloat()
    )

}

