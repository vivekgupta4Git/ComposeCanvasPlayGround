package com.example

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan2

@Composable
fun MorphPath(
    modifier: Modifier = Modifier,
    message: String = "Vivek",
    textStyle: TextStyle = TextStyle(
        color = Magenta,
        fontSize = 64.sp,
        fontWeight = FontWeight.Bold
    )
) {
    val textMeasure = rememberTextMeasurer()
    val textLayoutResult = textMeasure.measure(message,textStyle)
    val pathMeasure = remember { PathMeasure() }
    val textWidth = textLayoutResult.getBoundingBox(message.lastIndex).bottomRight.x
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )
    Canvas(
        modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        //val centerX = width/2f
        val startPoint = Offset(0f, centerY)
        val endPoint = Offset(width, centerY)
        val controlY = lerp(centerY, centerY + 400f, progress)
        val controlY2 = lerp(centerY, centerY - 400f, progress)
        val cp1 = Offset(width / 3, controlY)
        val cp2 = Offset(width * 2 / 3, controlY2)
        val path = Path().apply {
            moveTo(startPoint.x, startPoint.y)
            cubicTo(
                cp1.x, cp1.y,
                cp2.x, cp2.y,
                endPoint.x, endPoint.y
            )
        }
        drawPath(
            path = path,
            color = Magenta,
            style = Stroke(4.dp.toPx(), cap = StrokeCap.Round)
        )
         pathMeasure.setPath(path,false)
        val wordStartAt = pathMeasure.length - textWidth
        message.forEachIndexed { index, ch ->
            val rect = textLayoutResult.getBoundingBox(index)
            val distance = rect.left + (wordStartAt * progress.absoluteValue)
            val pathOffset =pathMeasure.getPosition(distance)
            val rotation = pathMeasure.getTangent(distance).let {tan ->
                (atan2(tan.y,tan.x)* (180/ PI)).toFloat()
            }
            rotate(
                degrees = rotation,
                pivot = pathOffset
            ){
                drawText(
                    textMeasurer = textMeasure,
                    text = ch.toString(),
                    style = textStyle,
                    topLeft = pathOffset - Offset(0f,rect.height/2),
                    size = rect.size
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewMorphPath() {
    MorphPath()
}