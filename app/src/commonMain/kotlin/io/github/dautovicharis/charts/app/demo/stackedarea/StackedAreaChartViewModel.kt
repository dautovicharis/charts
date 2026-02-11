package io.github.dautovicharis.charts.app.demo.stackedarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dautovicharis.charts.app.data.StackedAreaSampleUseCase
import io.github.dautovicharis.charts.model.MultiChartDataSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val LIVE_UPDATE_INTERVAL_MS = 2000L

data class StackedAreaChartState(
    val dataSet: MultiChartDataSet,
    val seriesKeys: List<String> = emptyList(),
)

class StackedAreaChartViewModel(
    private val stackedAreaSampleUseCase: StackedAreaSampleUseCase,
) : ViewModel() {
    private val refreshRange = stackedAreaSampleUseCase.stackedAreaRefreshRange()

    private val _dataSet =
        MutableStateFlow(
            stackedAreaSampleUseCase.initialStackedAreaSample().let { sample ->
                StackedAreaChartState(
                    dataSet = sample.dataSet,
                    seriesKeys = sample.seriesKeys,
                )
            },
        )

    val dataSet: StateFlow<StackedAreaChartState> = _dataSet.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    private var liveUpdatesJob: Job? = null

    fun regenerateDataSet(range: IntRange = refreshRange) {
        val sample = stackedAreaSampleUseCase.stackedAreaSample(range = range)
        _dataSet.value =
            StackedAreaChartState(
                dataSet = sample.dataSet,
                seriesKeys = sample.seriesKeys,
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
