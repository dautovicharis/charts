package io.github.dautovicharis.charts.app.demo.pie

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
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.app.demo.ChartViewDemoStyle
import io.github.dautovicharis.charts.app.ui.composable.ChartDemo
import io.github.dautovicharis.charts.app.ui.theme.LocalChartColors
import io.github.dautovicharis.charts.app.ui.theme.seriesColors
import io.github.dautovicharis.charts.style.PieChartDefaults
import io.github.dautovicharis.charts.style.PieChartStyle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val LIVE_UPDATE_INTERVAL_MS = 2000L
private val LIVE_PIE_POINTS_RANGE = 9..9

object PieChartDemoStyle {
    @Composable
    fun default(): PieChartStyle {
        return PieChartDefaults.style(chartViewStyle = ChartViewDemoStyle.custom())
    }

    @Composable
    fun custom(pieColors: List<Color>): PieChartStyle {
        return PieChartDefaults.style(
            borderColor = MaterialTheme.colorScheme.surface,
            donutPercentage = 40f,
            borderWidth = 5f,
            pieColors = pieColors,
            legendVisible = true,
            chartViewStyle = ChartViewDemoStyle.custom(),
        )
    }
}

@Composable
fun PieChartBasicDemo(viewModel: PieChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    val refresh: () -> Unit = viewModel::regenerateDefaultDataSet
    val liveRefresh: () -> Unit = {
        viewModel.regenerateDefaultDataSet(numOfPoints = LIVE_PIE_POINTS_RANGE)
    }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            liveRefresh()
        }
    }

    ChartDemo(
        styleItems = PieChartStyleItems.default(),
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
        PieChart(
            dataSet = dataSet.dataSet,
            style = PieChartDemoStyle.default(),
        )
    }
}

@Composable
fun PieChartCustomDemo(viewModel: PieChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val chartColors = LocalChartColors.current
    val pieColors =
        remember(dataSet.segmentKeys, chartColors) {
            chartColors.seriesColors(dataSet.segmentKeys)
        }
    LaunchedEffect(Unit) {
        viewModel.regenerateCustomDataSet()
    }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            viewModel.regenerateCustomDataSet()
        }
    }

    ChartDemo(
        styleItems = PieChartStyleItems.custom(pieColors),
        onRefresh = viewModel::regenerateCustomDataSet,
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
        PieChart(
            dataSet = dataSet.dataSet,
            style = PieChartDemoStyle.custom(pieColors),
        )
    }
}
