import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.dautovicharis.charts.app.di.initKoin
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    onWasmReady {
        ComposeViewport("Charts") {
            JsMainScreen()
        }
    }
}
