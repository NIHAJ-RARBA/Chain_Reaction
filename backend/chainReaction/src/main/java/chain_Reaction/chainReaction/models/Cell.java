package chain_Reaction.chainReaction.models;

import lombok.Data;

@Data
public class Cell {
    private int row;
    private int column;
    private int orbCount;
    private int maxOrbs;
    
    // REMINDER: keep null if empty
    private Player ownerPlayer = null;

    public Cell(int row, int column, int maxOrbs) {
        this.row = row;
        this.column = column;
        this.maxOrbs = maxOrbs;
        this.orbCount = 0;
    }

    public boolean isEmpty() {
        return orbCount == 0 && ownerPlayer == null;
    }


    public boolean hasOwner() {
        return ownerPlayer != null;
    }
}
