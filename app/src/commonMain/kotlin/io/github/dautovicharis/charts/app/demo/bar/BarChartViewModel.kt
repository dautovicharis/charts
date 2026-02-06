package io.github.dautovicharis.charts.app.demo.bar

import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.data.BarSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BarChartViewModel(
    private val barSampleUseCase: BarSampleUseCase,
) : ViewModel() {
    companion object {
        private const val CHART_TITLE = "Bar Chart"
    }

    private val _dataSet =
        MutableStateFlow(
            barSampleUseCase.initialBarDataSet(title = CHART_TITLE),
        )

    val dataSet: StateFlow<ChartDataSet> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun regenerateDataSet(
        range: IntRange = -100..100,
        numOfPoints: IntRange = 5..15,
    ) {
        _dataSet.value =
            barSampleUseCase.barDataSet(
                range = range,
                numOfPoints = numOfPoints,
                title = CHART_TITLE,
            )
    }

    fun togglePlaying() {
        _isPlaying.update { !it }
    }
}
