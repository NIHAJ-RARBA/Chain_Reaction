import { makeHvAiMove, makeMove } from "../middleware/api";

interface CellProps {
  cell: {
    orbCount: number;
    owner: string | null;
  };
  row: number;
  col: number;
  gameMode: 'HvH' | 'HvAI' | 'AiVAi';
  refreshBoard: () => void;
  refreshPlayer: () => void;
}

function Cell({ cell, row, col, gameMode, refreshBoard, refreshPlayer }: CellProps) {
  const handleClick = async () => {
    console.log(`Clicked ${row}, ${col}`);
    if (gameMode === 'HvAI') {
      console.log(`Making move for HvAI at ${row}, ${col}`);
      await makeHvAiMove(row, col);
      refreshPlayer();
      refreshBoard();
    } else
    {
        if(gameMode == "HvH") {
      console.log(`Making move for HvH at ${row}, ${col}`);
      await makeMove(row, col);
      refreshBoard();
      refreshPlayer();
    }
    }
  };

  const colorMap: Record<string, string> = {
    R: 'rgb(227, 80, 80)', // Red with transparency
    B: 'rgb(80, 112, 227)', // Blue with transparency
  };

  const color = cell.owner ? colorMap[cell.owner] || 'gray' : 'white';

  return (
    <div
      onClick={handleClick}
      style={{
        border: '1px solid black',
        borderRadius: '5px',
        cursor: 'pointer',
        padding: '10px',
        textAlign: 'center',
        backgroundColor: color,
        width: '40px',
        height: '35px',
      }}
    >
      {cell.orbCount > 0 ? cell.orbCount : ''}
    </div>
  );
}

export default Cell;
