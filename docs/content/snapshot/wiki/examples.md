# Code Examples

## Default Preset

### Pie Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/pie_default.gif" alt="Pie Chart Default Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddDefaultPieChart() {
    val dataSet = listOf(32f, 21f, 24f, 14f, 9f).toChartDataSet(
        title = "Household Energy",
        postfix = "%",
        labels = listOf("Heating", "Cooling", "Appliances", "Water Heating", "Lighting")
    )

    PieChart(
        dataSet = dataSet,
        style = PieChartDefaults.style(),
        animateOnStart = true
    )
}
```

### Line Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/line_default.gif" alt="Line Chart Default Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddDefaultLineChart() {
    val dataSet = listOf(42f, 38f, 45f, 51f, 47f, 54f, 49f).toChartDataSet(
        title = "Daily Support Tickets",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    )

    LineChart(
        dataSet = dataSet,
        style = LineChartDefaults.style(),
        animateOnStart = true
    )
}
```

### MultiLine Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/multi_line_default.gif" alt="MultiLine Chart Default Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddDefaultMultiLineChart() {
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

    LineChart(
        dataSet = dataSet,
        style = LineChartDefaults.style(),
        animateOnStart = true
    )
}
```

### Bar Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/bar_default.gif" alt="Bar Chart Default Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddDefaultBarChart() {
    val dataSet = listOf(45f, -12f, 38f, 27f, -19f, 42f, 31f).toChartDataSet(
        title = "Daily Net Cash Flow",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    )

    BarChart(
        dataSet = dataSet,
        style = BarChartDefaults.style(),
        animateOnStart = true
    )
}
```

### Stacked Bar Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/stacked_bar_default.gif" alt="Stacked Bar Chart Default Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddDefaultStackedBarChart() {
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

    StackedBarChart(
        dataSet = dataSet,
        style = StackedBarChartDefaults.style(),
        animateOnStart = true
    )
}
```

### Stacked Area Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/stacked_area_default.gif" alt="Stacked Area Chart Default Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddDefaultStackedAreaChart() {
    val items = listOf(
        "Free Plan" to listOf(620f, 650f, 690f, 720f, 760f, 800f),
        "Standard Plan" to listOf(240f, 260f, 285f, 310f, 340f, 365f),
        "Premium Plan" to listOf(90f, 95f, 105f, 118f, 130f, 142f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = "Monthly Active Subscribers by Plan",
        categories = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
    )

    StackedAreaChart(
        dataSet = dataSet,
        style = StackedAreaChartDefaults.style(),
        animateOnStart = true
    )
}
```

### Radar Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/radar_default.gif" alt="Radar Chart Default Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddDefaultRadarChart() {
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

    RadarChart(
        dataSet = dataSet,
        style = RadarChartDefaults.style(),
        animateOnStart = true
    )
}
```

## Custom Preset

### Pie Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/pie_custom.gif" alt="Pie Chart Custom Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddCustomPieChart() {
    val dataSet = listOf(35f, 20f, 12f, 8f, 18f, 7f).toChartDataSet(
        title = "Monthly Budget Allocation",
        postfix = "%",
        labels = listOf("Housing", "Food", "Transport", "Healthcare", "Savings", "Leisure")
    )

    val pieColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer
    )

    val style = PieChartDefaults.style(
        borderColor = MaterialTheme.colorScheme.surface,
        donutPercentage = 40f,
        borderWidth = 5f,
        legendVisible = true,
        pieColors = pieColors
    )

    PieChart(
        dataSet = dataSet,
        style = style,
        animateOnStart = true
    )
}
```

