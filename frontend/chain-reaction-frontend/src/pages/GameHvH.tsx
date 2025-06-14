import { useEffect, useRef, useState } from 'react';
import GameBoard from '../components/GameBoard';
import GameControls from '../components/GameControls';
import { getCurrentPlayer, getGameStateVersion, isGameOver } from '../middleware/api';
import { useNavigate } from 'react-router-dom';

function GameHvH() {
  const [player, setPlayer] = useState('');
  const [version, setVersion] = useState(0);
  const lastVersion = useRef(0);

  const navigate = useNavigate();




  const refreshPlayer = () => {
    getCurrentPlayer().then(async res => {
      const name = typeof res.data === 'string' ? res.data : res.data.player;
      setPlayer(name);
      const gameOverRes = await isGameOver();
        if (gameOverRes.data === true) {
            console.log("Game is over, navigating to results page");
            await new Promise(resolve => setTimeout(resolve, 180));
            navigate('/results');
        }
    });
  };

  // Poll for file changes
  useEffect(() => {
    const interval = setInterval(() => {
      getGameStateVersion()
        .then(res => {
          const newVersion = res.data;
          console.log("New version:", newVersion, "Last version:", lastVersion.current);
          if (newVersion !== lastVersion.current) {
            lastVersion.current = newVersion;
            setVersion(newVersion); // triggers update
            refreshPlayer();
          }
        })
        .catch(console.error);
    }, 1000);
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
      background: 'linear-gradient(145deg, ' + (gradientStyleMap[player] || 'rgb(144, 187, 255), rgba(253, 176, 176, 0.83)') + ')',
      fontFamily: 'Arial, sans-serif',
      padding: '20px',
      boxSizing: 'border-box',
    }}>
      <div style={{
        backgroundColor: 'rgba(151, 136, 248, 0.99)',
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
        <h1 style={{ fontSize: '28px', fontWeight: 'bold', color: '#333', marginBottom: '10px' }}>
          Human vs Human Game
        </h1>
        <div style={{
          display: 'block',
          justifyContent: 'space-between',
          alignItems: 'center',
          width: '18%',
          marginBottom: '16px',
        }}>
        <GameControls
          gameMode="HvH"
          player={player}
          refreshPlayer={refreshPlayer}
        />
        <div>
          <span style={{ fontSize: '16px' }}> Current Move:{' '} </span>
          <span style={{ fontSize: '20px', fontWeight: 'bold', color: player === 'RED' ? 'red' : 'blue' }}> {player} </span>
        </div>
        </div>

        <GameBoard
          gameMode="HvH"
          refreshPlayer={refreshPlayer}
          version={version}
        />

      </div>
    </div>
  );
}


export default GameHvH;
