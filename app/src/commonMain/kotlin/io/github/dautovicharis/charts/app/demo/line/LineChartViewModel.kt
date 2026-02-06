package io.github.dautovicharis.charts.app.demo.line

import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.data.LineSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LineChartViewModel(
    private val lineSampleUseCase: LineSampleUseCase,
) : ViewModel() {
    companion object {
        private const val CHART_TITLE = "Line Chart"
    }

    private val _dataSet =
        MutableStateFlow(
            lineSampleUseCase.initialLineDataSet(title = CHART_TITLE),
        )

    val dataSet: StateFlow<ChartDataSet> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun regenerateDataSet(
        range: IntRange = 10..100,
        numOfPoints: IntRange = 5..15,
    ) {
        _dataSet.value =
            lineSampleUseCase.lineDataSet(
                range = range,
                numOfPoints = numOfPoints,
                title = CHART_TITLE,
            )
    }

    fun togglePlaying() {
        _isPlaying.update { !it }
    }
}
