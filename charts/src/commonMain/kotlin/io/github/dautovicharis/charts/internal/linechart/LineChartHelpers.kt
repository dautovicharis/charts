package io.github.dautovicharis.charts.internal.linechart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.util.lerp

internal fun findNearestPoint(
    touchX: Float,
    scaledValues: List<Float>,
    size: Size,
    bezier: Boolean
): Offset {

    if (scaledValues.isEmpty()) {
        return Offset(0f, 0f)
    }

    val clampedX = touchX.coerceIn(0f, size.width)
    if (scaledValues.size == 1 || size.width == 0f) {
        return Offset(clampedX, size.height - scaledValues.first())
    }

    val lastIndex = scaledValues.size - 1
    val step = size.width / lastIndex
    val index = (clampedX / step)
        .toInt()
        .coerceIn(0, lastIndex)

    if (!bezier || index == lastIndex) {
        val pointBefore = scaledValues[index]
        val pointAfter = when (index + 1 < scaledValues.size) {
            true -> scaledValues[index + 1]
            else -> pointBefore
        }

        val ratio = ((clampedX - (index * step)) / step).coerceIn(0f, 1f)
        val interpolatedY = lerp(pointBefore, pointAfter, ratio)
        return Offset(clampedX, size.height - interpolatedY)
    }

    val nextIndex = index + 1
    val prevX = index * step
    val currentX = nextIndex * step
    val prevY = size.height - scaledValues[index]
    val currentY = size.height - scaledValues[nextIndex]

    val controlPointDiv = 2.2f
    val controlX1 = prevX + (currentX - prevX) / controlPointDiv
    val controlX2 = currentX - (currentX - prevX) / controlPointDiv

    val targetX = clampedX.coerceIn(prevX, currentX)
    val t = solveBezierTForX(
        targetX = targetX,
        x0 = prevX,
        x1 = controlX1,
        x2 = controlX2,
        x3 = currentX
    )
    val y = cubicBezier(
        t = t,
        p0 = prevY,
        p1 = prevY,
        p2 = currentY,
        p3 = currentY
    )
    return Offset(targetX, y)
}

private fun solveBezierTForX(
    targetX: Float,
    x0: Float,
    x1: Float,
    x2: Float,
    x3: Float
): Float {
    var low = 0f
    var high = 1f
    repeat(20) {
        val mid = (low + high) / 2f
        val x = cubicBezier(mid, x0, x1, x2, x3)
        if (x < targetX) {
            low = mid
        } else {
            high = mid
        }
    }
    return (low + high) / 2f
}

private fun cubicBezier(
    t: Float,
    p0: Float,
    p1: Float,
    p2: Float,
    p3: Float
): Float {
    val oneMinusT = 1f - t
    val oneMinusT2 = oneMinusT * oneMinusT
    val t2 = t * t
    return (oneMinusT2 * oneMinusT * p0) +
        (3f * oneMinusT2 * t * p1) +
        (3f * oneMinusT * t2 * p2) +
        (t2 * t * p3)
}

internal fun scaleValues(
    values: List<Double>,
    size: Size,
    minValue: Double = values.min(),
    maxValue: Double = values.max()
): List<Float> {
    val valueRange = maxValue - minValue
    val scale = if (valueRange != 0.0) size.height / valueRange else 1.0
    return values.map { value ->
        ((value - minValue) * scale).toFloat()
    }
}
