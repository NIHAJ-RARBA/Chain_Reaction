package chain_Reaction.chainReaction.service;



import chain_Reaction.chainReaction.models.Board;
import chain_Reaction.chainReaction.models.Cell;
import chain_Reaction.chainReaction.models.Player;
import chain_Reaction.chainReaction.utils.MiniMaxHeuristics;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

@Service
public class AgentAlgoService {

    private final Random random = new Random();
    private MiniMaxHeuristics miniMaxHeuristics = new MiniMaxHeuristics();

    private Board tempBoard;

    


    public int[] randomMove(Board board, Player player) {
        
        List<int[]> validMoves = getAllValidMoves(board, player);
        if (validMoves == null || validMoves.isEmpty()) {
            return null; 
        }

        if (validMoves.isEmpty()) return null;
        return validMoves.get(random.nextInt(validMoves.size()));
    }


    private List<int[]> getAllValidMoves(Board board, Player player) {
        
        List<int[]> validMoves = new ArrayList<>();
        Cell[][] grid = board.getBoard();

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Cell cell = grid[i][j];
                if (cell.isEmpty() || cell.getOwnerPlayer() == player) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }

        return validMoves;
    }



    public int[] getMiniMaxMove(Board board, Player player, int depth, int heuristic)
    {
        int[] moves = new int[2];
        
        if (board == null || player == null || depth < 0) {
            System.out.println("Invalid input parameters for minimax.");
            return null; 
        }
        tempBoard = board;
        System.out.println("starting:\n" + getGameState(board));

        int[][] result = minimax(board, player, heuristic, true, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);

        if (result == null || result.length == 0 || result[1] == null) {
            System.out.println("Returning null.");
            return null; 
        }

        int bestValue = result[0][0];

        moves[0] = result[1][0];
        moves[1] = result[1][1];

        System.out.println("Best move found: \n" + "Best Heuristic Value: " + bestValue + 
                           "\nMove: (" + moves[0] + ", " + moves[1] + ")");

        return moves;
    }

    private boolean isGameOver(Board gameBoard)
    {
        int[] score = {0, 0}; 
        int movesCount = 0;
        
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
                    movesCount++;
                    score[cell.getOwnerPlayer().ordinal()] += cell.getOrbCount();
                }
            }

        }

        if (movesCount < 2) {
            return false; // Not enough moves to determine a winner
        }
        int playersWithOrbs = 0;

        for (int i = 0; i < Player.values().length; i++) {
            if (score[i] > 0) {
                playersWithOrbs++;
            }
        }


        if (playersWithOrbs == 1) {
            return true; // Only one player has orbs left, they win
        }

        return false;
    }

    public String getGameState(Board gameBoard) {
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

    private Board checkAndHandleExplosion(Board gameBoard, int row, int column) 
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
            return gameBoard;
        }

        
        neighbours.add(startCell);
        while (!neighbours.isEmpty()) 
        {
            if (isGameOver(gameBoard)) {
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
            // System.out.println("Explosion at (" + currentCell.getRow() + ", " + currentCell.getColumn() + ")");

            
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
                        
                        if(isGameOver(gameBoard))
                        {
                            break;
                        }

                        if (neighbourCell.getOrbCount() >= neighbourCell.getMaxOrbs()) 
                        {
                            neighbours.add(neighbourCell);
                        }
                }
            }
            if(isGameOver(gameBoard))
            {
                break;
            }



        }
       
       return gameBoard;
       
    }

    private Board tryMove(Board board, Player player, int row, int column) 
    {
        Board tempBoard = board.deepCopy();
        Cell currentCell = tempBoard.getBoard()[row][column];
        currentCell.setOwnerPlayer(player);
        currentCell.setOrbCount(currentCell.getOrbCount() + 1);
        return checkAndHandleExplosion(tempBoard, row, column);
    }


    private int[][] minimax(Board board, Player player, int heuristic, boolean maximizingPlayer, int depth, int alpha, int beta)
    {
        if (depth == 0 || isGameOver(board)) {
            // System.out.println("Board\n" + getGameState(board));
            // System.out.println("Reached base case: depth = " + depth + ", game over: " + isGameOver(board));
            
            // System.out.println("Depth: " + depth + ", " + player + " Eval: " + miniMaxHeuristics.maxNearExplosionsEval(board, player));

            
            int eval = miniMaxHeuristics.getEval(board, player, heuristic);
            
            
            // eval = miniMaxHeuristics.maxOrbsEval(board, player);


            // if (!maximizingPlayer) {
            //     eval = -eval;
            // }

            return new int[][]{{eval}, tempBoard.getMoveMade(board)};
        }

        int bestValue;
        int[] bestMove = new int[2];
        // depth--;
        if (maximizingPlayer) 
        {
            List<int[]> validMoves = getAllValidMoves(board, player);
            // System.out.println("MAX---"+player+" Depth: " + depth + ", Valid Moves: " + validMoves.size());
            bestValue = Integer.MIN_VALUE;
            for (int[] move : validMoves) 
            {
                int row = move[0];
                int col = move[1];
                
                Board afterMove = tryMove(board, player, row, col);
                Player nextPlayer = player == Player.RED ? Player.BLUE : Player.RED;

                int[][] result = minimax(afterMove, nextPlayer, heuristic,false, depth - 1, alpha, beta);
                int eval = result[0][0];
                // System.out.println("Evaluating move by " + player + " at (" + row + ", " + col + 
                // ") with heuristic value: " + eval);

                // if (eval >= beta) {
                //     break;
                // }


                if (eval > bestValue) {
                    bestValue = eval;
                    bestMove[0] = row;
                    bestMove[1] = col;
                }
                
                alpha = Math.max(alpha, eval);
                
                if (beta <= alpha) {
                    break; 
                }
            }
            
            return new int[][]{{bestValue}, bestMove};
        }
        else
        {
            bestValue = Integer.MAX_VALUE;
            
            List<int[]> validMoves = getAllValidMoves(board, player);
            // System.out.println("MIN---"+player+" Depth: " + depth + ", Valid Moves: " + validMoves.size());
            for (int[] move : validMoves) 
            {
                int row = move[0];
                int col = move[1];
                
                Board tempBoard = tryMove(board, player, row, col);
                Player nextPlayer = player == Player.RED ? Player.BLUE : Player.RED;

                int[][] result = minimax(tempBoard, nextPlayer, heuristic, true, depth - 1, alpha, beta);
                int eval = result[0][0];
                // System.out.println("Evaluating move by " + player + " at (" + row + ", " + col + 
                //                    ") with heuristic value: " + eval);
                
                // if (eval <= alpha)
                // {
                //     break;
                // }

                if (eval < bestValue) {
                    bestValue = eval;
                    bestMove[0] = row;
                    bestMove[1] = col;
                }

                beta = Math.min(beta, eval);

                if (beta <= alpha) {
                    break; 
                }
            }

            return new int[][]{{bestValue}, bestMove};

        }



    }




}
