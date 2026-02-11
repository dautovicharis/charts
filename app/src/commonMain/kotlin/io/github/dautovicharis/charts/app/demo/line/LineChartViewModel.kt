package io.github.dautovicharis.charts.app.demo.line

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dautovicharis.charts.app.data.LineSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val LIVE_UPDATE_INTERVAL_MS = 2000L

class LineChartViewModel(
    private val lineSampleUseCase: LineSampleUseCase,
) : ViewModel() {
    private val refreshRange = lineSampleUseCase.lineRefreshRange()
    private val refreshPointCountRange =
        lineSampleUseCase.lineRefreshPointsCount().let { count ->
            count..count
        }

    private val _dataSet =
        MutableStateFlow(
            lineSampleUseCase.initialLineDataSet(),
        )

    val dataSet: StateFlow<ChartDataSet> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    private var liveUpdatesJob: Job? = null

    fun regenerateDataSet(
        range: IntRange = refreshRange,
        numOfPoints: IntRange = refreshPointCountRange,
    ) {
        _dataSet.value =
            lineSampleUseCase.lineDataSet(
                range = range,
                numOfPoints = numOfPoints,
            )
    }

    fun refresh() {
        regenerateDataSet()
    }

    fun togglePlaying() {
        val shouldPlay = !_isPlaying.value
        _isPlaying.value = shouldPlay
        if (shouldPlay) {
            startLiveUpdates()
        } else {
            stopLiveUpdates()
        }
    }

    override fun onCleared() {
        stopLiveUpdates()
        super.onCleared()
    }

    private fun startLiveUpdates() {
        liveUpdatesJob?.cancel()
        liveUpdatesJob =
            viewModelScope.launch {
                refresh()
                while (isActive) {
                    delay(LIVE_UPDATE_INTERVAL_MS)
                    refresh()
                }
            }
    }

    private fun stopLiveUpdates() {
        liveUpdatesJob?.cancel()
        liveUpdatesJob = null
    }
}
