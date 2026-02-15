package model

sealed interface PlaygroundAction {
    data class SelectChart(
        val chartType: ChartType,
    ) : PlaygroundAction

    data class SelectRightPanelTab(
        val tab: PlaygroundRightPanelTab,
    ) : PlaygroundAction

    data class UpdateTitle(
        val title: String,
    ) : PlaygroundAction

    data class UpdateCodegenMode(
        val mode: CodegenMode,
    ) : PlaygroundAction

    data class UpdateStyleState(
        val styleState: PlaygroundStyleState,
    ) : PlaygroundAction

    data class UpdateEditorCell(
        val rowIndex: Int,
        val columnId: String,
        val value: String,
    ) : PlaygroundAction

    data object AddRow : PlaygroundAction

    data class DeleteRow(
        val rowIndex: Int,
    ) : PlaygroundAction

    data object Randomize : PlaygroundAction

    data object Reset : PlaygroundAction
}

fun defaultPlaygroundState(registry: PlaygroundChartRegistry): PlaygroundState {
    val sessions =
        registry.charts.associate { definition ->
            definition.type to definition.resetSession(codegenMode = CodegenMode.MINIMAL)
        }
    val initialType = registry.primaryChartTypes.firstOrNull() ?: registry.charts.first().type
    return PlaygroundState(
        selectedChartType = initialType,
        rightPanelTab = PlaygroundRightPanelTab.SETTINGS,
        sessions = sessions,
    )
}

object PlaygroundReducer {
    fun reduce(
        state: PlaygroundState,
        action: PlaygroundAction,
        registry: PlaygroundChartRegistry,
    ): PlaygroundState =
        when (action) {
            is PlaygroundAction.SelectChart -> state.copy(selectedChartType = action.chartType)
            is PlaygroundAction.SelectRightPanelTab -> state.copy(rightPanelTab = action.tab)
            is PlaygroundAction.UpdateTitle ->
                updateCurrentSession(state, registry) { session, _ ->
                    session.copy(title = action.title)
                }
            is PlaygroundAction.UpdateCodegenMode ->
                updateCurrentSession(state, registry) { session, _ ->
                    session.copy(codegenMode = action.mode)
                }
            is PlaygroundAction.UpdateStyleState ->
                updateCurrentSession(state, registry) { session, _ ->
                    session.copy(styleState = action.styleState)
                }
            is PlaygroundAction.UpdateEditorCell ->
                updateCurrentSession(state, registry) { session, definition ->
                    val updatedEditor =
                        session.editorState.updateCell(
                            rowIndex = action.rowIndex,
                            columnId = action.columnId,
                            value = action.value,
                        )
                    applyValidation(
                        session = session,
                        definition = definition,
                        updatedEditor = updatedEditor,
                    )
                }
            PlaygroundAction.AddRow ->
                updateCurrentSession(state, registry) { session, definition ->
                    val rowIndex = session.editorState.rows.size
                    val cells = definition.newRowCells(rowIndex, session.editorState.columns)
                    val updatedEditor = session.editorState.withAddedRow(cells, insertAtTop = true)
                    applyValidation(
                        session = session,
                        definition = definition,
                        updatedEditor = updatedEditor,
                    )
                }
            is PlaygroundAction.DeleteRow ->
                updateCurrentSession(state, registry) { session, definition ->
                    val updatedEditor = session.editorState.withDeletedRow(action.rowIndex)
                    applyValidation(
                        session = session,
                        definition = definition,
                        updatedEditor = updatedEditor,
                    )
                }
            PlaygroundAction.Randomize ->
                updateCurrentSession(state, registry) { session, definition ->
                    val randomizedEditor = definition.randomize(session.editorState)
                    applyValidation(
                        session = session,
                        definition = definition,
                        updatedEditor = randomizedEditor,
                    )
                }
            PlaygroundAction.Reset ->
                updateCurrentSession(state, registry) { session, definition ->
                    definition.resetSession(codegenMode = session.codegenMode)
                }
        }

    private fun updateCurrentSession(
        state: PlaygroundState,
        registry: PlaygroundChartRegistry,
        updater: (PlaygroundChartSession, PlaygroundChartDefinition) -> PlaygroundChartSession,
    ): PlaygroundState {
        val chartType = state.selectedChartType
        val definition = registry.definition(chartType)
        val current = state.sessions.getValue(chartType)
        val next = updater(current, definition)
        return state.copy(sessions = state.sessions + (chartType to next))
    }

    private fun applyValidation(
        session: PlaygroundChartSession,
        definition: PlaygroundChartDefinition,
        updatedEditor: DataEditorState,
    ): PlaygroundChartSession {
        val result = definition.validate(updatedEditor)
        val nextDataModel = result.dataModel
        val nextEditor = result.sanitizedEditor
        if (nextDataModel == null || nextEditor == null) {
            return session.copy(
                editorState = updatedEditor,
                validationMessage = result.message,
            )
        }

        return session.copy(
            editorState = nextEditor,
            appliedData = nextDataModel,
            validationMessage = result.message,
        )
    }
}
