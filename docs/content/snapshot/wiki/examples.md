# Code Examples

### Pie Chart

```kotlin
@Composable
private fun AddDefaultPieChart() {
    val dataSet = listOf(8.0f, 23.0f, 54.0f, 32.0f, 12.0f, 37.0f, 7.0f, 23.0f, 43.0f)
        .toChartDataSet(
            title = stringResource(R.string.pie_chart),
            postfix = " °C"
        )
    PieChart(dataSet)
}
```


### Line Chart

```kotlin
@Composable
private fun AddDefaultLineChart() {
    val dataSet = listOf(
        8f, 23f, 54f, 32f, 12f, 37f, 7f, 23f, 43f
    ).toChartDataSet(
        title = stringResource(R.string.line_chart)
    )
    LineChart(dataSet)
}
```


### MultiLine Chart

```kotlin
@Composable
private fun AddDefaultMultiLineChart() {
    val items = listOf(
        "Cherry St." to listOf(26000.68f, 28000.34f, 32000.57f, 45000.57f),
        "Strawberry Mall" to listOf(15261.68f, 17810.34f, 40000.57f, 85000f),
        "Lime Av." to listOf(4000.87f, 5000.58f, 30245.81f, 135000.58f),
        "Apple Rd." to listOf(1000.87f, 9000.58f, 16544.81f, 100444.87f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = stringResource(R.string.line_chart),
        prefix = "$",
        categories = listOf("Jan", "Feb", "Mar", "Apr")
    )

    LineChart(dataSet)
}
```


### Bar Chart

```kotlin
@Composable
private fun AddDefaultBarChart() {
    BarChart(
        dataSet = listOf(100f, 50f, 5f, 60f, -50f, 50f, 60f)
            .toChartDataSet(
                title = stringResource(R.string.bar_chart),
                prefix = "$"
        )
    )
}
```


### Stacked Bar Chart

```kotlin
@Composable
private fun AddDefaultStackedBarChart() {
    val items = listOf(
        "Cherry St." to listOf(8261.68f, 8810.34f, 30000.57f),
        "Strawberry Mall" to listOf(8261.68f, 8810.34f, 30000.57f),
        "Lime Av." to listOf(1500.87f, 2765.58f, 33245.81f),
        "Apple Rd." to listOf(5444.87f, 233.58f, 67544.81f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = stringResource(R.string.bar_chart),
        prefix = "$",
        categories = listOf("Jan", "Feb", "Mar")
    )

    StackedBarChart(dataSet)
}
```

### Stacked Area Chart

```kotlin
@Composable
private fun AddDefaultStackedAreaChart() {
    val items = listOf(
        "Cherry St." to listOf(16000.68f, 19000.34f, 24000.57f, 28000.57f),
        "Strawberry Mall" to listOf(11000.68f, 15000.34f, 19000.57f, 23000f),
        "Lime Av." to listOf(7000.87f, 9000.58f, 13000.81f, 15500.58f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = stringResource(R.string.stacked_area_chart),
        prefix = "$",
        categories = listOf("Jan", "Feb", "Mar", "Apr")
    )

    StackedAreaChart(dataSet)
}
```

### Radar Chart

```kotlin
@Composable
private fun AddDefaultRadarChart() {
    val categories = listOf("Speed", "Strength", "Agility", "Stamina", "Skill", "Luck")

    val dataSet = listOf(78f, 62f, 90f, 55f, 70f, 80f)
        .toChartDataSet(
            title = stringResource(R.string.radar_chart),
            labels = categories
        )

    RadarChart(dataSet)
}
```


## Customizing Chart Styles

Use each chart's `*ChartDefaults.style(...)` factory to create a customized style:

### Pie Chart

```kotlin
@Composable
private fun AddCustomPieChart() {
    val pieColors = listOf(
        navyBlue, darkBlue, deepPurple, magenta, darkPink, coral, orange, yellow
    )

    val style = PieChartDefaults.style(
        borderColor = Color.White,
        donutPercentage = 40f,
        borderWidth = 6f,
        pieColors = pieColors,
        chartViewStyle = ChartViewDefaults.style(width = 200.dp)
    )

    val dataSet = listOf(8, 23, 54, 32, 12, 37, 7, 23, 43)
        .toChartDataSet(
            title = stringResource(R.string.pie_chart),
            postfix = " °C"
        )

    PieChart(dataSet = dataSet, style = style)
}
```


### Line Chart

```kotlin
@Composable
private fun AddCustomLineChart() {
    val style = LineChartDefaults.style(
        lineColor = ColorPalette.DataColor.deepPurple,
        pointColor = ColorPalette.DataColor.magenta,
        pointSize = 9f,
        bezier = false,
        dragPointColor = ColorPalette.DataColor.deepPurple,
        dragPointVisible = false,
        dragPointSize = 8f,
        dragActivePointSize = 15f,
        chartViewStyle = ChartViewDefaults.style(width = 200.dp)
    )

    val dataSet = listOf("10", "100", "20", "50", "150", "70", "10", "20", "40")
        .toChartDataSet(
            title = stringResource(R.string.line_chart)
        )

    LineChart(dataSet = dataSet, style = style)
}
```


