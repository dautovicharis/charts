package io.github.dautovicharis.charts.app.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.cd_hide_parameters
import chartsproject.app.generated.resources.cd_refresh_data
import chartsproject.app.generated.resources.cd_regenerate_chart
import chartsproject.app.generated.resources.cd_show_parameters
import chartsproject.app.generated.resources.ic_replay
import chartsproject.app.generated.resources.ic_visibility_off
import chartsproject.app.generated.resources.ic_visibility_on
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StyleAndChartComponent(
    modifier: Modifier = Modifier,
    tableItems: StyleItems,
    columns: List<String> = listOf("Parameter", "Value"),
    buttonsVisibility: Boolean = true,
    extraButtons: @Composable RowScope.() -> Unit = {},
    chartItem: @Composable () -> Unit,
    onRefresh: () -> Unit,
) {
    val titleStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
    val columnWeight = if (columns.isNotEmpty()) 1f / columns.size else 1f
    val contentPadding = 16.dp

    var tableItemsVisible by remember { mutableStateOf(!buttonsVisibility) }
    var chartItemKey by remember { mutableIntStateOf(0) }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(contentPadding),
    ) {
        Text(
            modifier = Modifier.padding(bottom = 12.dp),
            text = tableItems.name,
            style = titleStyle,
            color = MaterialTheme.colorScheme.onSurface,
        )

        AnimatedVisibility(
            visible = tableItemsVisible,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            StyleAndChartContent(
                columns = columns,
                items = tableItems.items,
                columnWeight = columnWeight,
            )
        }

        StyleAndChartChartItem(chartItem, chartItemKey)

        if (buttonsVisibility) {
            StyleAndChartButtons(
                tableItemsVisible = tableItemsVisible,
                onToggleTable = { tableItemsVisible = !tableItemsVisible },
                onRegenerateChart = { chartItemKey += 1 },
                extraButtons = extraButtons,
                onRefresh = onRefresh,
            )
        }
    }
}

@Composable
private fun StyleAndChartContent(
    columns: List<String>,
    items: List<StyleItem>,
    columnWeight: Float,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            columns.forEachIndexed { index, column ->
                StyleAndChartItemRow(
                    text = column,
                    weight = columnWeight,
                    fontWeight = FontWeight.Medium,
                )
                if (index < columns.lastIndex) {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }
        }

        // Items
        items.forEach { item ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
                    .border(
                        BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface),
                        shape = MaterialTheme.shapes.small,
                    ),
            ) {
                StyleAndChartItemRow(text = item.name, weight = columnWeight)
                Spacer(modifier = Modifier.width(1.dp))
                StyleAndChartItemRow(
                    text = item.value,
                    weight = columnWeight,
                    color = item.color,
                    isChanged = item.isChanged,
                )
            }
        }
    }
}

@Composable
private fun RowScope.StyleAndChartItemRow(
    text: String,
    weight: Float,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color? = null,
    isChanged: Boolean = false,
) {
    val textFontWeight = if (isChanged) FontWeight.SemiBold else fontWeight

    if (color == null) {
        Text(
            modifier =
                Modifier
                    .weight(weight)
                    .padding(5.dp),
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = textFontWeight,
        )
    } else {
        Row(
            modifier =
                Modifier
                    .weight(weight)
                    .height(IntrinsicSize.Min),
        ) {
            Text(
                modifier =
                    Modifier
                        .weight(0.9f)
                        .padding(5.dp),
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = textFontWeight,
            )
            Box(
                modifier =
                    Modifier
                        .weight(0.1f)
                        .fillMaxHeight()
                        .background(color)
                        .border(0.5.dp, MaterialTheme.colorScheme.onSurface),
            )
        }
    }
}

@Composable
private fun StyleAndChartChartItem(
    chartItem: @Composable () -> Unit,
    chartItemKey: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        key(chartItemKey) {
            DrawerGestureLockContainer {
                chartItem()
            }
        }
    }
}

@Composable
private fun StyleAndChartButtons(
    tableItemsVisible: Boolean,
    onToggleTable: () -> Unit,
    onRegenerateChart: () -> Unit,
    extraButtons: @Composable RowScope.() -> Unit,
    onRefresh: () -> Unit,
) {
    val visibilityIcon =
        if (tableItemsVisible) Res.drawable.ic_visibility_on else Res.drawable.ic_visibility_off
    val visibilityContentDescription =
        stringResource(
            if (tableItemsVisible) Res.string.cd_hide_parameters else Res.string.cd_show_parameters,
        )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButton(
            onClick = onToggleTable,
        ) {
            Icon(
                painter = painterResource(visibilityIcon),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = visibilityContentDescription,
            )
        }

        IconButton(
            onClick = onRegenerateChart,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_replay),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(Res.string.cd_regenerate_chart),
            )
        }

        extraButtons()

        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(Res.string.cd_refresh_data),
            )
        }
    }
}
