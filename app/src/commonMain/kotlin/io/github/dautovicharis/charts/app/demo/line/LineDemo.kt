package io.github.dautovicharis.charts.app.demo.line

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.cd_pause_live_updates
import chartsproject.app.generated.resources.cd_play_live_updates
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.app.ui.composable.ChartDemo
import io.github.dautovicharis.charts.app.ui.composable.ChartPreset
import io.github.dautovicharis.charts.app.ui.composable.ChartPresetToggle
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.LineChartDefaults
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LineChartDemo(
    viewModel: LineChartViewModel = koinViewModel(),
    onStyleItemsChanged: (StyleItems?) -> Unit = {},
) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    var preset by remember { mutableStateOf(ChartPreset.Default) }

    val refresh: () -> Unit = viewModel::refresh

    val styleItems =
        when (preset) {
            ChartPreset.Default -> lineChartTableItems(LineChartDefaults.style())
            ChartPreset.Custom -> LineChartStyleItems.custom()
        }

    ChartDemo(
        styleItems = styleItems,
        onRefresh = refresh,
        onStyleItemsChanged = onStyleItemsChanged,
        presetContent = {
            ChartPresetToggle(
                selectedPreset = preset,
                onPresetSelected = { preset = it },
            )
        },
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
        when (preset) {
            ChartPreset.Default -> {
                LineChart(
                    dataSet = dataSet,
                )
            }

            ChartPreset.Custom -> {
                LineChart(
                    dataSet = dataSet,
                    style = LineChartStyleItems.customStyle(),
                )
            }
        }
    }
}
