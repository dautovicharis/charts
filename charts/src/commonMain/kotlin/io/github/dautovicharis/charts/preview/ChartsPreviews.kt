package io.github.dautovicharis.charts.preview

import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

internal const val CHARTS_PREVIEW_DEVICE: String = "spec:width=411dp,height=891dp,dpi=420"

/**
 * Multi-preview annotation (Light + Dark)
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(
    name = "Light",
    uiMode = UI_MODE_NIGHT_NO or UI_MODE_TYPE_NORMAL,
    device = CHARTS_PREVIEW_DEVICE
)
@Preview(
    name = "Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    device = CHARTS_PREVIEW_DEVICE
)
internal annotation class ChartsPreviewLightDark

