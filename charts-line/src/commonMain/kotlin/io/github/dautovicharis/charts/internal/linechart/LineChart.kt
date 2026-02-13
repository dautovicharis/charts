package io.github.dautovicharis.charts.internal.linechart

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.LineChartRenderMode
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.style.LineChartStyle
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LineChart(
    data: MultiChartData,
    style: LineChartStyle,
    colors: ImmutableList<Color>,
    interactionEnabled: Boolean,
    animateOnStart: Boolean,
    renderMode: LineChartRenderMode = LineChartRenderMode.Morph,
    animationDurationMillis: Int = 420,
    isDenseMorphMode: Boolean = false,
    scrollState: ScrollState,
    zoomScale: Float = 1f,
    onValueChanged: (Int) -> Unit = {},
) {
    LineChartContent(
        data = data,
        style = style,
        colors = colors,
        interactionEnabled = interactionEnabled,
        animateOnStart = animateOnStart,
        renderMode = renderMode,
        animationDurationMillis = animationDurationMillis,
        isDenseMorphMode = isDenseMorphMode,
        scrollState = scrollState,
        zoomScale = zoomScale,
        onValueChanged = onValueChanged,
    )
}
