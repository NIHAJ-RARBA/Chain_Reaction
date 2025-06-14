import { useEffect, useRef, useState } from 'react';
import GameBoard from '../components/GameBoard';
import GameControls from '../components/GameControls';
import { getCurrentPlayer, getGameStateVersion, isCurrentPlayerHuman, isGameOver } from '../middleware/api';

import { useNavigate } from 'react-router-dom';


function GameHvAi() {
  const [player, setPlayer] = useState('');
  const [version, setVersion] = useState(0);
  const [aiThinking, setAiThinking] = useState(false);
  const lastVersion = useRef(0);
  const navigate = useNavigate();

  const refreshPlayer = async () => {
    try {
      const res = await getCurrentPlayer();
      const name = typeof res.data === 'string' ? res.data : res.data.player;
      setPlayer(name);

      const isHumanRes = await isCurrentPlayerHuman();
        console.log("isCurrentPlayerHuman response:", isHumanRes.data);
        setAiThinking(!isHumanRes.data);
        const gameOverRes = await isGameOver();
        if (gameOverRes.data === true) {
            console.log("Game is over, navigating to results page");
            await new Promise(resolve => setTimeout(resolve, 180));
            navigate('/results');
        }

    } catch (err) {
      console.error("Failed to refresh player", err);
    }
  };

  useEffect(() => {
    const interval = setInterval(() => {
      getGameStateVersion()
        .then(async res => {
          const newVersion = res.data.version ?? res.data;
          if (newVersion !== lastVersion.current) {
            lastVersion.current = newVersion;
            setVersion(newVersion);
            refreshPlayer();
            await new Promise((resolve) => setTimeout(resolve, 800));
                  
          }
        })
        .catch(console.error);
    }, 700);

    return () => clearInterval(interval);
  }, []);

  const gradientStyleMap: Record<string, string> = {
    RED: 'rgb(144, 187, 255), rgba(253, 176, 176, 0.83)',
    BLUE: 'rgba(253, 176, 176, 0.83), rgb(144, 187, 255)',
  };


  return (
    <div style={{
      height: '100vh',
      width: '100vw',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(210deg, ' + (gradientStyleMap[player] || 'rgb(144, 187, 255), rgba(253, 176, 176, 0.83)') + ')',
      fontFamily: 'Arial, sans-serif',
      padding: '20px',
      boxSizing: 'border-box',
    }}>
      <div style={{
        backgroundColor: 'rgba(126, 119, 206, 0.69)',
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
          Human vs AI Game
        </h1>

        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          width: '100%',
          marginBottom: '16px',
        }}>
          <div>
            <GameControls gameMode={"HvAI"} player={player} refreshPlayer={refreshPlayer} />
          </div>

          <div style={{
            fontSize: '18px',
            fontWeight: 'bold',
            color: '#222',
            textAlign: 'center',
            flex: '1',
          }}>
            <span style={{ fontSize: '16px' }}> Current Move:{' '} </span>
            <span style={{ fontSize: '20px', fontWeight: 'bold', color: player === 'RED' ? 'red' : 'blue' }}> {player} </span>
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

        {/* Game board */}
        <GameBoard gameMode={"HvAI"} refreshPlayer={refreshPlayer} version={version} />
      </div>
    </div>
  );
}

export default GameHvAi;
