package com.example.mycomposeplayground

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

@Composable
fun CubicPath(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val path = Path().apply {
            moveTo(0f, size.height / 3)
            cubicTo(
                size.width.times(1.5f),
                size.height.times(0.2f),
                size.width / 3f,
                size.height.times(1.2f),
                size.width.times(0.19f),
                size.height.times(0.88f)
            )
            quadraticTo(
                x2 = size.width,
                y2 = size.height.times(0.3f),
                x1 = size.width.times(0.1f),
                y1 = size.height/4f
            )

        }
        drawPath(path, style = Stroke(1f), color = Color.Gray)
    }
}

@Preview
@Composable
private fun PreviewCubicPath() {
    Column {
        AnimateTextAlongPath(modifier = Modifier.weight(1f))
        CubicPath(modifier = Modifier.weight(1f))
    }
}