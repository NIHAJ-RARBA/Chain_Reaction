import { useEffect, useRef, useState } from 'react';
import GameBoard from '../components/GameBoard';
import GameControls from '../components/GameControls';
import { getCurrentPlayer, getGameStateVersion, isGameOver, makeAIMove } from '../middleware/api';
import { useNavigate } from 'react-router-dom';

function GameAivAi() {
  const [player, setPlayer] = useState('');
  const [version, setVersion] = useState(0);
  const lastVersion = useRef(0);
  const aiLooping = useRef(false);
  const [aiThinking, setAiThinking] = useState(false);
  const navigate = useNavigate();



  const refreshPlayer = async () => {
    try {
      const res = await getCurrentPlayer();
      const name = typeof res.data === 'string' ? res.data : res.data.player;
      setPlayer(name);
    } catch (err) {
      console.error("Failed to refresh player", err);
    }
  };

    // const setBoard = (): string => {
    //   console.log("Fetching game board");
    //   getBoard().then(res => {
    //       // get dimensions from the board string
    //     const boardString: string = res.data;
    //     let rows = 0;
    //     let cols = 0;
    //     for (let i = 0; i < boardString.length; i++) {
    //       if (boardString[i] === '\n') {
    //         rows++;
    //         cols = 0;
    //       } else {
    //         cols++;
    //       }
    //     }
    //     console.log(`Board dimensions: ${rows} rows, ${cols} columns`);
    //     return `${rows}x${cols}`;
    //   }).catch(err => {
    //     console.error("Failed to fetch board", err);
    //   });
    //   return '9x6'
            
    // };
  

  const runAILoop = async () => {
  if (aiLooping.current) return;
  aiLooping.current = true;

  while (true) {
    try {

      const gameOverRes = await isGameOver();
      console.log("Game over check:", gameOverRes.data);
      if (gameOverRes.data === true) {
        console.log("Game over detected");
        await new Promise(resolve => setTimeout(resolve, 180));
        navigate("/results");
        return;
      }
        setAiThinking(true);
        await new Promise((resolve) => setTimeout(resolve, 800));
      await makeAIMove();
        setAiThinking(false);

      const versionRes = await getGameStateVersion();
      const newVersion = versionRes.data; // backend returns plain number

      console.log("New version:", newVersion, "Last version:", lastVersion.current);

      if (newVersion !== lastVersion.current) {
        lastVersion.current = newVersion;
        setVersion(newVersion);
      }

      await refreshPlayer();

      
      

    } catch (err) {
        setAiThinking(false);
        console.error("AI loop error", err);
        break;
    }
  }

  aiLooping.current = false;
};


  const gradientStyleMap: Record<string, string> = {
    RED: 'rgb(90, 142, 225), rgba(249, 143, 143, 0.83)',
    BLUE: 'rgba(253, 150, 176, 0.83), rgb(90, 142, 225)',
  };

  useEffect(() => {
    refreshPlayer();
    runAILoop();
  }, []);

  return (
    <div style={{
      height: '100vh',
      width: '100vw',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(350deg, ' + (gradientStyleMap[player] || 'rgb(90, 142, 225), rgba(249, 143, 143, 0.83)') + ')',
      fontFamily: 'Arial, sans-serif',
      padding: '20px',
      boxSizing: 'border-box',
    }}>
      <div style={{
        backgroundColor: 'rgba(214, 192, 254, 0.08)',
        borderRadius: '16px',
        boxShadow: '0 8px 24px rgba(0, 0, 0, 0.1)',
        padding: '40px',
        width: '100%',
        maxWidth: '900px',
        minHeight: '600px',
        display: 'flex',
        flexDirection: 'column',
        gap: '24px',
        justifyContent: 'center',
        alignItems: 'center',
      }}>
        <h1 style={{ fontSize: '28px', color: '#333', marginBottom: '10px' }}>
          Spectating AI vs AI Game
        </h1>

        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          width: '100%',
          marginBottom: '16px',
        }}>
          <div>
            <GameControls gameMode={"AiVAi"} player={player} refreshPlayer={refreshPlayer} />
          </div>

          <div style={{
            fontSize: '18px',
            fontWeight: 'bold',
            color: '#222',
            textAlign: 'center',
            flex: '1',
          }}>
            Current Move:{' '}
              <span style={{ color: player === 'RED' ? 'red' : 'blue' }}>
                {player}
              </span>
          </div>

          <div style={{ minWidth: '150px', textAlign: 'right' }}>
            {aiThinking && (
              <div style={{
                fontSize: '16px',
                color: '#555',
                fontWeight: 'bold',
                padding: '6px 12px',
                backgroundColor: '#f1f1f1',
                borderRadius: '8px',
                boxShadow: 'inset 0 0 5px rgba(0,0,0,0.1)',
                display: 'inline-block'
              }}>
                AI is thinking...
              </div>
            )}
          </div>
        </div>

        <GameBoard gameMode={"AiVAi"} refreshPlayer={refreshPlayer} version={version} />
      </div>
    </div>
  );
}

export default GameAivAi;
