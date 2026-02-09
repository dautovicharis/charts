package io.github.dautovicharis.charts.app.demo.stackedarea

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
import io.github.dautovicharis.charts.StackedAreaChart
import io.github.dautovicharis.charts.app.demo.ChartViewDemoStyle
import io.github.dautovicharis.charts.app.ui.composable.ChartDemo
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.app.ui.theme.LocalChartColors
import io.github.dautovicharis.charts.app.ui.theme.seriesColors
import io.github.dautovicharis.charts.style.StackedAreaChartDefaults
import io.github.dautovicharis.charts.style.StackedAreaChartStyle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val LIVE_UPDATE_INTERVAL_MS = 2000L

object StackedAreaDemoStyle {
    @Composable
    fun default(): StackedAreaChartStyle {
        return StackedAreaChartDefaults.style(
            chartViewStyle = ChartViewDemoStyle.custom(),
        )
    }

    @Composable
    fun custom(areaColors: List<Color>): StackedAreaChartStyle {
        return StackedAreaChartDefaults.style(
            areaColors = areaColors,
            lineColors = areaColors,
            fillAlpha = 0.3f,
            lineVisible = true,
            lineWidth = 3.5f,
            bezier = false,
            chartViewStyle = ChartViewDemoStyle.custom(),
        )
    }
}

@Composable
fun StackedAreaBasicDemo(
    viewModel: StackedAreaChartViewModel = koinViewModel(),
    onStyleItemsChanged: (StyleItems?) -> Unit = {},
) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        viewModel.regenerateDataSet()
        while (true) {
            delay(LIVE_UPDATE_INTERVAL_MS)
            viewModel.regenerateDataSet()
        }
    }

    ChartDemo(
        styleItems = StackedAreaChartStyleItems.default(),
        onRefresh = viewModel::regenerateDataSet,
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
        StackedAreaChart(
            dataSet = dataSet.dataSet,
            style = StackedAreaDemoStyle.default(),
        )
    }
}

@Composable
fun StackedAreaCustomDemo(
    viewModel: StackedAreaChartViewModel = koinViewModel(),
    onStyleItemsChanged: (StyleItems?) -> Unit = {},
) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val chartColors = LocalChartColors.current
    val areaColors =
        remember(dataSet.seriesKeys, chartColors) {
            chartColors.seriesColors(dataSet.seriesKeys.size)
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
        styleItems = StackedAreaChartStyleItems.custom(areaColors),
        onRefresh = viewModel::regenerateDataSet,
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
        StackedAreaChart(
            dataSet = dataSet.dataSet,
            style = StackedAreaDemoStyle.custom(areaColors = areaColors),
        )
    }
}
