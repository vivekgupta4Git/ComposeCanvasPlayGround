package com.example.mycomposeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mycomposeplayground.ui.theme.MyComposePlayGroundTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposePlayGroundTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    // color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val newDp = remember {
        mutableStateOf(0.dp)
    }

    val density = LocalDensity.current

    Text(
        text = "Hello $name!",
        modifier = modifier
            .onPlaced {
                val xV = with(density) { it.positionInParent().x.toDp() }
                println("\uD83C\uDF4F position in dp = $xV ")
                println(
                    "🍏 $name onPlaced() " +
                            "Size = ${it.size} \n" +
                            "positionInParent: ${it.positionInParent().x} ${it.positionInParent().y}\n"
                )
                val myPosition = it.positionInParent().x
                val myPositionInDp = with(density) { myPosition.toDp() }
                newDp.value = 20.dp + myPositionInDp
                //  println( "MyPosition = ${myPosition.value} and in dp = ${with(density){ myPosition.value.toDp() }}")
                println("New Dp = ${newDp.value} before that myPositionInDp = $myPositionInDp")
            }
            .offset(x = newDp.value)
    )
}

@Composable
fun CanVasPlay() {
    val density = LocalDensity.current

    Canvas(
        modifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .background(Color.White)
            .fillMaxSize()
            .height(60.dp)
    ) {

        /*  drawLine(
              start = Offset(x=10f,y=30f),
              end = Offset(x= size.width-10f,y=30f),
              color = Color.Blue
          )*/

          drawLine(
              brush = Brush.linearGradient(
                  colors = listOf(Color.Red,Color.Blue),
              //    tileMode = TileMode.Repeated,
                  end = Offset(size.width-10f,30f)
              ) ,
              start = Offset(10f,30f),
              end = Offset(size.width-10f,30f),
              strokeWidth = 20f,

          )
          drawLine(
              brush = Brush.radialGradient(
                  colors = listOf(Color.Red, Color.Green),
                  center = Offset(size.width/2,60f)
              ),
              start = Offset(x = 100f, y = 60f),
              end = Offset(x = size.width - 100f, y = 60f),
              strokeWidth = 20f,
          )

          drawLine(
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f,10f)),
              start = Offset(10f,90f),
              end = Offset(size.width-10f,90f),
              color = Color.Blue,
              strokeWidth = 5f
          )
          drawLine(
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f,10f), phase = 25f),
              start = Offset(10f,120f),
              end = Offset(size.width-10f,120f),
              color = Color.Blue,
              strokeWidth = 5f
          )
        val path = Path().apply {
            moveTo(10f, 100f)
            lineTo(500f, 100f)
            arcTo(
                Rect(450f, 100f, 550f, 200f),
                sweepAngleDegrees = -180f,
                forceMoveTo = false,
                startAngleDegrees = 90f
            )
            // arcTo(Rect(50f,100f,150f,200f), sweepAngleDegrees = 180f, startAngleDegrees = -90f, forceMoveTo = false)
            lineTo(500f, 200f)
            lineTo(10f, 200f)
            // lineTo(50f,500f)

        }
        drawPath(path, color = Color.Red, style = Fill)
        val newPath = Path().apply {
            arcTo(
                Rect(50f, 250f, 150f, 350f),
                sweepAngleDegrees = 359f,
                startAngleDegrees = 0f,
                forceMoveTo = false
            )
        }
        drawPath(newPath, color = Color.Blue, style = Fill)

        val iconSizeInDp = 80.dp
        val iconSizeInPx = with(density){ iconSizeInDp.toPx()}
        val horizontalMargin = with(density){ 10.dp.toPx()}
        val contentHeight = with(density){ 100.dp.toPx()}
        val curvePath = Path().apply {
            /*quadraticBezierTo(
                x1 =size.width,
                x2 = 0f,
                y1 = size.height,
                y2 = size.height
            )*/
         //  moveTo(0f,2000f)
       //     cubicTo(x3 = size.width, y3 = 0f,
         //       x1 = size.width/2, y1 = 0f,
          //      x2 = size.width/2, y2 = size.height)
            moveTo(horizontalMargin,size.height/2)
            lineTo(size.width/2 - iconSizeInPx/2,size.height/2)
              quadraticBezierTo(
                x2 = size.width/2 -horizontalMargin+ iconSizeInPx/2,
              y2 =  size.height/2,
             x1 = size.width/2 -horizontalMargin,
             y1 = size.height/2 - iconSizeInPx/2,
            )
           // moveTo(size.width/2 + 200f, size.height/2)
            lineTo(size.width -horizontalMargin,size.height/2)
            lineTo(size.width -horizontalMargin,size.height/2 + contentHeight)
            lineTo(horizontalMargin,size.height/2 + contentHeight)
            close()


        }
        //   drawLine(Color.Green, start =  Offset(100f,0f), end = Offset(100f,300f), strokeWidth = 2f)
        drawPath(curvePath, color = Color.Black, style = Stroke(width = 2f))

        drawCircle(Color.Blue, radius = 100f,Offset(200f,500f), style = Fill)
        drawOval(Color.Red, Offset(200f,700f), style = Fill,size= Size(40f,80f))
        drawLine(Color.Green, start = Offset(200f,0f), end =Offset(200f, y = 500f))
        drawLine(Color.Green, start = Offset(0f,500f), end = Offset(200f,500f))
        drawCircle(color = Color.Magenta, radius = 100f, Offset(300f,500f),
        style = Stroke(
            width = 5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f,10f), phase = 100f),
            join = StrokeJoin.Bevel,
            cap = StrokeCap.Butt
        ))
        val path3 = Path().apply {
            moveTo(10f, 0f)
            lineTo(20f, 10f)
            lineTo(10f, 20f)
            lineTo(0f, 10f)
        }
        val pathEffect = PathEffect.stampedPathEffect(
            shape = newPath,
            advance = 140f,
            phase = 0f,
            style = StampedPathEffectStyle.Morph
        )
        drawCircle(
            color = Color.Red,
            radius = 100.dp.toPx(),
            center = Offset(300f, 800f),
            style = Stroke(
                width = 5.dp.toPx(),
                join = StrokeJoin.Miter,
                cap = StrokeCap.Butt,
                pathEffect = pathEffect
            )
        )

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyComposePlayGroundTheme {
        CanVasPlay()
    }
}