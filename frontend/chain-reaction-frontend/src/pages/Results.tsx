import React, { useEffect } from 'react';
import { getResults, resetPlayerTypes } from '../middleware/api';
import { useNavigate } from 'react-router-dom';


function Results() {
  const [winner, setWinner] = React.useState('');
  const [stats, setStats] = React.useState({});
  const navigate = useNavigate();

  const handleReset = () => {
    resetPlayerTypes();
    localStorage.removeItem('ai1Heuristic');
    localStorage.removeItem('ai2Heuristic');
    navigate('/');
  };

  useEffect(() => {
    getResults()
      .then(res => {
        const data = res;
        setWinner(data.winner || 'No winner');
        setStats(data.stats || {});
      })
      .catch(err => {
        console.error("Failed to fetch results", err);
        setWinner('Error fetching results');
        setStats({});
      });
  }, []);

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
        boxShadow: '0 8px 24px rgba(226, 222, 222, 0.1)',
        padding: '30px',
        width: '100%',
        maxWidth: '600px',
        textAlign: 'center',
      }}>
        
                
        <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px'
        }}>
        <h1 style={{ fontSize: '32px', color: '#333', margin: 0 }}>Game Over</h1>
        <button
            onClick={handleReset}
            style={{
            backgroundColor: '#007bff',
            color: '#fff',
            border: 'none',
            padding: '8px 16px',
            borderRadius: '8px',
            fontSize: '14px',
            cursor: 'pointer',
            transition: 'background-color 0.3s ease',
            marginLeft: '20px'
            }}
            onMouseOver={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#0056b3')}
            onMouseOut={e => ((e.target as HTMLButtonElement).style.backgroundColor = '#007bff')}
        >
            Go to Home
        </button>
        </div>

        <p style={{ fontSize: '18px', color: '#555', marginBottom: '30px' }}>Thanks for playing!</p>



        
        <div style={{
          backgroundColor: 'rgba(135, 174, 233, 0.9)',
          borderRadius: '12px',
          padding: '20px',
          marginBottom: '20px',
          boxShadow: 'inset 0 0 4px rgba(55, 39, 39, 0.05)'
        }}>
          <h2 style={{ fontSize: '24px', color: 'rgba(0, 4, 9, 0.9)', marginBottom: '8px' }}>Winner</h2>
          <p style={{ fontSize: '20px', fontWeight: 'bold', color: '#28a745' }}>{winner}</p>
        </div>

        <div style={{
          backgroundColor: 'rgba(135, 174, 233, 0.9)',
          borderRadius: '12px',
          padding: '20px',
          textAlign: 'left',
          boxShadow: 'inset 0 0 4px rgba(55, 39, 39, 0.05)'
        }}>
          <h3 style={{ fontSize: '20px', color: 'rgba(0, 4, 9, 0.9)', marginBottom: '12px' }}> Game Statistics</h3>
          <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            {Object.entries(stats).map(([key, value]) => (
              <li key={key} style={{
                display: 'flex',
                justifyContent: 'space-between',
                padding: '6px 0',
                borderBottom: '1px solid #ddd',
                fontSize: '16px',
                color: '#333'
              }}>
                <span style={{ textTransform: 'capitalize' }}>{key === "scoreOfRed" ? "Red Score" : "Blue Score"}</span>
                <span>{String(value)}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}

export default Results;
