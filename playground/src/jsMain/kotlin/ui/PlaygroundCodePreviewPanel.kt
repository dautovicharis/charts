package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import interop.copyTextToClipboard
import kotlinx.coroutines.delay
import model.CodegenMode
import model.GeneratedSnippet

private enum class CopyState {
    IDLE,
    COPIED,
    FAILED,
}

@Composable
fun PlaygroundCodePreviewPanel(
    snippet: GeneratedSnippet,
    mode: CodegenMode,
    onModeChange: (CodegenMode) -> Unit,
    expandToFillHeight: Boolean = false,
    showTitle: Boolean = true,
    modifier: Modifier = Modifier,
) {
    var copyState by remember(snippet.code) { mutableStateOf(CopyState.IDLE) }

    LaunchedEffect(copyState) {
        if (copyState == CopyState.IDLE) return@LaunchedEffect
        delay(1000)
        copyState = CopyState.IDLE
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .let { base -> if (expandToFillHeight) base.fillMaxHeight() else base }
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (showTitle) {
                    Text(text = "Code")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val copyLabel =
                        when (copyState) {
                            CopyState.IDLE -> "Copy"
                            CopyState.COPIED -> "Copied ✓"
                            CopyState.FAILED -> "Copy failed"
                        }

                    Button(
                        onClick = {
                            copyTextToClipboard(snippet.code) { success ->
                                copyState = if (success) CopyState.COPIED else CopyState.FAILED
                            }
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    ) {
                        Text(copyLabel)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { onModeChange(CodegenMode.MINIMAL) },
                    colors =
                        if (mode == CodegenMode.MINIMAL) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Minimal")
                }
                Button(
                    onClick = { onModeChange(CodegenMode.FULL) },
                    colors =
                        if (mode == CodegenMode.FULL) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Full")
                }
            }

            SelectionContainer(
                modifier =
                    if (expandToFillHeight) {
                        Modifier.fillMaxWidth().weight(1f)
                    } else {
                        Modifier.fillMaxWidth()
                    },
            ) {
                val styledCode = buildStyledCode(snippet.code, MaterialTheme.colorScheme)
                Text(
                    text = styledCode,
                    fontFamily = FontFamily.Monospace,
                    modifier =
                        if (expandToFillHeight) {
                            Modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState())
                        } else {
                            Modifier.fillMaxWidth().heightIn(min = 220.dp, max = 320.dp).verticalScroll(rememberScrollState())
                        },
                )
            }
        }
    }
}
