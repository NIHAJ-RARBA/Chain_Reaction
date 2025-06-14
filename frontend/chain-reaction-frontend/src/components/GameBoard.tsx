import { useEffect, useState } from 'react';
import { getBoard } from '../middleware/api';
import Cell from './Cell';

interface ParsedCell {
  orbCount: number;
  owner: string | null;
  gameMode?: "HvH" | "HvAI" | "AiVAi";
}

function GameBoard( { refreshPlayer, version, gameMode }: { refreshPlayer: () => void, version: number, gameMode: "HvH" | "HvAI" | "AiVAi" }) {
  const [board, setBoard] = useState<ParsedCell[][]>([]);

  const fetchBoard = async() => {
    getBoard().then(res => {
      const boardString: string = res.data;
      const parsed = parseBoardString(boardString);
      setBoard(parsed);
    });
  };

  useEffect(() => {
        console.log("Board useEffect triggered. Version:", version);
        fetchBoard();
        console.log("GameMode:", gameMode);
    }, [version]);

    if (board.length === 0) {
        return <div>Loading board...</div>;
    }


  return (
    <div style={{ display: 'grid', gridTemplateColumns: `repeat(${board[0]?.length || 0}, 1fr)` }}>
      {board.map((row, i) =>
        row.map((cell, j) => (
          <Cell key={`${i}-${j}`} cell={cell} row={i} col={j} refreshBoard={fetchBoard} refreshPlayer={refreshPlayer} gameMode={gameMode} />

        ))
      )}
    </div>
  );
}

function parseBoardString(raw: string): ParsedCell[][] {
  const lines = raw.trim().split('\n');
  return lines.map(line =>
    line.trim().split(/\s+/).map(token => {
      if (token === '0') {
        return { orbCount: 0, owner: null };
      } else {
        const orbCount = parseInt(token[0]);
        const owner = token[1]; // e.g., 'R', 'B'
        return { orbCount, owner };
      }
    })
  );
}

export default GameBoard;