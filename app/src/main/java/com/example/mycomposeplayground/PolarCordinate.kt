package com.example.mycomposeplayground

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

fun polarToCartesian(degree: Float, radius: Float, origin: Offset = Offset.Zero): Offset {
    val radians = Math.toRadians(degree.toDouble())
    val x = (radius * cos(-radians)).toFloat()
    val y = (radius * sin(-radians)).toFloat()
    return Offset(x + origin.x, y + origin.y)
}

fun Path.polarMoveTo(degree: Float, radius: Float, origin: Offset = Offset.Zero): Path {
    val target = polarToCartesian(degree, radius, origin)
    moveTo(target.x, target.y)
    return this
}

fun Path.polarLineTo(degree: Float, radius: Float, origin: Offset = Offset.Zero): Path {
    val target = polarToCartesian(degree, radius, origin)
    lineTo(target.x, target.y)
    return this
}

fun Path.polarRelativeLineTo(degree: Float, distance: Float): Path {
    val radians = Math.toRadians(degree.toDouble())
    val dx = (distance * cos(-radians)).toFloat()
    val dy = (distance * sin(-radians)).toFloat()
    relativeLineTo(dx, dy)
    return this
}

@Composable
fun PolarLineExample(modifier: Modifier = Modifier) {
    Canvas(
        modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        val center = Offset(size.width / 2f, size.height / 2f) // Define once

        val path = Path()
            .polarMoveTo(degree = 0f, radius = 0f, origin = center)
            .polarLineTo(degree = 0f, radius = 200f, origin = center)
            .polarRelativeLineTo(40f, 100f)

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(2.dp.toPx())
        )
    }
}


fun createStarPath(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    points: Int = 5
): Path = Path().apply {
    val totalPoints = points * 2
    val degreesPerStep = 360f / totalPoints
    val startAngle = -90f

    for (i in 0 until totalPoints) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = startAngle + (i * degreesPerStep)

        if (i == 0) {
            polarMoveTo(angle, radius, center)
        } else {
            polarLineTo(angle, radius, center)
        }
    }
    close()
}

@Composable
fun StarPath(
    modifier: Modifier = Modifier,
    points: Int,
    outerRadius: Float,
    innerRadius: Float
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val starPath = createStarPath(
            center = center,
            outerRadius = outerRadius,
            innerRadius = innerRadius, // Adjust this for "pointiness"
            points = points
        )

        drawPath(
            path = starPath,
            color = Color.Yellow,
            style = Fill // or Stroke(width = 5f)
        )
    }
}
fun DrawScope.createSpiralPath(
    maxRadius: Float,
    rotations: Int = 5,
    pathColor: Color = Color.Cyan,
    strokeWidth: Dp = 1.dp,
) {
    val path = Path().apply {
        val center = Offset(
            this@createSpiralPath.size.width / 2f,
            this@createSpiralPath.size.height / 2f
        )
        val totalDegrees = rotations * 360f
        val step = 2f// Lower for smoother curve
        // Start at center
        polarMoveTo(0f, 0f, center)

        var currentAngle = 0f
        while (currentAngle <= totalDegrees) {
            // Radius grows as angle increases
            val currentRadius = (currentAngle / totalDegrees) * maxRadius
            polarLineTo(currentAngle, currentRadius, center)
            currentAngle += step
        }
    }
    drawPath(
        path = path,
        color = pathColor,
        style = Stroke(strokeWidth.toPx())
    )
}

fun DrawScope.drawSpiral(
    gap: Dp, // Distance between each ring
    rotations: Int = 5,
    pathColor: Color = Color.Cyan,
    strokeWidth: Dp = 1.dp
) {
    val gapPx = gap.toPx()
    val center = size.center
    val step = 2f

    val path = Path().apply {
        polarMoveTo(0f, 0f, center)

        var currentAngle = 0f
        val totalDegrees = rotations * 360f

        while (currentAngle <= totalDegrees) {
            // Radius calculation based on the gap
            val currentRadius = (currentAngle / 360f) * gapPx

            polarLineTo(currentAngle, currentRadius, center)
            currentAngle += step
        }
    }

    drawPath(
        path = path,
        color = pathColor,
        style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
    )
}


@Preview
@Composable
private fun PreviewPolarLine() {
    StarPath(
        points = 5,
        innerRadius = 500f,
        outerRadius = 200f
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawSpiral(25.dp, rotations = 5, strokeWidth = 15.dp)
    }
    PolarLineExample()
}