package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ProjectEntity
import com.example.data.local.PromptHistoryEntity
import com.example.ui.theme.DeveloperCyan
import com.example.ui.theme.DeveloperEmerald
import com.example.ui.theme.DeveloperViolet

@Composable
fun PromptHistoryDrawerContent(
    projects: List<ProjectEntity>,
    activeProjectId: Long?,
    recentPrompts: List<PromptHistoryEntity>,
    messages: List<ChatMessageEntity>,
    onSelectProject: (Long) -> Unit,
    onReRunPrompt: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0: Active Project History, 1: Prompt Library

    ModalDrawerSheet(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Drawer Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DeveloperCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = DeveloperCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Prompt & Response Log",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "History & Re-run Queue",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier.testTag("close_drawer_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Drawer")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selector
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth()
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Active History", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("history_tab_active")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Recent Prompts", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("history_tab_prompts")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (activeTab) {
                0 -> ActiveProjectExchangesList(
                    messages = messages,
                    onReRunPrompt = { prompt ->
                        onReRunPrompt(prompt)
                        onCloseDrawer()
                    }
                )
                1 -> RecentPromptsList(
                    recentPrompts = recentPrompts,
                    projects = projects,
                    onSelectProject = onSelectProject,
                    onReRunPrompt = { prompt ->
                        onReRunPrompt(prompt)
                        onCloseDrawer()
                    }
                )
            }
        }
    }
}

@Composable
private fun ActiveProjectExchangesList(
    messages: List<ChatMessageEntity>,
    onReRunPrompt: (String) -> Unit
) {
    val pairedExchanges = remember(messages) {
        val exchanges = mutableListOf<Pair<ChatMessageEntity, ChatMessageEntity?>>()
        var lastUserMsg: ChatMessageEntity? = null

        messages.forEach { msg ->
            if (msg.sender == "USER") {
                if (lastUserMsg != null) {
                    exchanges.add(Pair(lastUserMsg!!, null))
                }
                lastUserMsg = msg
            } else if (msg.sender == "AGENT" && lastUserMsg != null) {
                exchanges.add(Pair(lastUserMsg!!, msg))
                lastUserMsg = null
            }
        }
        if (lastUserMsg != null) {
            exchanges.add(Pair(lastUserMsg!!, null))
        }
        exchanges.reversed()
    }

    if (pairedExchanges.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No prompt exchanges recorded in this project yet.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(pairedExchanges, key = { it.first.id }) { (userMsg, agentMsg) ->
                var expanded by remember { mutableStateOf(false) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prompt_exchange_card_${userMsg.id}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = DeveloperCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = userMsg.message,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    maxLines = 2
                                )
                            }

                            IconButton(
                                onClick = { onReRunPrompt(userMsg.message) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("rerun_prompt_btn_${userMsg.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay,
                                    contentDescription = "Re-run prompt",
                                    tint = DeveloperEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (agentMsg != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                    .clickable { expanded = !expanded }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = DeveloperViolet,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (expanded) "Hide Agent Response" else "View Agent Response",
                                    fontSize = 11.sp,
                                    color = DeveloperViolet,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            AnimatedVisibility(visible = expanded) {
                                Text(
                                    text = agentMsg.message,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentPromptsList(
    recentPrompts: List<PromptHistoryEntity>,
    projects: List<ProjectEntity>,
    onSelectProject: (Long) -> Unit,
    onReRunPrompt: (String) -> Unit
) {
    if (recentPrompts.isEmpty() && projects.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No prompt history found.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (projects.isNotEmpty()) {
                item {
                    Text(
                        text = "Projects & Prompts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = DeveloperCyan,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(projects) { proj ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectProject(proj.id) }
                            .testTag("drawer_project_item_${proj.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(proj.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = proj.prompt,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }

                            IconButton(
                                onClick = { onReRunPrompt(proj.prompt) },
                                modifier = Modifier.size(28.dp).testTag("rerun_proj_prompt_${proj.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Re-build project",
                                    tint = DeveloperEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (recentPrompts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recent Searches & Ideas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = DeveloperViolet,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(recentPrompts) { prompt ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReRunPrompt(prompt.promptText) }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = DeveloperViolet,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = prompt.promptText,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
