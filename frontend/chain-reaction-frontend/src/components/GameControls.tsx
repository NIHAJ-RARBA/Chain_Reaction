import { startGameHvH, startGameHvAi, startGameAivAi } from '../middleware/api';

interface GameControlsProps {
    gameMode: 'HvH' | 'HvAI' | 'AiVAi';
    player: string;
    refreshPlayer: () => void;
}

function GameControls({ gameMode, player, refreshPlayer }: GameControlsProps) {

  console.log("GameControls rendered with gameMode:", gameMode, "and player:", player);
  const getAiHeuristics = () => {
    const ai1Heuristic = parseInt(localStorage.getItem('ai1Heuristic') || '1');
    const ai2Heuristic = parseInt(localStorage.getItem('ai2Heuristic') || '1');
    return { ai1Heuristic, ai2Heuristic };
  };
  const handleStart = async () => {
    console.log(`Restarting game in ${gameMode} mode`);
    const { rows, cols } = getConfig();
    const { ai1Heuristic, ai2Heuristic } = getAiHeuristics();
    
    try {
      if (gameMode === 'HvH') {
        await startGameHvH(rows, cols);
      } else if (gameMode === 'HvAI') {
        await startGameHvAi(rows, cols, ai1Heuristic);
      } else {
        await startGameAivAi(rows, cols, ai1Heuristic, ai2Heuristic);
      }
      refreshPlayer();
      console.log(`Game restarted in ${gameMode} mode`);
    } catch (err) {
      console.error("Failed to start game", err);
    }
  };

  const handleRestart = async () => {
    await handleStart();
    
    // Wait a bit for the backend to process the restart
    await new Promise(resolve => setTimeout(resolve, 500));

    window.location.reload();
  };

  const getConfig = () => {
    const savedBoard = localStorage.getItem('boardSize') || '9x6';
    const [rows, cols] = savedBoard.split('x').map(n => parseInt(n, 10));
    return { rows, cols };
  };

  return (
    <div>
      <button onClick={handleRestart}>Restart Game</button>
      <h3>
        {/* Current Move:{' '}
        <span style={{ color: player === 'RED' ? 'red' : 'blue' }}>
          {player}
        </span> */}
      </h3>
    </div>
  );
}

export default GameControls;