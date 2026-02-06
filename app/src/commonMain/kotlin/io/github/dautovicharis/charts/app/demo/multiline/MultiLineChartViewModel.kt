package io.github.dautovicharis.charts.app.demo.multiline

import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.data.MultiLineSampleUseCase
import io.github.dautovicharis.charts.model.MultiChartDataSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MultiLineChartState(
    val dataSet: MultiChartDataSet,
    val seriesKeys: List<String> = emptyList(),
)

class MultiLineChartViewModel(
    private val multiLineSampleUseCase: MultiLineSampleUseCase,
) : ViewModel() {
    companion object {
        private const val CHART_TITLE = "Multiline Chart"
        private const val PREFIX = "$"
    }

    private val _dataSet =
        MutableStateFlow(
            multiLineSampleUseCase.initialMultiLineSample(
                title = CHART_TITLE,
                prefix = PREFIX,
            ).let { sample ->
                MultiLineChartState(
                    dataSet = sample.dataSet,
                    seriesKeys = sample.seriesKeys,
                )
            },
        )

    val dataSet: StateFlow<MultiLineChartState> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun regenerateDataSet(range: IntRange = 100..1000) {
        val sample =
            multiLineSampleUseCase.multiLineSample(
                range = range,
                title = CHART_TITLE,
                prefix = PREFIX,
            )
        _dataSet.value =
            MultiLineChartState(
                dataSet = sample.dataSet,
                seriesKeys = sample.seriesKeys,
            )
    }

    fun togglePlaying() {
        _isPlaying.update { !it }
    }
}