### MultiLine Chart

```kotlin
@Composable
private fun AddCustomMultiLineChart() {
    val lineColors = listOf(
        navyBlue, darkBlue, deepPurple, magenta
    )
    val style = LineChartDefaults.style(
        lineColors = lineColors,
        dragPointVisible = false,
        pointVisible = true,
        pointColor = ColorPalette.DataColor.magenta,
        dragPointColor = deepPurple,
        chartViewStyle = ChartViewDefaults.style()
    )

    val items = listOf(
        "Cherry St." to listOf(26000.68f, 28000.34f, 32000.57f, 45000.57f),
        "Strawberry Mall" to listOf(15261.68f, 17810.34f, 40000.57f, 85000f),
        "Lime Av." to listOf(4000.87f, 5000.58f, 30245.81f, 135000.58f),
        "Apple Rd." to listOf(1000.87f, 9000.58f, 16544.81f, 100444.87f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = stringResource(R.string.line_chart),
        prefix = "$",
        categories = listOf("Jan", "Feb", "Mar", "Apr")
    )

    LineChart(dataSet = dataSet, style = style)
}
```


### Bar Chart

```kotlin
@Composable
private fun AddCustomBarChart() {
    val style = BarChartDefaults.style(
        barColor = ColorPalette.DataColor.deepPurple,
        space = 12.dp,
        chartViewStyle = ChartViewDefaults.style(width = 200.dp)
    )

    BarChart(
        dataSet = listOf(100f, 50f, 5f, 60f, 1f, 30f, 50f, 35f, 50f, -100f)
            .toChartDataSet(title = stringResource(R.string.bar_chart)),
        style = style
    )
}
```


### Stacked Bar Chart

```kotlin
@Composable
private fun AddCustomStackedBarChart() {
    val colors = listOf(navyBlue, darkBlue, deepPurple)
    val style =  StackedBarChartDefaults.style(
        barColors = colors,
        chartViewStyle = ChartViewDefaults.style(width = 240.dp)
    )

    val items = listOf(
        "Cherry St." to listOf(8261.68f, 8810.34f, 30000.57f),
        "Strawberry Mall" to listOf(8261.68f, 8810.34f, 30000.57f),
        "Lime Av." to listOf(1500.87f, 2765.58f, 33245.81f),
        "Apple Rd." to listOf(5444.87f, 233.58f, 67544.81f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = stringResource(R.string.stacked_bar_chart),
        prefix = "$",
        categories = listOf("Jan", "Feb", "Mar")
    )

    StackedBarChart(dataSet = dataSet, style = style)
}
```

### Stacked Area Chart

```kotlin
@Composable
private fun AddCustomStackedAreaChart() {
    val colors = listOf(navyBlue, darkBlue, deepPurple)
    val style = StackedAreaChartDefaults.style(
        areaColors = colors,
        lineColors = colors,
        fillAlpha = 0.3f,
        lineWidth = 3f,
        bezier = false,
        chartViewStyle = ChartViewDefaults.style(width = 240.dp)
    )

    val items = listOf(
        "Cherry St." to listOf(16000.68f, 19000.34f, 24000.57f, 28000.57f),
        "Strawberry Mall" to listOf(11000.68f, 15000.34f, 19000.57f, 23000f),
        "Lime Av." to listOf(7000.87f, 9000.58f, 13000.81f, 15500.58f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = stringResource(R.string.stacked_area_chart),
        prefix = "$",
        categories = listOf("Jan", "Feb", "Mar", "Apr")
    )

    StackedAreaChart(dataSet = dataSet, style = style)
}
```

### Radar Chart (Custom Style)

```kotlin
@Composable
private fun AddCustomRadarChart() {
    val categories = listOf("Speed", "Strength", "Agility", "Stamina", "Skill", "Luck")
    val items = listOf(
        "Falcon" to listOf(78f, 62f, 90f, 55f, 70f, 80f),
        "Tiger" to listOf(65f, 88f, 60f, 82f, 55f, 68f)
    )

    val dataSet = items.toMultiChartDataSet(
        title = stringResource(R.string.radar_chart),
        categories = categories
    )

    val style = RadarChartDefaults.style(
        lineWidth = 2.5f,
        fillAlpha = 0.2f,
        axisLabelVisible = true,
        categoryLegendVisible = true,
        categoryPinsVisible = true,
        chartViewStyle = ChartViewDefaults.style(width = 240.dp)
    )

    RadarChart(dataSet = dataSet, style = style)
}
```
