package io.github.dautovicharis.charts.app.screenshot.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.dautovicharis.charts.app.ui.theme.AppTheme
import io.github.dautovicharis.charts.app.ui.theme.Theme
import io.github.dautovicharis.charts.app.ui.theme.deepRed
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.MultiChartDataSet
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.BarChartStyle
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.ChartViewStyle
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.LineChartStyle
import io.github.dautovicharis.charts.style.PieChartDefaults
import io.github.dautovicharis.charts.style.PieChartStyle
import io.github.dautovicharis.charts.style.StackedBarChartDefaults
import io.github.dautovicharis.charts.style.StackedBarChartStyle

internal val ScreenshotTheme = Theme(deepRed)

@Composable
internal fun ScreenshotSurface(
    content: @Composable () -> Unit,
) {
    val darkTheme = isSystemInDarkTheme()
    AppTheme(theme = ScreenshotTheme, darkTheme = darkTheme, useDynamicColors = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Composable
internal fun screenshotChartViewStyle(): ChartViewStyle = ChartViewDefaults.style()

@Composable
internal fun screenshotBarStyle(): BarChartStyle = BarChartDefaults.style(
    chartViewStyle = screenshotChartViewStyle()
)

@Composable
internal fun screenshotLineStyle(): LineChartStyle = LineChartDefaults.style(
    chartViewStyle = screenshotChartViewStyle()
)

@Composable
internal fun screenshotStackedStyle(): StackedBarChartStyle = StackedBarChartDefaults.style(
    chartViewStyle = screenshotChartViewStyle()
)

@Composable
internal fun screenshotPieStyle(): PieChartStyle = PieChartDefaults.style(
    chartViewStyle = screenshotChartViewStyle(),
    donutPercentage = 0.45f,
    legendVisible = true
)

internal fun barBasicData(): ChartDataSet =
    listOf(12f, 26f, 18f, 34f, 24f).toChartDataSet(
        title = "Weekly Sales",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri")
    )

internal fun barNegativeData(): ChartDataSet =
    listOf(-12f, 8f, 16f, -6f, 10f).toChartDataSet(
        title = "Net Change",
        labels = listOf("Q1", "Q2", "Q3", "Q4", "Q5")
    )

internal fun lineSingleData(): ChartDataSet =
    listOf(6f, 10f, 8f, 14f, 9f, 16f).toChartDataSet(
        title = "Active Users",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    )

internal fun lineMultiData(): MultiChartDataSet =
    listOf(
        "Alpha" to listOf(8f, 12f, 10f, 14f, 11f, 16f),
        "Beta" to listOf(6f, 9f, 7f, 11f, 8f, 12f),
        "Gamma" to listOf(4f, 7f, 5f, 8f, 6f, 9f)
    ).toMultiChartDataSet(
        title = "Weekly Trends",
        categories = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    )

internal fun stackedBasicData(): MultiChartDataSet =
    listOf(
        "North" to listOf(10f, 12f, 14f, 16f),
        "South" to listOf(8f, 9f, 10f, 11f),
        "East" to listOf(6f, 7f, 8f, 9f)
    ).toMultiChartDataSet(
        title = "Regional Sales",
        categories = listOf("Q1", "Q2", "Q3", "Q4")
    )

internal fun stackedNoCategoriesData(): MultiChartDataSet =
    listOf(
        "Series A" to listOf(9f, 8f, 12f),
        "Series B" to listOf(6f, 5f, 7f),
        "Series C" to listOf(4f, 3f, 5f)
    ).toMultiChartDataSet(
        title = "No Categories"
    )

internal fun pieBasicData(): ChartDataSet =
    listOf(38f, 24f, 16f, 12f, 10f).toChartDataSet(
        title = "Market Share",
        labels = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
    )
