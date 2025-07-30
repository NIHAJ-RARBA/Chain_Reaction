package chain_Reaction.chainReaction.utils;



import chain_Reaction.chainReaction.models.Board;
import chain_Reaction.chainReaction.models.Cell;
import chain_Reaction.chainReaction.models.Player;
public class MiniMaxHeuristics {

    public int getEval(Board board, Player player, int heuristicType) {
        switch (heuristicType) {
            case 0:
                return equalEval(board, player);
            case 1:
                return maxNearExplosionsEval(board, player);
            case 2:
                return leastAttackingEval(board, player);
            case 3:
                return maxOrbsEval(board, player);
            case 4:
                return maximizeOrbDifference(board, player);
            case 5:
                return bigExplosionPotential(board, player);
            case 6:
                return vulnerabilityPenaltyEval(board, player);
            case 7:
                return orbCapturePotentialEval(board, player);
            case 8:
                return positionalControlEval(board, player);
            case 9:
                return evenRowColEval(board, player);

            default:
                throw new IllegalArgumentException("Invalid heuristic type");
        }
    }
    

    // results: 
    // very deterministic. always keeps choosing the top left and keeps going
    // games are not challenging
    // however, it is nice to see how it evolves
    
    // doesn't take time to run
    public int equalEval(Board board, Player player) {
        return 1;
    }


    // results:
    // for lower depth it does not predict too future-safely
    // higher depths like 5 show good moves
    // also fun to watch AI vs AI because they do well to go against each other

    // adding the EXTRA weight for explosion potential made this heuristic better

    // takes a little more time to run than equalEval

