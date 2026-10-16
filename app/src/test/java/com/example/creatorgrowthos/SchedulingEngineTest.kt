package com.example.creatorgrowthos

import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.data.local.entity.ScheduleBlockEntity
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.TaskStatus
import com.example.creatorgrowthos.domain.scheduler.SchedulingEngine
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class SchedulingEngineTest {

    private lateinit var scheduler: SchedulingEngine

    @Before
    fun setup() {
        scheduler = SchedulingEngine()
    }

    @Test
    fun detectConflicts_overlappingTasks_returnsConflicts() {
        val now = System.currentTimeMillis()
        val task = ContentTaskEntity(
            id = "t1",
            title = "Task 1",
            scheduledStartTimeUtc = now,
            scheduledEndTimeUtc = now + 3600000L // 1 hour
        )
        val existingBlock = ScheduleBlockEntity(
            id = "b1",
            title = "Existing Block",
            blockType = "ROUTINE",
            startTimeUtc = now + 1800000L, // starts 30 min in
            endTimeUtc = now + 5400000L
        )

        val conflicts = scheduler.detectConflicts(task, listOf(existingBlock))
        assertTrue("Overlapping task intervals must produce scheduling conflicts", conflicts.isNotEmpty())
        assertEquals("t1", conflicts[0].conflictingTask.id)
    }

    @Test
    fun detectConflicts_nonOverlappingTasks_returnsNoConflicts() {
        val now = System.currentTimeMillis()
        val task = ContentTaskEntity(
            id = "t1",
            title = "Task 1",
            scheduledStartTimeUtc = now,
            scheduledEndTimeUtc = now + 3600000L
        )
        val existingBlock = ScheduleBlockEntity(
            id = "b1",
            title = "Existing Block",
            blockType = "ROUTINE",
            startTimeUtc = now + 3600000L, // starts exactly when task ends
            endTimeUtc = now + 7200000L
        )

        val conflicts = scheduler.detectConflicts(task, listOf(existingBlock))
        assertTrue("Sequential tasks without overlap must not conflict", conflicts.isEmpty())
    }

    @Test
    fun findNextAvailableSlot_respectsExistingBlocksAndWorkingHours() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 8)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startOfWork = cal.timeInMillis

        // Block occupies 08:00 - 10:00
        val busyBlock = ScheduleBlockEntity(
            id = "busy",
            title = "Recording Session",
            blockType = "RECORDING",
            startTimeUtc = startOfWork,
            endTimeUtc = startOfWork + 7200000L
        )

        // Find 60-min slot with 15-min buffer
        val slot = scheduler.findNextAvailableSlot(
            durationMinutes = 60,
            earliestStartUtc = startOfWork,
            existingBlocks = listOf(busyBlock),
            workStartHour = 8,
            workEndHour = 17,
            bufferMinutes = 15
        )

        assertNotNull("Should find a valid slot", slot)
        val expectedStart = busyBlock.endTimeUtc + (15 * 60 * 1000L)
        assertEquals(expectedStart, slot.startUtc)
        assertEquals(expectedStart + (60 * 60 * 1000L), slot.endUtc)
    }

    @Test
    fun prioritySorting_overdueCriticalTasksComeFirst() {
        val now = System.currentTimeMillis()
        val normalTask = ContentTaskEntity(
            id = "normal",
            title = "Normal Task",
            scheduledStartTimeUtc = now,
            scheduledEndTimeUtc = now + 3600000L,
            priority = TaskPriority.MEDIUM,
            isCritical = false
        )
        val criticalOverdue = ContentTaskEntity(
            id = "critical_overdue",
            title = "Critical Overdue",
            scheduledStartTimeUtc = now - 7200000L,
            scheduledEndTimeUtc = now - 3600000L,
            priority = TaskPriority.HIGH,
            isCritical = true,
            status = TaskStatus.MISSED
        )
        val criticalToday = ContentTaskEntity(
            id = "critical_today",
            title = "Critical Today",
            scheduledStartTimeUtc = now + 1000L,
            scheduledEndTimeUtc = now + 3600000L,
            priority = TaskPriority.CRITICAL,
            isCritical = true
        )

        val sorted = scheduler.sortTasksByPriorityOrder(
            listOf(normalTask, criticalToday, criticalOverdue),
            nowUtc = now,
            endOfDayUtc = now + 86400000L
        )

        assertEquals("Critical overdue task must be first in priority", "critical_overdue", sorted[0].id)
        assertEquals("critical_today", sorted[1].id)
        assertEquals("normal", sorted[2].id)
    }

    @Test
    fun generateRecoveryPlan_offersRescheduleAndSplitOptions() {
        val now = System.currentTimeMillis()
        val missedTask = ContentTaskEntity(
            id = "missed_1",
            title = "Edit Long-Form Video",
            scheduledStartTimeUtc = now - 7200000L,
            scheduledEndTimeUtc = now - 3600000L,
            estimatedDurationMinutes = 90,
            isCritical = true,
            status = TaskStatus.MISSED
        )

        val options = scheduler.generateRecoveryPlan(
            missedTask = missedTask,
            existingBlocks = emptyList(),
            dependentTasks = emptyList()
        )

        assertTrue("Must provide recovery options for critical missed tasks", options.size >= 3)
        val hasReschedule = options.any { it.actionType == "RESCHEDULE" }
        val hasSplit = options.any { it.actionType == "SPLIT" }
        val hasBlock = options.any { it.actionType == "MARK_BLOCKED" }

        assertTrue("Must propose valid reschedule slot", hasReschedule)
        assertTrue("Must propose split for 90min task", hasSplit)
        assertTrue("Must offer to mark blocked", hasBlock)
    }
}
