package interop

import kotlinx.browser.window
import kotlin.js.Promise

fun copyTextToClipboard(
    text: String,
    onResult: (Boolean) -> Unit,
) {
    val clipboard = window.navigator.asDynamic().clipboard
    if (clipboard == null) {
        onResult(false)
        return
    }

    val promise = clipboard.writeText(text) as Promise<Unit>
    promise.then(
        {
            onResult(true)
            Unit
        },
        {
            onResult(false)
            Unit
        },
    )
}