### Line Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/line_custom.gif" alt="Line Chart Custom Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddCustomLineChart() {
    val dataSet = listOf(42f, 38f, 45f, 51f, 47f, 54f, 49f).toChartDataSet(
        title = "Daily Support Tickets",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    )

    val style = LineChartDefaults.style(
        lineColor = MaterialTheme.colorScheme.primary,
        pointColor = MaterialTheme.colorScheme.tertiary,
        pointSize = 9f,
        bezier = false,
        dragPointVisible = false,
        dragPointColor = MaterialTheme.colorScheme.secondary,
        dragPointSize = 8f,
        dragActivePointSize = 15f
    )

    LineChart(
        dataSet = dataSet,
        style = style,
        animateOnStart = true
    )
}
```

### MultiLine Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/multi_line_custom.gif" alt="MultiLine Chart Custom Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddCustomMultiLineChart() {
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

    val style = LineChartDefaults.style(
        lineColors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary
        ),
        bezier = false,
        pointVisible = true,
        dragPointVisible = false,
        pointColor = MaterialTheme.colorScheme.tertiary,
        dragPointColor = MaterialTheme.colorScheme.secondary
    )

    LineChart(
        dataSet = dataSet,
        style = style,
        animateOnStart = true
    )
}
```

### Bar Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/bar_custom.gif" alt="Bar Chart Custom Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddCustomBarChart() {
    val dataSet = listOf(45f, -12f, 38f, 27f, -19f, 42f, 31f).toChartDataSet(
        title = "Daily Net Cash Flow",
        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    )

    val style = BarChartDefaults.style(
        barColor = MaterialTheme.colorScheme.tertiary,
        gridColor = MaterialTheme.colorScheme.outlineVariant,
        axisColor = MaterialTheme.colorScheme.outline,
        xAxisLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        xAxisLabelTiltDegrees = 34f,
        selectionLineVisible = true,
        selectionLineColor = MaterialTheme.colorScheme.secondary,
        selectionLineWidth = 2f
    )

    BarChart(
        dataSet = dataSet,
        style = style,
        animateOnStart = true
    )
}
```

### Stacked Bar Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/stacked_bar_custom.gif" alt="Stacked Bar Chart Custom Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddCustomStackedBarChart() {
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

    val style = StackedBarChartDefaults.style(
        barColors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.error
        ),
        space = 8.dp
    )

    StackedBarChart(
        dataSet = dataSet,
        style = style,
        animateOnStart = true
    )
}
```

### Stacked Area Chart

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/stacked_area_custom.gif" alt="Stacked Area Chart Custom Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddCustomStackedAreaChart() {
    val items = listOf(
        "Free Plan" to listOf(620f, 650f, 690f, 720f, 760f, 800f),
        "Standard Plan" to listOf(240f, 260f, 285f, 310f, 340f, 365f),
        "Premium Plan" to listOf(90f, 95f, 105f, 118f, 130f, 142f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = "Monthly Active Subscribers by Plan",
        categories = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
    )

    val areaColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary
    )
    val style = StackedAreaChartDefaults.style(
        areaColors = areaColors,
        lineColors = areaColors,
        fillAlpha = 0.3f,
        lineVisible = true,
        lineWidth = 3.5f,
        bezier = false
    )

    StackedAreaChart(
        dataSet = dataSet,
        style = style,
        animateOnStart = true
    )
}
```

### Radar Chart (Custom Style)

<div style="text-align: center; margin: 1rem 0 1.25rem;">
  <img src="/content/snapshot/wiki/assets/radar_custom.gif" alt="Radar Chart Custom Demo" style="max-width: 360px; width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

```kotlin
@Composable
private fun AddCustomRadarChart() {
    val categories = listOf(
        "Performance",
        "Reliability",
        "Usability",
        "Security",
        "Scalability",
        "Observability"
    )
    val items = listOf(
        "Android App" to listOf(88f, 81f, 79f, 90f, 83f, 76f),
        "iOS App" to listOf(84f, 86f, 82f, 88f, 80f, 79f),
        "Web App" to listOf(78f, 74f, 85f, 83f, 88f, 84f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = "Platform Readiness Score",
        categories = categories
    )

    val style = RadarChartDefaults.style(
        lineColors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary
        ),
        lineWidth = 3.5f,
        pointColor = MaterialTheme.colorScheme.tertiary,
        pointSize = 5f,
        gridSteps = 6,
        gridLineWidth = 1.4f,
        axisLineColor = MaterialTheme.colorScheme.outline,
        axisLineWidth = 1.2f,
        axisLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        fillAlpha = 0.2f,
        categoryLegendVisible = false
    )

    RadarChart(
        dataSet = dataSet,
        style = style,
        animateOnStart = true
    )
}
```
