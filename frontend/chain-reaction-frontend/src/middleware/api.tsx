import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080/game',
  headers: {
    'Content-Type': 'application/json'
  }
});

export const isGameOver = async () => {
  console.log("Checking if game is over");
  return await API.get('/game-over');
};

export const getResults = async () => {
  console.log("Fetching game results");
  const res = await API.get('/game-over-stats');
  const [winner, scoreOfRed, scoreOfBlue] = res.data.split(' ');
  const stats = { scoreOfRed: parseInt(scoreOfRed), scoreOfBlue: parseInt(scoreOfBlue) };
  return { winner, stats };
};

export const setBoardSize = async (rows: number, cols: number) => {
  console.log(`Setting board size to ${rows} rows and ${cols} columns`);
  await API.post('/start', { rows, columns : cols });
};

export const setHvAiHeuristic = async (heuristic: number) => {
  console.log(`Setting heuristic for human vs AI game to ${heuristic}`);
  await API.post('/set-ai-type/single', { heuristicType: heuristic });
};

export const setAiVsAiHeuristic = async (ai1Heuristic: number, ai2Heuristic: number) => {
  console.log(`Setting heuristic for AI vs AI game to ${ai1Heuristic} and ${ai2Heuristic}`);
  await API.post('/set-ai-type/both', { ai1HeuristicType: ai1Heuristic, ai2HeuristicType: ai2Heuristic });
};

export const resetPlayerTypes = async () => {
  console.log("Resetting player types to default");
  await API.post('/start/setupPlayers', { isRedHuman: 1, isBlueHuman: 1 });
};

export const startGameHvH = async (rows: number, cols: number) => {
  console.log(`Starting game with ${rows} rows and ${cols} columns`);
  await API.post('/start/setupPlayers', { isRedHuman: 1, isBlueHuman: 1 });
  await API.post('/start', { rows, columns: cols });
};

export const startGameHvAi = async (rows: number, cols: number, heuristic: number) => {
  console.log(`Starting game with ${rows} rows and ${cols} columns`);
  await API.post('/start/setupPlayers', { isRedHuman: 1, isBlueHuman: 0 });
  await API.post('/set-ai-type/single', { heuristicType: heuristic });
  await API.post('/start', { rows, columns: cols });
};

export const startGameAivAi = async (rows: number, cols: number, ai1Heuristic: number, ai2Heuristic: number) => {

  console.log(`Starting game with ${rows} rows and ${cols} columns`);
  await API.post('/start/setupPlayers', { isRedHuman: 0, isBlueHuman: 0 });
  await API.post('/set-ai-type/both', { ai1HeuristicType: ai1Heuristic, ai2HeuristicType: ai2Heuristic });
  await API.post('/start', { rows, columns: cols });
  console.log(`AI vs AI game started with heuristics ${ai1Heuristic} and ${ai2Heuristic}`);
};


export const makeMove = async (row: number, col: number) => {
  console.log(`Making move at row ${row}, column ${col}`);
  await API.post('/move', { row: row, column: col });
    // await API.get("/move/ai");
};

export const makeAIMove = async () => {
  console.log("Making AI move");
  await API.get("/move/ai");
};

export const makeHvAiMove = async (row: number, col: number) => {
    console.log(`Making move at row ${row}, column ${col} for human vs AI game`);
    await API.post('/move/humanVai', { row: row, column: col });
};

export const getBoard = async () => {
  console.log("Fetching game board");
  return await API.get('/state');
};

export const getGameStateVersion = async () => {
    console.log("Checking for file changes");
    return await API.get('/version');
};

export const getCurrentPlayer = async () => {
  console.log("Fetching current player");
  return await API.get('/current-player');
};

export const isCurrentPlayerHuman = async () => {
  console.log("Checking if current player is human");
  return await API.get('/isHuman');
};
