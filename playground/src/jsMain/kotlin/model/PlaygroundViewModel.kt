package model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlaygroundViewModel(
    val registry: PlaygroundChartRegistry = playgroundChartRegistry,
) {
    private val _state = MutableStateFlow(defaultPlaygroundState(registry))
    val state: StateFlow<PlaygroundState> = _state.asStateFlow()

    fun dispatch(action: PlaygroundAction) {
        _state.update { currentState ->
            PlaygroundReducer.reduce(
                state = currentState,
                action = action,
                registry = registry,
            )
        }
    }
}
