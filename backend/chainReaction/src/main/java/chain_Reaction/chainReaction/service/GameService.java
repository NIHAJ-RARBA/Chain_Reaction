package chain_Reaction.chainReaction.service;


import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chain_Reaction.chainReaction.models.Board;
import chain_Reaction.chainReaction.models.Cell;
import chain_Reaction.chainReaction.models.Player;
import chain_Reaction.chainReaction.utils.FileIO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class GameService {

    private Board gameBoard;

    @Autowired
    private AgentAlgoService agentAlgoService;


    private int playerIndex = 0;
    private int winnerIndex = -1;

    // for now, there are only two players
    // turnskipping will be needed if more players are added
    private int score[] = new int[Player.values().length]; 
    private boolean isPlayerHuman[] = new boolean[Player.values().length];
    
    private int heuristicTypeIfBothAi[] = {1,1};
    private int heuristicTypeIfOnlyOneAi = 8;

    private int movesCount = 0;
    private int moveCount[] = {0, 0}; // for each player
    private boolean isGameOver = false;

    private long timeLimitMillis = 5000;
    private boolean lastWasHeuristic = true; 
    private int depth[] = {6, 6}; 

    public void initializeGame(int rows, int columns) 
    {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Rows and columns must be greater than zero.");
        }

        this.gameBoard = new Board(rows, columns);
        // System.out.println("Game initialized with board size " + rows + "x" + columns);
        this.playerIndex = 0;
        this.isGameOver = false;
        this.movesCount = 0;
        this.winnerIndex = -1;

        this.moveCount = new int[Player.values().length];
        for (int i = 0; i < Player.values().length; i++) {
            score[i] = 0;
        }
        String[] empty = new String[rows];
        for (int i = 0; i < rows; i++) {
            empty[i] = "0 ".repeat(columns).trim();
        }
        try {
            FileIO.writeGameState(String.join("\n", empty), "Human Move:");
        } catch (IOException e) {
            System.err.println("Error writing initial game state to file.");
        }
    }

    public void initializeGame(int rows, int columns, boolean isRedHuman, boolean isBlueHuman) 
    {
        initializeGame(rows, columns);
        setPlayerTypes(isRedHuman, isBlueHuman);
    }

    public void setPlayerTypes(boolean isRedHuman, boolean isBlueHuman) 
    {
        isPlayerHuman[Player.RED.ordinal()] = isRedHuman;
        isPlayerHuman[Player.BLUE.ordinal()] = isBlueHuman;
    }

    public boolean isCurrentPlayerHuman() 
    {
        return isPlayerHuman[playerIndex];
    }


    public boolean setBothAiTypes(int redHeuristicType, int blueHeuristicType) 
    {
        if (redHeuristicType < 0 || redHeuristicType > 8 || blueHeuristicType < 0 || blueHeuristicType > 8) {
            System.err.println("Invalid heuristic type. Must be between 0 and 8.");
            return false;
        }
        heuristicTypeIfBothAi[Player.RED.ordinal()] = redHeuristicType;
        heuristicTypeIfBothAi[Player.BLUE.ordinal()] = blueHeuristicType;

        // if (redHeuristicType == blueHeuristicType) {
        //     System.out.println("Both AI players have the same heuristic type: " + redHeuristicType);
        // } else {
        //     System.out.println("AI 1 heuristic: " + redHeuristicType + ", AI 2 heuristic: " + blueHeuristicType);
        // }

        return true;
    }

    public boolean setAiType(int heuristicType) 
    {
        if (heuristicType < 0 || heuristicType > 8) {
            System.err.println("Invalid heuristic type. Must be between 0 and 8.");
            return false;
        }
        // System.out.println("AI heuristic: " + heuristicTypeIfOnlyOneAi);
        heuristicTypeIfOnlyOneAi = heuristicType;
        return true;
    }

    public Player getCurrentPlayer() {
        return Player.values()[playerIndex];
    }

    public boolean makeMove(int row, int column)
    {
        if (this.isGameOver) {
            System.out.println("Game is already over. No more moves can be made.");
            return false;
        }

        System.out.println("Player " + getCurrentPlayer() + " is trying to make a move at (" + row + ", " + column + ")");
        Player currentPlayer = getCurrentPlayer();
        Cell currentCell = gameBoard.getBoard()[row][column];
        String humanOrAI = isCurrentPlayerHuman() ? "Human" : "AI";

        if (currentCell.isEmpty() || currentCell.getOwnerPlayer() == currentPlayer)  
        {


            if (currentCell.hasOwner() == false)
            {
                currentCell.setOwnerPlayer(currentPlayer);
            }
        
            currentCell.setOrbCount(currentCell.getOrbCount() + 1);

            System.out.println("Player " + currentPlayer + " placed an orb at (" + row + ", " + column + ")");
            checkAndHandleExplosion(row, column);
            System.out.println("Explosions handled, if any.");
            calculateScore();
            playerRotation();
            
            movesCount++;
            moveCount[currentPlayer.ordinal()]++;




            if (checkForWinner()) 
            {
                try {
                    FileIO.writeGameState(getGameState(), humanOrAI + " Move:");
                } catch (IOException e) {
                    System.err.println("Error writing game state to file: " + e.getMessage());
                }
                System.out.println("Game over. Player " + currentPlayer + " has won!\n" + getGameState());
                System.out.println("Moves made by each player: " + 
                    Player.RED + ": " + moveCount[Player.RED.ordinal()] + ", " + 
                    Player.BLUE + ": " + moveCount[Player.BLUE.ordinal()]);

                moveCount[0] = 0;
                moveCount[1] = 0;
                movesCount = 0; 
            } 
            else 
            {
                try {
                    FileIO.writeGameState(getGameState(), humanOrAI + " Move:");
                } catch (IOException e) {
                    System.err.println("Error writing game state to file: " + e.getMessage());
                }
                System.out.println("Current game state:\n" + getGameState());
            }

            return true;
            
        }
        
        System.out.println("Invalid move by " + humanOrAI + " player " + currentPlayer + " at (" + row + ", " + column + ")");
        return false;
    }


    // only used for AI vs Random games. 
    //will not be used for live site games

    /*
    public boolean makeAIMove() {
        if (!isCurrentPlayerHuman()) {
            if (this.isGameOver) {
                System.out.println("Game is already over. AI cannot make a move.");
                return false;
            }

            System.out.println("AI (" + getCurrentPlayer() + ") is thinking...");

            int[] move;

            // Alternate: heuristic first, then random, then heuristic, ...
            if (!lastWasHeuristic) {
                System.out.println("AI is making a HEURISTIC move. Heuristic Type: " +
                    (areBothAi() ? heuristicTypeIfBothAi[getPlayerIndex()] : heuristicTypeIfOnlyOneAi));
                int depth = 5;
                int heuristic = areBothAi() ? heuristicTypeIfBothAi[getPlayerIndex()] : heuristicTypeIfOnlyOneAi;
                
                Future<int[]> futureMove = Executors.newSingleThreadExecutor().submit(() ->
                    agentAlgoService.getMiniMaxMove(gameBoard, getCurrentPlayer(), depth, heuristic)
                );

                try {
                    move = futureMove.get(timeLimitMillis, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    System.err.println("Minimax timed out. Using random move.");
                    futureMove.cancel(true);
                    move = agentAlgoService.randomMove(gameBoard, getCurrentPlayer());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

            } else {
                System.out.println("AI is making a RANDOM move.");
                move = agentAlgoService.randomMove(gameBoard, getCurrentPlayer());
            }

            lastWasHeuristic = !lastWasHeuristic;

            if (move == null) {
                System.err.println("AI has no valid moves.");
                return false;
            }

            int row = move[0];
            int col = move[1];
            System.out.println("AI (" + getCurrentPlayer() + ") chooses: " + row + ", " + col);
            makeMove(row, col);
        }

        return true;
    }

    */

    // AI vs AI setup
    // main game e use hobena etao

   /*
    public boolean makeAIMove() 
    {
        if (!isCurrentPlayerHuman()) {
            if (this.isGameOver) {
                System.out.println("Game is already over. AI cannot make a move.");
                return false;
            }

            System.out.println("AI (" + getCurrentPlayer() + ") is thinking... Heuristic Type: " +
                    (areBothAi() ? heuristicTypeIfBothAi[getPlayerIndex()] : heuristicTypeIfOnlyOneAi)
                    + "And Depth: " + depth[getPlayerIndex()]);

            int[] fallbackMove = agentAlgoService.randomMove(gameBoard, getCurrentPlayer());
            int[] selectedMove = fallbackMove;

            // int depth = 5;
            int heuristic = areBothAi() ? heuristicTypeIfBothAi[getPlayerIndex()] : heuristicTypeIfOnlyOneAi;
            int aiDepth = depth[getPlayerIndex()];

            Future<int[]> futureMove = Executors.newSingleThreadExecutor().submit(() -> {
                if (areBothAi() && movesCount < 3) {
                    return fallbackMove;
                } else {
                    return agentAlgoService.getMiniMaxMove(gameBoard, getCurrentPlayer(), aiDepth, heuristic);
                }
            });

            try {
                selectedMove = futureMove.get(timeLimitMillis, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                System.err.println("Minimax timed out. Using random move.");
                futureMove.cancel(true); // try to stop the task
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            if (selectedMove == null) {
                System.err.println("AI has no valid moves.");
                return false;
            }

            int row = selectedMove[0];
            int col = selectedMove[1];
            System.out.println("AI (" + getCurrentPlayer() + ") chooses: " + row + ", " + col);

            makeMove(row, col);
        }

        return true;
    }

    */

    public boolean makeAIMove() {

        // while(!isCurrentPlayerHuman()) {
        if (!isCurrentPlayerHuman()) {
            if (this.isGameOver) {
                System.out.println("Game is already over. AI cannot make a move.");
                return false;
            }

            
            
            System.out.println("AI (" + getCurrentPlayer() + ") is thinking... Heuristic Type: " + 
                (areBothAi() ? heuristicTypeIfBothAi[getPlayerIndex()] : heuristicTypeIfOnlyOneAi));
            
            int[] move = null;
            if (areBothAi()) {
                System.out.println("Both players are AI. No human player to make a move.");
                if (movesCount < 3) {

                    move = agentAlgoService.randomMove(gameBoard, getCurrentPlayer());
                    // move = agentAlgoService.getMiniMaxMove(gameBoard, getCurrentPlayer(), 5, heuristicTypeIfBothAi[getPlayerIndex()]);
                }
                else 
                {
                    // System.out.println("hi");
                    move = agentAlgoService.getMiniMaxMove(gameBoard, getCurrentPlayer(), 5, heuristicTypeIfBothAi[getPlayerIndex()]);
                }
            }
            else {
                // System.out.println("hi");
                move = agentAlgoService.getMiniMaxMove(gameBoard, getCurrentPlayer(), 5, heuristicTypeIfOnlyOneAi);
            }

            if (move == null) {
                System.err.println("AI has no valid moves.");
                return false;
            }

            int row = move[0];
            int col = move[1];


            System.out.println("AI (" + getCurrentPlayer() + ") chooses: " + row + ", " + col);

            makeMove(row, col);


            // try {
            //     Thread.sleep(800);
            // } catch (InterruptedException e) {
            //     Thread.currentThread().interrupt();
            // }
        }

        return true;
    }

    private boolean areBothAi() {
        return !isPlayerHuman[Player.RED.ordinal()] && !isPlayerHuman[Player.BLUE.ordinal()];
    }


    private void playerRotation() 
    {
        playerIndex = (playerIndex + 1) % Player.values().length;
    }
    

    private void checkAndHandleExplosion(int row, int column) 
    {
        Queue<Cell> neighbours = new LinkedList<>();
        Cell startCell = gameBoard.getBoard()[row][column];
        Player exploderPlayer = startCell.getOwnerPlayer();

        // int explosionCount = 0;

        int[][] directions = {
            {-1, 0}, // Up
            {1, 0},  // Down
            {0, -1}, // Left
            {0, 1}   // Right
        };

        if (startCell.getOrbCount() < startCell.getMaxOrbs()) 
        {
            return; // No explosion needed
        }

        
        neighbours.add(startCell);
        while (!neighbours.isEmpty()) 
        {
            if (isGameOver) {
                System.out.println("Game is over, no further explosions will be processed.");
                break;
            }
            Cell currentCell = neighbours.poll();
            
            if (currentCell.getOrbCount() < currentCell.getMaxOrbs()) continue; 
            
            // if (explosionCount >= 10000) 
            // {
            //     System.err.println("Explosion limit reached, stopping further explosions.");
            //     break; // Prevent infinite loop
            // }

            // explosionCount++;
            currentCell.setOrbCount(0);
            currentCell.setOwnerPlayer(null);
            System.out.println("Explosion at (" + currentCell.getRow() + ", " + currentCell.getColumn() + ")");

            
            for (int[] direction : directions)
            {
                int newRow = currentCell.getRow() + direction[0];
                int newColumn = currentCell.getColumn() + direction[1];
                
                if (newRow >= 0 && newRow < gameBoard.getRows() &&
                    newColumn >= 0 && newColumn < gameBoard.getColumns()) 
                    {
                        Cell neighbourCell = gameBoard.getBoard()[newRow][newColumn];
                        
                        neighbourCell.setOrbCount(neighbourCell.getOrbCount() + 1);
                        neighbourCell.setOwnerPlayer(exploderPlayer);
                        
                        if(checkForWinner())
                        {
                            break;
                        }

                        if (neighbourCell.getOrbCount() >= neighbourCell.getMaxOrbs()) 
                        {
                            neighbours.add(neighbourCell);
                        }
                }
            }
            calculateScore();
            if(checkForWinner())
            {
                break;
            }



        }
       
       
       
    }

    private void calculateScore() 
    {
        for (int i = 0; i < Player.values().length; i++) 
        {
            score[i] = 0;
        }

        for (int i = 0; i < gameBoard.getRows(); i++) 
        {
            for (int j = 0; j < gameBoard.getColumns(); j++) 
            {
                Cell cell = gameBoard.getBoard()[i][j];
                if (!cell.isEmpty()) 
                {
                    score[cell.getOwnerPlayer().ordinal()] += cell.getOrbCount();
                }
            }

        }
    }

    private boolean checkForWinner() 
    {
        if (movesCount < 2) {
            return false; // Not enough moves to determine a winner
        }
        int playersWithOrbs = 0;
        int previousPlayerWithOrbs = -1;

        for (int i = 0; i < Player.values().length; i++) {
            if (score[i] > 0) {
                playersWithOrbs++;
                previousPlayerWithOrbs = i;
            }
        }


        if (playersWithOrbs == 1) {
            gameWon(previousPlayerWithOrbs);
            winnerIndex = previousPlayerWithOrbs;

            return true; // Only one player has orbs left, they win
        }

        return false;
    }

    private boolean gameWon(int playerIndex) {
        
        this.isGameOver = true;
        System.out.println("Player " + Player.values()[playerIndex] + " has won the game!");
        return true;
    }


    public String getGameState() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < gameBoard.getRows(); i++) {
            for (int j = 0; j < gameBoard.getColumns(); j++) {
                Cell cell = gameBoard.getBoard()[i][j];
                if (cell.isEmpty() || !cell.hasOwner()) {
                    sb.append("0");
                } else {
                    sb.append(cell.getOrbCount());
                    sb.append(cell.getOwnerPlayer() == Player.RED ? "R" : "B");
                }
                if (j < gameBoard.getColumns() - 1) sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getGameStateFromFile() {
        try {
            Board temp = FileIO.readGameState();
            if (temp == null) {
                System.err.println("No moves made since last save");
                return getGameState();
            }
            else if (temp.getMoveMade(gameBoard) == null) {
                System.err.println("No moves made since last save");
                return getGameState();
            } else {
                this.gameBoard = temp.deepCopy();
                return getGameState();
            }
        } catch (IOException e) {
            System.err.println("Error reading game state from file: " + e.getMessage());
            return null;
        }
    }

    public boolean updateBoard() 
    {
        
        Board tempBoard = null;
        try {
            tempBoard = FileIO.readGameState();
        } catch (IOException e) {
            System.err.println("Error reading game state from file: " + e.getMessage());
            return false;
        }

        if (tempBoard == null) {
            System.err.println("Failed to read game state from file.");
            return false;
        }

        gameBoard = tempBoard.deepCopy();
        return true;
    }

    public int[] getScore() {
        return score;
    }


    public boolean isCurrentPlayerRed() {
        return getCurrentPlayer() == Player.RED;
    }

    public boolean isWinnerRed() {
        if (isGameOver) {
            return Player.values()[winnerIndex] == Player.RED;
        }
        return false;
    }


}