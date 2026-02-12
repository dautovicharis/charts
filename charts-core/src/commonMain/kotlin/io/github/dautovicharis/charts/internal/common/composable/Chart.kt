package io.github.dautovicharis.charts.internal.common.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dautovicharis.charts.internal.common.theme.ChartsDefaultTheme
import io.github.dautovicharis.charts.style.ChartViewStyle

@Composable
fun Chart(
    chartViewsStyle: ChartViewStyle,
    content: @Composable () -> Unit,
) {
    ChartsDefaultTheme {
        Box(
            modifier = chartViewsStyle.modifierMain,
        ) {
            Column(
                modifier =
                    Modifier
                        .wrapContentSize(),
            ) {
                content()
            }
        }
    }
}
