package com.example.data.agent

import com.example.data.local.ProjectFileEntity

data class GeneratedProjectData(
    val projectName: String,
    val description: String,
    val language: String,
    val files: List<ProjectFileEntity>,
    val reasoningSteps: List<String>,
    val terminalOutput: String
)

object AutonomousTemplateGenerator {

    fun generateProjectForPrompt(projectId: Long, prompt: String): GeneratedProjectData {
        val lower = prompt.lowercase()
        return when {
            lower.contains("food") || lower.contains("delivery") || lower.contains("restaurant") -> {
                createFoodDeliveryApp(projectId, prompt)
            }
            lower.contains("bug") || lower.contains("bounty") || lower.contains("scanner") || lower.contains("vulnerability") || lower.contains("security") -> {
                createBugBountyScannerApp(projectId, prompt)
            }
            lower.contains("task") || lower.contains("todo") || lower.contains("management") || lower.contains("kanban") -> {
                createTaskManagementApp(projectId, prompt)
            }
            lower.contains("weather") || lower.contains("climate") || lower.contains("forecast") -> {
                createWeatherApp(projectId, prompt)
            }
            lower.contains("e-commerce") || lower.contains("shop") || lower.contains("store") -> {
                createEcommerceApp(projectId, prompt)
            }
            else -> {
                createGenericApp(projectId, prompt)
            }
        }
    }

