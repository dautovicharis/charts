package io.github.dautovicharis.charts.app.demo.line

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.cd_pause_live_updates
import chartsproject.app.generated.resources.cd_play_live_updates
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.app.demo.ChartViewDemoStyle
import io.github.dautovicharis.charts.app.ui.composable.ChartDemo
import io.github.dautovicharis.charts.app.ui.theme.LocalChartColors
import io.github.dautovicharis.charts.app.ui.theme.seriesColor
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.LineChartStyle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val LIVE_UPDATE_INTERVAL_MS = 2000L
private val LIVE_LINE_POINTS_RANGE = 9..9

object LineDemoStyle {

    @Composable
    fun default(): LineChartStyle {
        return LineChartDefaults.style(chartViewStyle = ChartViewDemoStyle.custom())
    }

    @Composable
    fun custom(): LineChartStyle {
        val chartColors = LocalChartColors.current
        return LineChartDefaults.style(
            lineColor = chartColors.seriesColor(1),
            pointColor = chartColors.highlight,
            pointSize = 9f,
            bezier = false,
            dragPointColor = chartColors.selection,
            dragPointVisible = false,
            dragPointSize = 8f,
            dragActivePointSize = 15f,
            chartViewStyle = ChartViewDemoStyle.custom()
        )
    }
}

@Composable
fun LineChartBasicDemo(viewModel: LineChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    val refresh: () -> Unit = viewModel::regenerateDataSet
    val liveRefresh: () -> Unit = {
        viewModel.regenerateDataSet(numOfPoints = LIVE_LINE_POINTS_RANGE)
    }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            liveRefresh()
        }
    }

    ChartDemo(
        styleItems = LineChartStyleItems.default(),
        onRefresh = refresh,
        extraButtons = {
            IconButton(
                onClick = viewModel::togglePlaying
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = stringResource(
                        if (isPlaying) Res.string.cd_pause_live_updates else Res.string.cd_play_live_updates
                    )
                )
            }
        }
    ) {
        LineChart(
            dataSet = dataSet,
            style = LineDemoStyle.default()
        )
    }
}

@Composable
fun LineChartCustomDemo(viewModel: LineChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    val refresh: () -> Unit = viewModel::regenerateDataSet
    val liveRefresh: () -> Unit = {
        viewModel.regenerateDataSet(numOfPoints = LIVE_LINE_POINTS_RANGE)
    }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            liveRefresh()
        }
    }

    ChartDemo(
        styleItems = LineChartStyleItems.custom(),
        onRefresh = refresh,
        extraButtons = {
            IconButton(
                onClick = viewModel::togglePlaying
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = stringResource(
                        if (isPlaying) Res.string.cd_pause_live_updates else Res.string.cd_play_live_updates
                    )
                )
            }
        }
    ) {
        LineChart(
            dataSet = dataSet,
            style = LineDemoStyle.custom()
        )
    }
}
