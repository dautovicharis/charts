package io.github.dautovicharis.charts.style

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Immutable
class StackedAreaChartStyle(
    val modifier: Modifier,
    val chartViewStyle: ChartViewStyle,
    val areaColor: Color,
    val areaColors: List<Color>,
    val fillAlpha: Float,
    val lineVisible: Boolean,
    val lineColor: Color,
    val lineColors: List<Color>,
    val lineWidth: Float,
    val bezier: Boolean,
) : Style {
    override fun getProperties(): List<Pair<String, Any>> {
        return listOf(
            StackedAreaChartStyle::areaColor.name to areaColor,
            StackedAreaChartStyle::areaColors.name to areaColors,
            StackedAreaChartStyle::fillAlpha.name to fillAlpha,
            StackedAreaChartStyle::lineVisible.name to lineVisible,
            StackedAreaChartStyle::lineColor.name to lineColor,
            StackedAreaChartStyle::lineColors.name to lineColors,
            StackedAreaChartStyle::lineWidth.name to lineWidth,
            StackedAreaChartStyle::bezier.name to bezier,
        )
    }
}

object StackedAreaChartDefaults {
    @Composable
    fun style(
        areaColor: Color = MaterialTheme.colorScheme.primary,
        areaColors: List<Color> = emptyList(),
        fillAlpha: Float = defaultChartAlpha(),
        lineVisible: Boolean = true,
        lineColor: Color = MaterialTheme.colorScheme.primary,
        lineColors: List<Color> = emptyList(),
        lineWidth: Float = 4f,
        bezier: Boolean = false,
        chartViewStyle: ChartViewStyle = ChartViewDefaults.style(),
    ): StackedAreaChartStyle {
        val padding = chartViewStyle.innerPadding
        val modifier: Modifier =
            Modifier
                .padding(padding)
                .aspectRatio(1f)
                .fillMaxSize()

        return StackedAreaChartStyle(
            modifier = modifier,
            chartViewStyle = chartViewStyle,
            areaColor = areaColor,
            areaColors = areaColors,
            fillAlpha = fillAlpha.coerceIn(0f, 1f),
            lineVisible = lineVisible,
            lineColor = lineColor,
            lineColors = lineColors,
            lineWidth = lineWidth.coerceAtLeast(0f),
            bezier = bezier,
        )
    }
}