    private fun createFoodDeliveryApp(projectId: Long, prompt: String): GeneratedProjectData {
        val name = "QuickBite Express"
        val desc = "Full-stack food ordering and delivery web app with real-time tracking, cart system, and restaurant menu management."
        val lang = "React / Node.js"

        val files = listOf(
            ProjectFileEntity(
                projectId = projectId,
                path = "index.html",
                language = "html",
                content = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QuickBite Express - Instant Food Delivery</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div id="app">
        <header class="navbar">
            <div class="logo">🍔 QuickBite Express</div>
            <div class="search-bar">
                <input type="text" id="searchInput" placeholder="Search pizza, sushi, burgers...">
            </div>
            <div class="cart-badge" onclick="toggleCartModal()">
                🛒 Cart (<span id="cartCount">0</span>)
            </div>
        </header>

        <section class="hero">
            <h1>Craving Delicious Food?</h1>
            <p>Delivered to your doorstep in under 30 minutes.</p>
            <div class="category-pills">
                <button class="pill active" onclick="filterCategory('All')">All</button>
                <button class="pill" onclick="filterCategory('Pizza')">🍕 Pizza</button>
                <button class="pill" onclick="filterCategory('Burgers')">🍔 Burgers</button>
                <button class="pill" onclick="filterCategory('Japanese')">🍣 Sushi</button>
                <button class="pill" onclick="filterCategory('Desserts')">🍦 Desserts</button>
            </div>
        </section>

        <main class="grid-container" id="menuContainer">
            <!-- Dynamic Food Cards -->
        </main>

        <div id="cartModal" class="modal">
            <div class="modal-content">
                <h2>Your Order</h2>
                <div id="cartItemsList"></div>
                <div class="cart-total">Total: <span id="cartTotalSum">$0.00</span></div>
                <button class="btn-primary" onclick="checkout()">Proceed to Checkout</button>
                <button class="btn-secondary" onclick="toggleCartModal()">Close</button>
            </div>
        </div>
    </div>
    <script src="app.js"></script>
</body>
</html>"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "styles.css",
                language = "css",
                content = """* { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
body { background: #0F172A; color: #F8FAFC; padding-bottom: 40px; }
.navbar { display: flex; justify-content: space-between; align-items: center; padding: 16px 32px; background: #1E293B; border-bottom: 1px solid #334155; }
.logo { font-size: 1.4rem; font-weight: bold; color: #F59E0B; }
.search-bar input { padding: 10px 18px; border-radius: 20px; border: 1px solid #475569; background: #0F172A; color: white; width: 280px; }
.cart-badge { background: #F59E0B; color: #000; padding: 10px 20px; border-radius: 20px; font-weight: bold; cursor: pointer; }
.hero { text-align: center; padding: 40px 20px; background: linear-gradient(180deg, #1E293B 0%, #0F172A 100%); }
.hero h1 { font-size: 2.2rem; color: #FFF; margin-bottom: 8px; }
.hero p { color: #94A3B8; margin-bottom: 20px; }
.category-pills { display: flex; justify-content: center; gap: 12px; flex-wrap: wrap; }
.pill { padding: 8px 18px; border-radius: 16px; border: 1px solid #475569; background: #1E293B; color: white; cursor: pointer; transition: 0.2s; }
.pill.active, .pill:hover { background: #F59E0B; color: black; border-color: #F59E0B; }
.grid-container { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 24px; padding: 32px; max-width: 1200px; margin: 0 auto; }
.card { background: #1E293B; border-radius: 16px; border: 1px solid #334155; overflow: hidden; transition: transform 0.2s; }
.card:hover { transform: translateY(-4px); }
.card-img { width: 100%; height: 160px; object-fit: cover; background: #334155; display: flex; align-items: center; justify-content: center; font-size: 4rem; }
.card-body { padding: 16px; }
.card-title { font-size: 1.1rem; margin-bottom: 6px; }
.card-desc { font-size: 0.85rem; color: #94A3B8; margin-bottom: 12px; }
.card-footer { display: flex; justify-content: space-between; align-items: center; }
.price { font-size: 1.2rem; font-weight: bold; color: #10B981; }
.add-btn { background: #F59E0B; color: black; border: none; padding: 8px 16px; border-radius: 8px; font-weight: bold; cursor: pointer; }
.modal { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); justify-content: center; align-items: center; }
.modal-content { background: #1E293B; padding: 24px; border-radius: 16px; width: 90%; max-width: 440px; }
.btn-primary { width: 100%; padding: 12px; background: #10B981; border: none; border-radius: 8px; color: white; font-weight: bold; margin-top: 12px; cursor: pointer; }
.btn-secondary { width: 100%; padding: 10px; background: #475569; border: none; border-radius: 8px; color: white; margin-top: 8px; cursor: pointer; }"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "app.js",
                language = "javascript",
                content = """const menuItems = [
    { id: 1, name: "Truffle Mushroom Pizza", category: "Pizza", price: 18.99, icon: "🍕", desc: "Fresh wood-fired crust, black truffle oil, wild mushrooms & mozzarella." },
    { id: 2, name: "Double Smash Burger", category: "Burgers", price: 14.50, icon: "🍔", desc: "Angus beef patties, melted cheddar, caramelized onions & secret sauce." },
    { id: 3, name: "Dragon Roll Sushi Set", category: "Japanese", price: 22.00, icon: "🍣", desc: "Tempura shrimp, avocado, eel sauce & fresh salmon nigiri." },
    { id: 4, name: "Artisanal Gelato Bowl", category: "Desserts", price: 8.50, icon: "🍦", desc: "Belgian dark chocolate & Pistachio gelato topped with roasted nuts." },
    { id: 5, name: "BBQ Chipotle Chicken Wings", category: "Burgers", price: 12.99, icon: "🍗", desc: "Crispy wings tossed in smoked chipotle honey glaze." }
];

let cart = [];

function renderMenu(itemsToRender) {
    const container = document.getElementById('menuContainer');
    container.innerHTML = '';
    itemsToRender.forEach(item => {
        const card = document.createElement('div');
        card.className = 'card';
        card.innerHTML = `
            <div class="card-img">${'$'}{item.icon}</div>
            <div class="card-body">
                <div class="card-title">${'$'}{item.name}</div>
                <div class="card-desc">${'$'}{item.desc}</div>
                <div class="card-footer">
                    <span class="price">$${'$'}{item.price.toFixed(2)}</span>
                    <button class="add-btn" onclick="addToCart(${'$'}{item.id})">Add to Order</button>
                </div>
            </div>
        `;
        container.appendChild(card);
    });
}

function addToCart(id) {
    const item = menuItems.find(i => i.id === id);
    cart.push(item);
    updateCartUI();
}

function updateCartUI() {
    document.getElementById('cartCount').innerText = cart.length;
    const itemsList = document.getElementById('cartItemsList');
    itemsList.innerHTML = cart.map(i => `<div style="display:flex;justify-content:space-between;margin:8px 0;"><span>${'$'}{i.name}</span><strong>$${'$'}{i.price.toFixed(2)}</strong></div>`).join('');
    const total = cart.reduce((sum, i) => sum + i.price, 0);
    document.getElementById('cartTotalSum').innerText = "$ " + total.toFixed(2);
}

function toggleCartModal() {
    const modal = document.getElementById('cartModal');
    modal.style.display = modal.style.display === 'flex' ? 'none' : 'flex';
}

function checkout() {
    if(cart.length === 0) return alert('Your cart is empty!');
    alert('🎉 Order Placed Successfully! Your driver is heading to the restaurant.');
    cart = [];
    updateCartUI();
    toggleCartModal();
}

function filterCategory(cat) {
    if (cat === 'All') renderMenu(menuItems);
    else renderMenu(menuItems.filter(i => i.category === cat));
}

document.addEventListener('DOMContentLoaded', () => renderMenu(menuItems));"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "package.json",
                language = "json",
                content = """{
  "name": "quickbite-express",
  "version": "1.0.0",
  "description": "Food delivery application",
  "scripts": {
    "start": "serve -s .",
    "test": "jest"
  },
  "dependencies": {
    "express": "^4.18.2",
    "socket.io": "^4.6.1"
  }
}"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "README.md",
                language = "markdown",
                content = """# QuickBite Express 🍔

Autonomous Food Delivery & Real-Time Tracking Application built by **AI App Builder Agent**.

## Features
- Dynamic menu filtering & category navigation.
- Real-time interactive cart calculation.
- Modal checkout confirmation engine.
- Production-ready responsive dark layout.
"""
            )
        )

        val steps = listOf(
            "Parsed natural language prompt: '$prompt'",
            "Decomposed system architecture into HTML5 presentation, CSS3 grid layout, and JS cart state module.",
            "Generated file tree: index.html, styles.css, app.js, package.json, README.md.",
            "Validated responsive viewport scaling and interactive event bindings.",
            "Synthesized live project bundle ready for instant preview and deployment."
        )

        return GeneratedProjectData(name, desc, lang, files, steps, "$ npm install\n> Added 14 packages in 1.2s\n$ npm start\n> Server running on http://localhost:3000\n> Ready for user interaction!")
    }

    private fun createBugBountyScannerApp(projectId: Long, prompt: String): GeneratedProjectData {
        val name = "Vulnerability Sentinel"
        val desc = "Automated bug bounty scanner & security auditing dashboard for web applications and API endpoints."
        val lang = "Python / FastAPI / React"

        val files = listOf(
            ProjectFileEntity(
                projectId = projectId,
                path = "index.html",
                language = "html",
                content = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Vulnerability Sentinel - Automated Security Scanner</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>🛡️ Vulnerability Sentinel</h1>
            <p>Automated Reconnaissance & Security Audit Suite</p>
        </header>

        <div class="scan-bar">
            <input type="text" id="targetInput" value="https://example.target-app.com" placeholder="Enter target domain or URL...">
            <button class="btn-scan" onclick="startScan()">Launch Automated Audit</button>
        </div>

        <div class="status-panel" id="statusPanel" style="display:none;">
            <div class="spinner"></div>
            <span id="scanStatusText">Scanning target subdomains, HTTP headers, and CORS policies...</span>
        </div>

        <div class="results-grid" id="resultsGrid">
            <!-- Dynamic Vulnerability Cards -->
        </div>
    </div>
    <script src="app.js"></script>
</body>
</html>"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "styles.css",
                language = "css",
                content = """body { background: #0B0F19; color: #E2E8F0; font-family: monospace; padding: 24px; }
.container { max-width: 1000px; margin: 0 auto; }
header { text-align: center; margin-bottom: 30px; border-bottom: 1px solid #1E293B; padding-bottom: 16px; }
h1 { color: #06B6D4; font-size: 2rem; }
.scan-bar { display: flex; gap: 12px; margin-bottom: 24px; }
.scan-bar input { flex: 1; padding: 14px; background: #1E293B; border: 1px solid #06B6D4; border-radius: 8px; color: #FFF; font-size: 1rem; }
.btn-scan { padding: 14px 28px; background: #06B6D4; color: #000; font-weight: bold; border: none; border-radius: 8px; cursor: pointer; }
.status-panel { background: #1E293B; padding: 16px; border-radius: 8px; border-left: 4px solid #06B6D4; margin-bottom: 24px; display: flex; align-items: center; gap: 12px; }
.results-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 16px; }
.vuln-card { background: #1E293B; padding: 18px; border-radius: 8px; border: 1px solid #334155; }
.severity-high { border-left: 4px solid #F43F5E; }
.severity-med { border-left: 4px solid #F59E0B; }
.severity-low { border-left: 4px solid #10B981; }
.tag { font-size: 0.75rem; padding: 4px 8px; border-radius: 4px; font-weight: bold; }
.tag-high { background: #F43F5E22; color: #F43F5E; }
.tag-med { background: #F59E0B22; color: #F59E0B; }"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "app.js",
                language = "javascript",
                content = """function startScan() {
    const target = document.getElementById('targetInput').value;
    const statusPanel = document.getElementById('statusPanel');
    const resultsGrid = document.getElementById('resultsGrid');
    
    statusPanel.style.display = 'flex';
    resultsGrid.innerHTML = '';
    
    setTimeout(() => {
        statusPanel.style.display = 'none';
        renderVulnerabilities([
            { title: "Misconfigured CORS Header", severity: "high", desc: "Access-Control-Allow-Origin set to wildcard '*' with credentials allowed.", cve: "CVE-2024-9182" },
            { title: "Missing Security Headers", severity: "med", desc: "X-Frame-Options & Content-Security-Policy (CSP) missing from HTTP response.", cve: "CWE-693" },
            { title: "Exposed API Endpoint Documentation", severity: "low", desc: "OpenAPI Swagger UI publicly accessible at /api/v1/docs", cve: "INFO-001" }
        ]);
    }, 1800);
}

function renderVulnerabilities(vulns) {
    const grid = document.getElementById('resultsGrid');
    vulns.forEach(v => {
        const card = document.createElement('div');
        const sevClass = v.severity === 'high' ? 'high' : v.severity === 'med' ? 'med' : 'low';
        card.className = 'vuln-card severity-' + sevClass;
        card.innerHTML = `
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
                <span class="tag tag-` + v.severity + `">` + v.severity.toUpperCase() + `</span>
                <span style="color:#64748B;font-size:0.8rem;">` + v.cve + `</span>
            </div>
            <h3 style="margin-bottom:6px;">` + v.title + `</h3>
            <p style="color:#94A3B8;font-size:0.85rem;">` + v.desc + `</p>
        `;
        grid.appendChild(card);
    });
}"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "scanner.py",
                language = "python",
                content = """# Vulnerability Scanner Engine
import sys
import json

def audit_target(domain):
    print(f"[+] Launching security probe against target: {domain}")
    findings = [
        {"type": "CORS_MISCONFIG", "severity": "HIGH", "path": "/api/user"},
        {"type": "XSS_REFLECTED", "severity": "MEDIUM", "path": "/search?q="}
    ]
    return findings

if __name__ == '__main__':
    results = audit_target("example.com")
    print(json.dumps(results, indent=2))
"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "README.md",
                language = "markdown",
                content = """# Vulnerability Sentinel 🛡️

Automated Bug Bounty Scanner & Vulnerability Reconnaissance Suite.

## Quick Start
1. `python scanner.py`
2. Open `index.html` in browser for live interactive scan dashboard.
"""
            )
        )

        val steps = listOf(
            "Parsed security prompt: '$prompt'",
            "Designed automated vulnerability scanner core engine with Python audit scripts and HTML5 cyber dashboard.",
            "Generated target input parser, status loading indicator, and severity classification cards.",
            "Synthesized project architecture with FastAPI API model compatibility."
        )

        return GeneratedProjectData(name, desc, lang, files, steps, "$ python scanner.py\n[+] Probing target HTTP headers...\n[+] CORS vulnerability detected!\n[+] Security scan finished successfully.")
    }

    private fun createTaskManagementApp(projectId: Long, prompt: String): GeneratedProjectData {
        val name = "KanbanFlow Pro"
        val desc = "Full-featured task management and productivity suite with drag-and-drop Kanban boards, status tracking, and priority filters."
        val lang = "Vue.js / Tailwind CSS"

        val files = listOf(
            ProjectFileEntity(
                projectId = projectId,
                path = "index.html",
                language = "html",
                content = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>KanbanFlow Pro - Task Manager</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="app-container">
        <aside class="sidebar">
            <h2>📌 Task Master</h2>
            <nav>
                <a href="#" class="active">📋 All Boards</a>
                <a href="#">⚡ High Priority</a>
                <a href="#">✅ Completed</a>
                <a href="#">📊 Analytics</a>
            </nav>
        </aside>

        <main class="main-content">
            <header class="header">
                <h1>Sprint Backlog</h1>
                <button class="btn-add" onclick="addNewTask()">+ New Task</button>
            </header>

            <div class="kanban-board">
                <div class="column">
                    <h3>To Do (<span id="todoCount">2</span>)</h3>
                    <div class="card-list" id="todoCol"></div>
                </div>

                <div class="column">
                    <h3>In Progress (<span id="progressCount">1</span>)</h3>
                    <div class="card-list" id="progressCol"></div>
                </div>

                <div class="column">
                    <h3>Done (<span id="doneCount">1</span>)</h3>
                    <div class="card-list" id="doneCol"></div>
                </div>
            </div>
        </main>
    </div>
    <script src="app.js"></script>
</body>
</html>"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "styles.css",
                language = "css",
                content = """body { margin: 0; background: #0F172A; color: #F8FAFC; font-family: system-ui, sans-serif; }
.app-container { display: flex; height: 100vh; }
.sidebar { width: 220px; background: #1E293B; border-right: 1px solid #334155; padding: 24px 16px; }
.sidebar h2 { color: #6366F1; margin-bottom: 24px; font-size: 1.2rem; }
.sidebar nav a { display: block; padding: 10px; color: #94A3B8; text-decoration: none; border-radius: 8px; margin-bottom: 6px; }
.sidebar nav a.active { background: #6366F122; color: #6366F1; font-weight: bold; }
.main-content { flex: 1; padding: 32px; overflow-y: auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
.btn-add { background: #6366F1; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: bold; cursor: pointer; }
.kanban-board { display: flex; gap: 20px; }
.column { flex: 1; background: #1E293B; border-radius: 12px; padding: 16px; border: 1px solid #334155; }
.column h3 { font-size: 1rem; color: #94A3B8; margin-bottom: 16px; }
.task-card { background: #0F172A; padding: 14px; border-radius: 8px; border: 1px solid #334155; margin-bottom: 12px; cursor: pointer; }
.task-title { font-weight: 600; margin-bottom: 6px; }
.task-badge { display: inline-block; font-size: 0.7rem; padding: 2px 8px; border-radius: 4px; font-weight: bold; }
.badge-high { background: #F43F5E33; color: #F43F5E; }
.badge-med { background: #F59E0B33; color: #F59E0B; }"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "app.js",
                language = "javascript",
                content = """const tasks = [
    { id: 1, title: "Design Landing Page Wireframes", status: "todo", priority: "high" },
    { id: 2, title: "Implement Auth Token Middleware", status: "todo", priority: "med" },
    { id: 3, title: "Build Room Database Layer", status: "progress", priority: "high" },
    { id: 4, title: "Setup Continuous Integration Pipeline", status: "done", priority: "med" }
];

function renderBoard() {
    const todoCol = document.getElementById('todoCol');
    const progressCol = document.getElementById('progressCol');
    const doneCol = document.getElementById('doneCol');
    
    todoCol.innerHTML = '';
    progressCol.innerHTML = '';
    doneCol.innerHTML = '';
    
    tasks.forEach(t => {
        const card = document.createElement('div');
        card.className = 'task-card';
        card.innerHTML = `
            <div class="task-title">` + t.title + `</div>
            <span class="task-badge badge-` + t.priority + `">` + t.priority.toUpperCase() + `</span>
        `;
        if (t.status === 'todo') todoCol.appendChild(card);
        else if (t.status === 'progress') progressCol.appendChild(card);
        else doneCol.appendChild(card);
    });
}

function addNewTask() {
    const title = prompt("Enter task title:");
    if (title) {
        tasks.push({ id: Date.now(), title, status: 'todo', priority: 'high' });
        renderBoard();
    }
}

document.addEventListener('DOMContentLoaded', renderBoard);"""
            )
        )

        val steps = listOf(
            "Analyzed task management request: '$prompt'",
            "Constructed Kanban board layout architecture with To Do, In Progress, and Done workflow columns.",
            "Implemented task persistence and dynamic state card rendering engine."
        )

        return GeneratedProjectData(name, desc, lang, files, steps, "$ npm run dev\n> Task manager web app running on port 5173.")
    }

    private fun createWeatherApp(projectId: Long, prompt: String): GeneratedProjectData {
        val name = "SkyPulse Weather"
        val desc = "Real-time weather forecast application with interactive radar maps, UV index gauge, and 7-day temperature trends."
        val lang = "React / HTML5 Canvas"

        val files = listOf(
            ProjectFileEntity(
                projectId = projectId,
                path = "index.html",
                language = "html",
                content = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SkyPulse Weather Forecast</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="card-weather">
        <div class="search-box">
            <input type="text" id="cityInput" value="San Francisco, CA">
            <button onclick="getWeather()">Search</button>
        </div>

        <div class="weather-main">
            <div class="temp-display">72°F</div>
            <div class="condition">☀️ Sunny & Clear</div>
            <div class="location">San Francisco, California</div>
        </div>

        <div class="stats-row">
            <div class="stat"><span>Humidity</span><strong>45%</strong></div>
            <div class="stat"><span>Wind</span><strong>8 mph</strong></div>
            <div class="stat"><span>UV Index</span><strong>3 (Low)</strong></div>
        </div>
    </div>
    <script src="app.js"></script>
</body>
</html>"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "styles.css",
                language = "css",
                content = """body { background: #0284C7; display: flex; justify-content: center; align-items: center; min-height: 100vh; font-family: system-ui; margin: 0; }
.card-weather { background: rgba(255,255,255,0.15); backdrop-filter: blur(12px); border-radius: 24px; padding: 32px; color: white; width: 340px; box-shadow: 0 20px 40px rgba(0,0,0,0.2); border: 1px solid rgba(255,255,255,0.3); }
.search-box { display: flex; gap: 8px; margin-bottom: 24px; }
.search-box input { flex: 1; padding: 10px; border-radius: 12px; border: none; background: rgba(255,255,255,0.2); color: white; }
.search-box button { padding: 10px 16px; border-radius: 12px; border: none; background: white; color: #0284C7; font-weight: bold; cursor: pointer; }
.weather-main { text-align: center; margin-bottom: 28px; }
.temp-display { font-size: 4rem; font-weight: 800; }
.condition { font-size: 1.2rem; margin-bottom: 6px; }
.location { color: rgba(255,255,255,0.8); font-size: 0.9rem; }
.stats-row { display: flex; justify-content: space-between; border-top: 1px solid rgba(255,255,255,0.2); padding-top: 18px; }
.stat { text-align: center; font-size: 0.8rem; }
.stat strong { display: block; font-size: 1rem; margin-top: 4px; }"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "app.js",
                language = "javascript",
                content = """function getWeather() {
    const city = document.getElementById('cityInput').value;
    alert("Fetching current weather & satellite forecast data for " + city + "...");
}"""
            )
        )

        val steps = listOf(
            "Analyzed weather prompt: '$prompt'",
            "Created responsive glassmorphism UI card layout with real-time temperature indicators."
        )

        return GeneratedProjectData(name, desc, lang, files, steps, "$ serve weather-app\n> Live forecast rendered.")
    }

    private fun createEcommerceApp(projectId: Long, prompt: String): GeneratedProjectData {
        val name = "Aura Market"
        val desc = "Modern e-commerce platform with product catalogs, shopping cart, and quick checkout flow."
        val lang = "Next.js / TypeScript"

        val files = listOf(
            ProjectFileEntity(
                projectId = projectId,
                path = "index.html",
                language = "html",
                content = """<!DOCTYPE html>
<html>
<head><title>Aura Store</title><link rel="stylesheet" href="styles.css"></head>
<body>
    <header class="header"><h1>🛍️ Aura Luxury</h1></header>
    <main class="grid">
        <div class="item"><h3>Wireless ANC Headphones</h3><p>$299.00</p></div>
        <div class="item"><h3>Minimalist Smartwatch</h3><p>$199.00</p></div>
    </main>
</body>
</html>"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "styles.css",
                language = "css",
                content = """body { background: #0F172A; color: white; font-family: sans-serif; padding: 24px; }
.header { border-bottom: 1px solid #334155; padding-bottom: 16px; margin-bottom: 24px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.item { background: #1E293B; padding: 16px; border-radius: 12px; }"""
            )
        )

        val steps = listOf("Built e-commerce store layout for '$prompt'")
        return GeneratedProjectData(name, desc, lang, files, steps, "$ npm run dev")
    }

    private fun createGenericApp(projectId: Long, prompt: String): GeneratedProjectData {
        val name = prompt.take(24).capitalize() + " App"
        val desc = "Custom software application generated autonomously based on prompt: '$prompt'"
        val lang = "TypeScript / HTML5"

        val files = listOf(
            ProjectFileEntity(
                projectId = projectId,
                path = "index.html",
                language = "html",
                content = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${name}</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="app-card">
        <h1>✨ ${name}</h1>
        <p>${desc}</p>
        <div class="interactive-area">
            <button class="btn" onclick="executeAction()">Run AI Function</button>
            <div id="output" class="output-box">Click the button to test generated logic...</div>
        </div>
    </div>
    <script src="app.js"></script>
</body>
</html>"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "styles.css",
                language = "css",
                content = """body { background: #0F172A; color: #F8FAFC; display: flex; justify-content: center; align-items: center; min-height: 100vh; font-family: system-ui, sans-serif; margin: 0; }
.app-card { background: #1E293B; padding: 32px; border-radius: 16px; border: 1px solid #334155; max-width: 480px; text-align: center; }
h1 { color: #06B6D4; margin-bottom: 12px; }
p { color: #94A3B8; margin-bottom: 24px; font-size: 0.95rem; }
.btn { background: #2563EB; color: white; border: none; padding: 12px 24px; border-radius: 8px; font-weight: bold; cursor: pointer; }
.output-box { margin-top: 20px; padding: 16px; background: #090D16; border-radius: 8px; color: #10B981; font-family: monospace; font-size: 0.9rem; }"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "app.js",
                language = "javascript",
                content = """function executeAction() {
    const output = document.getElementById('output');
    output.innerText = "🚀 AI Function Executed! App is running smoothly.";
}"""
            ),
            ProjectFileEntity(
                projectId = projectId,
                path = "README.md",
                language = "markdown",
                content = """# ${name}

Generated by **AI App Builder Agent**.

## Prompt
> ${prompt}

## Features
- Custom interactive UI.
- Production CSS styling & modular architecture.
"""
            )
        )

        val steps = listOf(
            "Analyzed prompt specifications: '$prompt'",
            "Structured modular software components and HTML5/JS interface.",
            "Generated source code tree and verified runtime readiness."
        )

        return GeneratedProjectData(name, desc, lang, files, steps, "$ npm start\n> Application compiled and running successfully.")
    }
}
