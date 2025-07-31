# Chain Reaction Game

A strategy game where players place atoms on a grid to trigger explosive chain reactions. Features AI opponents with advanced algorithms and a modern web interface.

## Game Description

Chain Reaction is a strategic board game where players take turns placing atoms on a grid. When a cell reaches its critical mass, it explodes and spreads atoms to adjacent cells, potentially causing chain reactions that can dramatically change the game state.

## Game Features

### Game Modes
- **Human vs Human**: Classic two-player mode
- **Human vs AI**: Play against intelligent computer opponents
- **AI vs AI**: Watch AI algorithms compete against each other

### AI Implementation
- **Minimax Algorithm**: Optimal move selection with configurable depth
- **Alpha-Beta Pruning**: Performance optimization for faster decisions
- **Multiple Heuristics**: Various board evaluation strategies
- **Timeout Protection**: AI moves have time limits to prevent hanging

### User Interface
- **Clean, Modern Design**: React-based interface with TypeScript
- **Real-time Updates**: Live game board updates via file polling
- **Player Status Indicators**: Clear visual feedback for game state
- **Responsive Layout**: Works on different screen sizes

### Technical Features
- **Full-Stack Architecture**: Spring Boot backend + React frontend
- **File-based Game State**: Persistent game state management
- **RESTful API**: Clean separation between frontend and backend
- **Hot Reload**: Development-friendly with automatic reloading

## Running the Game

### Quick Start
1. **Download** the `setup-complete.bat` script
2. **Run** the script in any directory to set up the complete project
3. **Launch** the game using the generated `start.bat` file
4. **Play** at http://localhost:3000

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Internet connection (for dependency downloads)

### First Time Setup
The `setup-complete.bat` script automatically:
- Checks and guides installation of required tools
- Creates the complete project structure
- Downloads all dependencies (Maven + npm)
- Generates helper scripts for easy launching

### Running After Setup
- **Quick Launch**: Double-click `start.bat`
- **Backend Only**: Run `start-backend.bat`
- **Frontend Only**: Run `start-frontend.bat`
- **Cleanup**: Run `cleanup.bat` to remove build artifacts

### Application URLs
- **Game Interface**: http://localhost:3000
- **Backend API**: http://localhost:8080

### Performance Notes
- **First startup**: ~30 seconds (building Java application)
- **Subsequent runs**: ~10 seconds (using cached build)
- **Auto-features**: Handles dependencies, port conflicts, browser opening

### Stopping the Application
- **Recommended**: Close terminal windows with Ctrl+C
- **If stuck**: Run `cleanup-ports.bat` to force-stop services
- **Manual**: Kill Java and Node processes via Task Manager

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

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.5.0
- **Language**: Java 17
- **Build Tool**: Maven
- **API**: RESTful endpoints for game operations

### Frontend (React + TypeScript)
- **Framework**: React 19 with TypeScript
- **Build Tool**: Vite
- **Styling**: CSS with responsive design
- **State Management**: React Hooks

## Troubleshooting

### Common Issues

**Setup fails?**
- Ensure Java 17+ and Node.js 18+ are installed
- Run `setup-complete.bat` as Administrator
- Check internet connection for dependency downloads

**Ports already in use?**
```bash
.\cleanup.bat
```

**Services won't start?**
- Verify prerequisites: `java -version` and `node -version`
- Run cleanup first, then restart
- Check Windows firewall/antivirus blocking

**Build errors?**
- Ensure Java 17+ is properly installed and in PATH
- Clear Maven cache if needed
- Check internet connection for dependencies

### Manual Commands

If automatic scripts fail:

```bash
# Backend
cd "backend/chainReaction"
./mvnw.cmd spring-boot:run

# Frontend  
cd "frontend/chain-reaction-frontend"
npm install
npm run dev
```

## Contributing

This is an academic project for AI/Adversarial Search coursework. The implementation demonstrates various AI algorithms and game theory concepts.

## License

Academic project - see course documentation for usage terms.

---

**Note**: This application was built as part of an AI course focusing on adversarial search algorithms and game theory implementations.
