package io.github.dautovicharis.charts.app.demo.bar

import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.data.BarSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BarChartControlsState(
    val points: Int,
    val minValue: Int,
    val maxValue: Int,
)

class BarChartViewModel(
    private val barSampleUseCase: BarSampleUseCase,
) : ViewModel() {
    companion object {
        private const val CHART_TITLE = "Bar Chart"
        const val MIN_SUPPORTED_POINTS = 10
        const val MAX_SUPPORTED_POINTS = 500
        const val MIN_SUPPORTED_VALUE = -500
        const val MAX_SUPPORTED_VALUE = 500
        const val DEFAULT_POINTS = 120
        const val DEFAULT_MIN_VALUE = -100
        const val DEFAULT_MAX_VALUE = 100
    }

    private val _dataSet =
        MutableStateFlow(
            barSampleUseCase.barDataSet(
                title = CHART_TITLE,
                points = DEFAULT_POINTS,
            ),
        )

    val dataSet: StateFlow<ChartDataSet> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    private val _controlsState =
        MutableStateFlow(
            BarChartControlsState(
                points = DEFAULT_POINTS,
                minValue = DEFAULT_MIN_VALUE,
                maxValue = DEFAULT_MAX_VALUE,
            ),
        )
    val controlsState: StateFlow<BarChartControlsState> = _controlsState.asStateFlow()

    fun regenerateDataSet(
        points: Int = _controlsState.value.points,
        range: IntRange = _controlsState.value.minValue.._controlsState.value.maxValue,
    ) {
        val safePoints =
            points.coerceIn(
                minimumValue = MIN_SUPPORTED_POINTS,
                maximumValue = MAX_SUPPORTED_POINTS,
            )
        val safeRangeStart = range.first.coerceIn(MIN_SUPPORTED_VALUE, MAX_SUPPORTED_VALUE)
        val safeRangeEnd = range.last.coerceIn(safeRangeStart, MAX_SUPPORTED_VALUE)
        _dataSet.value =
            barSampleUseCase.barDataSet(
                title = CHART_TITLE,
                points = safePoints,
                range = safeRangeStart..safeRangeEnd,
            )
    }

    fun updateDataPoints(points: Int) {
        val controls = _controlsState.value
        val safePoints = points.coerceIn(MIN_SUPPORTED_POINTS, MAX_SUPPORTED_POINTS)
        if (safePoints == controls.points) return

        _controlsState.value = controls.copy(points = safePoints)
        regenerateDataSet(
            points = safePoints,
            range = controls.minValue..controls.maxValue,
        )
    }

    fun updateDataRange(
        minValue: Int,
        maxValue: Int,
    ) {
        val safeMin = minValue.coerceIn(MIN_SUPPORTED_VALUE, MAX_SUPPORTED_VALUE)
        val safeMax = maxValue.coerceIn(safeMin, MAX_SUPPORTED_VALUE)
        val controls = _controlsState.value

        if (
            controls.minValue == safeMin &&
            controls.maxValue == safeMax
        ) {
            return
        }

        _controlsState.value =
            controls.copy(
                minValue = safeMin,
                maxValue = safeMax,
            )
        regenerateDataSet(
            points = controls.points,
            range = safeMin..safeMax,
        )
    }

    fun togglePlaying() {
        _isPlaying.update { !it }
    }
}
