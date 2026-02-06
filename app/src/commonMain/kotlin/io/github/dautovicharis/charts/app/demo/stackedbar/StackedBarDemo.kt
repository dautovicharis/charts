package io.github.dautovicharis.charts.app.demo.stackedbar

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
import io.github.dautovicharis.charts.StackedBarChart
import io.github.dautovicharis.charts.app.demo.ChartViewDemoStyle
import io.github.dautovicharis.charts.app.ui.composable.ChartDemo
import io.github.dautovicharis.charts.app.ui.theme.LocalChartColors
import io.github.dautovicharis.charts.app.ui.theme.seriesColors
import io.github.dautovicharis.charts.style.StackedBarChartDefaults
import io.github.dautovicharis.charts.style.StackedBarChartStyle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val LIVE_UPDATE_INTERVAL_MS = 2000L

object StackedBarDemoStyle {
    @Composable
    fun custom(barColors: List<Color>): StackedBarChartStyle {
        return StackedBarChartDefaults.style(
            barColors = barColors,
            chartViewStyle = ChartViewDemoStyle.custom()
        )
    }
}

@Composable
fun StackedBarCustomDemo(viewModel: StackedBarChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val chartColors = LocalChartColors.current
    val barColors = remember(dataSet.segmentKeys, chartColors) {
        chartColors.seriesColors(dataSet.segmentKeys)
    }

    LaunchedEffect(Unit) {
        viewModel.regenerateDataSet()
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
        styleItems = StackedBarChartStyleItems.custom(barColors),
        onRefresh = viewModel::regenerateDataSet,
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
        StackedBarChart(
            dataSet = dataSet.dataSet,
            style = StackedBarDemoStyle.custom(barColors)
        )
    }
}
