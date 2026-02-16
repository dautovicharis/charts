package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.ChartType
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChartTypeSelector(
    selectedType: ChartType,
    primaryTypes: List<ChartType>,
    overflowTypes: List<ChartType>,
    onTypeSelected: (ChartType) -> Unit,
    compact: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val chartTypes = primaryTypes + overflowTypes
    val spacing = if (compact) 6.dp else 8.dp
    val buttonContentPadding =
        if (compact) {
            PaddingValues(horizontal = 8.dp, vertical = 6.dp)
        } else {
            PaddingValues(horizontal = 10.dp, vertical = 8.dp)
        }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
    ) {
        chartTypes.forEach { chartType ->
            val selected = selectedType == chartType
            Button(
                onClick = { onTypeSelected(chartType) },
                contentPadding = buttonContentPadding,
                colors =
                    if (selected) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                modifier = Modifier.weight(1f),
            ) {
                ChartTypeButtonIcon(
                    chartType = chartType,
                    compact = compact,
                )
            }
        }
    }
}

@Composable
private fun ChartTypeButtonIcon(
    chartType: ChartType,
    compact: Boolean,
) {
    val iconSize = if (compact) 22.dp else 24.dp
    Icon(
        painter = painterResource(chartTypeIconResource(chartType)),
        contentDescription = chartType.displayName,
        modifier = Modifier.size(iconSize),
    )
}
