package io.github.dautovicharis.charts.app.demo.pie

import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.data.PieSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PieChartState(
    val dataSet: ChartDataSet,
    val segmentKeys: List<String> = emptyList()
)

class PieChartViewModel(
    private val pieSampleUseCase: PieSampleUseCase
) : ViewModel() {

    companion object {
        private const val CHART_TITLE = "Pie Chart"
        private const val POSTFIX = " °C"
    }

    private val _dataSet = MutableStateFlow(
        pieSampleUseCase.initialPieSample(
            title = CHART_TITLE,
            postfix = POSTFIX
        ).let { sample ->
            PieChartState(
                dataSet = sample.dataSet,
                segmentKeys = sample.segmentKeys
            )
        }
    )

    val dataSet: StateFlow<PieChartState> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun regenerateDefaultDataSet(range: IntRange = 10..100, numOfPoints: IntRange = 5..15) {
        val sample = pieSampleUseCase.pieSample(
            range = range,
            numOfPoints = numOfPoints,
            title = CHART_TITLE,
            postfix = POSTFIX
        )
        _dataSet.update {
            it.copy(
                dataSet = sample.dataSet,
                segmentKeys = sample.segmentKeys
            )
        }
    }

    fun regenerateCustomDataSet(range: IntRange = 10..1000) {
        val sample = pieSampleUseCase.pieCustomSample(
            range = range,
            title = CHART_TITLE,
            postfix = POSTFIX
        )
        _dataSet.update {
            it.copy(
                dataSet = sample.dataSet,
                segmentKeys = sample.segmentKeys
            )
        }
    }

    fun togglePlaying() {
        _isPlaying.update { !it }
    }
}
