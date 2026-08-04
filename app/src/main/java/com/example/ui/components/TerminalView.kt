package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCodeBackground
import com.example.ui.theme.DeveloperCyan
import com.example.ui.theme.DeveloperEmerald
import com.example.ui.theme.DeveloperRose

@Composable
fun TerminalView(
    logs: List<String>,
    onRunCommand: (String) -> Unit,
    onAutoDebug: () -> Unit,
    modifier: Modifier = Modifier
) {
    var commandInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCodeBackground)
    ) {
        // Terminal Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = DeveloperEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Terminal Output & Build Logs",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAutoDebug,
                        colors = ButtonDefaults.buttonColors(containerColor = DeveloperRose),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("auto_debug_btn")
                    ) {
                        Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto Fix Errors", fontSize = 11.sp)
                    }

                    IconButton(
                        onClick = { onRunCommand("clear") },
                        modifier = Modifier.size(28.dp).testTag("clear_terminal_btn")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear logs", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Terminal Log Lines
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(logs) { line ->
                val lineColor = when {
                    line.startsWith("$") -> DeveloperCyan
                    line.startsWith("> 🎉") -> DeveloperEmerald
                    line.startsWith("> Error") -> DeveloperRose
                    line.startsWith(">") -> Color(0xFFCBD5E1)
                    else -> Color(0xFF94A3B8)
                }

                Text(
                    text = line,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = lineColor,
                    lineHeight = 16.sp
                )
            }
        }

        // Command Prompt Input
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$ ",
                    color = DeveloperEmerald,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    placeholder = { Text("npm install, python app.py, git commit...", fontSize = 12.sp, color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("terminal_command_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (commandInput.isNotBlank()) {
                            onRunCommand(commandInput)
                            commandInput = ""
                        }
                    })
                )

                IconButton(
                    onClick = {
                        if (commandInput.isNotBlank()) {
                            onRunCommand(commandInput)
                            commandInput = ""
                        }
                    },
                    modifier = Modifier.testTag("execute_command_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Run command", tint = DeveloperEmerald)
                }
            }
        }
    }
}
