package com.example.creatorgrowthos.ui.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.data.repository.ContentRepository
import com.example.creatorgrowthos.data.repository.PublishingRepository
import com.example.creatorgrowthos.data.repository.TaskScheduleRepository
import com.example.creatorgrowthos.data.repository.WeeklyContentPlanSummary
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.TaskStatus
import com.example.creatorgrowthos.domain.model.WorkflowStage
import com.example.creatorgrowthos.domain.scheduler.RecoveryOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TodayUiState(
    val currentDateFormatted: String = "",
    val activeTask: ContentTaskEntity? = null,
    val nextTask: ContentTaskEntity? = null,
    val todayTasks: List<ContentTaskEntity> = emptyList(),
    val completionPercentage: Float = 0f,
    val overdueTasks: List<ContentTaskEntity> = emptyList(),
    val criticalMissedTasks: List<ContentTaskEntity> = emptyList(),
    val scheduledPostsToday: List<ContentPublicationEntity> = emptyList(),
    val postsAwaitingConfirmation: List<ContentPublicationEntity> = emptyList(),
    val weeklyProgress: WeeklyContentPlanSummary? = null,
    val selectedTaskForRecovery: ContentTaskEntity? = null,
    val recoveryOptions: List<RecoveryOption> = emptyList(),
    val showRecoveryDialog: Boolean = false
)

class TodayViewModel(
    private val taskRepository: TaskScheduleRepository,
    private val contentRepository: ContentRepository,
    private val publishingRepository: PublishingRepository
) : ViewModel() {

    private val _recoveryState = MutableStateFlow<Pair<ContentTaskEntity?, List<RecoveryOption>>>(Pair(null, emptyList()))
    private val _showRecoveryDialog = MutableStateFlow(false)
    val showRecoveryDialog = _showRecoveryDialog.asStateFlow()

    private val calendar = Calendar.getInstance()

    init {
        initToday()
    }

    fun initToday() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val startOfDay = getStartOfDayUtc(now)
            val endOfDay = getEndOfDayUtc(now)
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))

            taskRepository.ensureDailyRoutineGenerated(dateStr, startOfDay)
            taskRepository.auditTaskStatus(now)
        }
    }

    private fun getStartOfDayUtc(now: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfDayUtc(now: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    private fun getWeekRange(now: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_WEEK, 6)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis
        return Pair(start, end)
    }

    val uiState: StateFlow<TodayUiState> = combine(
        taskRepository.getTodayTasks(getStartOfDayUtc(System.currentTimeMillis()), getEndOfDayUtc(System.currentTimeMillis())),
        taskRepository.observeActiveTask(),
        taskRepository.getCriticalMissedTasks(),
        publishingRepository.getPublicationsScheduledForDay(getStartOfDayUtc(System.currentTimeMillis()), getEndOfDayUtc(System.currentTimeMillis())),
        publishingRepository.getPublicationsAwaitingConfirmation(),
        _recoveryState
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val tasks = args[0] as List<ContentTaskEntity>
        val activeTask = args[1] as ContentTaskEntity?
        @Suppress("UNCHECKED_CAST")
        val criticalMissed = args[2] as List<ContentTaskEntity>
        @Suppress("UNCHECKED_CAST")
        val scheduledPosts = args[3] as List<ContentPublicationEntity>
        @Suppress("UNCHECKED_CAST")
        val awaitingConfirm = args[4] as List<ContentPublicationEntity>
        @Suppress("UNCHECKED_CAST")
        val recovery = args[5] as Pair<ContentTaskEntity?, List<RecoveryOption>>

        val now = System.currentTimeMillis()
        val total = tasks.size
        val completed = tasks.count { it.status == TaskStatus.COMPLETED }
        val pct = if (total > 0) (completed.toFloat() / total) * 100f else 0f

        val overdue = tasks.filter {
            it.scheduledEndTimeUtc < now && it.status != TaskStatus.COMPLETED && it.status != TaskStatus.SKIPPED
        }

        val nextTask = tasks.firstOrNull {
            it.status == TaskStatus.UPCOMING && it.scheduledStartTimeUtc >= now
        }

        val dateFormatted = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(now))

        val (weekStart, weekEnd) = getWeekRange(now)
        val weeklySummary = try {
            publishingRepository.getWeeklyPublishingSummary(weekStart, weekEnd)
        } catch (e: Exception) {
            null
        }

        TodayUiState(
            currentDateFormatted = dateFormatted,
            activeTask = activeTask,
            nextTask = nextTask,
            todayTasks = tasks.sortedBy { it.scheduledStartTimeUtc },
            completionPercentage = pct,
            overdueTasks = overdue,
            criticalMissedTasks = criticalMissed,
            scheduledPostsToday = scheduledPosts,
            postsAwaitingConfirmation = awaitingConfirm,
            weeklyProgress = weeklySummary,
            selectedTaskForRecovery = recovery.first,
            recoveryOptions = recovery.second,
            showRecoveryDialog = recovery.first != null
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TodayUiState()
    )

    fun startTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.startTask(taskId)
        }
    }

    fun pauseTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.pauseTask(taskId)
        }
    }

    fun resumeTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.resumeTask(taskId)
        }
    }

    fun completeTask(taskId: String, actualDurationMinutes: Int = 30) {
        viewModelScope.launch {
            taskRepository.completeTask(taskId, actualDurationMinutes)
        }
    }

    fun skipTask(taskId: String, reason: String) {
        viewModelScope.launch {
            taskRepository.skipTask(taskId, reason)
            dismissRecoveryDialog()
        }
    }

    fun blockTask(taskId: String, reason: String) {
        viewModelScope.launch {
            taskRepository.blockTask(taskId, reason)
            dismissRecoveryDialog()
        }
    }

    fun resolveBlocker(taskId: String) {
        viewModelScope.launch {
            taskRepository.resolveBlocker(taskId)
        }
    }

    fun rescheduleTask(taskId: String, newStartUtc: Long, newEndUtc: Long, reason: String) {
        viewModelScope.launch {
            taskRepository.rescheduleTask(taskId, newStartUtc, newEndUtc, reason)
            dismissRecoveryDialog()
        }
    }

    fun recordEmergencyOverride(taskId: String) {
        viewModelScope.launch {
            taskRepository.recordEmergencyOverride(taskId)
            dismissRecoveryDialog()
        }
    }

    fun openRecoveryDialog(task: ContentTaskEntity) {
        viewModelScope.launch {
            val options = taskRepository.getRecoveryOptionsForTask(task.id)
            _recoveryState.value = Pair(task, options)
            _showRecoveryDialog.value = true
        }
    }

    fun dismissRecoveryDialog() {
        _recoveryState.value = Pair(null, emptyList())
        _showRecoveryDialog.value = false
    }
}
