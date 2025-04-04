import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dautovicharis.charts.app.ChartSubmenuItem
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
import io.github.dautovicharis.charts.app.ui.theme.AppTheme
import io.github.dautovicharis.charts.app.ui.theme.Theme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun JsMainScreen(viewModel: MainViewModel = koinViewModel()) {
    KoinContext {
        val themeState = viewModel.themeState.collectAsStateWithLifecycle()
        val menuState = viewModel.menuState.collectAsStateWithLifecycle()

        remember {
            viewModel.setDefaultState()
        }

        AppTheme(
            theme = themeState.value.selectedTheme,
            darkTheme = viewModel.resolveDarkTheme(isSystemInDarkTheme())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .width(350.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                ) {

                    JsMainScreenContent(
                        themeState = themeState, menuState = menuState,
                        onThemeSelected = { theme ->
                            viewModel.onThemeSelected(theme)
                        },
                        onSubmenuSelected = { selected ->
                            viewModel.onSubmenuSelected(selected)
                        },
                        onMenuToggle = { index ->
                            viewModel.onMenuToggle(index)
                        },
                        onDarkModeToggle = {
                            viewModel.toggleDarkMode()
                        },
                        onDynamicToggle = {
                            viewModel.toggleDynamicColor()
                        }
                    )
                }

                menuState.value.selectedSubmenu?.let {
                    when (it) {
                        ChartSubmenuItem.PieChartBasic -> {
                            PieChartBasicDemo()
                        }

                        ChartSubmenuItem.PieChartCustom -> {
                            PieChartCustomDemo()
                        }

                        ChartSubmenuItem.LineChartBasic -> {
                            LineChartBasicDemo()
                        }

                        ChartSubmenuItem.LineChartCustom -> {
                            LineChartCustomDemo()
                        }

                        ChartSubmenuItem.MultiLineChartBasic -> {
                            MultiLineBasicDemo()
                        }

                        ChartSubmenuItem.MultiLineChartCustom -> {
                            MultiLineCustomDemo()
                        }

                        ChartSubmenuItem.BarChartBasic -> {
                            BarChartBasicDemo()
                        }

                        ChartSubmenuItem.BarChartCustom -> {
                            BarChartCustomDemo()
                        }

                        ChartSubmenuItem.StackedBarChartBasic -> {
                            StackedBarBasicDemo()
                        }

                        ChartSubmenuItem.StackedBarChartCustom -> {
                            StackedBarCustomDemo()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JsMainScreenContent(
    themeState: State<ThemesState>,
    menuState: State<MenuState>,
    onThemeSelected: (Theme) -> Unit,
    onSubmenuSelected: (selected: ChartSubmenuItem) -> Unit,
    onMenuToggle: (index: Int) -> Unit,
    onDarkModeToggle: () -> Unit,
    onDynamicToggle: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 700))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AddGithubIcon(themeState = themeState, onDarkModeToggle = onDarkModeToggle)

                AddThemes(
                    themeState = themeState,
                    onDynamicToggle = onDynamicToggle
                ) {
                    onThemeSelected(it)
                }

                AddMenuItems(
                    menuState = menuState,
                    onSubmenuSelected = onSubmenuSelected,
                    onMenuToggle = onMenuToggle
                )
            }
        }
    }
}
