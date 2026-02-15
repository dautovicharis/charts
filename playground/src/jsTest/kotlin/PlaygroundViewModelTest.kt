import model.ChartType
import model.PlaygroundAction
import model.PlaygroundViewModel
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaygroundViewModelTest {
    @Test
    fun dispatch_updates_selected_chart_type() {
        val viewModel = PlaygroundViewModel()

        viewModel.dispatch(PlaygroundAction.SelectChart(ChartType.PIE))

        assertEquals(ChartType.PIE, viewModel.state.value.selectedChartType)
    }

    @Test
    fun dispatch_preserves_state_per_chart_session() {
        val viewModel = PlaygroundViewModel()

        viewModel.dispatch(PlaygroundAction.UpdateTitle("Line Session Title"))
        viewModel.dispatch(PlaygroundAction.SelectChart(ChartType.PIE))
        viewModel.dispatch(PlaygroundAction.UpdateTitle("Pie Session Title"))
        viewModel.dispatch(PlaygroundAction.SelectChart(ChartType.LINE))

        val lineTitle =
            viewModel.state.value.sessions
                .getValue(ChartType.LINE)
                .title
        val pieTitle =
            viewModel.state.value.sessions
                .getValue(ChartType.PIE)
                .title
        assertEquals("Line Session Title", lineTitle)
        assertEquals("Pie Session Title", pieTitle)
    }
}
