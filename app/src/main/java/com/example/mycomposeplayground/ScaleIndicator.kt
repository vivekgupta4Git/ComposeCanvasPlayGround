package com.example.mycomposeplayground

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//custom Indication
private class ScaleIndicationNode(
    private val interactionSource: InteractionSource,
    private val scaleFactor: Float
) : Modifier.Node(), DrawModifierNode {

    var currentPressPosition: Offset = Offset.Zero
    val animatedScalePercent = Animatable(1f)

    private suspend fun animateToPressed(pressPosition: Offset) {
        currentPressPosition = pressPosition
        animatedScalePercent.animateTo(scaleFactor, spring())
    }

    private suspend fun animateToResting() {
        animatedScalePercent.animateTo(1f, spring())
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> animateToPressed(interaction.pressPosition)
                    is PressInteraction.Release -> animateToResting()
                    is PressInteraction.Cancel -> animateToResting()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        scale(
            scale = animatedScalePercent.value,
            pivot = currentPressPosition
        ) {
            this@draw.drawContent()
        }
    }
}

@Stable
class ScaleAnimationFactory(private val scaleBy: Float) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return ScaleIndicationNode(interactionSource, scaleFactor = scaleBy)
    }

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this
}

@Stable
fun scaleIndication(scaleBy: Float = 2f): IndicationNodeFactory {
    return ScaleAnimationFactory(scaleBy)
}

@Composable
fun ScaleComposable(modifier: Modifier = Modifier) {
    // This InteractionSource will emit hover, focus, and press interactions
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier
            .width(300.dp)
            .clickable(
                onClick = {},
                interactionSource = interactionSource,

                // Also show a scale effect
                indication = scaleIndication()
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(10.dp))
        , contentAlignment = Alignment.Center
    ) {
        Text(
            "Hello!", color = Color.Red, modifier = Modifier
        )

    }

}

@Composable
fun PressAndHover(modifier: Modifier = Modifier) {
    val pressInteractionSource = remember { MutableInteractionSource() }
    val hoverInteractionSource = remember { MutableInteractionSource() }
    val isPressed = pressInteractionSource.collectIsPressedAsState()
    val isHovered = hoverInteractionSource.collectIsHoveredAsState()
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { /* do something */ }, interactionSource = pressInteractionSource) {
            Text(if (isPressed.value) "Pressed!" else "Not pressed")
        }
        Button(onClick = {}, interactionSource = hoverInteractionSource) {
            Text(if (isHovered.value) "Hovered !" else "Not Hovered")
        }
    }
}