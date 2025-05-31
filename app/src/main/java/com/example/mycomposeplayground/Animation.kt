import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateRect
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication.*
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AnimateBox(modifier: Modifier = Modifier) {
    var isClicked by remember {
        mutableStateOf(false)
    }
    val offset by animateIntOffsetAsState(
        targetValue = if (isClicked) IntOffset(100, 100) else IntOffset.Zero,
        label = "animation"
    )
    Box(
        modifier = modifier
            .offset {
                offset
            }
            .size(100.dp)
            .background(color = Color.Red)
            .clickable {
                isClicked = !isClicked
            }
    )
}

enum class BoxState {
    Expanded,
    Collapsed
}

@OptIn(ExperimentalTransitionApi::class)
@Composable
fun TransitionBox(modifier: Modifier = Modifier) {
    var currentState by remember { mutableStateOf(BoxState.Collapsed) }
    val transition = updateTransition(currentState, label = "box state")
    val rect by transition.animateRect(
        transitionSpec = {
            tween(500, easing = FastOutSlowInEasing)
        }
    ) { state ->
        when (state) {
            BoxState.Expanded -> Rect(200f, 200f, 400f, 400f)
            BoxState.Collapsed -> Rect(0f, 0f, 100f, 100f)
        }
    }
    val rotate by transition.animateFloat(
        transitionSpec = {
            tween(500, easing = FastOutSlowInEasing)
        }
    ) { state ->
        when (state) {
            BoxState.Expanded -> 45f
            BoxState.Collapsed -> 0f
        }
    }
    val color by transition.animateColor(
        transitionSpec = {
            when {
                BoxState.Expanded isTransitioningTo BoxState.Collapsed ->
                    spring(stiffness = 50f)

                else ->
                    tween(durationMillis = 500)
            }
        }, label = "color"
    ) { state ->
        when (state) {
            BoxState.Collapsed -> MaterialTheme.colorScheme.primary
            BoxState.Expanded -> MaterialTheme.colorScheme.tertiary
        }
    }
    val shapeOfBox by transition.animateInt(
        transitionSpec = {
            tween(500, easing = FastOutSlowInEasing)
        }
    ) { state ->
        when (state) {
            BoxState.Expanded -> 10
            BoxState.Collapsed -> 50
        }
    }

    Box(
        modifier = modifier
            .offset {
                IntOffset(rect.topLeft.x.toInt(), rect.topLeft.y.toInt())
            }
            .rotate(rotate)
            .size(100.dp)
            .clip(RoundedCornerShape(shapeOfBox))
            .background(color = color)
            .clickable {
                currentState = when (currentState) {
                    BoxState.Expanded -> BoxState.Collapsed
                    else -> BoxState.Expanded
                }
            }
    )
}

@Composable
fun AnimateText(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1000, delayMillis = 100), RepeatMode.Reverse)
    )
    Text(
        text = "UTKARSH AND ATHARV",
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin.Center
            },
        style = LocalTextStyle.current.copy(
            textMotion = TextMotion.Animated
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun GestureAnimation(modifier: Modifier = Modifier) {
    var translationY = remember {
        Animatable(initialValue = 0f)
    }
    val scope = rememberCoroutineScope()
    val draggableState = rememberDraggableState { dragAmount ->
        scope.launch {
            translationY.snapTo(translationY.value + dragAmount)
        }
    }
    val maxHeight = 500f

    translationY.updateBounds(-maxHeight, maxHeight)
    val decay = rememberSplineBasedDecay<Float>()
    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer {
                this.translationY = translationY.value
                // val scale = androidx.compose.ui.util.lerp(1f,0.8f,translationX.value/0.1f)
                //this.scaleX = scale
                //this.scaleY = scale
            }
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    val decayY = decay.calculateTargetValue(
                        translationY.value,
                        velocity
                    )
                    scope.launch {
                        val targetY = if (decayY > 0f) maxHeight else -maxHeight
                        val canReachTargetWithDecay = (decayY > targetY && targetY == maxHeight)
                                || (decayY < targetY && targetY == -maxHeight)
                        if (canReachTargetWithDecay)
                            translationY.animateDecay(
                                initialVelocity = velocity,
                                animationSpec = decay
                            )
                        else
                            translationY.animateTo(targetY, initialVelocity = velocity)
                    }
                })
            .background(color = Color.Blue)
    )
}

enum class DragState {
    DRAGGED_DOWN,
    INITIAL,
    DRAGGED_UP
}
/*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GestureAnimationSimpler(modifier: Modifier = Modifier) {
    val maxHeight = 500f
    val anchors = DraggableAnchors {
        DragState.DRAGGED_DOWN at maxHeight
        DragState.INITIAL at 0f
        DragState.DRAGGED_UP at -maxHeight
    }
    val density = LocalDensity.current
    val state : AnchoredDraggableState<DragState> = remember {
        AnchoredDraggableState(
            initialValue = DragState.INITIAL,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.7f },
            velocityThreshold = { with(density) { 80.dp.toPx() } }
        )
    }
    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer {
                this.translationY = state.requireOffset()
            }
            .anchoredDraggable(state, Orientation.Vertical)
            .background(color = Color.Magenta)
    )

}*/

@Composable
fun DraggingAllAround(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(500.dp)
            .background(Color.LightGray)
    ) {
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                }
                .background(Color.Yellow)
                .size(100.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, amount ->
                        change.consume()
                        offsetX += amount.x
                        offsetY += amount.y
                    }
                }
        ) {
            Text("offset -> x : $offsetX , y : $offsetY")
        }
    }
}

@Composable
fun InterruptDragAnimation(modifier: Modifier = Modifier) {
    var offset = remember {
        Animatable(Offset(0f, 0f), Offset.VectorConverter)
    }
    Box(
        modifier = modifier
            .size(500.dp)
            .background(Color.Gray.copy(alpha = 0.5f))
    ) {
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .background(Color.Green)
                .size(100.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, amount ->
                        change.consume()
                        offsetX += amount.x
                        offsetY += amount.y
                    }
                    coroutineScope {
                        while (true) {
                            awaitPointerEventScope {
                                val position = awaitFirstDown().position
                                launch {
                                    offset.animateTo(position)
                                }
                            }
                        }
                    }
                }
        ) {

        }
    }
}

@Composable
fun CustomPointerIcon(modifier: Modifier = Modifier) {
    Column(modifier.pointerHoverIcon(PointerIcon.Crosshair)) {
        SelectionContainer {
            Column {
                Text("Selectable text")
                Text(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand, true),
                    text = "Selectable text with hand"
                )
            }
        }
        Text("Just text with global pointerIcon")
    }
}

@Composable
fun PressIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        modifier = modifier,
        interactionSource = interactionSource
    ) {
        AnimatedVisibility(visible = isPressed) {
            if (isPressed) {
                Row {
                    icon()
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                }
            }
        }
        text()
    }
}

@Composable
fun UsagePressIconButton(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hold for a sec on the Below button to see the magic")
        PressIconButton(
            modifier = modifier,
            onClick = {},
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
            text = { Text("Add to cart") }
        )
    }
}

