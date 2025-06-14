package chain_Reaction.chainReaction.utils;

import chain_Reaction.chainReaction.models.Board;
import chain_Reaction.chainReaction.models.Cell;
import chain_Reaction.chainReaction.models.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileIO {
    private static final String FILE_PATH = "gamestate.txt";
    private static int ROWS;
    private static int COLS;

    public static void writeGameState(String boardString, String header) throws IOException {
        
        Board board = stringToBoard(boardString);

        if (board == null) {
            throw new IOException("Invalid board string");
        }

        StringBuilder content = new StringBuilder("");
        // content.append(ROWS).append(" ");
        // content.append(COLS).append("\n");

        content.append(header).append("\n");
        content.append(boardString).append("\n");

        System.out.println("Writing to file: \n" + content.toString());
        Files.writeString(Paths.get(FILE_PATH), content.toString(), 
                         StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static Board stringToBoard(String boardString) throws IOException {
        if (boardString == null || boardString.isEmpty()) {
            throw new IOException("Board string is empty or null");
        }
        String[] lines = boardString.split("\n");
        ROWS = lines.length;
        COLS = lines[0].trim().split("\\s+").length;
        System.out.println("Parsed dimensions: " + ROWS + " rows, " + COLS + " columns");
        Board board = new Board(ROWS, COLS);

        String[] rows = boardString.split("\n");
        for (int i = 0; i < rows.length; i++) {
            String[] cells = rows[i].trim().split("\\s+");
            for (int j = 0; j < cells.length; j++) {
                String cellStr = cells[j];
                if (cellStr.equals("0")) {
                    board.setCell(i, j);
                
                    board.getBoard()[i][j].setOrbCount(0);
                    board.getBoard()[i][j].setOwnerPlayer(null);
                } else {
                    try {
                        int orbCount = Integer.parseInt(cellStr.substring(0, cellStr.length() - 1));
                        String color = cellStr.substring(cellStr.length() - 1);
                        if (!color.equals("R") && !color.equals("B")) throw new IOException("Invalid color");
                        Player player = color.equals("R") ? Player.RED : Player.BLUE;
                        Cell cell = board.setCell(i, j);
                        cell.setOrbCount(orbCount);
                        cell.setOwnerPlayer(player);
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        System.out.println("Invalid cell format");
                        return null;
                    }
                }
            }
        }
        // System.out.println("Board: \n" + board.toString());
        return board;
    }

    public static Board readGameState() throws IOException {
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) return null;
        System.out.println("Reading game state from file: " + FILE_PATH);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            // String firstLine = reader.readLine();
            // if (firstLine == null) {
            //     return null; // Empty file
            // }
            // String[] dimensions = firstLine.split(" ");
            // if (dimensions.length != 2) {
            //     throw new IOException("Invalid dimensions format");
            // }

            // ROWS = Integer.parseInt(dimensions[0]);
            // COLS = Integer.parseInt(dimensions[1]);
            // if (ROWS <= 0 || COLS <= 0) {
            //     throw new IOException("Invalid board dimensions");
            // }

            String header = reader.readLine();
            if (header == " ")
            {
                StringBuilder boardString = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    boardString.append(line).append("\n");
                }
                // if (boardString.length() == 0) {
                //     return null; // Empty board
                // }

                return stringToBoard(boardString.toString().trim());
            }
            if (header == null || (!header.equals("Human Move:") && !header.equals("AI Move:"))) {
                return null;
            }

            StringBuilder boardString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                boardString.append(line).append("\n");
            }
            if (boardString.length() == 0) {
                return null; // Empty board
            }

            return stringToBoard(boardString.toString().trim());
        }
    }

    public static Board waitForHeader(String expectedHeader, int timeoutMs) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            Board board = readGameState();
            if (board != null) {
                try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
                    String firstLine = reader.readLine();
                    if (firstLine == null) {
                        return null; // Empty file
                    }
                    // first line consumed.
                    if (reader.readLine().equals(expectedHeader)) {
                        return board;
                    }
                }
            }
            Thread.sleep(100);
        }
        return null;
    }
}