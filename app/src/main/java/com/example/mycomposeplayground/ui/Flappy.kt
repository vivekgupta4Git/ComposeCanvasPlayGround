package com.example.mycomposeplayground.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.android.awaitFrame
import kotlin.random.Random

@Composable
fun FlappyMiniGame(
    modifier: Modifier = Modifier,
    skyColor: Color = Color(0xFFB3E5FC),
    pipeColor: Color = Color(0xFF4CAF50),
    groundColor: Color = Color(0xFF8D6E63),
    birdColor: Color = Color(0xFFFFC107),
) {
    val worldWidth = 360f

    data class Pipe(var x: Float, val gapCenterY: Float, val gapHalf: Float)

    var running by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    var birdY by remember { mutableFloatStateOf(0f) }
    var birdVel by remember { mutableFloatStateOf(0f) }

    val pipes = remember { mutableStateListOf<Pipe>() }

    // Difficulty / physics
    val gravity = 900f
    val flapVelocity = -320f
    var scrollSpeed by remember { mutableFloatStateOf(110f) } // units/sec
    var spawnEveryPx by remember { mutableFloatStateOf(220f) }
    val minGap = 120f
    val maxGap = 170f
    val groundHWorld = 28f

    var canvasWidthPx by remember { mutableFloatStateOf(0f) }
    var canvasHeightPx by remember { mutableFloatStateOf(0f) }

    fun worldHeight(): Float {
        val scale = if (worldWidth == 0f) 1f else canvasWidthPx / worldWidth
        return if (scale == 0f) 0f else canvasHeightPx / scale
    }

    fun reset() {
        val h = worldHeight()
        running = false
        gameOver = false
        score = 0
        birdY = if (h > 0f) h * 0.5f else 0f
        birdVel = 0f
        pipes.clear()
        scrollSpeed = 110f
        spawnEveryPx = 220f
    }

    LaunchedEffect(canvasWidthPx, canvasHeightPx) {
        if (canvasWidthPx > 0f && canvasHeightPx > 0f) reset()
    }

    LaunchedEffect(running, gameOver, canvasWidthPx, canvasHeightPx) {
        if (!running || gameOver || canvasWidthPx == 0f || canvasHeightPx == 0f) return@LaunchedEffect

        var distanceSinceSpawn = 0f
        val h = worldHeight()

        while (running && !gameOver) {
            awaitFrame() // rough ~60fps
            val dt = 1f / 60f

            birdVel += gravity * dt
            birdY += birdVel * dt

            val groundTop = h - groundHWorld
            if (birdY < 0f) {
                birdY = 0f
                birdVel = 0f
                gameOver = true
            }
            if (birdY > groundTop) {
                birdY = groundTop
                gameOver = true
            }

            val dx = scrollSpeed * dt
            distanceSinceSpawn += dx
            if (distanceSinceSpawn >= spawnEveryPx) {
                distanceSinceSpawn = 0f
                val gapHalf = Random.nextFloat() * (maxGap - minGap) / 2f + (minGap / 2f)
                val margin = 24f
                val center =
                    Random.nextFloat() * (h - groundHWorld - 2 * (gapHalf + margin)) + (gapHalf + margin)
                pipes.add(Pipe(x = worldWidth + 40f, gapCenterY = center, gapHalf = gapHalf))

                // Gradually increase difficulty
                scrollSpeed = (scrollSpeed + 0.8f).coerceAtMost(260f)
                spawnEveryPx = (spawnEveryPx - 0.6f).coerceAtLeast(150f)
            }

            val iterator = pipes.listIterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.x -= dx
                if (p.x + 30f < 0f) iterator.remove()
            }

            val birdX = worldWidth * 0.28f
            val birdR = 10f
            pipes.forEach { p ->
                val pipeW = 40f
                val gapTop = p.gapCenterY - p.gapHalf
                val gapBottom = p.gapCenterY + p.gapHalf

                val justPassed = (p.x + pipeW < birdX) && (p.x + pipeW >= birdX - scrollSpeed * dt)
                if (justPassed) score += 1

                val nearHorizontally = birdX + birdR > p.x && birdX - birdR < p.x + pipeW
                val outsideGap = birdY - birdR < gapTop || birdY + birdR > gapBottom
                if (nearHorizontally && outsideGap) gameOver = true
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(skyColor)
            .onSizeChanged { size ->
                canvasWidthPx = size.width.toFloat()
                canvasHeightPx = size.height.toFloat()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (!running) running = true
                        if (gameOver) {
                            reset()
                            return@detectTapGestures
                        }
                        birdVel = flapVelocity
                    },
                    onLongPress = {
                        if (!running) running = true
                        if (gameOver) {
                            reset()
                            return@detectTapGestures
                        }
                        birdVel = flapVelocity
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scale = if (worldWidth == 0f) 1f else size.width / worldWidth
            val groundHPx = groundHWorld * scale

            drawRect(
                color = groundColor,
                topLeft = Offset(0f, size.height - groundHPx),
                size = androidx.compose.ui.geometry.Size(size.width, groundHPx)
            )

            val pipeW = 40f * scale
            pipes.forEach { p ->
                val xPx = p.x * scale
                val gapTopPx = (p.gapCenterY - p.gapHalf) * scale
                val gapBottomPx = (p.gapCenterY + p.gapHalf) * scale

                drawRect(
                    color = pipeColor,
                    topLeft = Offset(xPx, 0f),
                    size = androidx.compose.ui.geometry.Size(pipeW, gapTopPx)
                )
                drawRect(
                    color = pipeColor,
                    topLeft = Offset(xPx, gapBottomPx),
                    size = androidx.compose.ui.geometry.Size(
                        pipeW,
                        size.height - gapBottomPx - groundHPx
                    )
                )
            }

            val birdX = worldWidth * 0.28f * scale
            val birdR = 10f * scale
            drawCircle(
                color = birdColor,
                radius = birdR,
                center = Offset(birdX, birdY * scale)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            ScoreChip(score)
        }

        if (!running && !gameOver) CenterOverlay(text = "Tap to start")
        if (gameOver) CenterOverlay(
            text = "Game Over Tap to retry "
        )
    }
}

@Composable
private fun ScoreChip(score: Int) {
    Surface(
        modifier = Modifier.padding(top = 20.dp),
        color = Color.Black.copy(alpha = 0.35f),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = score.toString(),
            fontSize = 28.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun CenterOverlay(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            )
        }
    }
}