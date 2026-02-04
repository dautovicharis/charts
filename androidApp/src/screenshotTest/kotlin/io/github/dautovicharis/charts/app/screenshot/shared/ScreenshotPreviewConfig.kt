package io.github.dautovicharis.charts.app.screenshot.shared

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

internal const val PREVIEW_DEVICE: String = "spec:width=411dp,height=891dp,dpi=420"

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = PREVIEW_DEVICE
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = PREVIEW_DEVICE
)
internal annotation class ScreenshotPreview
