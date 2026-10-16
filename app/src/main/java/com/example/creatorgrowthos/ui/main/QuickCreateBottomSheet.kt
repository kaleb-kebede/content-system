package com.example.creatorgrowthos.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCreateBottomSheet(
    onDismiss: () -> Unit,
    onNewIdea: () -> Unit,
    onNewScript: () -> Unit,
    onSchedulePost: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Slate600) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "QUICK CREATE",
                style = Typography.labelSmall,
                color = ElectricBluePrimary,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Capture & Production",
                style = Typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Slate100
            )

            Spacer(modifier = Modifier.height(20.dp))

            QuickCreateItem(
                title = "New Idea",
                subtitle = "Rapid capture with demand score & content format",
                icon = Icons.Default.Lightbulb,
                onClick = {
                    onDismiss()
                    onNewIdea()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuickCreateItem(
                title = "New Script",
                subtitle = "Draft long-form video or short-form viral hook & beats",
                icon = Icons.Default.EditNote,
                onClick = {
                    onDismiss()
                    onNewScript()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuickCreateItem(
                title = "Schedule a Post",
                subtitle = "Plan a YouTube, TikTok, or Instagram publication slot",
                icon = Icons.Default.CalendarMonth,
                onClick = {
                    onDismiss()
                    onSchedulePost()
                }
            )
        }
    }
}

@Composable
private fun QuickCreateItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Slate850,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Slate800,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ElectricBluePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Text(
                    text = subtitle,
                    style = Typography.bodySmall,
                    color = Slate400
                )
            }
        }
    }
}
