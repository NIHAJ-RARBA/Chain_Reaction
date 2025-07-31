package chain_Reaction.chainReaction.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import chain_Reaction.chainReaction.DTOs.AiDTO;
import chain_Reaction.chainReaction.DTOs.HumanAIDTO;
import chain_Reaction.chainReaction.DTOs.MoveDTO;
import chain_Reaction.chainReaction.DTOs.SetupDTO;
import chain_Reaction.chainReaction.DTOs.SingleAIDTO;
import chain_Reaction.chainReaction.models.Cell;
import chain_Reaction.chainReaction.service.GameService;
import chain_Reaction.chainReaction.utils.GameStateWatcher;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    private final GameStateWatcher gameStateWatcher;

    public GameController(GameStateWatcher gameStateWatcher) {
        this.gameStateWatcher = gameStateWatcher;
    }

    @GetMapping()
    public ResponseEntity<String> sayHi() {
        // Return the current game status
        return ResponseEntity.ok("Hi");
    }

    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestBody SetupDTO setupDTO) {
        
        int rows, cols;
        System.out.println("Received setupDTO: " + setupDTO);
        
        // Clear/empty the game state file first before starting new game
        try {
            // Write empty content to clear the file
            java.nio.file.Files.writeString(java.nio.file.Paths.get("gamestate.txt"), "", 
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Game state file cleared for new game");
        } catch (Exception e) {
            System.err.println("Warning: Could not clear game state file: " + e.getMessage());
        }
        
        if (setupDTO == null || setupDTO.getRows() <= 0 || setupDTO.getColumns() <= 0) 
        {
            rows = 9;
            cols = 6;
            gameService.initializeGame(rows, cols);
            System.out.println("Game setup is missing. Setting up with default size 9x6");
            return ResponseEntity.ok("Game setup is missing. Setting up with default size 9x6");
        }

        else
        {
            
            rows = setupDTO.getRows();
            cols = setupDTO.getColumns();
            gameService.initializeGame(rows, cols);
            System.out.println("Game started with board size " + rows + "x" + cols);
            return ResponseEntity.ok("Game started with board size " + rows + "x" + cols);
        }
        
    }

    @PostMapping("/start/setupPlayers")
    public ResponseEntity<String> setupPlayerTypes(@RequestBody HumanAIDTO humanAIDTO) {

        boolean isRedHuman, isBlueHuman;
        System.out.println("Received humanAIDTO: " + humanAIDTO);
        if (humanAIDTO == null) {
            isRedHuman = true; // Default to human player for red
            isBlueHuman = true; // Default to human player for blue
        } else {
            isRedHuman = humanAIDTO.getIsRedHuman() == 1; // Assuming 1 means human, 0 means AI
            isBlueHuman = humanAIDTO.getIsBlueHuman() == 1; // Assuming 1 means human, 0 means AI   
        }
            
        gameService.setPlayerTypes(isRedHuman, isBlueHuman);
        System.out.println("Players selected: Red - " + (isRedHuman ? "Human" : "AI") + ", Blue - " + (isBlueHuman ? "Human" : "AI"));
        return ResponseEntity.ok("Players selected: Red - " + (isRedHuman ? "Human" : "AI") + ", Blue - " + (isBlueHuman ? "Human" : "AI"));

    }

    @PostMapping("/set-ai-type/both")
    public ResponseEntity<String> setAIType(@RequestBody AiDTO aiType) {
        if (aiType == null) {
            return ResponseEntity.badRequest().body("Invalid AI type data.");
        }

        System.out.println("Received AI types: " + aiType.getAi1HeuristicType() + ", " + aiType.getAi2HeuristicType());
        boolean success = gameService.setBothAiTypes(aiType.getAi1HeuristicType(), aiType.getAi2HeuristicType());
        // System.out.println("Ai types set: " + aiType.getAi1HeuristicType() + ", " + aiType.getAi2HeuristicType());
        if (success) {
            return ResponseEntity.ok("AI types set successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to set AI types.");
        }
    }

    @PostMapping("/set-ai-type/single")
    public ResponseEntity<String> setSingleAIType(@RequestBody SingleAIDTO aiType) {
        if (aiType == null) {
            return ResponseEntity.badRequest().body("Invalid AI type data.");
        }
        System.out.println("Received AI type: " + aiType.getHeuristicType());
        boolean success = gameService.setAiType(aiType.getHeuristicType());
        // System.out.println("Ai type set: " + aiType.getHeuristicType());
        if (success) {
            return ResponseEntity.ok("AI types set successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to set AI types.");
        }
    }

    @GetMapping("/isHuman")
    public ResponseEntity<Boolean> isHumanPlayer() {
        return ResponseEntity.ok(gameService.isCurrentPlayerHuman());
    }

    @PostMapping("/move")
    public ResponseEntity<String> makeMove(@RequestBody MoveDTO moveDTO) {
        if (moveDTO == null || moveDTO.getRow() < 0 || moveDTO.getColumn() < 0
                || moveDTO.getRow() >= gameService.getGameBoard().getRows()
                || moveDTO.getColumn() >= gameService.getGameBoard().getColumns()) {
            return ResponseEntity.badRequest().body("Invalid move data.");
        }
        int row = moveDTO.getRow();
        int col = moveDTO.getColumn();
        boolean success = gameService.makeMove(row, col);
        if (success) {
            return ResponseEntity.ok("Move made at (" + row + ", " + col + ")");
        } else {
            return ResponseEntity.badRequest().body("Invalid move.");
        }
    }

    @GetMapping("/move/ai")
    public ResponseEntity<String> makeAIMove() {
            
            boolean updatedBoard = gameService.updateBoard();
            if (!updatedBoard) {
                return ResponseEntity.badRequest().body("Failed to update game board from file.");
            }
            boolean success = gameService.makeAIMove();
            if (success) {
                return ResponseEntity.ok(gameService.getGameState());
            } else {
                if(gameService.isGameOver()) {
                    return ResponseEntity.ok("Game over. No more moves possible.");
                }
                return ResponseEntity.badRequest().body("AI failed to make a move.");
            }
        
    }


    @PostMapping("/move/humanVai")
    public ResponseEntity<String> makeHumanVAI(@RequestBody MoveDTO moveDTO) {
        if (moveDTO == null || moveDTO.getRow() < 0 || moveDTO.getColumn() < 0
                || moveDTO.getRow() >= gameService.getGameBoard().getRows()
                || moveDTO.getColumn() >= gameService.getGameBoard().getColumns()) {
            return ResponseEntity.badRequest().body("Invalid move data.");
        }
        int row = moveDTO.getRow();
        int col = moveDTO.getColumn();
        boolean success = gameService.makeMove(row, col);
        if (success) {
            // After human move, let AI make its move
            // sleep
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            boolean aiSuccess = gameService.makeAIMove();
            if (aiSuccess) {
                return ResponseEntity.ok("Human move made at (" + row + ", " + col + ") and AI moved successfully.");
            } else {
                return ResponseEntity.badRequest().body("AI failed to make a move after human's turn.");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid move.");
        }
    }

    @GetMapping("/version")
    public ResponseEntity<Long> getGameStateVersion() {
        return ResponseEntity.ok(gameStateWatcher.getVersion());
    }

    @GetMapping("/board")
    public ResponseEntity<Cell[][]> getBoard() {
        if (gameService.getGameBoard() == null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            return ResponseEntity.ok(gameService.getGameBoard().getBoard());
        }            
    }

    @GetMapping("/game-over-stats")
    public ResponseEntity<String> getGameStats() {
        if (!gameService.isGameOver()) {
            return ResponseEntity.badRequest().body("Game is not over yet.");
        }
        String winner = gameService.isWinnerRed() ? "RED" : "BLUE";
        int[] score = gameService.getScore();
        String stats = String.format("%d %d", score[0], score[1]);
        return ResponseEntity.ok(winner + " " + stats);
    }

    @GetMapping("/state")
    public ResponseEntity<String> getGameState() {
        // return ResponseEntity.ok(gameService.getGameState());
        return ResponseEntity.ok(gameService.getGameStateFromFile());
    }

    @GetMapping("/game-over")
    public ResponseEntity<Boolean> isGameOver() {
        return ResponseEntity.ok(gameService.isGameOver());
    }

    @GetMapping("/current-player")
    public ResponseEntity<String> getCurrentPlayer() {
        return ResponseEntity.ok(gameService.getCurrentPlayer().name());
    }
}
