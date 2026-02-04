package io.github.dautovicharis.charts.app.di

import MainViewModel
import io.github.dautovicharis.charts.app.demo.bar.BarChartViewModel
import io.github.dautovicharis.charts.app.demo.line.LineChartViewModel
import io.github.dautovicharis.charts.app.demo.multiline.MultiLineChartViewModel
import io.github.dautovicharis.charts.app.demo.pie.PieChartViewModel
import io.github.dautovicharis.charts.app.demo.radar.RadarChartViewModel
import io.github.dautovicharis.charts.app.demo.stackedbar.StackedBarChartViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { PieChartViewModel() }
    viewModel { MainViewModel() }
    viewModel { LineChartViewModel() }
    viewModel { MultiLineChartViewModel() }
    viewModel { BarChartViewModel() }
    viewModel { StackedBarChartViewModel() }
    viewModel { RadarChartViewModel() }
}
