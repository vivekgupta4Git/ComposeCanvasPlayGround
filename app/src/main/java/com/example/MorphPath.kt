package com.example

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
import kotlin.math.pow

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
    val textLayoutResult = textMeasure.measure(message, textStyle)
    val pathMeasure = remember { PathMeasure() }
    val textWidth = textLayoutResult.getBoundingBox(message.lastIndex).bottomRight.x
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
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
        pathMeasure.setPath(path, false)
        val wordStartAt = pathMeasure.length - textWidth
        message.forEachIndexed { index, ch ->
            val rect = textLayoutResult.getBoundingBox(index)
            val distance = rect.left + (wordStartAt * progress.absoluteValue)
            val pathOffset = pathMeasure.getPosition(distance)
            val rotation = pathMeasure.getTangent(distance).let { tan ->
                (atan2(tan.y, tan.x) * (180 / PI)).toFloat()
            }
            rotate(
                degrees = rotation,
                pivot = pathOffset
            ) {
                drawText(
                    textMeasurer = textMeasure,
                    text = ch.toString(),
                    style = textStyle,
                    topLeft = pathOffset - Offset(0f, rect.height / 2),
                    size = rect.size
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewMorphPath() {
    //MorphPath()
    //MorphVerticalPath()
    BungeeRevealPath()
}

@Composable
fun MorphVerticalPath(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        -1f,
        1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ProgressVertical"
    )
    Canvas(modifier
        .fillMaxSize()
        .padding(20.dp)) {
        val centerX = size.width / 2f
        val height = size.height
        val startPoint = Offset(centerX, 0f)
        val endPoint = Offset(centerX, height)
        val controlAnim1 = lerp(centerX, centerX + 400f, progress)
        val controlAnim2 = lerp(centerX, centerX - 400f, progress)
        val cp1 = Offset(controlAnim1, height / 3)
        val cp2 = Offset(controlAnim2, height * 2 / 3)

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
    }
}

@Composable
fun BungeeRevealPath(message: String = "BOUNCE") {
    val textMeasure = rememberTextMeasurer()
    val textStyle = TextStyle(color = Color.Blue, fontSize = 40.sp, fontWeight = FontWeight.Bold)
    val textLayoutResult = textMeasure.measure(message, textStyle)
    val textWidth = textLayoutResult.size.width.toFloat()

    val infiniteTransition = rememberInfiniteTransition(label = "bungee")

    // 1. The Bungee Progress (Using a Spring for the 'snap' feel)
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                2000,
                easing = CubicBezierEasing(
                    0.7f,
                    -0.5f,
                    0.3f,
                    1.5f
                )
            ),
            /*animation = tween(2000, easing = { fraction ->
                // This is a "Back" easing that overshoots the end (Bungee effect)
                val s =  3.0f//1.70158f
                // We "slow down" the input fraction by squaring it first
                val slowFraction = fraction.pow(2)
                val t = slowFraction - 1.0f
                t * t * ((s + 1) * t + s) + 1.0f
            }),*/
            repeatMode = RepeatMode.Reverse
        ), label = "progress"
    )

    Canvas(modifier = Modifier
        .fillMaxSize()
        .padding(50.dp)) {
        val pathMeasure = PathMeasure()
        val fullPath = Path().apply {
            moveTo(0f, size.height / 2)
            // A static U-shape path
            cubicTo(
                size.width / 4,
                size.height,
                size.width * 3 / 4,
                size.height,
                size.width,
                size.height / 2
            )
        }

        // 2. Reveal Logic: Create a segment of the path based on progress
        pathMeasure.setPath(fullPath, false)
        val partialPath = Path()
        pathMeasure.getSegment(0f, pathMeasure.length * progress, partialPath, true)

        // Draw the "invisible" track (optional)
        drawPath(
            fullPath,
            Color.LightGray,
            style = Stroke(
                2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        )

        // Draw the revealed path
        drawPath(partialPath, Color.Blue, style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))

        // 3. Text follows the revealed tip
        val wordStartAt = (pathMeasure.length * progress) - textWidth

        message.forEachIndexed { index, ch ->
            val rect = textLayoutResult.getBoundingBox(index)
            // Distance is relative to the CURRENT revealed length
            val distance = rect.left + wordStartAt

            // Only draw characters that have "emerged" onto the path
            if (distance > 0f) {
                val position = pathMeasure.getPosition(distance)
                val tangent = pathMeasure.getTangent(distance)
                val rotation = (atan2(tangent.y, tangent.x) * (180 / PI)).toFloat()

                rotate(rotation, pivot = position) {
                    drawText(
                        textMeasurer = textMeasure,
                        text = ch.toString(),
                        style = textStyle,
                        topLeft = position - Offset(0f, rect.height / 2f)
                    )
                }
            }
        }
    }
}