    public int maxNearExplosionsEval(Board board, Player player) 
    {
        int numOneLessThanExplosion = 0;
        
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Cell cell = board.getBoard()[i][j];
                if (cell.getOwnerPlayer() == player) {
                    if (cell.getOrbCount() == cell.getMaxOrbs() -1) {
                        switch (cell.getMaxOrbs()) {
                            case 2:
                                numOneLessThanExplosion *= 200;
                                break;
                            case 3:
                                numOneLessThanExplosion += 150;
                                break;
                            case 4:
                                numOneLessThanExplosion *= 100;
                                break;
                            default:
                                break;
                        }
                    }
                    numOneLessThanExplosion += 1/cell.getOrbCount();
                }
            }
        }

        return numOneLessThanExplosion;
    }

    // results:
    // good defence
    // this is a good heuristic to use for the AI
    // lower depths not so good
    // ALWAYS using at least depth 5 for this
    // AI vs AI not 'that' fun to watch, but interesting nonetheless as it's less predictable

    // takes a little more time to run than maxNearExplosionsEval

    // fun to play against

    public int leastAttackingEval(Board board, Player player) 
    {
        int numAttackingCells = 0;
        
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Cell cell = board.getBoard()[i][j];
                player = cell.getOwnerPlayer();
                int[] directions = {
                    -1, 0, // Up
                    1, 0,  // Down
                    0, -1, // Left
                    0, 1   // Right
                };
                for (int k = 0; k < directions.length; k += 2) {
                    int newRow = i + directions[k];
                    int newCol = j + directions[k + 1];
                    if (newRow >= 0 && newRow < board.getRows() && newCol >= 0 && newCol < board.getColumns()) {
                        if (board.getBoard()[newRow][newCol].getOwnerPlayer() != player) {
                            numAttackingCells++;
                        }
                    }
                }
            }
        }

        return numAttackingCells;
    }


    // results:
    // this just wants to take over the board
    // games take way too long
    // literally has to take over the whole board before any explosions happen
    // interesting but not that fun to watch

    public int maxOrbsEval(Board board, Player player) {
        int numOrbs = 0;
        
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Cell cell = board.getBoard()[i][j];
                if (cell.getOwnerPlayer() == player) {
                    numOrbs += cell.getOrbCount();
                }
            }
        }

        return numOrbs;
    }


    // results:
    // realistic heuristic but a little bit shortsighted
    // more interesting to watch on a smaller board as it takes a while before it begings putting more than one orb in a cell
    // pretty fun to play against as it presents a legitimate challenge
    // should get better with higher depth, with depth 5, it was fun to play against

    // so far this takes the longest

    public int maximizeOrbDifference(Board board, Player player) {
        int playerOrbs = 0;
        int opponentOrbs = 0;

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Cell cell = board.getBoard()[i][j];
                if (cell.getOwnerPlayer() == player) {
                    playerOrbs += cell.getOrbCount();
                } else {
                    opponentOrbs += cell.getOrbCount();
                }
            }
        }

        return playerOrbs - opponentOrbs;
    }



    // results:
    // this is basically the opposite of the leastAttackingEval heuristic with a little change
    // expected: more agressive playstyle, since leastAttackingEval is more defensive
    // not that fun to play against, also not that fun to watch
    // sets itself up safe first, then goes towards making big moves

        public int bigExplosionPotential(Board board, Player player) {
            int criticalCellBonus = 0;
            int clusterBonus = 0;
            int centerWeightBonus = 0;

            int rows = board.getRows();
            int cols = board.getColumns();
            Cell[][] cells = board.getBoard();

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Cell cell = cells[i][j];

                    if (cell.getOwnerPlayer() == player) {
                        int orbCount = cell.getOrbCount();
                        int maxOrbs = cell.getMaxOrbs();

                        // Bonus for being on the verge of explosion
                        if (orbCount == maxOrbs - 1) {
                            criticalCellBonus += 50;

                            // Add extra if adjacent to other near-explosion cells (cluster synergy)
                            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                            for (int[] d : directions) {
                                int ni = i + d[0], nj = j + d[1];
                                if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                                    Cell neighbor = cells[ni][nj];
                                    if (neighbor.getOrbCount() == neighbor.getMaxOrbs() - 1) {
                                        clusterBonus += 25;
                                    }
                                }
                            }
                        }

                        
                        int distFromCenter = Math.abs(i - rows / 2) + Math.abs(j - cols / 2);
                        int centerWeight = (rows + cols) / 2 - distFromCenter;
                        centerWeightBonus += centerWeight * orbCount;
                    }
                }
            }

            return criticalCellBonus + clusterBonus + centerWeightBonus;
        }



        public int vulnerabilityPenaltyEval(Board board, Player player) {
            int penalty = 0;

            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    Cell cell = board.getBoard()[i][j];
                    if (cell.getOwnerPlayer() == player) {
                        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                        for (int[] d : directions) {
                            int ni = i + d[0], nj = j + d[1];
                            if (ni >= 0 && ni < board.getRows() && nj >= 0 && nj < board.getColumns()) {
                                Cell neighbor = board.getBoard()[ni][nj];
                                if (neighbor.getOwnerPlayer() != null && neighbor.getOwnerPlayer() != player &&
                                    neighbor.getOrbCount() == neighbor.getMaxOrbs() - 1) {
                                    penalty += (7 - neighbor.getMaxOrbs()); 
                                }
                            }
                        }
                    }
                }
            }

            return -penalty;
        }

        

        public int orbCapturePotentialEval(Board board, Player player) {
            int score = 0;

            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    Cell cell = board.getBoard()[i][j];

                    if (cell.getOwnerPlayer() == player && cell.getOrbCount() == cell.getMaxOrbs() - 1) {
                        int captureCount = 0;
                        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                        for (int[] d : directions) {
                            int ni = i + d[0], nj = j + d[1];
                            if (ni >= 0 && ni < board.getRows() && nj >= 0 && nj < board.getColumns()) {
                                Cell neighbor = board.getBoard()[ni][nj];
                                if (neighbor.getOwnerPlayer() != null && neighbor.getOwnerPlayer() != player) {
                                    captureCount++;
                                }
                            }
                        }
                        score += captureCount * 10;
                    }
                }
            }

            return score;
        }


            public int positionalControlEval(Board board, Player player) {
                int score = 0;

                for (int i = 0; i < board.getRows(); i++) {
                    for (int j = 0; j < board.getColumns(); j++) {
                        Cell cell = board.getBoard()[i][j];
                        if (cell.getOwnerPlayer() == player) {
                            int weight = 1;

                            // Corner
                            if ((i == 0 || i == board.getRows() - 1) && (j == 0 || j == board.getColumns() - 1)) {
                                weight = 3;
                            }
                            // Edge (but not corner)
                            else if (i == 0 || i == board.getRows() - 1 || j == 0 || j == board.getColumns() - 1) {
                                weight = 2;
                            }

                            // score += weight * cell.getOrbCount();
                            score += weight / cell.getOrbCount();
                        }
                    }
                }

                return score;
            }
                    

            public int evenRowColEval(Board board, Player player) {

                int score = 0;
                for (int i = 0; i < board.getRows(); i++) {
                    for (int j = 0; j < board.getColumns(); j++) {
                        Cell cell = board.getBoard()[i][j];
                        if (cell.getOwnerPlayer() == player) {
                            // int weight = 1;

                            // // Corner
                            // if ((i == 0 || i == board.getRows() - 1) && (j == 0 || j == board.getColumns() - 1)) {
                            //     weight = 3;
                            // }
                            // // Edge (but not corner)
                            // else if (i == 0 || i == board.getRows() - 1 || j == 0 || j == board.getColumns() - 1) {
                            //     weight = 2;
                            // }

                            // score += weight * cell.getOrbCount();

                            if (i % 2 == 0 || j % 2 == 0) {
                                // score += 7 + cell.getOrbCount() - i; // lower row gets higher value or priority when tied
                                score += 7 - i; // lower row gets higher value or priority when tied
                            }
                        }
                    }
                }
                return score;

            }

}
