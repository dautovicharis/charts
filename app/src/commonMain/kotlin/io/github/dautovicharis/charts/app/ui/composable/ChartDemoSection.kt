package io.github.dautovicharis.charts.app.ui.composable

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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.cd_refresh_data
import chartsproject.app.generated.resources.cd_regenerate_chart
import chartsproject.app.generated.resources.ic_replay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val StyleDetailsDefaultColumns = listOf("Parameter", "Value")

@Composable
fun ChartDemoSection(
    modifier: Modifier = Modifier,
    buttonsVisibility: Boolean = true,
    refreshVisible: Boolean = true,
    extraButtons: @Composable RowScope.() -> Unit = {},
    presetContent: @Composable () -> Unit = {},
    controlsContent: @Composable () -> Unit = {},
    chartContent: @Composable () -> Unit,
    onRefresh: () -> Unit,
) {
    val contentPadding = 16.dp
    var chartRefreshKey by remember { mutableIntStateOf(0) }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            presetContent()
        }

        ChartPreviewSection(chartContent = chartContent, refreshKey = chartRefreshKey)

        if (buttonsVisibility) {
            ChartActionRow(
                onRegenerateChart = { chartRefreshKey += 1 },
                extraButtons = extraButtons,
                onRefresh = onRefresh,
                refreshVisible = refreshVisible,
            )
        }

        controlsContent()
    }
}

@Composable
fun StyleDetailsTable(
    styleItems: StyleItems,
    columns: List<String> = StyleDetailsDefaultColumns,
    modifier: Modifier = Modifier,
) {
    val columnWeight = if (columns.isNotEmpty()) 1f / columns.size else 1f
    StyleDetailsTableContent(
        columns = columns,
        items = styleItems.items,
        columnWeight = columnWeight,
        modifier = modifier,
    )
}

@Composable
private fun StyleDetailsTableContent(
    columns: List<String>,
    items: List<StyleItem>,
    columnWeight: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        shape = MaterialTheme.shapes.small,
                    ),
        ) {
            columns.forEachIndexed { index, column ->
                StyleDetailsTableCell(
                    text = column,
                    weight = columnWeight,
                    fontWeight = FontWeight.SemiBold,
                )
                if (index < columns.lastIndex) {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }
        }

        items.forEach { item ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface),
                        shape = MaterialTheme.shapes.small,
                    ),
            ) {
                StyleDetailsTableCell(text = item.name, weight = columnWeight)
                Spacer(modifier = Modifier.width(1.dp))
                StyleDetailsTableCell(
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
private fun RowScope.StyleDetailsTableCell(
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
                        .weight(0.86f)
                        .padding(5.dp),
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = textFontWeight,
            )
            Box(
                modifier =
                    Modifier
                        .weight(0.14f)
                        .fillMaxHeight()
                        .background(color)
                        .border(0.5.dp, MaterialTheme.colorScheme.onSurface),
            )
        }
    }
}

@Composable
private fun ChartPreviewSection(
    chartContent: @Composable () -> Unit,
    refreshKey: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        key(refreshKey) {
            DrawerGestureLockContainer(
                modifier = Modifier.widthIn(max = LocalChartDemoMaxWidth.current),
            ) {
                chartContent()
            }
        }
    }
}

@Composable
private fun ChartActionRow(
    onRegenerateChart: () -> Unit,
    extraButtons: @Composable RowScope.() -> Unit,
    onRefresh: () -> Unit,
    refreshVisible: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
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

        if (refreshVisible) {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = stringResource(Res.string.cd_refresh_data),
                )
            }
        }
    }
}
