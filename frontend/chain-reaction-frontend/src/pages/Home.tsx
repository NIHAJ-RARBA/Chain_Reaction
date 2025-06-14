import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { setBoardSize, setAiVsAiHeuristic, setHvAiHeuristic } from '../middleware/api';

function Home() {
  const navigate = useNavigate();
  const [rows, setRows] = useState(9);
  const [cols, setCols] = useState(6);
  const [ai1Heuristic, setAi1Heuristic] = useState(localStorage.getItem('ai1Heuristic') || '1');
  const [ai2Heuristic, setAi2Heuristic] = useState(localStorage.getItem('ai2Heuristic') || '1');

  useEffect(() => {
    setBoardSize(rows, cols);
    localStorage.setItem('boardSize', `${rows}x${cols}`);
    
  }, [rows, cols]);

  useEffect(() => {
    
    setHvAiHeuristic(parseInt(ai1Heuristic));
    setAiVsAiHeuristic(parseInt(ai1Heuristic), parseInt(ai2Heuristic));
    localStorage.setItem('ai1Heuristic', ai1Heuristic);
    localStorage.setItem('ai2Heuristic', ai2Heuristic);


  }, [ai1Heuristic, ai2Heuristic]);


  const handleStart = (route: string) => {
    setBoardSize(rows, cols).then(() => {

      setHvAiHeuristic(parseInt(ai1Heuristic));
      setAiVsAiHeuristic(parseInt(ai1Heuristic), parseInt(ai2Heuristic));

      navigate(route);
      window.location.reload();
    });
  };

  const dropdownStyle = {
    width: '100%',
    padding: '8px',
    borderRadius: '6px',
    border: '1px solid #ccc',
    fontSize: '14px',
    marginTop: '6px',
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    cursor: 'pointer'
  };


  return (
    <div style={{
      height: '100vh',
      width: '100vw',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(to bottom right, rgb(59, 62, 73), rgb(11, 24, 56))',
      fontFamily: 'Arial, sans-serif',
      padding: '20px',
      boxSizing: 'border-box',
    }}>
      <div style={{
        backgroundColor: '#ffffff',
        borderRadius: '16px',
        boxShadow: '0 8px 24px rgba(0, 0, 0, 0.1)',
        padding: '40px',
        width: '100%',
        maxWidth: '600px',
        textAlign: 'center',
      }}>
        <h1 style={{ fontSize: '32px', color: '#333', marginBottom: '20px' }}>Welcome to Chain Reaction</h1>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginBottom: '30px' }}>
          <button
            onClick={() => handleStart('/humanVhuman')}
            style={buttonStyle}
            onMouseOver={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#0056b3')}
            onMouseOut={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#007bff')}
          >
            Play Against A Friend
          </button>
          <button
            onClick={() => handleStart('/humanVAi')}
            style={buttonStyle}
            onMouseOver={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#0056b3')}
            onMouseOut={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#007bff')}
          >
            Play Against AI
          </button>
          <button
            onClick={() => handleStart('/AiVAi')}
            style={buttonStyle}
            onMouseOver={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#0056b3')}
            onMouseOut={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#007bff')}
          >
            Watch AI vs AI
          </button>
        </div>

        <div style={{
          backgroundColor: 'rgba(135, 174, 233, 0.9)',
          borderRadius: '12px',
          padding: '20px',
          boxShadow: 'inset 0 0 4px rgba(55, 39, 39, 0.05)',
          justifyContent: 'space-between',
          textAlign: 'left'
        }}>
          <div>
          <h3 style={{ fontSize: '20px', color: '#000', marginBottom: '12px' }}>Board Settings</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <label style={{ fontSize: '16px', color: '#333' }}>
              Rows:
              <input
                type="number"
                min={1}
                value={rows}
                onChange={e => setRows(parseInt(e.target.value) || 1)}
                style={input1Style}
              />
            </label>
            <label style={{ fontSize: '16px', color: '#333' }}>
              Columns:
              <input
                type="number"
                min={1}
                value={cols}
                onChange={e => setCols(parseInt(e.target.value) || 1)}
                style={input2Style}
              />
            </label>
          
          
          </div>
          </div>


          <div style={{ marginTop: '20px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
            <div>
              <label style={{ fontWeight: 'bold', fontSize: '16px' }}>AI 1 Heuristic (Play Against AI):</label>
              <select
                style={dropdownStyle}
                onChange={(e) => setAi1Heuristic(e.target.value)}
                defaultValue={ai1Heuristic}
              >
                <option value="0">Heuristic 0 - Equal Eval</option>
                <option value="1">Heuristic 1 - Max Explosions</option>
                <option value="2">Heuristic 2 - Least Attacks</option>
                <option value="3">Heuristic 3 - Max Orbs</option>
                <option value="4">Heuristic 4 - Orb Difference</option>
                <option value="5">Heuristic 5 - Explosion Potential</option>
                <option value="6">Heuristic 6 - Vulnerability Penalty</option>
                <option value="7">Heuristic 7 - Capture Potential</option>
                <option value="8">Heuristic 8 - Positional Control</option>
              </select>
            </div>

            <div>
              <label style={{ fontWeight: 'bold', fontSize: '16px' }}>AI 2 Heuristic:</label>
              <select
                style={dropdownStyle}
                onChange={(e) => setAi2Heuristic(e.target.value)}
                defaultValue= {ai2Heuristic}
              >
                <option value="0">Heuristic 0 - Equal Eval</option>
                <option value="1">Heuristic 1 - Max Explosions</option>
                <option value="2">Heuristic 2 - Least Attacks</option>
                <option value="3">Heuristic 3 - Max Orbs</option>
                <option value="4">Heuristic 4 - Orb Difference</option>
                <option value="5">Heuristic 5 - Explosion Potential</option>
                <option value="6">Heuristic 6 - Vulnerability Penalty</option>
                <option value="7">Heuristic 7 - Capture Potential</option>
                <option value="8">Heuristic 8 - Positional Control</option>
              </select>
            </div>
          </div>


        </div>
      </div>
    </div>
  );
}

const buttonStyle = {
  backgroundColor: '#007bff',
  color: '#fff',
  border: 'none',
  padding: '10px 20px',
  borderRadius: '8px',
  fontSize: '16px',
  cursor: 'pointer',
  transition: 'background-color 0.3s ease'
};

const input1Style = {
  width: '60px',
  marginLeft: '35px',
  padding: '4px',
  paddingLeft: '10px',
  borderRadius: '6px',
  border: '1px solid #ccc',
  fontSize: '14px'
};
const input2Style = {
  width: '60px',
  marginLeft: '12px',
  padding: '4px',
  paddingLeft: '10px',
  borderRadius: '6px',
  border: '1px solid #ccc',
  fontSize: '14px'
};

export default Home;
