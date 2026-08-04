package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectFileEntity
import com.example.ui.theme.DarkCodeBackground
import com.example.ui.theme.DeveloperCyan
import com.example.ui.theme.DeveloperEmerald
import com.example.ui.theme.DeveloperViolet

@Composable
fun CodeEditorView(
    activeFile: ProjectFileEntity?,
    files: List<ProjectFileEntity>,
    onFileContentChange: (String) -> Unit,
    onSelectFile: (ProjectFileEntity) -> Unit,
    onRefactorCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCodeBackground)
    ) {
        // File Tabs Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            files.filter { !it.isDirectory }.forEach { file ->
                val isSelected = activeFile?.id == file.id
                Surface(
                    color = if (isSelected) DarkCodeBackground else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .padding(end = 1.dp)
                        .testTag("tab_file_${file.path.replace(".", "_")}")
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = file.path,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) DeveloperCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (file.isModified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("•", color = DeveloperEmerald, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Code Action / Refactor Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Language: ${activeFile?.language?.uppercase() ?: "PLAIN TEXT"}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DeveloperCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { showSearch = !showSearch },
                        modifier = Modifier.size(28.dp).testTag("editor_search_btn")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp))
                    }

                    AssistChip(
                        onClick = { onRefactorCode("ADD_ERROR_HANDLING") },
                        label = { Text("+ Try/Catch", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.testTag("refactor_try_catch")
                    )

                    AssistChip(
                        onClick = { onRefactorCode("GENERATE_TESTS") },
                        label = { Text("+ Unit Tests", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.testTag("refactor_unit_tests")
                    )

                    AssistChip(
                        onClick = { onRefactorCode("FORMAT") },
                        label = { Text("Format", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.testTag("refactor_format")
                    )
                }
            }
        }

        if (showSearch) {
            Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search in code...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).testTag("code_search_input"),
                        singleLine = true
                    )
                    IconButton(onClick = { showSearch = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close search")
                    }
                }
            }
        }

        // Code Area with Line Numbers Gutter
        if (activeFile != null) {
            val lines = remember(activeFile.content) { activeFile.content.split("\n") }

            Row(modifier = Modifier.fillMaxSize()) {
                // Line Numbers Gutter
                Column(
                    modifier = Modifier
                        .width(44.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF0F172A))
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    lines.indices.forEach { idx ->
                        Text(
                            text = "${idx + 1}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                // Code Input Text Area
                BasicTextField(
                    value = activeFile.content,
                    onValueChange = { onFileContentChange(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .testTag("monaco_code_editor"),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = Color(0xFFF8FAFC),
                        lineHeight = 18.sp
                    )
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No file selected. Select a file from the project tree to edit code.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }
}
