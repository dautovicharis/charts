package ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chartsproject.playground.generated.resources.Res
import chartsproject.playground.generated.resources.playground_editor_add_row
import chartsproject.playground.generated.resources.playground_editor_delete_row_content_description
import chartsproject.playground.generated.resources.playground_editor_randomize
import chartsproject.playground.generated.resources.playground_editor_reset
import chartsproject.playground.generated.resources.playground_editor_row_number_header
import kotlinx.coroutines.delay
import model.DataEditorState
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlaygroundEditorPanel(
    editorState: DataEditorState,
    validationMessage: String?,
    invalidRowIds: Set<Int>,
    onCellChange: (rowIndex: Int, columnId: String, value: String) -> Unit,
    onAddRow: () -> Unit,
    onDeleteRow: (rowIndex: Int) -> Unit,
    onRandomize: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canDeleteRows = editorState.rows.size > editorState.minRows
    val rowNumberColumnWidth = 42.dp
    val actionColumnWidth = 56.dp
    val cellHeight = 56.dp
    val scrollState = rememberScrollState()
    val currentRowIds = editorState.rows.map { row -> row.id }
    val visibleRows = editorState.rows.asReversed()
    var previousRowIds by remember { mutableStateOf(currentRowIds) }
    var highlightedRowId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(currentRowIds) {
        val isAppendInsertion =
            currentRowIds.size == previousRowIds.size + 1 &&
                currentRowIds.dropLast(1) == previousRowIds
        if (isAppendInsertion) {
            highlightedRowId = currentRowIds.lastOrNull()
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
                    Text(stringResource(Res.string.playground_editor_add_row))
                }
                Button(
                    onClick = onRandomize,
                    colors = ButtonDefaults.outlinedButtonColors(),
                ) {
                    Text(stringResource(Res.string.playground_editor_randomize))
                }
                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.outlinedButtonColors(),
                ) {
                    Text(stringResource(Res.string.playground_editor_reset))
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(cellHeight),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.width(rowNumberColumnWidth).fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.playground_editor_row_number_header),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    VerticalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f),
                        modifier = Modifier.fillMaxHeight(),
                    )
                    editorState.columns.forEachIndexed { index, column ->
                        Box(
                            modifier = Modifier.weight(column.weight).fillMaxHeight().padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = column.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (index < editorState.columns.lastIndex) {
                            VerticalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f),
                                modifier = Modifier.fillMaxHeight(),
                            )
                        }
                    }
                    Box(modifier = Modifier.width(actionColumnWidth).fillMaxHeight())
                }
            }
            HorizontalDivider()

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                visibleRows.forEachIndexed { visualRowIndex, row ->
                    val rowIndex = editorState.rows.lastIndex - visualRowIndex
                    val rowId = rowIndex + 1
                    val rowContainerColor =
                        if (rowId in invalidRowIds) {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.32f)
                        } else if (row.id == highlightedRowId) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        } else {
                            if (visualRowIndex % 2 == 0) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.16f)
                            }
                        }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            modifier = Modifier.width(rowNumberColumnWidth),
                            color = rowContainerColor,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(cellHeight),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = (rowIndex + 1).toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        editorState.columns.forEach { column ->
                            Surface(
                                modifier = Modifier.weight(column.weight),
                                color = rowContainerColor,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)),
                            ) {
                                TextField(
                                    value = row.cells[column.id].orEmpty(),
                                    onValueChange = { nextValue ->
                                        onCellChange(rowIndex, column.id, nextValue)
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().height(cellHeight),
                                    colors =
                                        TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            disabledContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                        ),
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.width(actionColumnWidth),
                            color = rowContainerColor,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(cellHeight),
                                contentAlignment = Alignment.Center,
                            ) {
                                IconButton(
                                    onClick = { onDeleteRow(rowIndex) },
                                    enabled = canDeleteRows,
                                    modifier = Modifier.fillMaxSize().alpha(if (canDeleteRows) 1f else 0.45f),
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription =
                                            stringResource(Res.string.playground_editor_delete_row_content_description),
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            validationMessage?.let { message ->
                val isAppliedMessage = message.startsWith("Applied ")
                Text(
                    text = message,
                    color = if (isAppliedMessage) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
