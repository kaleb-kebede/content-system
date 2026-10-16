package com.example.creatorgrowthos.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.data.repository.PublishingRepository
import com.example.creatorgrowthos.data.repository.TaskScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarUiState(
    val selectedDateUtc: Long = System.currentTimeMillis(),
    val selectedDateString: String = "",
    val tasksForDay: List<ContentTaskEntity> = emptyList(),
    val publicationsForDay: List<ContentPublicationEntity> = emptyList(),
    val isMonthView: Boolean = false
)

class CalendarViewModel(
    private val taskRepository: TaskScheduleRepository,
    private val publishingRepository: PublishingRepository
) : ViewModel() {

    private val _selectedDateUtc = MutableStateFlow(System.currentTimeMillis())

    val uiState: StateFlow<CalendarUiState> = _selectedDateUtc.flatMapLatest { selectedTime ->
        val startOfDay = getStartOfDay(selectedTime)
        val endOfDay = getEndOfDay(selectedTime)
        val dateStr = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(selectedTime))

        combine(
            taskRepository.getTodayTasks(startOfDay, endOfDay),
            publishingRepository.getPublicationsScheduledForDay(startOfDay, endOfDay)
        ) { tasks, publications ->
            CalendarUiState(
                selectedDateUtc = selectedTime,
                selectedDateString = dateStr,
                tasksForDay = tasks.sortedBy { it.scheduledStartTimeUtc },
                publicationsForDay = publications
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CalendarUiState()
    )

    fun selectDate(dateUtc: Long) {
        _selectedDateUtc.value = dateUtc
    }

    fun nextDay() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = _selectedDateUtc.value
        cal.add(Calendar.DAY_OF_YEAR, 1)
        _selectedDateUtc.value = cal.timeInMillis
    }

    fun previousDay() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = _selectedDateUtc.value
        cal.add(Calendar.DAY_OF_YEAR, -1)
        _selectedDateUtc.value = cal.timeInMillis
    }

    fun goToToday() {
        _selectedDateUtc.value = System.currentTimeMillis()
    }

    private fun getStartOfDay(now: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfDay(now: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
