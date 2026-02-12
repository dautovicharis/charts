package io.github.dautovicharis.charts.internal.linechart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.util.lerp

const val LINE_CHART_BEZIER_TENSION = 0.95f

data class CubicControlPoints(
    val first: Offset,
    val second: Offset,
)

fun cubicControlPointsForSegment(
    points: List<Offset>,
    segmentStartIndex: Int,
    tension: Float = LINE_CHART_BEZIER_TENSION,
): CubicControlPoints {
    val p1 = points[segmentStartIndex]
    val p2 = points[segmentStartIndex + 1]
    val p0 =
        when {
            segmentStartIndex > 0 -> points[segmentStartIndex - 1]
            else -> p1
        }
    val p3 =
        when {
            segmentStartIndex + 2 < points.size -> points[segmentStartIndex + 2]
            else -> p2
        }

    val factor = tension / 6f
    val control1 =
        Offset(
            x = p1.x + (p2.x - p0.x) * factor,
            y = p1.y + (p2.y - p0.y) * factor,
        )
    val control2 =
        Offset(
            x = p2.x - (p3.x - p1.x) * factor,
            y = p2.y - (p3.y - p1.y) * factor,
        )

    return CubicControlPoints(first = control1, second = control2)
}

fun findNearestPoint(
    touchX: Float,
    scaledValues: List<Float>,
    size: Size,
    bezier: Boolean,
    bezierTension: Float = LINE_CHART_BEZIER_TENSION,
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
    val index =
        (clampedX / step)
            .toInt()
            .coerceIn(0, lastIndex)

    if (!bezier || index == lastIndex) {
        val pointBefore = scaledValues[index]
        val pointAfter =
            when (index + 1 < scaledValues.size) {
                true -> scaledValues[index + 1]
                else -> pointBefore
            }

        val ratio = ((clampedX - (index * step)) / step).coerceIn(0f, 1f)
        val interpolatedY = lerp(pointBefore, pointAfter, ratio)
        return Offset(clampedX, size.height - interpolatedY)
    }

    val points =
        List(scaledValues.size) { pointIndex ->
            Offset(
                x = pointIndex * step,
                y = size.height - scaledValues[pointIndex],
            )
        }
    val segmentStart = index.coerceIn(0, lastIndex - 1)
    val startPoint = points[segmentStart]
    val endPoint = points[segmentStart + 1]
    val controls =
        cubicControlPointsForSegment(
            points = points,
            segmentStartIndex = segmentStart,
            tension = bezierTension,
        )
    val targetX = clampedX.coerceIn(startPoint.x, endPoint.x)
    val t =
        solveBezierTForX(
            targetX = targetX,
            x0 = startPoint.x,
            x1 = controls.first.x,
            x2 = controls.second.x,
            x3 = endPoint.x,
        )
    val y =
        cubicBezier(
            t = t,
            p0 = startPoint.y,
            p1 = controls.first.y,
            p2 = controls.second.y,
            p3 = endPoint.y,
        )
    return Offset(targetX, y)
}

private fun solveBezierTForX(
    targetX: Float,
    x0: Float,
    x1: Float,
    x2: Float,
    x3: Float,
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
    p3: Float,
): Float {
    val oneMinusT = 1f - t
    val oneMinusT2 = oneMinusT * oneMinusT
    val t2 = t * t
    return (oneMinusT2 * oneMinusT * p0) +
        (3f * oneMinusT2 * t * p1) +
        (3f * oneMinusT * t2 * p2) +
        (t2 * t * p3)
}

fun scaleValues(
    values: List<Double>,
    size: Size,
    minValue: Double = values.min(),
    maxValue: Double = values.max(),
): List<Float> {
    val valueRange = maxValue - minValue
    val scale = if (valueRange != 0.0) size.height / valueRange else 1.0
    return values.map { value ->
        ((value - minValue) * scale).toFloat()
    }
}
