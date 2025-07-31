# Quick Start Guide

## Getting Started

1. **Double-click** `start.bat` 
2. **Wait** for both windows to open and load
3. **Open browser** to http://localhost:5173 (or the port shown)
4. **Start playing!**

## Stopping the Application

When you're done playing:
1. **In the launcher window**: Press **Q** to quit and cleanup automatically
2. **Or manually**: Close both terminal windows (backend and frontend)
3. **If stuck**: Run `cleanup-ports.bat` to force-stop everything

## Project Files

### Launcher Scripts
- **`start.bat`** - Main launcher (use this!)
- **`cleanup-ports.bat`** - Emergency cleanup script
- **`start-backend.bat`** - Spring Boot server only
- **`start-frontend.bat`** - React UI only

### Application
- **`backend/`** - Java Spring Boot API server
- **`frontend/`** - React TypeScript game interface  
- **`README.md`** - Full documentation

## How It Works

1. **First run**: Builds the Java application automatically
2. **Subsequent runs**: Uses cached build for faster startup
3. **Dependencies**: Automatically installs npm packages if needed
4. **Ports**: Auto-detects available ports if defaults are busy
5. **Cleanup**: Automatically frees ports when exiting properly

## Game Modes Available

- Human vs Human
- Human vs AI  
- AI vs AI

## Performance Notes

- **First startup**: ~30 seconds (building Java app)
- **Later startups**: ~10 seconds (using cached build)
- **Supports**: Windows with Java 17+ and Node.js 16+
- **Cleanup**: Automatic port cleanup on proper exit

## Troubleshooting

**Ports already in use?**
```bash
.\cleanup-ports.bat
```

**Services won't start?**
- Check Java 17+ is installed
- Check Node.js 16+ is installed
- Run cleanup script first

---
**Ready to play? Just run `start.bat`!**
