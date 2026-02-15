package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.app.ui.theme.AppTheme
import io.github.dautovicharis.charts.app.ui.theme.docsSlate
import model.PlaygroundAction
import model.PlaygroundRightPanelTab
import model.PlaygroundViewModel

private val WideLayoutBreakpoint = 1000.dp

@Composable
fun PlaygroundScreen() {
    val viewModel = remember { PlaygroundViewModel() }
    val registry = viewModel.registry
    val state by viewModel.state.collectAsState()

    fun dispatch(action: PlaygroundAction) = viewModel.dispatch(action)

    val selectedDefinition = registry.definition(state.selectedChartType)
    val selectedSession = state.sessions.getValue(state.selectedChartType)
    val settingsSchema = selectedDefinition.settingsSchema(selectedSession)

    AppTheme(
        theme = docsSlate,
        useDynamicColors = false,
    ) {
        val generatedSnippet = selectedDefinition.generateCode(selectedSession)
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val inlineChartSwitcher = maxWidth >= WideLayoutBreakpoint

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Playground",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = "Chart-agnostic lab",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        if (inlineChartSwitcher) {
                            ChartTypeSelector(
                                selectedType = state.selectedChartType,
                                primaryTypes = registry.primaryChartTypes,
                                overflowTypes = registry.overflowChartTypes,
                                onTypeSelected = { chartType -> dispatch(PlaygroundAction.SelectChart(chartType)) },
                                compact = true,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    if (!inlineChartSwitcher) {
                        ChartTypeSelector(
                            selectedType = state.selectedChartType,
                            primaryTypes = registry.primaryChartTypes,
                            overflowTypes = registry.overflowChartTypes,
                            onTypeSelected = { chartType -> dispatch(PlaygroundAction.SelectChart(chartType)) },
                            compact = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        )
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) {
                    val useWideLayout = maxWidth >= WideLayoutBreakpoint

                    if (useWideLayout) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            PlaygroundEditorPanel(
                                editorState = selectedSession.editorState,
                                validationMessage = selectedSession.validationMessage,
                                onCellChange = { rowIndex, columnId, value ->
                                    dispatch(
                                        PlaygroundAction.UpdateEditorCell(
                                            rowIndex = rowIndex,
                                            columnId = columnId,
                                            value = value,
                                        ),
                                    )
                                },
                                onAddRow = { dispatch(PlaygroundAction.AddRow) },
                                onDeleteRow = { rowIndex -> dispatch(PlaygroundAction.DeleteRow(rowIndex)) },
                                onRandomize = { dispatch(PlaygroundAction.Randomize) },
                                onReset = { dispatch(PlaygroundAction.Reset) },
                                modifier = Modifier.weight(30f).fillMaxHeight(),
                            )

                            PlaygroundChartPanel(
                                session = selectedSession,
                                definition = selectedDefinition,
                                onTitleChange = { title ->
                                    dispatch(PlaygroundAction.UpdateTitle(title))
                                },
                                modifier = Modifier.weight(40f).fillMaxHeight(),
                            )

                            RightPanel(
                                tab = state.rightPanelTab,
                                onTabChange = { tab -> dispatch(PlaygroundAction.SelectRightPanelTab(tab)) },
                                settingsContent = {
                                    PlaygroundSettingsPanel(
                                        session = selectedSession,
                                        descriptors = settingsSchema,
                                        onStyleStateChange = { nextStyle ->
                                            dispatch(PlaygroundAction.UpdateStyleState(nextStyle))
                                        },
                                        modifier =
                                            Modifier.fillMaxWidth().fillMaxHeight().verticalScroll(
                                                rememberScrollState(),
                                            ),
                                    )
                                },
                                codeContent = {
                                    PlaygroundCodePreviewPanel(
                                        snippet = generatedSnippet,
                                        mode = selectedSession.codegenMode,
                                        onModeChange = { mode -> dispatch(PlaygroundAction.UpdateCodegenMode(mode)) },
                                        expandToFillHeight = true,
                                        showTitle = false,
                                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                    )
                                },
                                modifier = Modifier.weight(30f).fillMaxHeight(),
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            PlaygroundChartPanel(
                                session = selectedSession,
                                definition = selectedDefinition,
                                onTitleChange = { title ->
                                    dispatch(PlaygroundAction.UpdateTitle(title))
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )

                            PlaygroundEditorPanel(
                                editorState = selectedSession.editorState,
                                validationMessage = selectedSession.validationMessage,
                                onCellChange = { rowIndex, columnId, value ->
                                    dispatch(
                                        PlaygroundAction.UpdateEditorCell(
                                            rowIndex = rowIndex,
                                            columnId = columnId,
                                            value = value,
                                        ),
                                    )
                                },
                                onAddRow = { dispatch(PlaygroundAction.AddRow) },
                                onDeleteRow = { rowIndex -> dispatch(PlaygroundAction.DeleteRow(rowIndex)) },
                                onRandomize = { dispatch(PlaygroundAction.Randomize) },
                                onReset = { dispatch(PlaygroundAction.Reset) },
                                modifier = Modifier.fillMaxWidth(),
                            )

                            RightPanel(
                                tab = state.rightPanelTab,
                                onTabChange = { tab -> dispatch(PlaygroundAction.SelectRightPanelTab(tab)) },
                                settingsContent = {
                                    PlaygroundSettingsPanel(
                                        session = selectedSession,
                                        descriptors = settingsSchema,
                                        onStyleStateChange = { nextStyle ->
                                            dispatch(PlaygroundAction.UpdateStyleState(nextStyle))
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                },
                                codeContent = {
                                    PlaygroundCodePreviewPanel(
                                        snippet = generatedSnippet,
                                        mode = selectedSession.codegenMode,
                                        onModeChange = { mode -> dispatch(PlaygroundAction.UpdateCodegenMode(mode)) },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RightPanel(
    tab: PlaygroundRightPanelTab,
    onTabChange: (PlaygroundRightPanelTab) -> Unit,
    settingsContent: @Composable () -> Unit,
    codeContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { onTabChange(PlaygroundRightPanelTab.SETTINGS) },
                    colors =
                        if (tab == PlaygroundRightPanelTab.SETTINGS) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Settings")
                }

                Button(
                    onClick = { onTabChange(PlaygroundRightPanelTab.CODE) },
                    colors =
                        if (tab == PlaygroundRightPanelTab.CODE) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Code")
                }
            }

            HorizontalDivider()

            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                color = MaterialTheme.colorScheme.surface,
            ) {
                when (tab) {
                    PlaygroundRightPanelTab.SETTINGS -> settingsContent()
                    PlaygroundRightPanelTab.CODE -> codeContent()
                }
            }
        }
    }
}
