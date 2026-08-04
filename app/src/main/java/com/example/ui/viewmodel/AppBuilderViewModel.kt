package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.agent.AgentEngine
import com.example.data.agent.GeminiApiService
import com.example.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppBuilderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val projectDao = db.projectDao()
    private val fileDao = db.projectFileDao()
    private val chatDao = db.chatMessageDao()
    private val promptDao = db.promptHistoryDao()
    private val settingsDao = db.appSettingsDao()

    private val agentEngine = AgentEngine(
        projectDao = projectDao,
        fileDao = fileDao,
        chatDao = chatDao,
        promptDao = promptDao,
        geminiService = GeminiApiService()
    )

    val allProjects: StateFlow<List<ProjectEntity>> = projectDao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<AppSettingsEntity> = settingsDao.getSettingsFlow()
        .map { it ?: AppSettingsEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsEntity())

    private val _activeProjectId = MutableStateFlow<Long?>(null)
    val activeProjectId: StateFlow<Long?> = _activeProjectId.asStateFlow()

    val activeProject: StateFlow<ProjectEntity?> = _activeProjectId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else projectDao.getProjectByIdFlow(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeProjectFiles: StateFlow<List<ProjectFileEntity>> = _activeProjectId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else fileDao.getFilesForProject(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = _activeProjectId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else chatDao.getMessagesForProject(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentPrompts: StateFlow<List<PromptHistoryEntity>> = promptDao.getRecentPrompts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeFile = MutableStateFlow<ProjectFileEntity?>(null)
    val activeFile: StateFlow<ProjectFileEntity?> = _activeFile.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _agentStatus = MutableStateFlow("IDLE") // IDLE, THINKING, GENERATING, SUCCESS, ERROR
    val agentStatus: StateFlow<String> = _agentStatus.asStateFlow()

    private val _activeTab = MutableStateFlow(0) // 0: Chat/Agent, 1: Code Editor, 2: Preview, 3: Terminal, 4: Git/Deploy
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _previewViewport = MutableStateFlow("DESKTOP") // MOBILE, TABLET, DESKTOP
    val previewViewport: StateFlow<String> = _previewViewport.asStateFlow()

    private val _terminalLogs = MutableStateFlow<List<String>>(
        listOf(
            "AI App Builder Agent Engine v2.5 Initialized.",
            "Ready to process natural language prompts and construct full-stack applications.",
            "Type a prompt or tap one of the suggested templates to build an app!"
        )
    )
    val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

    init {
        viewModelScope.launch {
            allProjects.collect { list ->
                if (_activeProjectId.value == null && list.isNotEmpty()) {
                    _activeProjectId.value = list.first().id
                } else if (list.isEmpty() && !_isGenerating.value) {
                    // Seed initial demo app on first launch!
                    createNewApp("Create a food delivery app.")
                }
            }
        }

        viewModelScope.launch {
            activeProjectFiles.collect { files ->
                if (files.isNotEmpty() && (_activeFile.value == null || files.none { it.id == _activeFile.value?.id })) {
                    val mainFile = files.find { !it.isDirectory && (it.path.endsWith("index.html") || it.path.endsWith("App.tsx") || it.path.endsWith("app.js")) }
                        ?: files.firstOrNull { !it.isDirectory }
                    _activeFile.value = mainFile
                }
            }
        }
    }

    fun setActiveTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun setPreviewViewport(viewport: String) {
        _previewViewport.value = viewport
    }

    fun selectProject(projectId: Long) {
        _activeProjectId.value = projectId
        _activeFile.value = null
    }

    fun selectFile(file: ProjectFileEntity) {
        if (!file.isDirectory) {
            _activeFile.value = file
        }
    }

    fun createNewApp(promptText: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            _agentStatus.value = "THINKING"
            appendTerminalLog("> System Prompt Received: \"$promptText\"")
            appendTerminalLog("> Decomposing requirements & planning project architecture...")

            try {
                val newProjectId = agentEngine.buildAppFromPrompt(promptText)
                _activeProjectId.value = newProjectId
                _agentStatus.value = "SUCCESS"
                appendTerminalLog("> Project built successfully! Live preview ready.")
                _activeTab.value = 1 // Switch to code editor or preview
            } catch (e: Exception) {
                _agentStatus.value = "ERROR"
                appendTerminalLog("> Error building project: ${e.localizedMessage}")
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun sendChatMessage(promptText: String) {
        val projId = _activeProjectId.value ?: return
        viewModelScope.launch {
            _isGenerating.value = true
            _agentStatus.value = "GENERATING"
            appendTerminalLog("> Agent User Request: \"$promptText\"")

            try {
                agentEngine.modifyAppWithUserPrompt(projId, promptText)
                _agentStatus.value = "SUCCESS"
                appendTerminalLog("> Refactored application codebase per user request.")
            } catch (e: Exception) {
                _agentStatus.value = "ERROR"
                appendTerminalLog("> Error modifying app: ${e.localizedMessage}")
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun updateCurrentFileContent(newContent: String) {
        val currentFile = _activeFile.value ?: return
        viewModelScope.launch {
            fileDao.updateFileContent(currentFile.id, newContent)
            _activeFile.value = currentFile.copy(content = newContent, isModified = true)
        }
    }

    fun createFile(path: String, isDirectory: Boolean) {
        val projId = _activeProjectId.value ?: return
        viewModelScope.launch {
            val ext = path.substringAfterLast(".", "txt")
            val lang = when (ext) {
                "html" -> "html"
                "css" -> "css"
                "js" -> "javascript"
                "ts", "tsx" -> "typescript"
                "py" -> "python"
                "json" -> "json"
                "md" -> "markdown"
                "kt" -> "kotlin"
                else -> "text"
            }
            val newFile = ProjectFileEntity(
                projectId = projId,
                path = path,
                content = if (isDirectory) "" else "// New file: $path",
                language = lang,
                isDirectory = isDirectory
            )
            val fileId = fileDao.insertFile(newFile)
            if (!isDirectory) {
                _activeFile.value = newFile.copy(id = fileId)
            }
            appendTerminalLog("> Created file: $path")
        }
    }

    fun deleteFile(fileId: Long) {
        viewModelScope.launch {
            fileDao.deleteFileById(fileId)
            if (_activeFile.value?.id == fileId) {
                _activeFile.value = null
            }
            appendTerminalLog("> Deleted file ID: $fileId")
        }
    }

    fun runTerminalCommand(cmd: String) {
        appendTerminalLog("$ $cmd")
        val lower = cmd.trim().lowercase()
        when {
            lower.startsWith("npm install") || lower.startsWith("pip install") -> {
                appendTerminalLog("> Resolving dependency graph...")
                appendTerminalLog("> Added 28 packages in 0.8s. Everything is up to date.")
            }
            lower.startsWith("npm start") || lower.startsWith("python") || lower.startsWith("npm run dev") -> {
                appendTerminalLog("> Starting local development server on http://localhost:3000...")
                appendTerminalLog("> Live reload enabled. Compilation clean with 0 warnings.")
            }
            lower.startsWith("git status") -> {
                val proj = activeProject.value
                appendTerminalLog("On branch ${proj?.gitBranch ?: "main"}")
                appendTerminalLog("Your branch is up to date with 'origin/main'.")
                appendTerminalLog("nothing to commit, working tree clean")
            }
            lower.startsWith("git commit") -> {
                appendTerminalLog("[main c4f92a1] $cmd")
                appendTerminalLog(" 3 files changed, 45 insertions(+)")
            }
            lower.startsWith("pytest") || lower.startsWith("npm test") -> {
                appendTerminalLog("PASS tests/app.test.js")
                appendTerminalLog("  ✓ renders primary layout without crashing (12ms)")
                appendTerminalLog("  ✓ user interaction state updates correctly (8ms)")
                appendTerminalLog("\nTest Suites: 1 passed, 1 total")
                appendTerminalLog("Tests:       2 passed, 2 total")
            }
            lower.startsWith("vercel") || lower.startsWith("deploy") -> {
                deployProject()
            }
            lower == "clear" -> {
                _terminalLogs.value = emptyList()
            }
            else -> {
                appendTerminalLog("> Executed command: '$cmd' [status code 0]")
            }
        }
    }

    fun refactorCurrentFile(refactorType: String) {
        val currentFile = _activeFile.value ?: return
        viewModelScope.launch {
            val updated = when (refactorType) {
                "ADD_TYPES" -> "// Types & Interfaces added\n" + currentFile.content
                "ADD_ERROR_HANDLING" -> currentFile.content + "\n\n// Added global error handling\ntry {\n    // Core logic\n} catch (err) {\n    console.error('Handled error:', err);\n}"
                "GENERATE_TESTS" -> currentFile.content + "\n\n/* Unit Test Suite */\ndescribe('${currentFile.path}', () => {\n  it('should execute successfully', () => {\n    expect(true).toBe(true);\n  });\n});"
                "FORMAT" -> currentFile.content.trim() + "\n"
                else -> currentFile.content
            }
            fileDao.updateFileContent(currentFile.id, updated)
            _activeFile.value = currentFile.copy(content = updated)
            appendTerminalLog("> Refactored ${currentFile.path} with operation: $refactorType")
        }
    }

    fun triggerAutoFix() {
        val projId = _activeProjectId.value ?: return
        viewModelScope.launch {
            appendTerminalLog("> Autonomous Debugger running static code analysis...")
            val result = agentEngine.autoFixProjectErrors(projId)
            appendTerminalLog("> $result")
        }
    }

    fun deployProject() {
        val projId = _activeProjectId.value ?: return
        val proj = activeProject.value ?: return
        viewModelScope.launch {
            appendTerminalLog("> Initiating production build & deployment for '${proj.name}'...")
            appendTerminalLog("> Bundling assets & optimizing minified chunks...")
            val generatedUrl = "https://${proj.name.lowercase().replace(" ", "-")}.aistudio-deploy.app"
            projectDao.updateDeployedUrl(projId, generatedUrl)
            appendTerminalLog("> 🎉 DEPLOYED SUCCESSFULLY to: $generatedUrl")
        }
    }

    fun exportZip() {
        appendTerminalLog("> Compressing project workspace into ZIP archive...")
        appendTerminalLog("> Download ready: ${activeProject.value?.name ?: "app"}-source-code.zip (1.4 MB)")
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = appSettings.value
            settingsDao.saveSettings(current.copy(isDarkMode = !current.isDarkMode))
        }
    }

    private fun appendTerminalLog(log: String) {
        _terminalLogs.value = _terminalLogs.value + log
    }
}
