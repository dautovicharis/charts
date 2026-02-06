package io.github.dautovicharis.charts.app.demo.radar

import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.data.RadarSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.MultiChartDataSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RadarChartState(
    val basicDataSet: ChartDataSet,
    val customDataSet: MultiChartDataSet,
    val seriesKeys: List<String> = emptyList(),
)

class RadarChartViewModel(
    private val radarSampleUseCase: RadarSampleUseCase,
) : ViewModel() {
    companion object {
        private const val CHART_TITLE = "Radar Chart"
    }

    private val _dataSet =
        MutableStateFlow(
            radarSampleUseCase.initialRadarSample(
                title = CHART_TITLE,
            ).let { sample ->
                RadarChartState(
                    basicDataSet = sample.basicDataSet,
                    customDataSet = sample.customDataSet,
                    seriesKeys = sample.seriesKeys,
                )
            },
        )

    val dataSet: StateFlow<RadarChartState> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun regenerateBasicDataSet(range: IntRange = 30..100) {
        val dataSet =
            radarSampleUseCase.radarBasicDataSet(
                range = range,
                title = CHART_TITLE,
            )
        _dataSet.update {
            it.copy(basicDataSet = dataSet)
        }
    }

    fun regenerateCustomDataSet(range: IntRange = 30..100) {
        val sample =
            radarSampleUseCase.radarCustomSample(
                range = range,
                title = CHART_TITLE,
            )
        _dataSet.update {
            it.copy(
                customDataSet = sample.dataSet,
                seriesKeys = sample.seriesKeys,
            )
        }
    }

    fun togglePlaying() {
        _isPlaying.update { !it }
    }
}
