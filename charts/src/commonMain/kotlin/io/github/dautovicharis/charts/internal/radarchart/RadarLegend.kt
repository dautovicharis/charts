package io.github.dautovicharis.charts.internal.radarchart

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.style.ChartViewStyle
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RadarLegend(
    chartViewsStyle: ChartViewStyle,
    series: ImmutableList<String>,
    seriesColors: ImmutableList<Color>,
    categories: ImmutableList<String>,
    categoryColors: ImmutableList<Color>
) {
    Column(
        modifier = chartViewsStyle.modifierLegend.animateContentSize(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            )
        )
    ) {
        if (series.isNotEmpty()) {
            LegendTitle("Series")
            Spacer(modifier = Modifier.height(chartViewsStyle.innerPadding / 2f))
            LegendItems(
                items = series,
                colors = seriesColors,
                fallbackColor = MaterialTheme.colorScheme.primary,
                itemPadding = chartViewsStyle.innerPadding
            )
        }

        if (categories.isNotEmpty()) {
            if (series.isNotEmpty()) {
                Spacer(modifier = Modifier.height(chartViewsStyle.innerPadding / 2f))
            }
            LegendTitle("Categories")
            Spacer(modifier = Modifier.height(chartViewsStyle.innerPadding / 2f))
            val neutral = MaterialTheme.colorScheme.onSurfaceVariant
            LegendItems(
                items = categories,
                colors = categoryColors,
                fallbackColor = neutral,
                itemPadding = chartViewsStyle.innerPadding
            )
        }
    }
}

@Composable
private fun LegendTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LegendItems(
    items: ImmutableList<String>,
    colors: ImmutableList<Color>?,
    fallbackColor: Color,
    itemPadding: androidx.compose.ui.unit.Dp
) {
    FlowRow {
        items.forEachIndexed { index, label ->
            val color = colors?.getOrNull(index) ?: fallbackColor
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    end = itemPadding,
                    bottom = itemPadding / 2f
                )
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color, shape = CircleShape)
                )
                Text(
                    text = label,
                    color = color,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}
