package Controller;

import Model.ChineseCheckersFactory;
import Model.Engine;
import Model.EngineFactory;
import Model.FileManager;
import Model.Player;
import Model.Player.Type;
import View.UserInterface;
import generatedClasses.ChineseCheckers;
import java.awt.Point;
import java.util.*;

public class Game {

    private Engine gameEngine;
    private final UserInterface UI = new UserInterface();
    private static final int MAX_PLAYERS = 6;
    private static final int PLAY_TURN = 1;
    private static final int SAVE_GAME = 2;
    private static final int QUIT = 3;
    private static final int START = 1;
    private static final int END = 2;
    private String saveGamePath = null;

    public void Run() {
        int userChoice;
        do {
            userChoice = UI.checkIfUserWantToLoadGameOrPlayNewGame();
        } while (userChoice < 1 || userChoice > 2);
        if (userChoice == 1) {
            createNewGame();
        } else {
            loadGame(UI.getPathFromUser());
        }
    }

    private void createNewGame() {
        Engine.Settings gameSettings = getGameSettings();
        gameEngine = new Engine(gameSettings);
        playGame(gameSettings);
    }

    private void playGame(Engine.Settings gameSettings) {
        play();
        newGame(gameSettings);
    }

    private void play() {
        boolean isGameOver = false, isRunning = true;

        while (isRunning && !isGameOver) {
            isGameOver = gameEngine.isGameOver();
            isRunning = doIteration();
        }
    }

    private void newGame(Engine.Settings gameSettings) {
        gameOption option = UI.getGameOption();
        switch (option) {
            case NewGame:
                gameSettings = getGameSettings();
            case Restart:
                gameEngine.restart(gameSettings);
                playGame(gameSettings);
                break;
            default:
                break;
        }
    }

    private Engine.Settings getGameSettings() {
        int totalPlayers = getTotalPlayers();
        Engine.Settings gameSettings = initSettings(totalPlayers);
        gameSettings.setTotalPlayers(totalPlayers);

        return gameSettings;
    }

    private Engine.Settings initSettings(int totalPlayers) {
        Engine.Settings gameSettings = new Engine.Settings();
        initColorNumber(totalPlayers, gameSettings);
        initHumanPlayers(totalPlayers, gameSettings);
        initPlayerNames(gameSettings);

        return gameSettings;
    }

    private void initPlayerNames(Engine.Settings gameSettings) {
        ArrayList<String> playerNames = new ArrayList<>();
        String playerName;
        for (int i = 1; i <= gameSettings.getHumanPlayers(); i++) {
            playerName = UI.getName(i);
            i = validateUniquePlayerName(playerNames, playerName, i);
        }
        gameSettings.setPlayerNames(playerNames);
    }

    private int validateUniquePlayerName(ArrayList<String> playerNames, String playerName, int i) {
        if (!playerNames.contains(playerName)) {
            playerNames.add(playerName);
        } else {
            UI.alertUserThatPlayerNameIsNotUinque(playerName);
            i--;
        }
        return i;
    }

    private void initHumanPlayers(int totalPlayers, Engine.Settings gameSettings) {
        int humanPlayers;
        do {
            humanPlayers = UI.getHumanPlayers(totalPlayers);
        } while (humanPlayers < 0 || humanPlayers > totalPlayers);
        gameSettings.setHumanPlayers(humanPlayers);
    }

    private void initColorNumber(int totalPlayers, Engine.Settings gameSettings) {
        int numOfMaxColorsForEach = MAX_PLAYERS / totalPlayers;
        int colorNumber;
        do {
            colorNumber = UI.getColorNumberForEach(totalPlayers, numOfMaxColorsForEach);
        } while (colorNumber < 1 || colorNumber > numOfMaxColorsForEach);
        gameSettings.setColorNumber(colorNumber);
    }

    private boolean doIteration() {
        UI.printBoard(gameEngine.getGameBoard());
        Player curPlayer = gameEngine.getCurrentPlayer();
        if (curPlayer.getType() == Type.COMPUTER) {
            doAiIteration();
        } else {
            doPlayerIteration(curPlayer);
        }
        return UI.isRunning();
    }

    private void doAiIteration() {
        Point start, end;
        ArrayList<Point> usedPoints;
        usedPoints = gameEngine.doAiIteration();
        start = ChineseCheckersFactory.createSavedPoint(usedPoints.get(0), gameEngine);
        end = ChineseCheckersFactory.createSavedPoint(usedPoints.get(1), gameEngine);
        UI.showPlayerAiMove(start, end);
    }

    private void doPlayerIteration(Player curPlayer) {
        Point start, end;
        int userChoice;
        do {
            userChoice = UI.isUserWannaQuit(curPlayer.getName());
        } while (userChoice < 1 || userChoice > 3);
        makeTheUserChoice(userChoice, curPlayer);
    }

