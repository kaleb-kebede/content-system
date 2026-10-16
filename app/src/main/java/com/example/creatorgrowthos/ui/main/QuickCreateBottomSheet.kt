package com.example.creatorgrowthos.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                text = "QUICK CREATE ENGINE",
                style = Typography.labelSmall,
                color = ElectricBluePrimary,
                letterSpacing = 1.4.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Capture & Production",
                style = Typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Slate100
            )

            Spacer(modifier = Modifier.height(20.dp))

            QuickCreateItem(
                title = "New Idea",
                subtitle = "Rapid capture with demand score & content format",
                icon = Icons.Default.Lightbulb,
                accentColor = ElectricBluePrimary,
                containerColor = ElectricBluePrimary.copy(alpha = 0.12f),
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
                accentColor = PurpleAccentLight,
                containerColor = PurpleAccent.copy(alpha = 0.12f),
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
                accentColor = MintProgressLight,
                containerColor = MintProgress.copy(alpha = 0.12f),
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
    accentColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Slate850,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = containerColor,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
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
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = Typography.bodySmall,
                    color = Slate400
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Slate500,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

