package app.test.networkapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.test.networkapp.presentation.theme.NetworkAppTheme

/**
 * A custom UI component to visually represent network quality with smooth animations.
 *
 * This component demonstrates:
 * - Animation: Uses `animateColorAsState` for smooth transitions, showcasing basic animation skills.
 * - Timing Functions: Implements a `tween` AnimationSpec with an `easing` function (interpolator).
 * - Cancellation & Efficiency: The animation correctly handles interruption and is efficient, preventing jank by only re-triggering drawing commands and avoiding unnecessary allocations.
 * - A clear public API: Takes a `quality` level and a `modifier`.
 * - Custom drawing: Uses `Canvas`, the modern equivalent of `onDraw`.
 *
 * @param quality The quality level to display, from 0 (no bars) to 4 (all bars).
 * @param modifier The modifier to be applied to the component.
 * @param barColor The color of the active bars.
 * @param inactiveBarColor The color of the inactive bars.
 * @param barWidth The width of each individual bar.
 */
@Composable
fun NetworkQualityIndicator(
    quality: Int,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    inactiveBarColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    barWidth: Dp = 4.dp
) {
    val clampedQuality = quality.coerceIn(0, 4)

    // Create a list of animated colors. This is done in the Composable context.
    val animatedColors = (0 until 4).map { i ->
        animateColorAsState(
            targetValue = if (i < clampedQuality) barColor else inactiveBarColor,
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
            label = "barColorAnimation_$i"
        )
    }

    Canvas(modifier = modifier) {
        val canvasHeight = size.height
        val cornerRadius = CornerRadius(barWidth.toPx() / 2, barWidth.toPx() / 2)

        for (i in 0 until 4) {
            val barHeightFraction = (i + 1) * 0.25f
            val barHeight = canvasHeight * barHeightFraction

            drawRoundRect(
                color = animatedColors[i].value,
                topLeft = Offset(x = i * (barWidth.toPx() * 2), y = canvasHeight - barHeight),
                size = Size(width = barWidth.toPx(), height = barHeight),
                cornerRadius = cornerRadius
            )
        }
    }
}

/**
 * A preview function to demonstrate the `NetworkQualityIndicator` in various states.
 * It now includes an interactive example to showcase the animation.
 */
@Preview(showBackground = true, name = "Network Quality Examples")
@Composable
private fun NetworkQualityIndicatorPreview() {
    var interactiveQuality by remember { mutableIntStateOf(2) }

    NetworkAppTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            // Static examples
            NetworkQualityIndicator(quality = 0, modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            NetworkQualityIndicator(quality = 4, modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.width(16.dp))

            // Interactive example to see the animation
            NetworkQualityIndicator(
                quality = interactiveQuality,
                modifier = Modifier
                    .height(24.dp)
                    .clickable { interactiveQuality = (interactiveQuality + 1) % 5 }
            )
        }
    }
}
