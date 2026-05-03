package com.example.mycomposeplayground

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// Helper function to create a repeating list for "infinite" scroll illusion
fun <T> List<T>.repeat(count: Int): List<T> {
    if (this.isEmpty() || count <= 0) return emptyList()
    val result = mutableListOf<T>()
    repeat(count) {
        result.addAll(this)
    }
    return result
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfiniteAutoScrollLazyRow(
    items: List<String>,
    modifier: Modifier = Modifier,
    scrollSpeedMs: Long = 50, // Time between scroll steps
    initialScrollIndex: Int = 0,
    repeatedListMultiplier: Int = 200 // How many times to repeat the list
) {
    if (items.isEmpty()) {
        Text("No items to display.", modifier = modifier.padding(16.dp))
        return
    }

    val repeatedItems = remember(items, repeatedListMultiplier) {
        items.repeat(repeatedListMultiplier)
    }

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // MutableState to hold the auto-scroll Job
    var autoScrollJob: Job? by remember { mutableStateOf(null) }

    // Function to start the auto-scroll
    val startAutoScroll: () -> Unit = remember {
        {
            if (autoScrollJob == null || autoScrollJob?.isCompleted == true) {
                autoScrollJob = scope.launch {
                    // Initialize scroll position if needed on first start
                    if (lazyListState.firstVisibleItemIndex == 0 && initialScrollIndex != 0) {
                        lazyListState.scrollToItem(initialScrollIndex)
                    }

                    while (isActive) { // Coroutine is active
                        val currentFirstVisibleItem = lazyListState.firstVisibleItemIndex
                        val currentFirstVisibleItemOffset = lazyListState.firstVisibleItemScrollOffset

                        // Check if we are approaching the end of our repeated list
                        val threshold = repeatedItems.size - items.size * 2 // Jump when two full repeats are left
                        if (currentFirstVisibleItem >= threshold) {
                            val targetIndex = currentFirstVisibleItem % items.size
                            lazyListState.scrollToItem(targetIndex) // Instant jump
                            // Log.d("InfiniteScroll", "Jumped from $currentFirstVisibleItem to $targetIndex")
                        } else {
                            // Smoothly scroll one pixel at a time
                            // This ensures continuous movement even within an item
                            lazyListState.scrollBy(1f) // Scroll by 1 pixel, adjust for faster/slower
                        }
                        delay(scrollSpeedMs) // Control scroll speed
                    }
                }
            }
        }
    }

    // Function to stop/cancel the auto-scroll
    val stopAutoScroll: () -> Unit = remember {
        {
            autoScrollJob?.cancel()
            autoScrollJob = null
        }
    }

    // --- Initial Auto-Scroll Start ---
    // Start auto-scroll once when the composable enters the composition
    LaunchedEffect(Unit) {
        startAutoScroll()
    }

    // --- Pause/Resume on User Interaction (Dragging/Scrolling) ---
    val isUserScrolling by remember { derivedStateOf { lazyListState.isScrollInProgress } }

    LaunchedEffect(isUserScrolling) {
        if (isUserScrolling) {
            // User started scrolling, pause auto-scroll
            stopAutoScroll()
            // Log.d("InfiniteScroll", "User started scrolling, auto-scroll paused.")
        } else {
            // User stopped scrolling, resume auto-scroll after a short delay
            delay(2000) // Adjust this delay as needed, gives user time to read
            // Only resume if the user isn't scrolling anymore and no auto-scroll job is active
            if (!lazyListState.isScrollInProgress && autoScrollJob?.isCompleted == true) {
                startAutoScroll()
                // Log.d("InfiniteScroll", "User stopped scrolling, auto-scroll resumed.")
            }
        }
    }

    // --- Optional: Pause/Resume on User Interaction (Tap/Press) ---
    val pointerInputModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                // Pause auto-scroll when pressed
                stopAutoScroll()
                // Log.d("InfiniteScroll", "Tapped: auto-scroll paused.")
                tryAwaitRelease() // Wait for release event
                // Resume auto-scroll after release, with a slight delay
                delay(500) // Give a moment before resuming
                if (!lazyListState.isScrollInProgress && autoScrollJob?.isCompleted == true) {
                    startAutoScroll()
                    // Log.d("InfiniteScroll", "Released: auto-scroll resumed.")
                }
            }
        )
    }

    LazyRow(
        modifier = modifier.then(pointerInputModifier), // Apply pointer input for tap detection
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        itemsIndexed(repeatedItems, key = { index, _ -> index }) { index, item ->
            Card(
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$item (Idx: ${index % items.size})", // Show original item index
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfiniteAutoScrollLazyRowPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Auto-Scrolling LazyRow (Infinite)",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            InfiniteAutoScrollLazyRow(
                items = listOf("Item A", "Item B", "Item C", "Item D", "Item E"),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(Modifier.height(30.dp))
            Text("Try dragging or tapping the items to pause/resume scroll!")
        }
    }
}