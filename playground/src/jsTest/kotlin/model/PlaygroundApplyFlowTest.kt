package model

import io.github.dautovicharis.charts.app.data.impl.DefaultBarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultMultiLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultPieSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultRadarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedAreaSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedBarSampleUseCase
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
        assertEquals(
            listOf(ChartType.LINE, ChartType.BAR, ChartType.PIE, ChartType.RADAR, ChartType.AREA),
            registry.primaryChartTypes,
        )
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
        assertEquals(afterRows.maxOf { row -> row.id }, afterRows.last().id)
        assertEquals(beforeRows.map { row -> row.id }, afterRows.dropLast(1).map { row -> row.id })
        assertEquals(afterRows.last().cells.getValue("label"), afterData.labels?.last())

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

    @Test
    fun default_sessions_use_app_sample_use_cases() {
        val state = defaultPlaygroundState(registry)

        val pieData = state.sessions.getValue(ChartType.PIE).appliedData as PlaygroundDataModel.SimpleSeries
        val pieSample = DefaultPieSampleUseCase().initialPieSample()
        assertEquals(
            pieSample.dataSet.data.item.points
                .map(Double::toFloat),
            pieData.values,
        )
        assertEquals(pieSample.segmentKeys, pieData.labels)

        val lineData = state.sessions.getValue(ChartType.LINE).appliedData as PlaygroundDataModel.SimpleSeries
        val lineDataSet = DefaultLineSampleUseCase().initialLineDataSet()
        assertEquals(
            lineDataSet.data.item.points
                .map(Double::toFloat),
            lineData.values,
        )
        assertEquals(
            lineDataSet.data.item.labels
                .toList(),
            lineData.labels,
        )

        val barData = state.sessions.getValue(ChartType.BAR).appliedData as PlaygroundDataModel.SimpleSeries
        val barDataSet = DefaultBarSampleUseCase().initialBarDataSet()
        assertEquals(
            barDataSet.data.item.points
                .map(Double::toFloat),
            barData.values,
        )
        assertEquals(
            barDataSet.data.item.labels
                .toList(),
            barData.labels,
        )

        val multiLineData = state.sessions.getValue(ChartType.MULTI_LINE).appliedData as PlaygroundDataModel.MultiSeries
        val multiLineDataSet = DefaultMultiLineSampleUseCase().initialMultiLineSample().dataSet
        assertEquals(multiLineDataSet.data.categories.toList(), multiLineData.xLabels)
        assertEquals(
            multiLineDataSet.data.items.map { item ->
                item.label
            },
            multiLineData.series.map { series -> series.name },
        )
        assertEquals(
            multiLineDataSet.data.items.map { item -> item.item.points.map(Double::toFloat) },
            multiLineData.series.map { series -> series.values },
        )

        val areaData = state.sessions.getValue(ChartType.AREA).appliedData as PlaygroundDataModel.MultiSeries
        val areaDataSet = DefaultStackedAreaSampleUseCase().initialStackedAreaSample().dataSet
        assertEquals(areaDataSet.data.categories.toList(), areaData.xLabels)
        assertEquals(areaDataSet.data.items.map { item -> item.label }, areaData.series.map { series -> series.name })
        assertEquals(
            areaDataSet.data.items.map { item -> item.item.points.map(Double::toFloat) },
            areaData.series.map { series -> series.values },
        )

        val stackedBarData =
            state.sessions
                .getValue(
                    ChartType.STACKED_BAR,
                ).appliedData as PlaygroundDataModel.StackedSeries
        val stackedBarDataSet = DefaultStackedBarSampleUseCase().initialStackedBarSample().dataSet
        assertEquals(stackedBarDataSet.data.items.map { item -> item.label }, stackedBarData.segmentNames)
        assertEquals(stackedBarDataSet.data.categories.toList(), stackedBarData.bars.map { bar -> bar.label })
        assertEquals(
            stackedBarDataSet.data.categories.indices.map { categoryIndex ->
                stackedBarDataSet.data.items.map { item ->
                    item.item.points[categoryIndex].toFloat()
                }
            },
            stackedBarData.bars.map { bar -> bar.values },
        )

        val radarData = state.sessions.getValue(ChartType.RADAR).appliedData as PlaygroundDataModel.RadarSeries
        val radarDataSet = DefaultRadarSampleUseCase().initialRadarSample().customDataSet
        assertEquals(radarDataSet.data.categories.toList(), radarData.axes)
        assertEquals(radarDataSet.data.items.map { item -> item.label }, radarData.entries.map { entry -> entry.name })
        assertEquals(
            radarDataSet.data.items.map { item -> item.item.points.map(Double::toFloat) },
            radarData.entries.map { entry -> entry.values },
        )
    }
}
