package io.github.dautovicharis.charts.app.di

import io.github.dautovicharis.charts.app.ChartGalleryViewModel
import io.github.dautovicharis.charts.app.MainViewModel
import io.github.dautovicharis.charts.app.data.BarSampleUseCase
import io.github.dautovicharis.charts.app.data.ChartPreviewUseCase
import io.github.dautovicharis.charts.app.data.LineSampleUseCase
import io.github.dautovicharis.charts.app.data.LiveLatencyTimelineUseCase
import io.github.dautovicharis.charts.app.data.MultiLineSampleUseCase
import io.github.dautovicharis.charts.app.data.PieSampleUseCase
import io.github.dautovicharis.charts.app.data.RadarSampleUseCase
import io.github.dautovicharis.charts.app.data.StackedAreaSampleUseCase
import io.github.dautovicharis.charts.app.data.StackedBarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultBarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultChartPreviewUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultLiveLatencyTimelineUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultMultiLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultPieSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultRadarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedAreaSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedBarSampleUseCase
import io.github.dautovicharis.charts.app.demo.bar.BarChartViewModel
import io.github.dautovicharis.charts.app.demo.line.LineChartViewModel
import io.github.dautovicharis.charts.app.demo.multiline.MultiLineChartViewModel
import io.github.dautovicharis.charts.app.demo.pie.PieChartViewModel
import io.github.dautovicharis.charts.app.demo.radar.RadarChartViewModel
import io.github.dautovicharis.charts.app.demo.stackedarea.StackedAreaChartViewModel
import io.github.dautovicharis.charts.app.demo.stackedbar.StackedBarChartViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        single<ChartPreviewUseCase> { DefaultChartPreviewUseCase() }
        single<LiveLatencyTimelineUseCase> { DefaultLiveLatencyTimelineUseCase() }
        single<PieSampleUseCase> { DefaultPieSampleUseCase() }
        single<LineSampleUseCase> { DefaultLineSampleUseCase() }
        single<MultiLineSampleUseCase> { DefaultMultiLineSampleUseCase() }
        single<BarSampleUseCase> { DefaultBarSampleUseCase() }
        single<StackedBarSampleUseCase> { DefaultStackedBarSampleUseCase() }
        single<StackedAreaSampleUseCase> { DefaultStackedAreaSampleUseCase() }
        single<RadarSampleUseCase> { DefaultRadarSampleUseCase() }
        viewModel { PieChartViewModel(get()) }
        viewModel { ChartGalleryViewModel(get()) }
        viewModel { MainViewModel() }
        viewModel { LineChartViewModel(get()) }
        viewModel { MultiLineChartViewModel(get()) }
        viewModel { BarChartViewModel(get()) }
        viewModel { StackedBarChartViewModel(get()) }
        viewModel { StackedAreaChartViewModel(get()) }
        viewModel { RadarChartViewModel(get()) }
    }
