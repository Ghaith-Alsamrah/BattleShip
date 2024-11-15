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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
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


data class ship (
    var sizeX : Int,
    var sizeY : Int
    )



@Composable
fun MainGame (navController: NavController) {
    val context = LocalContext.current
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    val ships = Array(10) { row ->
        Array(10) { col ->
        }
    }

    val cells = Array(10) { row ->
        Array(10) {col ->
            val isEmpty : Boolean = true
        }
    }

    val imagePosition = remember { mutableStateOf(Offset(100f, 100f)) }
    val isVectorSelected = remember { mutableStateOf(false) }
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
                                imagePosition.value = tapOffset
                                isVectorSelected.value = false
                            }
                        }
                    )


                }

            ) {
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
                                        .onSizeChanged { size -> boxSize = size }

                                ){

                                }
                            }
                        }
                    }

                }
                DisplaySvg(context, "test1",
                    offset = imagePosition , isVectorSelected = isVectorSelected)
            }

        }
    }

}

@Composable
fun DisplaySvg(context: Context, shipType : String,
               offset: MutableState<Offset>, isVectorSelected: MutableState <Boolean>) {

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
                .width(50.dp)
                .height(150.dp)
                .offset{
                    IntOffset(
                        x = offset.value.x.roundToInt(), y = offset.value.y.roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures (
                        onTap = {
                            isVectorSelected.value = true

                        }
                    )

                }
        )


}