package com.example.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.local.ProjectFileEntity
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DeveloperCyan
import com.example.ui.theme.DeveloperEmerald

@Composable
fun LivePreviewView(
    files: List<ProjectFileEntity>,
    viewportMode: String, // MOBILE, TABLET, DESKTOP
    onViewportChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var keyToRefresh by remember { mutableIntStateOf(0) }

    val htmlContent = remember(files, keyToRefresh) {
        val indexHtml = files.find { it.path.endsWith("index.html") }?.content ?: ""
        val stylesCss = files.find { it.path.endsWith("styles.css") }?.content ?: ""
        val appJs = files.find { it.path.endsWith("app.js") || it.path.endsWith("main.js") }?.content ?: ""

        if (indexHtml.isNotBlank()) {
            var fullHtml = indexHtml
            if (stylesCss.isNotBlank() && !fullHtml.contains(stylesCss)) {
                fullHtml = fullHtml.replace("</head>", "<style>\n$stylesCss\n</style>\n</head>")
            }
            if (appJs.isNotBlank() && !fullHtml.contains(appJs)) {
                fullHtml = fullHtml.replace("</body>", "<script>\n$appJs\n</script>\n</body>")
            }
            fullHtml
        } else {
            """<!DOCTYPE html>
<html>
<head><style>body{background:#0F172A;color:white;font-family:sans-serif;padding:32px;text-align:center;}</style></head>
<body>
  <h2>🚀 Live Preview Canvas</h2>
  <p>Generating project files...</p>
</body>
</html>"""
        }
    }

    val canvasWidthModifier = remember(viewportMode) {
        when (viewportMode) {
            "MOBILE" -> Modifier.width(360.dp)
            "TABLET" -> Modifier.width(600.dp)
            else -> Modifier.fillMaxWidth()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Control Bar
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Address Bar
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = DeveloperEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "http://localhost:3000",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Viewport selector
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { onViewportChange("MOBILE") },
                        modifier = Modifier.size(32.dp).testTag("viewport_mobile_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneIphone,
                            contentDescription = "Mobile viewport",
                            tint = if (viewportMode == "MOBILE") DeveloperCyan else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onViewportChange("TABLET") },
                        modifier = Modifier.size(32.dp).testTag("viewport_tablet_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tablet,
                            contentDescription = "Tablet viewport",
                            tint = if (viewportMode == "TABLET") DeveloperCyan else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onViewportChange("DESKTOP") },
                        modifier = Modifier.size(32.dp).testTag("viewport_desktop_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Laptop,
                            contentDescription = "Desktop viewport",
                            tint = if (viewportMode == "DESKTOP") DeveloperCyan else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { keyToRefresh++ },
                        modifier = Modifier.size(32.dp).testTag("refresh_preview_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload preview",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Preview Render Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .then(canvasWidthModifier)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                    },
                    modifier = Modifier.fillMaxSize().testTag("live_preview_webview")
                )
            }
        }
    }
}
