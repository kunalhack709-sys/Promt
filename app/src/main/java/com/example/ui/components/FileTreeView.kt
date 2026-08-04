package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectFileEntity
import com.example.ui.theme.DeveloperCyan
import com.example.ui.theme.DeveloperEmerald
import com.example.ui.theme.DeveloperViolet

@Composable
fun FileTreeView(
    files: List<ProjectFileEntity>,
    activeFile: ProjectFileEntity?,
    onSelectFile: (ProjectFileEntity) -> Unit,
    onCreateFile: (path: String, isDirectory: Boolean) -> Unit,
    onDeleteFile: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(220.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    tint = DeveloperCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Project Tree",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("create_file_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New file",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

        // Files List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(files, key = { it.id }) { file ->
                val isSelected = activeFile?.id == file.id
                FileTreeItemRow(
                    file = file,
                    isSelected = isSelected,
                    onSelect = { onSelectFile(file) },
                    onDelete = { onDeleteFile(file.id) }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateFileDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { path, isDir ->
                onCreateFile(path, isDir)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun FileTreeItemRow(
    file: ProjectFileEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val fileIcon = remember(file.path, file.isDirectory) {
        if (file.isDirectory) Icons.Default.Folder
        else when (file.language) {
            "html" -> Icons.Default.Code
            "css" -> Icons.Default.Brush
            "javascript", "typescript" -> Icons.Default.Terminal
            "python" -> Icons.Default.Memory
            "json" -> Icons.Default.DataObject
            "markdown" -> Icons.Default.Description
            else -> Icons.Default.InsertDriveFile
        }
    }

    val iconTint = remember(file.language, file.isDirectory) {
        if (file.isDirectory) DeveloperCyan
        else when (file.language) {
            "html" -> Color(0xFFE44D26)
            "css" -> Color(0xFF264DE4)
            "javascript", "typescript" -> Color(0xFFF7DF1E)
            "python" -> Color(0xFF3776AB)
            "json" -> DeveloperEmerald
            else -> DeveloperViolet
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                else Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("file_item_${file.path.replace(".", "_").replace("/", "_")}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = fileIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = file.path,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete file",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CreateFileDialog(
    onDismiss: () -> Unit,
    onCreate: (path: String, isDir: Boolean) -> Unit
) {
    var filePath by remember { mutableStateOf("") }
    var isDirectory by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New File", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = filePath,
                    onValueChange = { filePath = it },
                    label = { Text("File Path (e.g. src/utils.js)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_file_path_input")
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (filePath.isNotBlank()) {
                        onCreate(filePath, isDirectory)
                    }
                },
                enabled = filePath.isNotBlank(),
                modifier = Modifier.testTag("confirm_create_file_btn")
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
