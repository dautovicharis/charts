import androidx.lifecycle.ViewModel
import io.github.dautovicharis.charts.app.ChartScreen
import io.github.dautovicharis.charts.app.ChartSubmenuItem
import io.github.dautovicharis.charts.app.ui.theme.Theme
import io.github.dautovicharis.charts.app.ui.theme.blueViolet
import io.github.dautovicharis.charts.app.ui.theme.deepOceanBlue
import io.github.dautovicharis.charts.app.ui.theme.deepRed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class MenuState(
    val menuItems: List<ChartScreen>,
    val selectedSubmenu: ChartSubmenuItem? = null,
    val expandedMenuIndex: Int? = null
)

enum class DarkModeSettings {
    System,
    On,
    Off
}

data class SubmenuState(val subMenuItems: List<ChartSubmenuItem>)

data class ThemesState(
    val themes: List<Theme>,
    val selectedTheme: Theme,
    val darkMode: DarkModeSettings,
    val useDynamicColors: Boolean
)

class MainViewModel : ViewModel() {
    private val _menuState = MutableStateFlow(
        MenuState(
            listOf(
                ChartScreen.PieChartScreen,
                ChartScreen.LineChartScreen,
                ChartScreen.MultiLineChartScreen,
                ChartScreen.BarChartScreen,
                ChartScreen.StackedBarChartScreen,
            )
        )
    )

    val menuState: StateFlow<MenuState> = _menuState

    private val _themeState = MutableStateFlow(
        ThemesState(
            themes = listOf(
                Theme(deepRed),
                Theme(blueViolet),
                Theme(deepOceanBlue)
            ),
            selectedTheme = Theme(deepRed),
            darkMode = DarkModeSettings.System,
            useDynamicColors = false
        )
    )
    val themeState: StateFlow<ThemesState> = _themeState

    fun onThemeSelected(newTheme: Theme) {
        _themeState.update {
            it.copy(
                selectedTheme = newTheme
            )
        }
    }

    fun toggleDarkMode() {
        _themeState.update {
            val newDarkMode = when (it.darkMode) {
                DarkModeSettings.System -> DarkModeSettings.Off
                DarkModeSettings.Off -> DarkModeSettings.On
                DarkModeSettings.On -> DarkModeSettings.System
            }
            it.copy(
                darkMode = newDarkMode
            )
        }
    }

    fun toggleDynamicColor() {
        _themeState.update {
            it.copy(
                useDynamicColors = !it.useDynamicColors
            )
        }
    }

    fun onSubmenuSelected(submenu: ChartSubmenuItem) {
        _menuState.update {
            it.copy(selectedSubmenu = submenu)
        }
    }

    fun onSubmenuUnselected() {
        _menuState.update {
            it.copy(selectedSubmenu = null)
        }
    }

    fun onMenuToggle(index: Int) {
        _menuState.update { state ->
            val newExpandedMenuIndex = if (state.expandedMenuIndex == index) {
                null
            } else {
                index
            }
            state.copy(expandedMenuIndex = newExpandedMenuIndex)
        }
    }

    fun setDefaultState() {
        onMenuToggle(0)
        onSubmenuSelected(ChartSubmenuItem.PieChartBasic)
    }

    fun resolveDarkTheme(isSystemInDark: Boolean): Boolean {
        return when (themeState.value.darkMode) {
            DarkModeSettings.System -> isSystemInDark
            DarkModeSettings.On -> true
            DarkModeSettings.Off -> false
        }
    }
}
