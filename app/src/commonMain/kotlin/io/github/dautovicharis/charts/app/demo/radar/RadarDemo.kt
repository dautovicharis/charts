package io.github.dautovicharis.charts.app.demo.radar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.cd_pause_live_updates
import chartsproject.app.generated.resources.cd_play_live_updates
import io.github.dautovicharis.charts.RadarChart
import io.github.dautovicharis.charts.app.demo.ChartViewDemoStyle
import io.github.dautovicharis.charts.app.ui.composable.ChartDemo
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.app.ui.theme.LocalChartColors
import io.github.dautovicharis.charts.app.ui.theme.seriesColors
import io.github.dautovicharis.charts.style.RadarChartDefaults
import io.github.dautovicharis.charts.style.RadarChartStyle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val LIVE_UPDATE_INTERVAL_MS = 2000L

object RadarDemoStyle {
    @Composable
    fun default(): RadarChartStyle {
        return RadarChartDefaults.style(
            chartViewStyle = ChartViewDemoStyle.custom(),
        )
    }

    @Composable
    fun custom(lineColors: List<Color>): RadarChartStyle {
        val chartColors = LocalChartColors.current
        return RadarChartDefaults.style(
            lineColors = lineColors,
            lineWidth = 3.5f,
            pointColor = chartColors.highlight,
            pointSize = 5f,
            gridSteps = 6,
            gridLineWidth = 1.4f,
            axisLineColor = chartColors.axisLine,
            axisLineWidth = 1.2f,
            axisLabelColor = chartColors.axisLabel,
            fillAlpha = 0.2f,
            categoryLegendVisible = false,
            chartViewStyle = ChartViewDemoStyle.custom(),
        )
    }
}

@Composable
fun RadarChartBasicDemo(
    viewModel: RadarChartViewModel = koinViewModel(),
    onStyleItemsChanged: (StyleItems?) -> Unit = {},
) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    val refresh: () -> Unit = {
        viewModel.regenerateBasicDataSet()
    }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            refresh()
        }
    }

    ChartDemo(
        styleItems = RadarChartStyleItems.default(),
        onRefresh = refresh,
        onStyleItemsChanged = onStyleItemsChanged,
        extraButtons = {
            IconButton(
                onClick = viewModel::togglePlaying,
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription =
                        stringResource(
                            if (isPlaying) Res.string.cd_pause_live_updates else Res.string.cd_play_live_updates,
                        ),
                )
            }
        },
    ) {
        RadarChart(
            dataSet = dataSet.basicDataSet,
            style = RadarDemoStyle.default(),
        )
    }
}

@Composable
fun RadarChartCustomDemo(
    viewModel: RadarChartViewModel = koinViewModel(),
    onStyleItemsChanged: (StyleItems?) -> Unit = {},
) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val chartColors = LocalChartColors.current
    val lineColors =
        remember(dataSet.seriesKeys, chartColors) {
            chartColors.seriesColors(dataSet.seriesKeys)
        }

    val refresh: () -> Unit = {
        viewModel.regenerateCustomDataSet()
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            refresh()
        }
    }

    ChartDemo(
        styleItems = RadarChartStyleItems.custom(lineColors),
        onRefresh = refresh,
        onStyleItemsChanged = onStyleItemsChanged,
        extraButtons = {
            IconButton(
                onClick = viewModel::togglePlaying,
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription =
                        stringResource(
                            if (isPlaying) Res.string.cd_pause_live_updates else Res.string.cd_play_live_updates,
                        ),
                )
            }
        },
    ) {
        RadarChart(
            dataSet = dataSet.customDataSet,
            style = RadarDemoStyle.custom(lineColors = lineColors),
        )
    }
}
