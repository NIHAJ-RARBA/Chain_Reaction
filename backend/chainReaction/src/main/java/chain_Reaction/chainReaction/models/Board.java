package chain_Reaction.chainReaction.models;

import lombok.Data;

@Data
public class Board {

    private int rows;
    private int columns;
    private Cell[][] board;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new Cell[rows][columns];

        initBoard();
    }

    private void initBoard() {
        for (int i = 0; i < rows; i++) 
        {
            for (int j = 0; j < columns; j++) 
            {
                board[i][j] = new Cell(i, j, getCriticalMass(i,j));
            }
        }
    }

    public Cell setCell(int row, int column)
    {
        // System.out.println("row: " + row + ", column: " + column + ", rows: " + rows + ", columns: " + columns);
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }
        
        Cell cell = board[row][column];
        if (cell == null) {
            cell = new Cell(row, column, getCriticalMass(row, column));
            board[row][column] = cell;
        }
        
        return cell;
    }


    public int getCriticalMass(int row, int column)
    {
        int count = 0;

        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }
        
        if (row > 0) count++; // Has cell above
        if (row < rows - 1) count++; // Has cell below

        if (column > 0) count++; // Has cell to the left
        if (column < columns - 1) count++; // Has cell to the right

        return count;
    }


    public int[] getMoveMade(Board otherBoard)
    {
        if (otherBoard == null || otherBoard.getRows() != this.rows || otherBoard.getColumns() != this.columns) {
            // throw new IllegalArgumentException("Invalid board dimensions");
            return null;
        }

        int[] move = new int[2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell currentCell = this.board[i][j];
                Cell otherCell = otherBoard.getBoard()[i][j];

                if (!currentCell.equals(otherCell)) {
                    move[0] = i;
                    move[1] = j;
                    return move;
                }
            }
        }
        return null; 
    }

    public Board deepCopy() {
        Board copy = new Board(rows, columns);
        Cell[][] newGrid = new Cell[rows][columns];

        for (int i = 0; i < rows; i++) 
        {
            for (int j = 0; j < columns; j++) 
            {
                Cell current = this.board[i][j];
                Cell cellCopy = new Cell(current.getRow(), current.getColumn(), current.getMaxOrbs());
                cellCopy.setOrbCount(current.getOrbCount());
                cellCopy.setOwnerPlayer(current.getOwnerPlayer());
                // cellCopy other fields if needed
                newGrid[i][j] = cellCopy;
            }
        }
        copy.setBoard(newGrid);
        return copy;
    }

    
}
