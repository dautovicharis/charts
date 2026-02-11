package io.github.dautovicharis.charts.app.demo.stackedbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dautovicharis.charts.app.data.StackedBarSampleUseCase
import io.github.dautovicharis.charts.model.MultiChartDataSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val LIVE_UPDATE_INTERVAL_MS = 2000L

data class StackedBarChartState(
    val dataSet: MultiChartDataSet,
    val segmentKeys: List<String> = emptyList(),
)

class StackedBarChartViewModel(
    private val stackedBarSampleUseCase: StackedBarSampleUseCase,
) : ViewModel() {
    private val refreshRange = stackedBarSampleUseCase.stackedBarRefreshRange()

    private val _dataSet =
        MutableStateFlow(
            stackedBarSampleUseCase.initialStackedBarSample().let { sample ->
                StackedBarChartState(
                    dataSet = sample.dataSet,
                    segmentKeys = sample.segmentKeys,
                )
            },
        )

    val dataSet: StateFlow<StackedBarChartState> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    private var liveUpdatesJob: Job? = null

    fun regenerateDataSet(range: IntRange = refreshRange) {
        val sample = stackedBarSampleUseCase.stackedBarSample(range = range)
        _dataSet.value =
            StackedBarChartState(
                dataSet = sample.dataSet,
                segmentKeys = sample.segmentKeys,
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