    private void makeTheUserChoice(int userChoice, Player curPlayer) {
        if (userChoice == PLAY_TURN) {
            getPointsForStartingIteration(curPlayer);
        } else if (userChoice == QUIT) {
            clearPlayerPointsFromBoard(curPlayer);
        } else {
            saveGame();
        }
    }

    private void getPointsForStartingIteration(Player curPlayer) {
        Point start;
        Point end;
        start = getValidStartingPoint(curPlayer);
        if (UI.isRunning()) {
            end = getValidEndPoint(start, curPlayer);
            gameEngine.doIteration(start, end);
        }
    }

    private Point getValidStartingPoint(Player curPlayer) {
        Point start;
        Point playerStart = null;
        ArrayList<Point> possibleMoves;
        ArrayList<Point> playerPoints;
        playerPoints = convertBoardPointsToPoints(curPlayer.getPoints());
        playerStart = validatePoint(playerStart, curPlayer, playerPoints, START);
        do{
            start = EngineFactory.createGamePoint(playerStart, gameEngine);
            possibleMoves = gameEngine.getPossibleMovesForCurPlayerInPoint(start);
        } while (possibleMoves == null);
        possibleMoves = convertBoardPointsToPoints(possibleMoves);
        UI.showPossiblePointsToPick(possibleMoves);
        
        return start;
    }

    private Point validatePoint(Point playerStart, Player curPlayer,ArrayList<Point> playerP, int whichPoint) {
        do {
            if (whichPoint == START) {
                playerStart = UI.getStartPoint(curPlayer, playerP);
            } else {
                playerStart = UI.getEndPoint(curPlayer);
            }
        } while (playerStart.x < 1 || playerStart.x > 17 || playerStart.y < 1 || playerStart.y > 17);

        return playerStart;
    }

    private Point getValidEndPoint(Point start, Player curPlayer) {
        Point end;
        Point playerEnd = null;
        do {
            playerEnd = validatePoint(playerEnd, curPlayer, null, END);
            end = ChineseCheckersFactory.createSavedPoint(playerEnd, gameEngine);
        } while (!gameEngine.isPossibleMove(start, end));
        return end;
    }

    public int getTotalPlayers() {
        int totalPlayers;
        do {
            totalPlayers = UI.getTotalPlayers();
        } while (totalPlayers > 6 || totalPlayers < 2);
        return totalPlayers;
    }

    private void clearPlayerPointsFromBoard(Player curPlayer) {
        UI.clearPlayerPointsFromBoard(curPlayer.getPoints(), gameEngine.getGameBoard());
        gameEngine.removePlayer(curPlayer);
    }

    private ArrayList<Point> convertBoardPointsToPoints(ArrayList<Point> possibleMoves) {
        ArrayList<Point> result = new ArrayList<>();
        for (Point possibleMove : possibleMoves) {
            result.add(ChineseCheckersFactory.createSavedPoint(possibleMove, gameEngine));

        }
        return result;
    }

    private void saveGame() {
        int userChoice;
        boolean isSavedGame = false;
        String path = null;

        do {
            userChoice = UI.checkIfUserWantToSaveAsOrJustSave();
        } while (userChoice < 1 || userChoice > 2);
        while (!isSavedGame) {
            if (userChoice == 1 || !isSaveGamePathExists()) {
                path = UI.getPathFromUser();
            }
            isSavedGame = tryToSaveGame(path);
        }
    }

    private boolean tryToSaveGame(String path) {
        boolean isFileSaved = true;

        try {
            if (path != null) {
                saveGamePath = path;
            }
            ChineseCheckers savedGame = ChineseCheckersFactory.createSavedGameObject(gameEngine);
            FileManager.saveGame(saveGamePath, savedGame);
        } catch (Exception ex) {
            UI.showExceptionToUser(ex);
            isFileSaved = false;
        }

        return isFileSaved;
    }

    public boolean isSaveGamePathExists() {
        return saveGamePath != null;
    }

    private void loadGame(String path) {
        boolean isFileLoaded = false;

        while (!isFileLoaded) {
            isFileLoaded = tryToLoadGame(path, isFileLoaded);
            if (!isFileLoaded) {
                path = UI.getPathFromUser();
            }
        }
    }

    private boolean tryToLoadGame(String path, boolean isFileLoaded) {
        try {
            ChineseCheckers savedGame = FileManager.loadGame(path);
            gameEngine = EngineFactory.createEngine(savedGame);
        } catch (Exception ex) {
            UI.showExceptionToUser(ex);
            isFileLoaded = false;
        }

        return isFileLoaded;
    }

}
