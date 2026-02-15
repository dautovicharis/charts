package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import model.DataEditorState

@Composable
fun PlaygroundEditorPanel(
    editorState: DataEditorState,
    validationMessage: String?,
    onCellChange: (rowIndex: Int, columnId: String, value: String) -> Unit,
    onAddRow: () -> Unit,
    onDeleteRow: (rowIndex: Int) -> Unit,
    onRandomize: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canDeleteRows = editorState.rows.size > editorState.minRows
    val actionColumnWidth = 56.dp
    val scrollState = rememberScrollState()
    val currentRowIds = editorState.rows.map { row -> row.id }
    var previousRowIds by remember { mutableStateOf(currentRowIds) }
    var highlightedRowId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(currentRowIds) {
        val isTopInsertion =
            currentRowIds.size == previousRowIds.size + 1 &&
                currentRowIds.drop(1) == previousRowIds
        if (isTopInsertion) {
            highlightedRowId = currentRowIds.firstOrNull()
        }
        previousRowIds = currentRowIds
    }

    LaunchedEffect(highlightedRowId) {
        val targetId = highlightedRowId ?: return@LaunchedEffect
        delay(2200)
        if (highlightedRowId == targetId) {
            highlightedRowId = null
        }
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onAddRow,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                ) {
                    Text("+ New row")
                }
                Button(
                    onClick = onRandomize,
                    colors = ButtonDefaults.outlinedButtonColors(),
                ) {
                    Text("Randomize")
                }
                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.outlinedButtonColors(),
                ) {
                    Text("Reset")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                editorState.columns.forEach { column ->
                    Text(
                        text = column.label,
                        modifier = Modifier.weight(column.weight),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Spacer(modifier = Modifier.width(actionColumnWidth))
            }
            HorizontalDivider()

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                editorState.rows.forEachIndexed { rowIndex, row ->
                    val rowContainerColor =
                        if (row.id == highlightedRowId) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
                        }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = rowContainerColor,
                            ),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            editorState.columns.forEach { column ->
                                OutlinedTextField(
                                    value = row.cells[column.id].orEmpty(),
                                    onValueChange = { nextValue ->
                                        onCellChange(rowIndex, column.id, nextValue)
                                    },
                                    singleLine = true,
                                    modifier = Modifier.weight(column.weight),
                                )
                            }

                            IconButton(
                                onClick = { onDeleteRow(rowIndex) },
                                enabled = canDeleteRows,
                                modifier = Modifier.width(actionColumnWidth).alpha(if (canDeleteRows) 1f else 0.45f),
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Delete row",
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }
            }

            validationMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
