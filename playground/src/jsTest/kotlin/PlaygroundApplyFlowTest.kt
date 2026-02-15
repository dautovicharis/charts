import model.ChartType
import model.CodegenMode
import model.PlaygroundAction
import model.PlaygroundDataModel
import model.PlaygroundReducer
import model.SettingDescriptor
import model.defaultPlaygroundState
import model.playgroundChartRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PlaygroundApplyFlowTest {
    private val registry = playgroundChartRegistry

    @Test
    fun registry_has_all_chart_types_once_and_deterministic_order() {
        val types = registry.charts.map { definition -> definition.type }
        assertEquals(ChartType.entries.toSet(), types.toSet())
        assertEquals(types.size, types.toSet().size)
        assertEquals(listOf(ChartType.LINE, ChartType.BAR, ChartType.PIE, ChartType.RADAR, ChartType.AREA), registry.primaryChartTypes)
        assertEquals(listOf(ChartType.MULTI_LINE, ChartType.STACKED_BAR), registry.overflowChartTypes)
    }

    @Test
    fun switching_chart_types_preserves_session_state() {
        var state = defaultPlaygroundState(registry)

        state = PlaygroundReducer.reduce(state, PlaygroundAction.UpdateTitle("Line Session Title"), registry)
        state = PlaygroundReducer.reduce(state, PlaygroundAction.SelectChart(ChartType.PIE), registry)
        state = PlaygroundReducer.reduce(state, PlaygroundAction.UpdateTitle("Pie Session Title"), registry)
        state = PlaygroundReducer.reduce(state, PlaygroundAction.SelectChart(ChartType.LINE), registry)

        val lineTitle = state.sessions.getValue(ChartType.LINE).title
        val pieTitle = state.sessions.getValue(ChartType.PIE).title
        assertEquals("Line Session Title", lineTitle)
        assertEquals("Pie Session Title", pieTitle)
    }

    @Test
    fun invalid_editor_value_keeps_last_known_good_applied_data() {
        var state = defaultPlaygroundState(registry)
        val before = state.sessions.getValue(ChartType.LINE).appliedData

        state =
            PlaygroundReducer.reduce(
                state,
                PlaygroundAction.UpdateEditorCell(
                    rowIndex = 0,
                    columnId = "value",
                    value = "oops",
                ),
                registry,
            )

        val afterSession = state.sessions.getValue(ChartType.LINE)
        assertEquals(before, afterSession.appliedData)
        assertTrue(afterSession.validationMessage.orEmpty().contains("valid numeric"))
    }

    @Test
    fun pie_row_count_updates_after_add_row_and_schema_item_count_tracks_it() {
        var state = defaultPlaygroundState(registry)
        state = PlaygroundReducer.reduce(state, PlaygroundAction.SelectChart(ChartType.PIE), registry)

        val beforeSession = state.sessions.getValue(ChartType.PIE)
        val beforeRows = beforeSession.editorState.rows
        val beforeData = beforeSession.appliedData as PlaygroundDataModel.SimpleSeries
        val beforeCount = beforeData.values.size

        state = PlaygroundReducer.reduce(state, PlaygroundAction.AddRow, registry)

        val afterSession = state.sessions.getValue(ChartType.PIE)
        val afterRows = afterSession.editorState.rows
        val afterData = afterSession.appliedData as PlaygroundDataModel.SimpleSeries
        assertEquals(beforeCount + 1, afterData.values.size)
        assertEquals(afterRows.maxOf { row -> row.id }, afterRows.first().id)
        assertEquals(beforeRows.map { row -> row.id }, afterRows.drop(1).map { row -> row.id })

        val descriptor =
            registry
                .definition(ChartType.PIE)
                .settingsSchema(afterSession)
                .filterIsInstance<SettingDescriptor.ColorPalette>()
                .firstOrNull()
        assertNotNull(descriptor)
        assertEquals(afterData.values.size, descriptor.itemCount(afterSession))
    }

    @Test
    fun codegen_mode_is_persisted_per_chart_session() {
        var state = defaultPlaygroundState(registry)

        state = PlaygroundReducer.reduce(state, PlaygroundAction.UpdateCodegenMode(CodegenMode.FULL), registry)
        state = PlaygroundReducer.reduce(state, PlaygroundAction.SelectChart(ChartType.PIE), registry)
        state = PlaygroundReducer.reduce(state, PlaygroundAction.UpdateCodegenMode(CodegenMode.MINIMAL), registry)
        state = PlaygroundReducer.reduce(state, PlaygroundAction.SelectChart(ChartType.LINE), registry)

        assertEquals(CodegenMode.FULL, state.sessions.getValue(ChartType.LINE).codegenMode)
        assertEquals(CodegenMode.MINIMAL, state.sessions.getValue(ChartType.PIE).codegenMode)
    }
}
