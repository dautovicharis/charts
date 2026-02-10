package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.common.model.ChartData
import io.github.dautovicharis.charts.style.BarChartStyle
import kotlin.math.min
import kotlin.math.roundToInt

// X Axis label layout constants
private val X_AXIS_LABEL_VERTICAL_PADDING: Dp = 10.dp
private val X_AXIS_LABEL_HORIZONTAL_PADDING: Dp = 8.dp

// Y Axis label layout constants
private val Y_AXIS_CHART_GAP: Dp = 10.dp

@Composable
internal fun BarChartContent(
    chartData: ChartData,
    style: BarChartStyle,
    interactionEnabled: Boolean,
    animatedValues: List<Animatable<Float, AnimationVector1D>>,
    barColor: Color,
    fixedMin: Double,
    fixedMax: Double,
    isScrollable: Boolean,
    spacingPx: Float,
    minBarWidthPx: Float,
    scrollState: ScrollState,
    zoomScale: Float,
    zoomMin: Float,
    zoomMax: Float,
    zoomStep: Float,
    selectedIndex: Int,
    onToggleSelection: (Int) -> Unit,
    onZoomScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dataSize = chartData.points.size

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val xAxisTilt = style.xAxisLabelTiltDegrees.coerceIn(0f, 75f)
        val xAxisLabelSizePx = with(density) { style.xAxisLabelSize.toPx() }
        val xAxisLabelFootprintPx =
            remember(chartData.labels, dataSize, xAxisLabelSizePx, xAxisTilt) {
                estimateXAxisLabelFootprintPx(
                    labels = chartData.labels,
                    dataSize = dataSize,
                    fontSizePx = xAxisLabelSizePx,
                    tiltDegrees = xAxisTilt,
                )
            }
        val xAxisHeight =
            if (!style.xAxisLabelsVisible) {
                0.dp
            } else {
                with(density) {
                    (xAxisLabelFootprintPx.height + X_AXIS_LABEL_VERTICAL_PADDING.toPx()).toDp()
                }
            }
        val chartHeight = (maxHeight - xAxisHeight).coerceAtLeast(0.dp)
        val chartHeightPx = with(density) { chartHeight.toPx() }.coerceAtLeast(1f)
        val yAxisTicks =
            remember(fixedMin, fixedMax, chartHeightPx, style.yAxisLabelCount) {
                buildYAxisTicks(
                    minValue = fixedMin,
                    maxValue = fixedMax,
                    labelCount = style.yAxisLabelCount,
                    chartHeightPx = chartHeightPx,
                )
            }
        val yAxisWidthPx =
            if (style.yAxisLabelsVisible) {
                estimateYAxisLabelWidthPx(
                    ticks = yAxisTicks,
                    fontSizePx = with(density) { style.yAxisLabelSize.toPx() },
                )
            } else {
                0f
            }
        val yAxisGapPx = if (style.yAxisLabelsVisible) with(density) { Y_AXIS_CHART_GAP.toPx() } else 0f
        val yAxisWidth = with(density) { yAxisWidthPx.toDp() }
        val plotStartPadding = with(density) { (yAxisWidthPx + yAxisGapPx).toDp() }
        val viewportWidthPx =
            (constraints.maxWidth.toFloat() - yAxisWidthPx - yAxisGapPx).coerceAtLeast(1f)

        val barWidthPx =
            when {
                isScrollable -> (minBarWidthPx * zoomScale).coerceAtLeast(1f)
                dataSize <= 0 -> viewportWidthPx
                else -> ((viewportWidthPx - spacingPx * (dataSize - 1)) / dataSize).coerceAtLeast(1f)
            }
        val unitWidthPx = unitWidth(barWidthPx, spacingPx)
        val contentWidthPx =
            if (isScrollable) {
                contentWidth(dataSize, unitWidthPx, spacingPx).coerceAtLeast(viewportWidthPx)
            } else {
                viewportWidthPx
            }
        val canvasWidth = with(density) { contentWidthPx.toDp() }
        val scrollOffsetPx = if (isScrollable) scrollState.value.toFloat() else 0f

        LaunchedEffect(contentWidthPx, viewportWidthPx, isScrollable) {
            val maxScroll = (contentWidthPx - viewportWidthPx).roundToInt().coerceAtLeast(0)
            if (!isScrollable && scrollState.value != 0) {
                scrollState.scrollTo(0)
            } else if (scrollState.value > maxScroll) {
                scrollState.scrollTo(maxScroll)
            }
        }

        val fitTapModifier =
            buildFitTapModifier(
                interactionEnabled = interactionEnabled,
                isScrollable = isScrollable,
                dataSize = dataSize,
                spacingPx = spacingPx,
                viewportWidthPx = viewportWidthPx,
                chartHeightPx = chartHeightPx,
                onTapIndex = onToggleSelection,
            )

        val scrollTapModifier =
            buildScrollTapModifier(
                interactionEnabled = interactionEnabled,
                isScrollable = isScrollable,
                dataSize = dataSize,
                unitWidthPx = unitWidthPx,
                scrollState = scrollState,
                onTapIndex = onToggleSelection,
                onDoubleTap = {
                    onZoomScaleChange((zoomScale * zoomStep).coerceIn(zoomMin, zoomMax))
                },
            )

        val pinchModifier =
            buildPinchModifier(
                isScrollable = isScrollable,
                dataSize = dataSize,
                zoomMin = zoomMin,
                zoomMax = zoomMax,
                getZoomScale = { zoomScale },
                setZoomScale = onZoomScaleChange,
            )

        val selectedCenterXContent =
            if (selectedIndex in 0 until dataSize) {
                selectedIndex * unitWidthPx + barWidthPx / 2f
            } else {
                Float.NaN
            }

        val visibleRange =
            if (isScrollable) {
                visibleIndexRange(
                    dataSize = dataSize,
                    viewportWidthPx = viewportWidthPx,
                    scrollOffsetPx = scrollOffsetPx,
                    unitWidthPx = unitWidthPx,
                )
            } else {
                0..<dataSize
            }
        val labelIndices =
            when {
                dataSize <= 0 -> emptyList()
                else -> {
                    val maxVisibleLabels =
                        min(
                            style.xAxisLabelMaxCount,
                            (
                                viewportWidthPx /
                                    (
                                        xAxisLabelFootprintPx.width +
                                            with(density) { X_AXIS_LABEL_HORIZONTAL_PADDING.toPx() }
                                    )
                            )
                                .toInt()
                                .coerceAtLeast(2),
                        )
                    if (isScrollable) {
                        scrollableLabelIndices(
                            dataSize = dataSize,
                            maxCount = maxVisibleLabels,
                            visibleRange = visibleRange,
                        )
                    } else {
                        sampledLabelIndices(
                            dataSize = dataSize,
                            maxCount = maxVisibleLabels,
                            visibleRange = visibleRange,
                        )
                    }
                }
            }

        val ticks =
            remember(
                chartData.labels,
                labelIndices,
                barWidthPx,
                unitWidthPx,
                scrollOffsetPx,
            ) {
                buildAxisTicks(
                    chartData = chartData,
                    labelIndices = labelIndices,
                    barWidthPx = barWidthPx,
                    unitWidthPx = unitWidthPx,
                    scrollOffsetPx = scrollOffsetPx,
                )
            }
        val interactionModifier =
            Modifier
                .then(fitTapModifier)
                .then(scrollTapModifier)
                .then(pinchModifier)

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .testTag(TestTags.BAR_CHART),
        ) {
            if (style.yAxisLabelsVisible) {
                BarYAxisLabels(
                    ticks = yAxisTicks,
                    color = style.yAxisLabelColor,
                    fontSize = style.yAxisLabelSize,
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .fillMaxHeight()
                            .width(yAxisWidth),
                )
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(start = plotStartPadding),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .then(interactionModifier)
                            .horizontalScroll(state = scrollState, enabled = isScrollable),
                ) {
                    Canvas(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .requiredWidth(canvasWidth),
                        onDraw = {
                            drawBars(
                                style = style,
                                animatedValues = animatedValues,
                                visibleRange = visibleRange,
                                selectedIndex = selectedIndex,
                                barColor = barColor,
                                maxValue = fixedMax,
                                minValue = fixedMin,
                                barWidthPx = barWidthPx,
                                spacingPx = spacingPx,
                                selectedCenterX = selectedCenterXContent,
                            )
                        },
                    )
                }
            }
        }

        if (style.xAxisLabelsVisible) {
            BarXAxisLabels(
                ticks = ticks,
                color = style.xAxisLabelColor,
                fontSize = style.xAxisLabelSize,
                tiltDegrees = xAxisTilt,
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(start = plotStartPadding)
                        .height(xAxisHeight),
            )
        }
    }
}
