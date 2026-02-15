package codegen.bar

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.style.BarChartDefaults
import model.BarStyleState
import model.StylePropertiesSnapshot

@Composable
fun barStylePropertiesSnapshot(styleState: BarStyleState): StylePropertiesSnapshot {
    val defaultStyle = BarChartDefaults.style()
    val currentStyle =
        BarChartDefaults.style(
            barColor = styleState.barColor ?: defaultStyle.barColor,
            barAlpha = styleState.barAlpha ?: defaultStyle.barAlpha,
            gridVisible = styleState.gridVisible ?: defaultStyle.gridVisible,
            axisVisible = styleState.axisVisible ?: defaultStyle.axisVisible,
            selectionLineVisible = styleState.selectionLineVisible ?: defaultStyle.selectionLineVisible,
            selectionLineWidth = styleState.selectionLineWidth ?: defaultStyle.selectionLineWidth,
            zoomControlsVisible = styleState.zoomControlsVisible ?: defaultStyle.zoomControlsVisible,
        )
    return StylePropertiesSnapshot(
        current = currentStyle.getProperties(),
        defaults = defaultStyle.getProperties(),
    )
}
