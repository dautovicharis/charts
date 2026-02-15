package ui.stylecontrols

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPaletteControl(
    title: String,
    customColors: List<Color>?,
    itemCount: Int,
    onCustomColorsChange: (List<Color>?) -> Unit,
) {
    val normalizedCount = itemCount.coerceAtLeast(1)
    val selectedColors = customColors?.let { colors -> repeatColors(colors, normalizedCount) }

    StyleSectionHeader(title)
    BooleanToggleControl(
        label = "Use custom palette",
        checked = customColors != null,
        onCheckedChange = { enabled ->
            if (enabled) {
                onCustomColorsChange(repeatColors(DefaultPalettes.first(), normalizedCount))
            } else {
                onCustomColorsChange(null)
            }
        },
    )

    if (customColors == null) {
        Text(
            text = "Using chart defaults.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp),
        )
        return
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DefaultPalettes.forEach { palette ->
            val candidate = repeatColors(palette, normalizedCount)
            val isSelected = selectedColors == candidate
            PaletteRow(
                colors = candidate,
                selected = isSelected,
                onClick = { onCustomColorsChange(candidate) },
            )
        }
    }
}

@Composable
private fun PaletteRow(
    colors: List<Color>,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor =
        if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp),
                ).clickable(onClick = onClick)
                .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        colors.forEach { color ->
            Box(
                modifier =
                    Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .background(color = color, shape = RoundedCornerShape(6.dp)),
            )
        }
    }
}

private fun repeatColors(
    baseColors: List<Color>,
    itemCount: Int,
): List<Color> {
    if (baseColors.isEmpty() || itemCount <= 0) return emptyList()
    return List(itemCount) { index -> baseColors[index % baseColors.size] }
}

private val DefaultPalettes: List<List<Color>> =
    listOf(
        listOf(
            Color(red = 180, green = 167, blue = 214),
            Color(red = 225, green = 170, blue = 87),
            Color(red = 52, green = 195, blue = 186),
            Color(red = 201, green = 67, blue = 186),
        ),
        listOf(
            Color(red = 255, green = 99, blue = 132),
            Color(red = 255, green = 159, blue = 64),
            Color(red = 255, green = 205, blue = 86),
            Color(red = 75, green = 192, blue = 192),
        ),
        listOf(
            Color(red = 113, green = 80, blue = 220),
            Color(red = 45, green = 120, blue = 255),
            Color(red = 3, green = 166, blue = 120),
            Color(red = 239, green = 71, blue = 111),
        ),
        listOf(
            Color(red = 29, green = 53, blue = 87),
            Color(red = 69, green = 123, blue = 157),
            Color(red = 168, green = 218, blue = 220),
            Color(red = 230, green = 57, blue = 70),
        ),
    )
