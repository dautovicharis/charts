package io.github.dautovicharis.charts.internal.common.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.style.ChartViewStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Legend(
    chartViewsStyle: ChartViewStyle,
    legend: ImmutableList<String>,
    colors: ImmutableList<Color>,
    labels: ImmutableList<String> = persistentListOf(),
) {
    FlowRow(
        modifier =
            chartViewsStyle.modifierLegend.animateContentSize(
                animationSpec =
                    tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing,
                    ),
            ),
    ) {
        repeat(legend.size) { index ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(15.dp)
                            .background(colors[index]),
                )

                val label =
                    when (labels.isEmpty()) {
                        true -> legend[index]
                        else -> "${legend[index]} - ${labels[index]}"
                    }

                Text(
                    text = label,
                    color = colors[index],
                    modifier =
                        Modifier.padding(
                            start = chartViewsStyle.innerPadding,
                            end = chartViewsStyle.innerPadding,
                        ),
                )
            }
        }
    }
}
