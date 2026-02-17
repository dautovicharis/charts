package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import chartsproject.app.generated.resources.charts_logo
import chartsproject.app.generated.resources.ic_github
import chartsproject.playground.generated.resources.Res
import chartsproject.playground.generated.resources.playground_editor_add_row
import chartsproject.playground.generated.resources.playground_editor_delete_row_content_description
import chartsproject.playground.generated.resources.playground_editor_randomize
import chartsproject.playground.generated.resources.playground_editor_reset
import chartsproject.playground.generated.resources.playground_editor_row_number_header
import chartsproject.playground.generated.resources.playground_logo_content_description
import chartsproject.playground.generated.resources.playground_open_github_content_description
import chartsproject.playground.generated.resources.playground_title
import io.github.dautovicharis.charts.app.ui.composable.ChartsStartupGate
import io.github.dautovicharis.charts.app.ui.composable.StartupResources
import io.github.dautovicharis.charts.app.ui.composable.rememberStartupResourcesReady
import io.github.dautovicharis.charts.app.ui.theme.AppTheme
import io.github.dautovicharis.charts.app.ui.theme.docsSlate
import model.ChartType
import org.jetbrains.skiko.wasm.onWasmReady
import chartsproject.app.generated.resources.Res as AppRes

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        ComposeViewport("Playground") {
            val resourcesReady = rememberPlaygroundStartupResourcesReady()

            AppTheme(
                theme = docsSlate,
                useDynamicColors = false,
            ) {
                ChartsStartupGate(resourcesReady) {
                    PlaygroundScreen()
                }
            }
        }
    }
}

@Composable
private fun rememberPlaygroundStartupResourcesReady(): Boolean {
    val iconResources =
        remember {
            listOf(
                ChartType.PIE,
                ChartType.LINE,
                ChartType.MULTI_LINE,
                ChartType.BAR,
                ChartType.STACKED_BAR,
                ChartType.AREA,
                ChartType.RADAR,
            ).map(::chartTypeIconResource).distinct()
        }

    val resources =
        remember(iconResources) {
            StartupResources(
                bitmapDrawables = listOf(AppRes.drawable.charts_logo),
                vectorDrawables = listOf(AppRes.drawable.ic_github) + iconResources,
                strings =
                    listOf(
                        Res.string.playground_title,
                        Res.string.playground_logo_content_description,
                        Res.string.playground_open_github_content_description,
                        Res.string.playground_editor_add_row,
                        Res.string.playground_editor_randomize,
                        Res.string.playground_editor_reset,
                        Res.string.playground_editor_row_number_header,
                        Res.string.playground_editor_delete_row_content_description,
                    ),
            )
        }

    return rememberStartupResourcesReady(resources)
}
