package io.github.dautovicharis.charts.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.bar_chart
import chartsproject.app.generated.resources.chart_basic
import chartsproject.app.generated.resources.chart_custom
import chartsproject.app.generated.resources.ic_bar_chart
import chartsproject.app.generated.resources.ic_line_chart
import chartsproject.app.generated.resources.ic_multi_line_chart
import chartsproject.app.generated.resources.ic_pie_chart
import chartsproject.app.generated.resources.ic_radar_chart
import chartsproject.app.generated.resources.line_chart
import chartsproject.app.generated.resources.multi_line_chart
import chartsproject.app.generated.resources.pie_chart
import chartsproject.app.generated.resources.radar_chart
import io.github.dautovicharis.charts.app.demo.bar.BarChartBasicDemo
import io.github.dautovicharis.charts.app.demo.line.LineChartBasicDemo
import io.github.dautovicharis.charts.app.demo.line.LineChartCustomDemo
import io.github.dautovicharis.charts.app.demo.multiline.MultiLineBasicDemo
import io.github.dautovicharis.charts.app.demo.multiline.MultiLineCustomDemo
import io.github.dautovicharis.charts.app.demo.pie.PieChartBasicDemo
import io.github.dautovicharis.charts.app.demo.pie.PieChartCustomDemo
import io.github.dautovicharis.charts.app.demo.radar.RadarChartBasicDemo
import io.github.dautovicharis.charts.app.demo.radar.RadarChartCustomDemo
import io.github.dautovicharis.charts.app.demo.stackedbar.StackedBarCustomDemo
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class ChartSubmenuItem(
    val route: String,
    val title: StringResource
) {

    // Pie Chart
    data object PieChartBasic :
        ChartSubmenuItem(
            route = "pieChartBasic",
            title = Res.string.chart_basic
        )

    data object PieChartCustom :
        ChartSubmenuItem(
            route = "pieChartCustom",
            title = Res.string.chart_custom
        )
    // Line Chart
    data object LineChartBasic :
        ChartSubmenuItem(
            route = "lineChartBasic",
            title = Res.string.chart_basic
        )

    data object LineChartCustom :
        ChartSubmenuItem(
            route = "lineChartCustom",
            title = Res.string.chart_custom
        )

    // Multi Line Chart
    data object MultiLineChartBasic :
        ChartSubmenuItem(
            route = "multiLineChartBasic",
            title = Res.string.chart_basic
        )

    data object MultiLineChartCustom :
        ChartSubmenuItem(
            route = "multiLineChartCustom",
            title = Res.string.chart_custom
        )

    // Bar Chart
    data object BarChartBasic :
        ChartSubmenuItem(
            route = "barChartBasic",
            title = Res.string.chart_basic
        )

    // Stacked Bar Chart
    data object StackedBarChartCustom :
        ChartSubmenuItem(
            route = "stackedBarChartCustom",
            title = Res.string.chart_custom
        )

    // Radar Chart
    data object RadarChartBasic :
        ChartSubmenuItem(
            route = "radarChartBasic",
            title = Res.string.chart_basic
        )

    data object RadarChartCustom :
        ChartSubmenuItem(
            route = "radarChartCustom",
            title = Res.string.chart_custom
        )
}

sealed class ChartDestination(
    val icon: DrawableResource,
    val title: StringResource,
    val submenus: List<ChartSubmenuItem> = emptyList()
) {
    object MainScreen {
        const val ROUTE = "main"
    }

    data object PieChartScreen :
        ChartDestination(
            icon = Res.drawable.ic_pie_chart,
            title = Res.string.pie_chart,
            submenus = listOf(
                ChartSubmenuItem.PieChartBasic,
                ChartSubmenuItem.PieChartCustom
            )
        )

    data object LineChartScreen :
        ChartDestination(
            icon = Res.drawable.ic_line_chart,
            title = Res.string.line_chart,
            submenus = listOf(
                ChartSubmenuItem.LineChartBasic,
                ChartSubmenuItem.LineChartCustom
            )
        )

    data object MultiLineChartScreen :
        ChartDestination(
            icon = Res.drawable.ic_multi_line_chart,
            title = Res.string.multi_line_chart,
            submenus = listOf(
                ChartSubmenuItem.MultiLineChartBasic,
                ChartSubmenuItem.MultiLineChartCustom
            )
        )

    data object BarChartScreen :
        ChartDestination(
            icon = Res.drawable.ic_bar_chart,
            title = Res.string.bar_chart,
            submenus = listOf(
                ChartSubmenuItem.BarChartBasic,
                ChartSubmenuItem.StackedBarChartCustom
            )
        )

    data object RadarChartScreen :
        ChartDestination(
            icon = Res.drawable.ic_radar_chart,
            title = Res.string.radar_chart,
            submenus = listOf(
                ChartSubmenuItem.RadarChartBasic,
                ChartSubmenuItem.RadarChartCustom
            )
        )
}

@Composable
fun Navigation(
    navController: NavHostController,
    menuState: MenuState,
    onSubmenuSelected: (selected: ChartSubmenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ChartDestination.MainScreen.ROUTE,
        modifier = modifier
    ) {
        composable(ChartDestination.MainScreen.ROUTE) {
            MainScreenContent(
                menuState = menuState,
                onSubmenuSelected = onSubmenuSelected
            )
        }
        composable(ChartSubmenuItem.PieChartBasic.route) {
            PieChartBasicDemo()
        }

        composable(ChartSubmenuItem.PieChartCustom.route) {
            PieChartCustomDemo()
        }

        composable(ChartSubmenuItem.LineChartBasic.route) {
            LineChartBasicDemo()
        }

        composable(ChartSubmenuItem.LineChartCustom.route) {
            LineChartCustomDemo()
        }

        composable(ChartSubmenuItem.MultiLineChartBasic.route) {
            MultiLineBasicDemo()
        }

        composable(ChartSubmenuItem.MultiLineChartCustom.route) {
            MultiLineCustomDemo()
        }

        composable(ChartSubmenuItem.BarChartBasic.route) {
            BarChartBasicDemo()
        }

        composable(ChartSubmenuItem.StackedBarChartCustom.route) {
            StackedBarCustomDemo()
        }

        composable(ChartSubmenuItem.RadarChartBasic.route) {
            RadarChartBasicDemo()
        }

        composable(ChartSubmenuItem.RadarChartCustom.route) {
            RadarChartCustomDemo()
        }
    }
}
