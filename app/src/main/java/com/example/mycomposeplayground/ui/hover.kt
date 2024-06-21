package com.example.mycomposeplayground.ui

/**
 *@author Vivek Gupta on
 */
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun MyApp() {
//    // When the magnifier center position is Unspecified, it is hidden.
//// Hide the magnifier until a drag starts.
//    var magnifierCenter by remember { mutableStateOf(Offset.Unspecified) }
//
//    if (Build.VERSION.SDK_INT < 28) {
//        Text("Magnifier is not supported on this platform.")
//    } else {
//        Box(
//            Modifier
//                .fillMaxSize()
//                .magnifier(
//                    sourceCenter = { magnifierCenter },
//                    zoom = 4f,
//                    style = MagnifierStyle(
//                        size = DpSize(100.dp,200.dp), cornerRadius = 100.dp
//                    )
//                )
//                .pointerInput(Unit) {
//                    detectDragGestures(
//                        // Show the magnifier at the original pointer position.
//                        onDragStart = { magnifierCenter = it },
//                        // Make the magnifier follow the finger while dragging.
//                        onDrag = { _, delta -> magnifierCenter += delta },
//                        // Hide the magnifier when the finger lifts.
//                        onDragEnd = { magnifierCenter = Offset.Unspecified },
//                        onDragCancel = { magnifierCenter = Offset.Unspecified }
//                    )
//                }
//                .drawBehind {
//                    // Some concentric circles to zoom in on.
//                    for (diameter in 2 until size.maxDimension.toInt() step 10) {
//                        drawCircle(
//                            color = Color.Black,
//                            radius = diameter / 2f,
//                            style = Stroke()
//                        )
//                    }
//                },
//            contentAlignment = Alignment.Center
//        ){
//            Text(text = "My content will be magnified by this box")
//        }
//    }
//}
class CustomShape(private  val roundedCorner : Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val pxRoundCorner = with(density){ roundedCorner.toPx()}
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f,pxRoundCorner)
            lineTo(0f, size.height-pxRoundCorner)
            quadraticBezierTo(x2 = pxRoundCorner, y2 = size.height, y1 = size.height, x1 = 0f)
            lineTo(size.width-pxRoundCorner, size.height)
            quadraticBezierTo(x2 = pxRoundCorner, y2 = size.height, y1 = size.height, x1 = size.width)

            lineTo(size.width, (size.height / 2.5).toFloat())
            close()

        }
        return Outline.Generic(path)
    }
}