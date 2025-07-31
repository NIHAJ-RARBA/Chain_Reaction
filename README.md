# Chain Reaction Game

![Java](https://img.shields.io/badge/Java-17+-orange) ![React](https://img.shields.io/badge/React-19-blue) ![TypeScript](https://img.shields.io/badge/TypeScript-5.7-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)

> A strategic board game with AI opponents featuring minimax algorithms and modern web interface

---

## Game Description

Chain Reaction is a strategic board game where players place atoms on a grid. When cells reach critical mass, they explode and spread atoms to adjacent cells, creating chain reactions.

## Game Features

<table width="100%">
<tr>
<td width="50%" valign="top">

<h3>Game Modes</h3>
<ul>
<li><strong>Human vs Human</strong> - Classic two-player mode</li>
<li><strong>Human vs AI</strong> - Play against intelligent computer opponents</li>  
<li><strong>AI vs AI</strong> - Watch AI algorithms compete</li>
</ul>

<h3>AI Implementation</h3>
<ul>
<li><strong>Minimax Algorithm</strong> - Optimal move selection</li>
<li><strong>Alpha-Beta Pruning</strong> - Performance optimization</li>
<li><strong>Multiple Heuristics</strong> - Various board evaluation strategies</li>
<li><strong>Timeout Protection</strong> - AI moves have time limits</li>
</ul>

</td>
<td width="50%" valign="top">

<h3>User Interface</h3>
<ul>
<li><strong>Clean, Modern Design</strong> - React-based interface with TypeScript</li>
<li><strong>Real-time Updates</strong> - Live game board updates</li>
<li><strong>Player Status Indicators</strong> - Clear visual feedback</li>
<li><strong>Responsive Layout</strong> - Works on different screen sizes</li>
</ul>

<h3>Technical Features</h3>
<ul>
<li><strong>Full-Stack Architecture</strong> - Spring Boot backend + React frontend</li>
<li><strong>File-based Game State</strong> - Persistent game state management</li>
<li><strong>RESTful API</strong> - Clean separation between frontend and backend</li>
<li><strong>Hot Reload</strong> - Development-friendly with automatic reloading</li>
</ul>

</td>
</tr>
</table>

## Running the Game

### Quick Start
```bash
# 1. Clone the repository
git clone https://github.com/NIHAJ-RARBA/Chain_Reaction.git

# 2. Launch the game
start.bat

# 3. Open browser
http://localhost:5173

# 4. When closing, close browser tab first. Then go to launcher window and press any key.
```

### Prerequisites
- **Java** 17+
- **Node.js** 18+
- **Internet** connection

### Setup Scripts
| Script | Purpose |
|--------|---------|
| `setup-complete.bat` | Complete project setup |
| `start.bat` | Launch both servers |
| `start-backend.bat` | Backend only |
| `start-frontend.bat` | Frontend only |
| `cleanup.bat` | Remove build artifacts |

### Application URLs
| Service | URL |
|---------|-----|
| **Game Interface** | http://localhost:5173 |
| **Backend API** | http://localhost:8080 |

## Project Structure

```
Chain_Reaction/
├── setup-complete.bat        # Complete project setup script
├── start.bat                 # Main launcher script
├── start-backend.bat         # Backend startup script
├── start-frontend.bat        # Frontend startup script
├── cleanup.bat               # Build cleanup script
├── backend/
│   └── chainReaction/        # Spring Boot application
│       ├── src/             # Java source code
│       ├── target/          # Built JAR files
│       ├── mvnw.cmd        # Maven wrapper
│       └── pom.xml         # Maven dependencies
└── frontend/
    └── chain-reaction-frontend/ # React application
        ├── src/            # React TypeScript source
        ├── public/         # Static assets
        ├── package.json    # npm dependencies
        └── vite.config.ts  # Build configuration
```

## Technical Implementation

<table>
<tr>
<td width="50%" valign="top">

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.5.0
- **Language**: Java 17
- **Build Tool**: Maven
- **API**: RESTful endpoints

</td>
<td width="50%" valign="top">

### Frontend (React + TypeScript)
- **Framework**: React 19 with TypeScript
- **Build Tool**: Vite
- **Styling**: CSS with responsive design
- **State Management**: React Hooks

</td>
</tr>
</table>

## Troubleshooting

| Issue | Solution |
|-------|----------|
| **Setup fails** | Run as Administrator, check Java/Node versions |
| **Ports in use** | Run `cleanup.bat` |
| **Services won't start** | Verify prerequisites, run cleanup first |
| **Build errors** | Check Java 17+ in PATH, internet connection |

### Manual Commands
```bash
# Backend
cd "backend/chainReaction"
./mvnw.cmd spring-boot:run

# Frontend  
cd "frontend/chain-reaction-frontend"
npm install && npm run dev
```

---

## Project Info

**Academic Project** - AI/Adversarial Search coursework demonstrating game theory and AI algorithms.
