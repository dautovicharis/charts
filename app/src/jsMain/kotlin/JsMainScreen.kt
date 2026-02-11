import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.app.MainScreen
import io.github.dautovicharis.charts.app.ui.composable.LocalChartDemoMaxWidth

@Composable
internal fun JsMainScreen() {
    CompositionLocalProvider(LocalChartDemoMaxWidth provides 400.dp) {
        MainScreen()
    }
}
