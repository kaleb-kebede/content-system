package com.example.creatorgrowthos.di

import com.example.creatorgrowthos.ui.screens.analytics.AnalyticsViewModel
import com.example.creatorgrowthos.ui.screens.backup.BackupViewModel
import com.example.creatorgrowthos.ui.screens.calendar.CalendarViewModel
import com.example.creatorgrowthos.ui.screens.extra.ExtrasViewModel
import com.example.creatorgrowthos.ui.screens.goals.GoalsViewModel
import com.example.creatorgrowthos.ui.screens.ideas.IdeasViewModel
import com.example.creatorgrowthos.ui.screens.pipeline.PipelineViewModel
import com.example.creatorgrowthos.ui.screens.publishing.PublishingViewModel
import com.example.creatorgrowthos.ui.screens.repurposing.RepurposingViewModel
import com.example.creatorgrowthos.ui.screens.script.ScriptViewModel
import com.example.creatorgrowthos.ui.screens.settings.SettingsViewModel
import com.example.creatorgrowthos.ui.screens.today.TodayViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TodayViewModel(get(), get(), get()) }
    viewModel { PipelineViewModel(get()) }
    viewModel { CalendarViewModel(get(), get()) }
    viewModel { AnalyticsViewModel(get()) }
    viewModel { IdeasViewModel(get()) }
    viewModel { ScriptViewModel(get()) }
    viewModel { PublishingViewModel(get(), get()) }
    viewModel { RepurposingViewModel(get(), get()) }
    viewModel { GoalsViewModel(get()) }
    viewModel { BackupViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { ExtrasViewModel(get(), get()) }
}
