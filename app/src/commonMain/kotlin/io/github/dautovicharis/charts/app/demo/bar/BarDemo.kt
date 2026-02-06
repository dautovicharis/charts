package io.github.dautovicharis.charts.app.demo.bar

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
import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.app.demo.ChartViewDemoStyle
import io.github.dautovicharis.charts.app.ui.composable.ChartDemo
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.BarChartStyle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val LIVE_UPDATE_INTERVAL_MS = 2000L
private val LIVE_BAR_POINTS_RANGE = 7..7

object BarDemoStyle {

    @Composable
    fun default(
        minValue: Float? = null,
        maxValue: Float? = null
    ): BarChartStyle {
        return BarChartDefaults.style(
            chartViewStyle = ChartViewDemoStyle.custom(),
            minValue = minValue,
            maxValue = maxValue
        )
    }
}

@Composable
fun BarChartBasicDemo(viewModel: BarChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    val refresh: () -> Unit = viewModel::regenerateDataSet
    val liveRefresh: () -> Unit = {
        viewModel.regenerateDataSet(range = 0..100, numOfPoints = LIVE_BAR_POINTS_RANGE)
    }
    val liveStyle = BarDemoStyle.default(minValue = 0f, maxValue = 100f)
    val defaultStyle = BarDemoStyle.default()

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        liveRefresh()
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            liveRefresh()
        }
    }

    ChartDemo(
        styleItems = BarChartStyleItems.default(),
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
        BarChart(
            dataSet,
            style = if (isPlaying) liveStyle else defaultStyle
        )
    }
}
