package io.github.dautovicharis.charts.app.demo.stackedarea

import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.data.StackedAreaSampleUseCase
import io.github.dautovicharis.charts.model.MultiChartDataSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StackedAreaChartState(
    val dataSet: MultiChartDataSet,
    val seriesKeys: List<String> = emptyList(),
)

class StackedAreaChartViewModel(
    private val stackedAreaSampleUseCase: StackedAreaSampleUseCase,
) : ViewModel() {
    companion object {
        private const val CHART_TITLE = "Stacked Area Chart"
        private const val PREFIX = "$"
    }

    private val _dataSet =
        MutableStateFlow(
            stackedAreaSampleUseCase.initialStackedAreaSample(
                title = CHART_TITLE,
                prefix = PREFIX,
            ).let { sample ->
                StackedAreaChartState(
                    dataSet = sample.dataSet,
                    seriesKeys = sample.seriesKeys,
                )
            },
        )

    val dataSet: StateFlow<StackedAreaChartState> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun regenerateDataSet(range: IntRange = 100..1000) {
        val sample =
            stackedAreaSampleUseCase.stackedAreaSample(
                range = range,
                title = CHART_TITLE,
                prefix = PREFIX,
            )
        _dataSet.value =
            StackedAreaChartState(
                dataSet = sample.dataSet,
                seriesKeys = sample.seriesKeys,
            )
    }

    fun togglePlaying() {
        _isPlaying.update { !it }
    }
}
