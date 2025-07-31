# Chain Reaction Game

A full-stack implementation of the Chain Reaction game with AI opponents using Spring Boot backend and React frontend.

## Game Description

Chain Reaction is a strategy game where players place atoms on a grid. When a cell reaches its critical mass, it explodes and spreads atoms to adjacent cells, potentially causing chain reactions.

## Project Structure

```
Chain_Reaction/
├── start.bat                 # Main launcher script
├── start-backend.bat         # Backend startup script
├── start-frontend.bat        # Frontend startup script
├── backend/
│   └── chainReaction/        # Spring Boot application
│       ├── src/
│       ├── target/           # Built JAR files
│       ├── mvnw.cmd         # Maven wrapper
│       └── pom.xml          # Maven dependencies
└── frontend/
    └── chain-reaction-frontend/ # React application
        ├── src/
        ├── public/
        ├── package.json
        └── vite.config.ts
```

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher

### Getting Started

1. **Double-click** `start.bat` or run from command line:
   ```bash
   .\start.bat
   ```
2. **Wait** for both windows to open and load
3. **Browser opens automatically** to the game interface
4. **Start playing!**

### Performance Notes
- **First startup**: ~30 seconds (building Java application)
- **Subsequent runs**: ~10 seconds (using cached build)
- **Auto-features**: Installs dependencies, handles port conflicts, opens browser

### Stopping the Application

When you're done playing:
1. **In the launcher window**: Press **Q** to quit and cleanup automatically  
2. **Or manually**: Close both terminal windows (backend and frontend)
3. **If stuck**: Run `cleanup-ports.bat` to force-stop everything

### Manual Start (Advanced)
- Backend only: Run `start-backend.bat`
- Frontend only: Run `start-frontend.bat`

### First Time Setup

The startup scripts automatically handle:
- Building the Spring Boot application (if needed)
- Installing npm dependencies (if needed)
- Starting both servers
- Handling username paths with spaces
- Opening browser to game interface

## Application URLs

- **Frontend (Game UI)**: http://localhost:5173 (or next available port)
- **Backend (API)**: http://localhost:8080

## Game Features

- **Game Modes**:
  - Human vs Human
  - Human vs AI
  - AI vs AI
- **AI Implementation**: Minimax algorithm with alpha-beta pruning and multiple heuristics
- **Game State Updates**: File-based polling for real-time board updates
- **Responsive UI**: React-based interface with TypeScript

## Development

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.5.0
- **Language**: Java 17
- **Build Tool**: Maven

### Frontend (React + TypeScript)
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **Styling**: Inline CSS styling
- **State Management**: React Hooks

## Key Files

- `start.bat` - Main application launcher (with cleanup options)
- `start-backend.bat` - Spring Boot server launcher
- `start-frontend.bat` - React development server launcher
- `cleanup-ports.bat` - Force cleanup all services and free ports
- `backend/chainReaction/src/main/java/` - Java source code
- `frontend/chain-reaction-frontend/src/` - React source code

## Troubleshooting

### Quick Fixes

**Ports already in use?**
```bash
.\cleanup-ports.bat
```

**Services won't start?**
- Check Java 17+ is installed: `java -version`
- Check Node.js 16+ is installed: `node -version`
- Run cleanup script first, then restart

### Port Cleanup

**Automatic Cleanup**: The main launcher (`start.bat`) includes cleanup options:
- Press **Q** when exiting to automatically stop all services and free ports
- Press **C** for manual cleanup commands
- Press any other key to exit launcher only (services continue running)

**Manual Cleanup**: If services are stuck or ports remain occupied:
```bash
# Run the dedicated cleanup script
.\cleanup-ports.bat

# Or manually kill processes
taskkill /F /IM java.exe    # Stop backend
taskkill /F /IM node.exe    # Stop frontend
```

### Common Issues

1. **Maven wrapper fails**: The scripts automatically handle username paths with spaces using Windows short path format.

2. **Port conflicts**: 
   - Frontend will automatically use the next available port if 5173 is busy
   - Backend uses port 8080 (ensure it's not in use)
   - Use `cleanup-ports.bat` to force-free occupied ports

3. **Build errors**: Check Java version (17+) and internet connection for Maven dependencies

4. **npm errors**: Ensure Node.js is installed and accessible from command line

5. **Services won't stop**: Use the cleanup script or close command windows with Ctrl+C

### Manual Commands

If automatic scripts fail, you can run manually:

```bash
# Backend
cd "backend/chainReaction"
./mvnw.cmd spring-boot:run

# Frontend  
cd "frontend/chain-reaction-frontend"
npm install
npm run dev
```

## Game Algorithms

The AI implementation includes:
- **Minimax Algorithm**: For optimal move selection with configurable depth
- **Alpha-Beta Pruning**: For performance optimization
- **Heuristic Evaluation Functions**: Multiple custom board evaluation strategies
- **Timeout Protection**: AI moves have time limits to prevent hanging

## UI Features

- Clean, modern interface
- Real-time game board updates via file polling
- Player status indicators
- Game mode selection
- Game results display

## Contributing

This is an academic project for AI/Adversarial Search coursework. The implementation demonstrates various AI algorithms and game theory concepts.

## License

Academic project - see course documentation for usage terms.

---

**Note**: This application was built as part of an AI course focusing on adversarial search algorithms and game theory implementations.
