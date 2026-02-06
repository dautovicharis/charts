package io.github.dautovicharis.charts.app.demo.stackedbar

import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.data.StackedBarSampleUseCase
import io.github.dautovicharis.charts.model.MultiChartDataSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StackedBarChartState(
    val dataSet: MultiChartDataSet,
    val segmentKeys: List<String> = emptyList()
)

class StackedBarChartViewModel(
    private val stackedBarSampleUseCase: StackedBarSampleUseCase
) : ViewModel() {

    companion object {
        private const val CHART_TITLE = "Stacked Bar Chart"
        private const val PREFIX = "$"
    }

    private val _dataSet = MutableStateFlow(
        stackedBarSampleUseCase.initialStackedBarSample(
            title = CHART_TITLE,
            prefix = PREFIX
        ).let { sample ->
            StackedBarChartState(
                dataSet = sample.dataSet,
                segmentKeys = sample.segmentKeys
            )
        }
    )

    val dataSet: StateFlow<StackedBarChartState> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun regenerateDataSet(range: IntRange = 100..1000) {
        val sample = stackedBarSampleUseCase.stackedBarSample(
            range = range,
            title = CHART_TITLE,
            prefix = PREFIX
        )
        _dataSet.value = StackedBarChartState(
            dataSet = sample.dataSet,
            segmentKeys = sample.segmentKeys
        )
    }

    fun togglePlaying() {
        _isPlaying.update { !it }
    }
}
