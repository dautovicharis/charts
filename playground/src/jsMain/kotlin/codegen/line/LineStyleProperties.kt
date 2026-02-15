package codegen.line

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.style.LineChartDefaults
import model.LineStyleState
import model.StylePropertiesSnapshot

@Composable
fun lineStylePropertiesSnapshot(styleState: LineStyleState): StylePropertiesSnapshot {
    val defaultStyle = LineChartDefaults.style()
    val currentStyle =
        LineChartDefaults.style(
            lineColor = styleState.lineColor ?: defaultStyle.lineColor,
            lineAlpha = styleState.lineAlpha ?: defaultStyle.lineAlpha,
            bezier = styleState.bezier ?: defaultStyle.bezier,
            pointColor = styleState.pointColor ?: defaultStyle.pointColor,
            pointVisible = styleState.pointVisible ?: defaultStyle.pointVisible,
            pointSize = styleState.pointSize ?: defaultStyle.pointSize,
            dragPointColor = styleState.dragPointColor ?: defaultStyle.dragPointColor,
            dragPointVisible = styleState.dragPointVisible ?: defaultStyle.dragPointVisible,
            dragPointSize = styleState.dragPointSize ?: defaultStyle.dragPointSize,
            dragActivePointSize = styleState.dragActivePointSize ?: defaultStyle.dragActivePointSize,
            axisVisible = styleState.axisVisible ?: defaultStyle.axisVisible,
            axisLineWidth = styleState.axisLineWidth ?: defaultStyle.axisLineWidth,
            xAxisLabelsVisible = styleState.xAxisLabelsVisible ?: defaultStyle.xAxisLabelsVisible,
            yAxisLabelsVisible = styleState.yAxisLabelsVisible ?: defaultStyle.yAxisLabelsVisible,
            zoomControlsVisible = styleState.zoomControlsVisible ?: defaultStyle.zoomControlsVisible,
        )

    return StylePropertiesSnapshot(
        current = currentStyle.getProperties(),
        defaults = defaultStyle.getProperties(),
    )
}
