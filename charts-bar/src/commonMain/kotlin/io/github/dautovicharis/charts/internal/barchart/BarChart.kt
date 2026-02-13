package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.common.model.ChartData
import io.github.dautovicharis.charts.internal.common.model.normalizeBarValues
import io.github.dautovicharis.charts.internal.common.model.resolveBarRange
import io.github.dautovicharis.charts.style.BarChartStyle

private const val ZOOM_MIN = 1f
private const val ZOOM_MAX = 4f
private const val ZOOM_STEP = 1.25f

@Composable
fun BarChart(
    chartData: ChartData,
    title: String,
    style: BarChartStyle,
    interactionEnabled: Boolean,
    animateOnStart: Boolean,
    onValueChanged: (Int) -> Unit = {},
) {
    val barColor = style.barColor.copy(alpha = style.barAlpha)
    val isPreview = LocalInspectionMode.current
    val dataSize = chartData.points.size
    val hasFixedRange = style.minValue != null || style.maxValue != null
    val (fixedMin, fixedMax) =
        remember(chartData, style) {
            chartData.resolveBarRange(style.minValue, style.maxValue)
        }
    val targetNormalized =
        remember(chartData, fixedMin, fixedMax, hasFixedRange) {
            chartData.normalizeBarValues(fixedMin, fixedMax, hasFixedRange)
        }
    val animatedValues =
        rememberBarChartAnimatedValues(
            chartData = chartData,
            targetNormalized = targetNormalized,
            isPreview = isPreview,
            animateOnStart = animateOnStart,
        )

    val spacingPx = with(LocalDensity.current) { style.space.toPx() }
    val minBarWidthPx = with(LocalDensity.current) { style.minBarWidth.toPx() }
    val isScrollable =
        remember(dataSize) {
            shouldUseScrollableDensity(
                pointsCount = dataSize,
            )
        }
    val scrollState = rememberScrollState()
    val zoomMin = ZOOM_MIN
    val zoomMax = ZOOM_MAX
    val zoomStep = ZOOM_STEP
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var selectedIndex by remember { mutableIntStateOf(NO_SELECTION) }

    LaunchedEffect(isScrollable, zoomMin, zoomMax) {
        zoomScale =
            when {
                !isScrollable -> 1f
                else -> zoomScale.coerceIn(zoomMin, zoomMax)
            }
    }

    LaunchedEffect(dataSize) {
        if (selectedIndex !in 0 until dataSize) {
            selectedIndex = NO_SELECTION
            onValueChanged(NO_SELECTION)
        }
    }

    val showZoomControlsInHeader = isScrollable && style.zoomControlsVisible
    val showHeader = title.isNotBlank() || showZoomControlsInHeader

    val onToggleSelection: (Int) -> Unit = { index ->
        if (selectedIndex == index) {
            selectedIndex = NO_SELECTION
            onValueChanged(NO_SELECTION)
        } else {
            selectedIndex = index
            onValueChanged(index)
        }
    }

    Column(modifier = style.modifier) {
        if (showHeader) {
            BarChartHeader(
                title = title,
                style = style,
                showZoomControls = showZoomControlsInHeader,
                zoomScale = zoomScale,
                minZoom = zoomMin,
                maxZoom = zoomMax,
                onZoomOut = {
                    zoomScale = (zoomScale / zoomStep).coerceIn(zoomMin, zoomMax)
                },
                onZoomIn = {
                    zoomScale = (zoomScale * zoomStep).coerceIn(zoomMin, zoomMax)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        BarChartContent(
            chartData = chartData,
            style = style,
            interactionEnabled = interactionEnabled,
            animatedValues = animatedValues,
            barColor = barColor,
            fixedMin = fixedMin,
            fixedMax = fixedMax,
            isScrollable = isScrollable,
            spacingPx = spacingPx,
            minBarWidthPx = minBarWidthPx,
            scrollState = scrollState,
            zoomScale = zoomScale,
            zoomMin = zoomMin,
            zoomMax = zoomMax,
            zoomStep = zoomStep,
            selectedIndex = selectedIndex,
            onToggleSelection = onToggleSelection,
            onZoomScaleChange = { updatedScale ->
                zoomScale = updatedScale
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = if (showHeader) style.chartViewStyle.innerPadding else 0.dp),
        )
    }
}
