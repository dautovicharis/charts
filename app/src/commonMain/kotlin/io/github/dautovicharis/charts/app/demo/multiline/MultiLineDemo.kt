package io.github.dautovicharis.charts.app.demo.multiline

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
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.app.demo.ChartViewDemoStyle
import io.github.dautovicharis.charts.app.ui.composable.ChartDemo
import io.github.dautovicharis.charts.app.ui.theme.LocalChartColors
import io.github.dautovicharis.charts.app.ui.theme.seriesColors
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.LineChartStyle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val LIVE_UPDATE_INTERVAL_MS = 2000L

object MultiLineDemoStyle {
    @Composable
    fun default(): LineChartStyle {
        return LineChartDefaults.style(chartViewStyle = ChartViewDemoStyle.custom())
    }

    @Composable
    fun custom(lineColors: List<Color>): LineChartStyle {
        val chartColors = LocalChartColors.current
        return LineChartDefaults.style(
            lineColors = lineColors,
            dragPointVisible = false,
            pointVisible = true,
            bezier = false,
            pointColor = chartColors.highlight,
            dragPointColor = chartColors.selection,
            chartViewStyle = ChartViewDemoStyle.custom(),
        )
    }
}

@Composable
fun MultiLineBasicDemo(viewModel: MultiLineChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    val refresh: () -> Unit = viewModel::regenerateDataSet
    val liveRefresh: () -> Unit = viewModel::regenerateDataSet

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        liveRefresh()
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            liveRefresh()
        }
    }

    ChartDemo(
        styleItems = MultiLineStyleItems.default(),
        onRefresh = refresh,
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
        LineChart(
            dataSet = dataSet.dataSet,
            style = MultiLineDemoStyle.default(),
        )
    }
}

@Composable
fun MultiLineCustomDemo(viewModel: MultiLineChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val chartColors = LocalChartColors.current
    val lineColors =
        remember(dataSet.seriesKeys, chartColors) {
            chartColors.seriesColors(dataSet.seriesKeys)
        }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        viewModel.regenerateDataSet()
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            viewModel.regenerateDataSet()
        }
    }

    ChartDemo(
        styleItems = MultiLineStyleItems.custom(lineColors),
        onRefresh = viewModel::regenerateDataSet,
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
        LineChart(
            dataSet = dataSet.dataSet,
            style = MultiLineDemoStyle.custom(lineColors = lineColors),
        )
    }
}
