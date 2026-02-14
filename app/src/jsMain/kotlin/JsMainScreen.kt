import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.app.LocalChartGalleryColumns
import io.github.dautovicharis.charts.app.MainScreen
import io.github.dautovicharis.charts.app.ui.composable.LocalChartDemoMaxWidth

@Composable
internal fun JsMainScreen() {
    CompositionLocalProvider(
        LocalChartDemoMaxWidth provides 500.dp,
        LocalChartGalleryColumns provides 3,
    ) {
        MainScreen()
    }
}
