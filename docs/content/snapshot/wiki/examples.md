# Code Examples

### Pie

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/pie_default.gif" alt="Pie Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun ShowPie() {
    val dataSet = listOf(32f, 21f, 24f, 14f, 9f).toChartDataSet(
        title = "Household Energy",
        postfix = "%",
        labels = listOf("Heating", "Cooling", "Appliances", "Water Heating", "Lighting")
    )

    PieChart(dataSet)
}
```

### Line

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/line_default.gif" alt="Line Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun ShowLine() {
    val dataSet = listOf(42f, 38f, 45f, 51f, 47f, 54f, 49f).toChartDataSet(
        title = "Daily Support Tickets",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    )

    LineChart(dataSet)
}
```

### MultiLine

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/multi_line_default.gif" alt="MultiLine Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun ShowMultiLine() {
    val items = listOf(
        "Web Store" to listOf(420f, 510f, 480f, 530f, 560f, 590f),
        "Mobile App" to listOf(360f, 420f, 410f, 460f, 500f, 540f),
        "Partner Sales" to listOf(280f, 320f, 340f, 360f, 390f, 420f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = "Weekly Revenue by Channel",
        prefix = "$",
        categories = listOf("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6")
    )

    LineChart(dataSet)
}
```

### Bar

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/bar_default.gif" alt="Bar Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun ShowBar() {
    val dataSet = listOf(45f, -12f, 38f, 27f, -19f, 42f, 31f).toChartDataSet(
        title = "Daily Net Cash Flow",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    )

    BarChart(dataSet)
}
```

### Stacked Bar

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/stacked_bar_default.gif" alt="Stacked Bar Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun ShowStackedBar() {
    val items = listOf(
        "North America" to listOf(320f, 340f, 360f, 390f),
        "Europe" to listOf(210f, 230f, 245f, 260f),
        "Asia Pacific" to listOf(180f, 205f, 225f, 250f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = "Quarterly Revenue by Region",
        prefix = "$",
        categories = listOf("Q1", "Q2", "Q3", "Q4")
    )

    StackedBarChart(dataSet)
}
```

### Stacked Area

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/stacked_area_default.gif" alt="Stacked Area Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun ShowStackedArea() {
    val items = listOf(
        "Free Plan" to listOf(620f, 650f, 690f, 720f, 760f, 800f),
        "Standard Plan" to listOf(240f, 260f, 285f, 310f, 340f, 365f),
        "Premium Plan" to listOf(90f, 95f, 105f, 118f, 130f, 142f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = "Monthly Active Subscribers by Plan",
        categories = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
    )

    StackedAreaChart(dataSet)
}
```

### Radar

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/radar_default.gif" alt="Radar Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun ShowRadar() {
    val categories = listOf(
        "Performance",
        "Reliability",
        "Usability",
        "Security",
        "Scalability",
        "Observability"
    )

    val dataSet = listOf(84f, 79f, 76f, 88f, 82f, 74f).toChartDataSet(
        title = "Platform Readiness Score",
        labels = categories
    )

    RadarChart(dataSet)
}
```


## Style Customization

To customize chart appearance, start from each chart's `*ChartDefaults.style(...)` factory and override only the fields you need.

```kotlin
@Composable
private fun ShowStyledBar() {
    val dataSet = listOf(45f, -12f, 38f, 27f, -19f, 42f, 31f).toChartDataSet(
        title = "Daily Net Cash Flow",
        prefix = "$",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    )

    val style = BarChartDefaults.style(
        barColor = Color(0xFF0F766E),
        barAlpha = 0.78f,
        space = 14.dp,
        gridVisible = true,
        gridSteps = 5,
        gridColor = Color(0xFF94A3B8),
        selectionLineColor = Color(0xFFEA580C),
        yAxisLabelCount = 6,
        chartViewStyle = ChartViewDefaults.style(
            width = 340.dp,
            cornerRadius = 18.dp,
            shadow = 2.dp,
            backgroundColor = Color(0xFFF8FAFC),
        ),
    )

    BarChart(dataSet = dataSet, style = style)
}
```
