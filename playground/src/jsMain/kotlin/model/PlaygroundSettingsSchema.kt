package model

sealed interface SettingDescriptor {
    data class Section(
        val title: String,
    ) : SettingDescriptor

    data object Divider : SettingDescriptor

    data class Toggle(
        val id: String,
        val label: String,
        val defaultValue: Boolean,
        val read: (PlaygroundStyleState) -> Boolean?,
        val write: (PlaygroundStyleState, Boolean?) -> PlaygroundStyleState,
    ) : SettingDescriptor

    data class Slider(
        val id: String,
        val label: String,
        val defaultValue: Float,
        val min: Float,
        val max: Float,
        val steps: Int,
        val read: (PlaygroundStyleState) -> Float?,
        val write: (PlaygroundStyleState, Float?) -> PlaygroundStyleState,
        val format: (Float) -> String = { value -> value.toString() },
    ) : SettingDescriptor

    data class Dropdown(
        val id: String,
        val label: String,
        val options: List<DropdownOption>,
        val defaultValue: String,
        val read: (PlaygroundStyleState) -> String?,
        val write: (PlaygroundStyleState, String?) -> PlaygroundStyleState,
    ) : SettingDescriptor

    data class Color(
        val id: String,
        val label: String,
        val read: (PlaygroundStyleState) -> androidx.compose.ui.graphics.Color?,
        val write: (PlaygroundStyleState, androidx.compose.ui.graphics.Color?) -> PlaygroundStyleState,
    ) : SettingDescriptor

    data class ColorPalette(
        val id: String,
        val title: String,
        val itemCount: (PlaygroundChartSession) -> Int,
        val read: (PlaygroundStyleState) -> List<androidx.compose.ui.graphics.Color>?,
        val write: (PlaygroundStyleState, List<androidx.compose.ui.graphics.Color>?) -> PlaygroundStyleState,
    ) : SettingDescriptor
}

data class DropdownOption(
    val label: String,
    val value: String,
)
