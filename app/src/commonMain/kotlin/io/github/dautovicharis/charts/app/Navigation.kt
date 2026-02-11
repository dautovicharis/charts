package io.github.dautovicharis.charts.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.bar_chart
import chartsproject.app.generated.resources.bar_stacked_chart
import chartsproject.app.generated.resources.ic_bar_chart
import chartsproject.app.generated.resources.ic_line_chart
import chartsproject.app.generated.resources.ic_multi_line_chart
import chartsproject.app.generated.resources.ic_pie_chart
import chartsproject.app.generated.resources.ic_radar_chart
import chartsproject.app.generated.resources.ic_stacked_bar_chart
import chartsproject.app.generated.resources.line_chart
import chartsproject.app.generated.resources.multi_line_chart
import chartsproject.app.generated.resources.pie_chart
import chartsproject.app.generated.resources.radar_chart
import chartsproject.app.generated.resources.stacked_area_chart
import io.github.dautovicharis.charts.app.demo.bar.BarChartDemo
import io.github.dautovicharis.charts.app.demo.line.LineChartDemo
import io.github.dautovicharis.charts.app.demo.multiline.MultiLineChartDemo
import io.github.dautovicharis.charts.app.demo.pie.PieChartDemo
import io.github.dautovicharis.charts.app.demo.radar.RadarChartDemo
import io.github.dautovicharis.charts.app.demo.stackedarea.StackedAreaChartDemo
import io.github.dautovicharis.charts.app.demo.stackedbar.StackedBarChartDemo
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class ChartDestination(
    val route: String,
    val icon: DrawableResource,
    val title: StringResource,
) {
    object MainScreen {
        const val ROUTE = "main"
    }

    data object PieChartScreen :
        ChartDestination(
            route = "pieChart",
            icon = Res.drawable.ic_pie_chart,
            title = Res.string.pie_chart,
        )

    data object LineChartScreen :
        ChartDestination(
            route = "lineChart",
            icon = Res.drawable.ic_line_chart,
            title = Res.string.line_chart,
        )

    data object MultiLineChartScreen :
        ChartDestination(
            route = "multiLineChart",
            icon = Res.drawable.ic_multi_line_chart,
            title = Res.string.multi_line_chart,
        )

    data object StackedAreaChartScreen :
        ChartDestination(
            route = "stackedAreaChart",
            icon = Res.drawable.ic_stacked_bar_chart,
            title = Res.string.stacked_area_chart,
        )

    data object BarChartScreen :
        ChartDestination(
            route = "barChart",
            icon = Res.drawable.ic_bar_chart,
            title = Res.string.bar_chart,
        )

    data object StackedBarChartScreen :
        ChartDestination(
            route = "stackedBarChart",
            icon = Res.drawable.ic_stacked_bar_chart,
            title = Res.string.bar_stacked_chart,
        )

    data object RadarChartScreen :
        ChartDestination(
            route = "radarChart",
            icon = Res.drawable.ic_radar_chart,
            title = Res.string.radar_chart,
        )
}

@Composable
fun Navigation(
    navController: NavHostController,
    menuState: MenuState,
    onChartSelected: (selected: ChartDestination) -> Unit,
    onStyleItemsChanged: (StyleItems?) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ChartDestination.MainScreen.ROUTE,
        modifier = modifier,
    ) {
        composable(ChartDestination.MainScreen.ROUTE) {
            MainScreenContent(
                menuState = menuState,
                onChartSelected = onChartSelected,
            )
        }
        composable(ChartDestination.PieChartScreen.route) {
            PieChartDemo(onStyleItemsChanged = onStyleItemsChanged)
        }

        composable(ChartDestination.LineChartScreen.route) {
            LineChartDemo(onStyleItemsChanged = onStyleItemsChanged)
        }

        composable(ChartDestination.MultiLineChartScreen.route) {
            MultiLineChartDemo(onStyleItemsChanged = onStyleItemsChanged)
        }

        composable(ChartDestination.StackedAreaChartScreen.route) {
            StackedAreaChartDemo(onStyleItemsChanged = onStyleItemsChanged)
        }

        composable(ChartDestination.BarChartScreen.route) {
            BarChartDemo(onStyleItemsChanged = onStyleItemsChanged)
        }

        composable(ChartDestination.StackedBarChartScreen.route) {
            StackedBarChartDemo(onStyleItemsChanged = onStyleItemsChanged)
        }

        composable(ChartDestination.RadarChartScreen.route) {
            RadarChartDemo(onStyleItemsChanged = onStyleItemsChanged)
        }
    }
}
