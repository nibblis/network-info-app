package app.test.networkapp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
 * A custom UI component to visually represent network quality.
 *
 * This component demonstrates:
 * - A clear public API: It takes a `quality` level and a `modifier`.
 * - A well-defined state contract: The number of active bars directly maps to the `quality` input.
 * - Custom drawing: It uses `Canvas`, the modern equivalent of `onDraw`.
 * - Efficiency: It avoids unnecessary recompositions because its inputs (`Int`, `Modifier`) are stable.
 * - Safety: Being a stateless composable, it's protected against memory leaks.
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

    Canvas(modifier = modifier) {
        val canvasHeight = size.height
        val cornerRadius = CornerRadius(barWidth.toPx() / 2, barWidth.toPx() / 2)

        // Draw 4 bars, coloring them based on the quality level.
        for (i in 0 until 4) {
            val barHeightFraction = (i + 1) * 0.25f
            val barHeight = canvasHeight * barHeightFraction

            drawRoundRect(
                color = if (i < clampedQuality) barColor else inactiveBarColor,
                topLeft = Offset(x = i * (barWidth.toPx() * 2), y = canvasHeight - barHeight),
                size = Size(width = barWidth.toPx(), height = barHeight),
                cornerRadius = cornerRadius
            )
        }
    }
}

/**
 * A preview function to demonstrate the `NetworkQualityIndicator` in various states.
 * This is a great way to test and showcase UI components without running the full app.
 */
@Preview(showBackground = true, name = "Network Quality Examples")
@Composable
private fun NetworkQualityIndicatorPreview() {
    NetworkAppTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            NetworkQualityIndicator(quality = 0, modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            NetworkQualityIndicator(quality = 1, modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            NetworkQualityIndicator(quality = 2, modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            NetworkQualityIndicator(quality = 3, modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            NetworkQualityIndicator(quality = 4, modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            // Example with different size
            NetworkQualityIndicator(quality = 4, modifier = Modifier.size(width = 60.dp, height = 48.dp))
        }
    }
}
