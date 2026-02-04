# Code Examples

## Basic Usage
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

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/pie-default.gif" alt="Pie Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>Pie Chart with default styling</em></p>
</div>

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

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/line-default.gif" alt="Line Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>Line Chart with default styling</em></p>
</div>

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

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/multi-line-default.gif" alt="MultiLine Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>MultiLine Chart with default styling</em></p>
</div>

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

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/bar-default.gif" alt="Bar Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>Bar Chart with default styling</em></p>
</div>

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

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/stacked-default.gif" alt="Stacked Bar Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>Stacked Bar Chart with default styling</em></p>
</div>

## Customizing Chart Styles

Each chart type has a `Defaults.style()` method that allows you to customize its appearance:

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
        chartViewStyle = ChartViewDemoStyle.custom(width = 200.dp)
    )

    val dataSet = listOf(8, 23, 54, 32, 12, 37, 7, 23, 43)
        .toChartDataSet(
            title = stringResource(R.string.pie_chart),
            postfix = " °C"
        )

    PieChart(dataSet = dataSet, style = style)
}
```

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/pie-custom.gif" alt="Custom Pie Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>Pie Chart with custom styling</em></p>
</div>

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
        chartViewStyle = ChartViewDemoStyle.custom(width = 200.dp)
    )

    val dataSet = listOf("10", "100", "20", "50", "150", "70", "10", "20", "40")
        .toChartDataSet(
            title = stringResource(R.string.line_chart)
        )

    LineChart(dataSet = dataSet, style = style)
}
```

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/line-custom.gif" alt="Custom Line Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>Line Chart with custom styling</em></p>
</div>

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
        chartViewStyle = ChartViewDemoStyle.custom()
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

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/multi-line-custom.gif" alt="Custom MultiLine Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>MultiLine Chart with custom styling</em></p>
</div>

### Bar Chart

```kotlin
@Composable
private fun AddCustomBarChart() {
    val style = BarChartDefaults.style(
        barColor = ColorPalette.DataColor.deepPurple,
        space = 12.dp,
        chartViewStyle = ChartViewDemoStyle.custom(width = 200.dp)
    )

    BarChart(
        dataSet = listOf(100f, 50f, 5f, 60f, 1f, 30f, 50f, 35f, 50f, -100f)
            .toChartDataSet(title = stringResource(R.string.bar_chart)),
        style = style
    )
}
```

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/bar-custom.gif" alt="Custom Bar Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>Bar Chart with custom styling</em></p>
</div>

### Stacked Bar Chart

```kotlin
@Composable
private fun AddCustomStackedBarChart() {
    val colors = listOf(navyBlue, darkBlue, deepPurple)
    val style =  StackedBarChartDefaults.style(
        barColors = colors,
        chartViewStyle = ChartViewDemoStyle.custom(width = 240.dp)
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

<div style="text-align: center; margin: 1.5rem 0; width: 200px; margin-left: auto; margin-right: auto;">
  <img src="assets/stacked-custom.gif" alt="Custom Stacked Bar Chart Animation" style="width: 100%; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <p><em>Stacked Bar Chart with custom styling</em></p>
</div>

## Next Steps

- Explore the [API Documentation](api/index.html) for detailed information about all available options
- Try the [Interactive Demo](jsdemo/index.html) to see the charts in action
- Check out these examples for more advanced usage:
    - [Pie Chart](https://github.com/dautovicharis/Charts/blob/main/app/src/commonMain/kotlin/io/github/dautovicharis/charts/app/demo/pie/PieChartDemo.kt)
    - [Line Chart](https://github.com/dautovicharis/Charts/blob/main/app/src/commonMain/kotlin/io/github/dautovicharis/charts/app/demo/line/LineDemo.kt)
    - [Multiline Chart](https://github.com/dautovicharis/Charts/blob/main/app/src/commonMain/kotlin/io/github/dautovicharis/charts/app/demo/multiline/MultiLineDemo.kt)
    - [Bar Chart](https://github.com/dautovicharis/Charts/blob/main/app/src/commonMain/kotlin/io/github/dautovicharis/charts/app/demo/bar/BarDemo.kt)
    - [Stacked Bar Chart](https://github.com/dautovicharis/Charts/blob/main/app/src/commonMain/kotlin/io/github/dautovicharis/charts/app/demo/stackedbar/StackedBarDemo.kt)
    - [Radar Chart](https://github.com/dautovicharis/Charts/blob/main/app/src/commonMain/kotlin/io/github/dautovicharis/charts/app/demo/radar/RadarDemo.kt)
