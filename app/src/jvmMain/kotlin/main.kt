import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.dautovicharis.charts.app.MainScreen
import io.github.dautovicharis.charts.app.di.initKoin

fun main() = application {
    initKoin()
    Window(
        title = "Charts Desktop Demo",
        state = rememberWindowState(width = 600.dp, height = 800.dp),
        onCloseRequest = ::exitApplication
    ) {
        MainScreen()
    }
}
