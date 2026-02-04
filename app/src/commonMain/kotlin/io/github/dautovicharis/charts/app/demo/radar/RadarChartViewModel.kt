package io.github.dautovicharis.charts.app.demo.radar

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.ui.theme.ColorPalette
import io.github.dautovicharis.charts.app.ui.theme.generateColors
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.MultiChartDataSet
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RadarChartState(
    val basicDataSet: ChartDataSet,
    val customDataSet: MultiChartDataSet,
    val lineColors: List<Color> = emptyList()
)

class RadarChartViewModel : ViewModel() {

    companion object {
        private const val CHART_TITLE = "Radar Chart"
    }

    private val categories = listOf(
        "Speed",
        "Strength",
        "Agility",
        "Stamina",
        "Skill",
        "Luck"
    )

    private val initialItems = listOf(
        "Falcon" to listOf(78f, 62f, 90f, 55f, 70f, 80f),
        "Tiger" to listOf(65f, 88f, 60f, 82f, 55f, 68f),
        "Octane" to listOf(92f, 58f, 76f, 62f, 86f, 60f)
    )

    private val customSeriesPalette = listOf(
        ColorPalette.DutchField.blue,
        ColorPalette.DutchField.orange,
        ColorPalette.DutchField.green,
        ColorPalette.DutchField.purple,
        ColorPalette.DutchField.red,
        ColorPalette.DutchField.teal,
        ColorPalette.DutchField.yellow,
        ColorPalette.DutchField.magenta,
        ColorPalette.DutchField.lightBlue
    )

    private val _dataSet = MutableStateFlow(
        RadarChartState(
            basicDataSet = listOf(74, 60, 82, 55, 69, 88)
                .toChartDataSet(title = CHART_TITLE, labels = categories),
            customDataSet = initialItems.toMultiChartDataSet(
                title = CHART_TITLE,
                categories = categories
            ),
            lineColors = generateColors(
                size = initialItems.size,
                fromColors = customSeriesPalette
            )
        )
    )

    val dataSet: StateFlow<RadarChartState> = _dataSet.asStateFlow()

    fun regenerateBasicDataSet(range: IntRange = 30..100) {
        val newData = categories.map { range.random() }
        _dataSet.update {
            it.copy(
                basicDataSet = newData.toChartDataSet(
                    title = CHART_TITLE,
                    labels = categories
                )
            )
        }
    }

    fun regenerateCustomDataSet(range: IntRange = 30..100) {
        val newItems = initialItems.map { (name, data) ->
            val newData = data.map { range.random().toFloat() }
            name to newData
        }

        val newColors = generateColors(
            size = newItems.size,
            fromColors = customSeriesPalette
        )

        val newDataSet = newItems.toMultiChartDataSet(
            title = CHART_TITLE,
            categories = categories
        )

        _dataSet.update {
            it.copy(
                customDataSet = newDataSet,
                lineColors = newColors
            )
        }
    }
}
