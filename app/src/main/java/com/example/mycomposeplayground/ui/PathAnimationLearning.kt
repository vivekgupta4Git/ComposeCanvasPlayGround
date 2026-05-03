package com.example.mycomposeplayground.ui

import android.R
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mycomposeplayground.ui.theme.Purple40
import com.example.mycomposeplayground.ui.theme.PurpleGrey40
import kotlinx.coroutines.launch

@Composable
fun PathAnimationLearning(modifier: Modifier = Modifier) {
    val path = Path().apply {
        moveTo(30f,30f)
        lineTo(30f,70f)
        lineTo(70f,70f)
        relativeLineTo(-20f,-20f)
        relativeLineTo(20f,-20f)
        close()
    }
    val progress = remember {
        Animatable(0f)
    }
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(500))
        }
    },modifier = Modifier.padding(25.dp)) {
        Text("Start Animation")
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        inset(100f){
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(Purple40, PurpleGrey40)
                ),
            )
        }
        inset(200f){
           //
            path.reset()
            path.quadraticTo(
                x1 = 250f, y1 = 30f,
                x2 = 300f,y2 = 0f
            )

            drawPath(path, color = Color.Blue)
        }
        inset(300f){
            path.reset()
            path.cubicTo(
                x1 = 410f,
                y1= 20f,
                x2 =40f,
                y2= 80f,
                x3= 400f,
                y3= 0f
            )
            drawPath(path, color = Color.Blue)

        }
        path.reset()
        path.addOval(
            oval = Rect(
                topLeft = center - Offset(300f,400f),
                bottomRight = center + Offset(300f,400f)
            )
        )
        //drawPath(path,Color.Red)

        inset(100f,400f){
            path.reset()
            path.moveTo(110f,475f)
            path.quadraticTo(450f,-350f,480f,475f)
            val shape = Path().apply {
                addOval(
                    oval = Rect(Offset.Zero,40f)
                )
            }
            val pathMeasure = PathMeasure().apply {
                setPath(path,false)
            }
            drawPath(
                path = path,
                color = Color.Red,
                style = Stroke(
                    width = 2f,
                /*    pathEffect = PathEffect.stampedPathEffect(
                        shape = shape,
                        advance = 85f,
                        phase = 0f,
                        style = StampedPathEffectStyle.Translate
                    )*/
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(
                            pathMeasure.length * progress.value,
                            pathMeasure.length
                        ),
                        phase = 2f
                    )
                )
            )
            drawCircle(
                color = Purple40,
                center = pathMeasure.getPosition(pathMeasure.length * progress.value),
                radius = 50f
            )
        }


    }
}




@Preview(showBackground = true)
@Composable
private fun PreviewPathAnimation() {
    PathAnimationLearning()
}