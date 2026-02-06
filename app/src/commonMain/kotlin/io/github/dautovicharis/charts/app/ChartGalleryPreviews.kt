package io.github.dautovicharis.charts.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.RadarChart
import io.github.dautovicharis.charts.StackedBarChart
import io.github.dautovicharis.charts.app.ui.theme.LocalChartColors
import io.github.dautovicharis.charts.app.ui.theme.seriesColor
import io.github.dautovicharis.charts.app.ui.theme.seriesColors
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.ChartViewStyle
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.PieChartDefaults
import io.github.dautovicharis.charts.style.RadarChartDefaults
import io.github.dautovicharis.charts.style.StackedBarChartDefaults

private val PreviewShape = RoundedCornerShape(18.dp)
private val PreviewChartSize = 140.dp

@Composable
internal fun ChartPreviewFrame(
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val frameBrush = Brush.linearGradient(
        0f to accent.copy(alpha = 0.16f),
        1f to accent.copy(alpha = 0.04f)
    )
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(PreviewShape)
            .then(clickModifier)
            .background(frameBrush)
            .border(1.dp, accent.copy(alpha = 0.18f), PreviewShape)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
internal fun ChartPreview(
    destination: ChartDestination,
    isCustom: Boolean,
    previews: ChartGalleryPreviewState
) {
    when (destination) {
        is ChartDestination.PieChartScreen -> PieChartPreview(previews.pieValues, isCustom)
        is ChartDestination.LineChartScreen -> LineChartPreview(previews.lineValues, isCustom)
        is ChartDestination.MultiLineChartScreen ->
            MultiLineChartPreview(previews.multiLineSeries, isCustom)
        is ChartDestination.BarChartScreen -> {
            if (isCustom) {
                StackedBarChartPreview(previews.stackedSeries)
            } else {
                BarChartPreview(previews.barValues)
            }
        }
        is ChartDestination.RadarChartScreen -> RadarChartPreview(previews.radarSeries, isCustom)
    }
}

@Composable
private fun PieChartPreview(values: List<Float>, isCustom: Boolean) {
    val dataSet = remember(values) {
        values.toChartDataSet(title = "")
    }
    val chartColors = LocalChartColors.current
    val colors = remember(chartColors) {
        chartColors.seriesColors(4)
    }
    val style = if (isCustom) {
        PieChartDefaults.style(
            pieColors = colors,
            donutPercentage = 40f,
            borderWidth = 5f,
            borderColor = chartColors.tooltipOnSurface,
            chartViewStyle = previewChartViewStyle()
        )
    } else {
        PieChartDefaults.style(
            chartViewStyle = previewChartViewStyle()
        )
    }
    PieChart(
        dataSet = dataSet,
        style = style,
        interactionEnabled = false,
        animateOnStart = false
    )
}

@Composable
private fun LineChartPreview(values: List<Float>, isCustom: Boolean) {
    val dataSet = remember(values) {
        values.toChartDataSet(title = "")
    }
    val chartColors = LocalChartColors.current
    val style = if (isCustom) {
        LineChartDefaults.style(
            lineColor = chartColors.seriesColor(1),
            pointColor = chartColors.highlight,
            pointSize = 9f,
            bezier = false,
            dragPointColor = chartColors.selection,
            dragPointVisible = false,
            dragPointSize = 8f,
            dragActivePointSize = 15f,
            chartViewStyle = previewChartViewStyle()
        )
    } else {
        LineChartDefaults.style(
            chartViewStyle = previewChartViewStyle()
        )
    }
    LineChart(
        dataSet = dataSet,
        style = style,
        interactionEnabled = false,
        animateOnStart = false
    )
}

@Composable
private fun MultiLineChartPreview(series: List<Pair<String, List<Float>>>, isCustom: Boolean) {
    val dataSet = remember(series) {
        series.toMultiChartDataSet(title = "")
    }
    val chartColors = LocalChartColors.current
    val seriesKeys = remember(series) {
        series.map { it.first }
    }
    val colors = remember(seriesKeys, chartColors) {
        chartColors.seriesColors(seriesKeys)
    }
    val style = if (isCustom) {
        LineChartDefaults.style(
            lineColors = colors,
            dragPointVisible = false,
            pointVisible = true,
            bezier = false,
            pointColor = chartColors.highlight,
            dragPointColor = chartColors.selection,
            chartViewStyle = previewChartViewStyle()
        )
    } else {
        LineChartDefaults.style(
            chartViewStyle = previewChartViewStyle()
        )
    }
    LineChart(
        dataSet = dataSet,
        style = style,
        interactionEnabled = false,
        animateOnStart = false
    )
}

@Composable
private fun BarChartPreview(values: List<Float>) {
    val dataSet = remember(values) {
        values.toChartDataSet(title = "")
    }
    BarChart(
        dataSet = dataSet,
        style = BarChartDefaults.style(
            minValue = 0f,
            maxValue = 100f,
            chartViewStyle = previewChartViewStyle()
        ),
        interactionEnabled = false,
        animateOnStart = false
    )
}

@Composable
private fun StackedBarChartPreview(series: List<Pair<String, List<Float>>>) {
    val segmentKeys = remember { listOf("Jan", "Feb", "Mar") }
    val dataSet = remember(series) {
        series.toMultiChartDataSet(title = "")
    }
    val chartColors = LocalChartColors.current
    val colors = remember(segmentKeys, chartColors) {
        chartColors.seriesColors(segmentKeys)
    }
    StackedBarChart(
        dataSet = dataSet,
        style = StackedBarChartDefaults.style(
            barColors = colors,
            chartViewStyle = previewChartViewStyle()
        ),
        interactionEnabled = false,
        animateOnStart = false
    )
}

@Composable
private fun RadarChartPreview(
    series: List<Pair<String, List<Float>>>,
    isCustom: Boolean
) {
    val categories = remember {
        listOf("Speed", "Strength", "Agility", "Stamina", "Skill", "Luck")
    }
    val previewSeries = remember(series, isCustom) {
        if (!isCustom || series.size > 1) {
            series
        } else {
            val baseValues = series.firstOrNull()?.second
                ?: listOf(78f, 62f, 90f, 55f, 70f, 80f)
            val normalized = if (baseValues.size == categories.size) {
                baseValues
            } else {
                categories.mapIndexed { index, _ -> baseValues.getOrElse(index) { 70f } }
            }
            val tiger = normalized.map { (it * 0.88f).coerceIn(30f, 100f) }
            val octane = normalized.map { (it * 1.05f).coerceIn(30f, 100f) }
            listOf(
                "Falcon" to normalized,
                "Tiger" to tiger,
                "Octane" to octane
            )
        }
    }
    val chartColors = LocalChartColors.current
    val seriesKeys = remember(previewSeries) {
        previewSeries.map { it.first }
    }
    val lineColors = remember(seriesKeys, chartColors) {
        chartColors.seriesColors(seriesKeys)
    }
    val dataSet = remember(previewSeries) {
        previewSeries.toMultiChartDataSet(
            title = "",
            categories = categories
        )
    }
    val style = if (isCustom) {
        RadarChartDefaults.style(
            lineColors = lineColors,
            lineWidth = 3.5f,
            pointColor = chartColors.highlight,
            pointSize = 7f,
            gridColor = chartColors.gridLine,
            gridSteps = 6,
            gridLineWidth = 1.4f,
            axisLineColor = chartColors.axisLine,
            axisLineWidth = 1.2f,
            axisLabelColor = chartColors.axisLabel,
            fillAlpha = 0.2f,
            categoryLegendVisible = false,
            chartViewStyle = previewChartViewStyle()
        )
    } else {
        RadarChartDefaults.style(
            chartViewStyle = previewChartViewStyle()
        )
    }

    RadarChart(
        dataSet = dataSet,
        style = style,
        interactionEnabled = false,
        animateOnStart = false
    )
}

@Composable
private fun previewChartViewStyle(): ChartViewStyle {
    return ChartViewDefaults.style(
        width = PreviewChartSize,
        outerPadding = 0.dp,
        innerPadding = 4.dp,
        cornerRadius = 14.dp,
        shadow = 0.dp,
        backgroundColor = Color.Transparent
    )
}

@Composable
internal fun chartAccent(item: ChartDestination): Color {
    val scheme = MaterialTheme.colorScheme
    val accents = remember(scheme) {
        listOf(
            scheme.primary,
            scheme.secondary,
            scheme.tertiary,
            lerp(scheme.primary, scheme.secondary, 0.45f),
            lerp(scheme.tertiary, scheme.primary, 0.45f)
        )
    }
    return when (item) {
        is ChartDestination.PieChartScreen -> accents[0]
        is ChartDestination.LineChartScreen -> accents[1]
        is ChartDestination.MultiLineChartScreen -> accents[2]
        is ChartDestination.BarChartScreen -> accents[3]
        is ChartDestination.RadarChartScreen -> accents[4]
    }
}
