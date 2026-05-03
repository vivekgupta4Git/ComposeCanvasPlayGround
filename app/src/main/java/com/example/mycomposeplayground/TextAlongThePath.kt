package com.example.mycomposeplayground

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import java.nio.file.Files.size
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2

@Composable
fun AnimateTextAlongPath(message : String = "Hello ! Vivek",
                         textStyle : TextStyle = TextStyle(
                             color = Magenta,
                             fontSize = 64.sp,
                             fontWeight = FontWeight.Bold
                         )
) {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )
    val textMeasure = rememberTextMeasurer()
    val textLayoutResult = textMeasure.measure(text = message, style = textStyle)

    val measurePath = remember { PathMeasure() }
    val textWidth = textLayoutResult.getBoundingBox(message.lastIndex).bottomRight.x
    Canvas(modifier = Modifier
        .fillMaxSize()) {
        val path = Path().apply {
            val w = size.width
            val h = size.height

            // 1. Start at the top left handle
            moveTo(w * 0.05f, h * 0.10f)

            // 2. The First Cross: Move from top-left to middle-right.
            // We use a high control point to keep the top line relatively straight.
            cubicTo(
                x1 = w * 0.40f, y1 = h * 0.15f,
                x2 = w * 0.70f, y2 = h * 0.20f,
                x3 = w * 0.80f, y3 = h * 0.40f // This is the right-side "turn"
            )

            // 3. The Bottom Loop: Sweep all the way to the bottom-left.
            // This creates the wide belly where the green text sits.
            cubicTo(
                x1 = w * 1.00f, y1 = h * 0.80f, // Pulls the curve out to the right
                x2 = w * 0.10f, y2 = h * 1.10f, // Deep pull to the bottom left
                x3 = w * 0.20f, y3 = h * 0.50f // Coming back up to create the left wall
            )

            // 4. The Final Cross: Intersection and Exit.
            // This crosses the first line and heads to the top-right handle.
            cubicTo(
                x1 = w * 0.30f, y1 = h * 0.10f, // Pulls the curve up and across the first line
                x2 = w * 0.60f, y2 = h * 0.05f,
                x3 = w * 0.95f, y3 = h * 0.08f // Final exit point
            )
        }
        drawPath(path = path, color = Color.Gray, style = Stroke(5f))
        measurePath.setPath(path,false)
        val wordStartAt = measurePath.length - textWidth
        message.forEachIndexed { index, ch ->
            val rect = textLayoutResult.getBoundingBox(index)
            val distance = rect.left + (wordStartAt * progress)
            val pathOffset = measurePath.getPosition(distance)
            val rotation = measurePath.getTangent(distance).let {tan ->
                (atan2(tan.y,tan.x) * (180/ PI)).toFloat()
            }
            rotate(
                degrees = rotation,
                pivot = pathOffset
            ){
                drawText(
                    textMeasurer = textMeasure,
                    text = ch.toString(),
                    style = textStyle,
                    topLeft = pathOffset - Offset(0f,rect.height * .5f),
                    size = rect.size
                )
            }
        }

    }

}

@Preview
@Composable
private fun PreviewTextAnimation() {
    AnimateTextAlongPath()
}