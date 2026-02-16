package model

import model.definitions.AreaChartDefinition
import model.definitions.BarChartDefinition
import model.definitions.LineChartDefinition
import model.definitions.MultiLineChartDefinition
import model.definitions.PieChartDefinition
import model.definitions.RadarChartDefinition
import model.definitions.StackedBarChartDefinition

val playgroundChartRegistry: PlaygroundChartRegistry =
    PlaygroundChartRegistry(
        charts =
            listOf(
                LineChartDefinition,
                BarChartDefinition,
                PieChartDefinition,
                RadarChartDefinition,
                AreaChartDefinition,
                MultiLineChartDefinition,
                StackedBarChartDefinition,
            ),
        primaryChartTypes =
            listOf(
                ChartType.LINE,
                ChartType.BAR,
                ChartType.PIE,
                ChartType.RADAR,
                ChartType.AREA,
            ),
        overflowChartTypes =
            listOf(
                ChartType.MULTI_LINE,
                ChartType.STACKED_BAR,
            ),
    )
