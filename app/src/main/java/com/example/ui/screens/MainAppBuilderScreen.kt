package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectEntity
import com.example.ui.components.*
import com.example.ui.components.PromptHistoryDrawerContent
import com.example.ui.theme.DeveloperCyan
import com.example.ui.theme.DeveloperEmerald
import com.example.ui.theme.DeveloperViolet
import com.example.ui.viewmodel.AppBuilderViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBuilderScreen(
    viewModel: AppBuilderViewModel,
    modifier: Modifier = Modifier
) {
    val activeProject by viewModel.activeProject.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val activeFiles by viewModel.activeProjectFiles.collectAsState()
    val activeFile by viewModel.activeFile.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val viewportMode by viewModel.previewViewport.collectAsState()
    val terminalLogs by viewModel.terminalLogs.collectAsState()
    val settings by viewModel.appSettings.collectAsState()

    val recentPrompts by viewModel.recentPrompts.collectAsState()

    var showProjectDropdown by remember { mutableStateOf(false) }
    var showNewProjectDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PromptHistoryDrawerContent(
                projects = allProjects,
                activeProjectId = activeProject?.id,
                recentPrompts = recentPrompts,
                messages = chatMessages,
                onSelectProject = { projId ->
                    viewModel.selectProject(projId)
                    scope.launch { drawerState.close() }
                },
                onReRunPrompt = { prompt ->
                    if (activeProject == null) {
                        viewModel.createNewApp(prompt)
                    } else {
                        viewModel.sendChatMessage(prompt)
                    }
                    scope.launch { drawerState.close() }
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("open_history_drawer_btn")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Open History Drawer")
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.testTag("app_top_bar")
                        ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DeveloperCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeProject?.name ?: "AI App Builder",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { showProjectDropdown = !showProjectDropdown },
                                    modifier = Modifier.size(24.dp).testTag("project_selector_btn")
                                ) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select project")
                                }
                            }
                            Text(
                                text = activeProject?.language ?: "Autonomous Software Agent",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = showProjectDropdown,
                            onDismissRequest = { showProjectDropdown = false }
                        ) {
                            allProjects.forEach { proj ->
                                DropdownMenuItem(
                                    text = { Text(proj.name, fontWeight = if (proj.id == activeProject?.id) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        viewModel.selectProject(proj.id)
                                        showProjectDropdown = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Folder,
                                            contentDescription = null,
                                            tint = if (proj.id == activeProject?.id) DeveloperCyan else Color.Gray
                                        )
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("+ New Project Prompt", color = DeveloperCyan, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    showProjectDropdown = false
                                    showNewProjectDialog = true
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showNewProjectDialog = true },
                        modifier = Modifier.testTag("new_project_action_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Project")
                    }

                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (settings.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Agent Chat") },
                    label = { Text("Agent AI") },
                    modifier = Modifier.testTag("nav_tab_agent")
                )

                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    icon = { Icon(Icons.Default.Code, contentDescription = "Code Editor") },
                    label = { Text("Code") },
                    modifier = Modifier.testTag("nav_tab_code")
                )

                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.setActiveTab(2) },
                    icon = { Icon(Icons.Default.PlayCircle, contentDescription = "Live Preview") },
                    label = { Text("Preview") },
                    modifier = Modifier.testTag("nav_tab_preview")
                )

                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { viewModel.setActiveTab(3) },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = "Terminal & Logs") },
                    label = { Text("Terminal") },
                    modifier = Modifier.testTag("nav_tab_terminal")
                )

                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { viewModel.setActiveTab(4) },
                    icon = { Icon(Icons.Default.CloudUpload, contentDescription = "Deploy & Git") },
                    label = { Text("Deploy") },
                    modifier = Modifier.testTag("nav_tab_deploy")
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (activeTab) {
                0 -> {
                    AgentReasoningView(
                        messages = chatMessages,
                        isGenerating = isGenerating,
                        onSendPrompt = { prompt ->
                            if (activeProject == null) {
                                viewModel.createNewApp(prompt)
                            } else {
                                viewModel.sendChatMessage(prompt)
                            }
                        }
                    )
                }

                1 -> {
                    Row(modifier = Modifier.fillMaxSize()) {
                        FileTreeView(
                            files = activeFiles,
                            activeFile = activeFile,
                            onSelectFile = { viewModel.selectFile(it) },
                            onCreateFile = { path, isDir -> viewModel.createFile(path, isDir) },
                            onDeleteFile = { id -> viewModel.deleteFile(id) }
                        )

                        CodeEditorView(
                            activeFile = activeFile,
                            files = activeFiles,
                            onFileContentChange = { newContent -> viewModel.updateCurrentFileContent(newContent) },
                            onSelectFile = { viewModel.selectFile(it) },
                            onRefactorCode = { type -> viewModel.refactorCurrentFile(type) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                2 -> {
                    LivePreviewView(
                        files = activeFiles,
                        viewportMode = viewportMode,
                        onViewportChange = { viewModel.setPreviewViewport(it) }
                    )
                }

                3 -> {
                    TerminalView(
                        logs = terminalLogs,
                        onRunCommand = { cmd -> viewModel.runTerminalCommand(cmd) },
                        onAutoDebug = { viewModel.triggerAutoFix() }
                    )
                }

                4 -> {
                    GitDeployView(
                        project = activeProject,
                        files = activeFiles,
                        onDeploy = { viewModel.deployProject() },
                        onExportZip = { viewModel.exportZip() }
                    )
                }
            }
        }
    }
}

    if (showNewProjectDialog) {
        NewProjectPromptDialog(
            onDismiss = { showNewProjectDialog = false },
            onSubmitPrompt = { prompt ->
                showNewProjectDialog = false
                viewModel.createNewApp(prompt)
            }
        )
    }
}

@Composable
private fun NewProjectPromptDialog(
    onDismiss: () -> Unit,
    onSubmitPrompt: (String) -> Unit
) {
    var promptInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DeveloperCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Build New App", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Describe your application idea. The AI agent will plan, generate source files, install dependencies, and build a live preview.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { Text("e.g. Create a food delivery app with cart and tracking") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_project_dialog_input"),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (promptInput.isNotBlank()) {
                        onSubmitPrompt(promptInput)
                    }
                },
                enabled = promptInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DeveloperCyan),
                modifier = Modifier.testTag("start_generating_btn")
            ) {
                Text("Generate App", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
