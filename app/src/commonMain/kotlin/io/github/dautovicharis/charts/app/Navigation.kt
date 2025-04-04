package io.github.dautovicharis.charts.app

import MainScreenContent
import MenuState
import ThemesState
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.bar_chart
import chartsproject.app.generated.resources.bar_stacked_chart
import chartsproject.app.generated.resources.chart_basic
import chartsproject.app.generated.resources.chart_custom
import chartsproject.app.generated.resources.ic_bar_chart
import chartsproject.app.generated.resources.ic_line_chart
import chartsproject.app.generated.resources.ic_multi_line_chart
import chartsproject.app.generated.resources.ic_pie_chart
import chartsproject.app.generated.resources.ic_stacked_bar_chart
import chartsproject.app.generated.resources.line_chart
import chartsproject.app.generated.resources.multi_line_chart
import chartsproject.app.generated.resources.pie_chart
import io.github.dautovicharis.charts.app.demo.bar.BarChartBasicDemo
import io.github.dautovicharis.charts.app.demo.bar.BarChartCustomDemo
import io.github.dautovicharis.charts.app.demo.line.LineChartBasicDemo
import io.github.dautovicharis.charts.app.demo.line.LineChartCustomDemo
import io.github.dautovicharis.charts.app.demo.multiline.MultiLineBasicDemo
import io.github.dautovicharis.charts.app.demo.multiline.MultiLineCustomDemo
import io.github.dautovicharis.charts.app.demo.pie.PieChartBasicDemo
import io.github.dautovicharis.charts.app.demo.pie.PieChartCustomDemo
import io.github.dautovicharis.charts.app.demo.stackedbar.StackedBarBasicDemo
import io.github.dautovicharis.charts.app.demo.stackedbar.StackedBarCustomDemo
import io.github.dautovicharis.charts.app.ui.theme.Theme
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

    data object BarChartCustom :
        ChartSubmenuItem(
            route = "barChartCustom",
            title = Res.string.chart_custom
        )

    // Stacked Bar Chart
    data object StackedBarChartBasic :
        ChartSubmenuItem(
            route = "stackedBarChartBasic",
            title = Res.string.chart_basic
        )

    data object StackedBarChartCustom :
        ChartSubmenuItem(
            route = "stackedBarChartCustom",
            title = Res.string.chart_custom
        )

}

sealed class ChartScreen(
    val icon: DrawableResource,
    val title: StringResource,
    val submenus: List<ChartSubmenuItem> = emptyList()
) {
    object MainScreen {
        const val ROUTE = "main"
    }

    data object PieChartScreen :
        ChartScreen(
            icon = Res.drawable.ic_pie_chart,
            title = Res.string.pie_chart,
            submenus = listOf(
                ChartSubmenuItem.PieChartBasic,
                ChartSubmenuItem.PieChartCustom
            )
        )

    data object LineChartScreen :
        ChartScreen(
            icon = Res.drawable.ic_line_chart,
            title = Res.string.line_chart,
            submenus = listOf(
                ChartSubmenuItem.LineChartBasic,
                ChartSubmenuItem.LineChartCustom
            )
        )

    data object MultiLineChartScreen :
        ChartScreen(
            icon = Res.drawable.ic_multi_line_chart,
            title = Res.string.multi_line_chart,
            submenus = listOf(
                ChartSubmenuItem.MultiLineChartBasic,
                ChartSubmenuItem.MultiLineChartCustom
            )
        )

    data object BarChartScreen :
        ChartScreen(
            icon = Res.drawable.ic_bar_chart,
            title = Res.string.bar_chart,
            submenus = listOf(
                ChartSubmenuItem.BarChartBasic,
                ChartSubmenuItem.BarChartCustom
            )
        )

    data object StackedBarChartScreen :
        ChartScreen(
            icon = Res.drawable.ic_stacked_bar_chart,
            title = Res.string.bar_stacked_chart,
            submenus = listOf(
                ChartSubmenuItem.StackedBarChartBasic,
                ChartSubmenuItem.StackedBarChartCustom
            )
        )
}

@Composable
fun Navigation(
    navController: NavHostController,
    themeState: State<ThemesState>,
    menuState: State<MenuState>,
    onThemeSelected: (Theme) -> Unit,
    onSubmenuSelected: (selected: ChartSubmenuItem) -> Unit,
    onMenuToggle: (index: Int) -> Unit,
    onDarkModeToggle: () -> Unit,
    onDynamicToggle: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = ChartScreen.MainScreen.ROUTE,
        modifier = Modifier.padding(top = 45.dp)
    ) {
        composable(ChartScreen.MainScreen.ROUTE) {
            MainScreenContent(
                themeState = themeState,
                onThemeSelected = onThemeSelected,
                menuState = menuState,
                onSubmenuSelected = onSubmenuSelected,
                onMenuToggle = onMenuToggle,
                onDarkModeToggle = onDarkModeToggle,
                onDynamicToggle = onDynamicToggle
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

        composable(ChartSubmenuItem.BarChartCustom.route) {
            BarChartCustomDemo()
        }

        composable(ChartSubmenuItem.StackedBarChartBasic.route) {
            StackedBarBasicDemo()
        }

        composable(ChartSubmenuItem.StackedBarChartCustom.route) {
            StackedBarCustomDemo()
        }
    }
}
