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
    private static final int MIN_PLAYERS = 2;
    private static final int PLAY_TURN = 1;
    private static final int SAVE_GAME = 2;
    private static final int QUIT = 3;
    private static final int START = 1;
    private static final int END = 2;
    private String saveGamePath = null;

    //Tamir King

    public void Run() {
        int userChoice = UI.checkIfUserWantToLoadGameOrPlayNewGame();
        while (userChoice < 1 || userChoice > 2) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(1, 2);
            userChoice = UI.checkIfUserWantToLoadGameOrPlayNewGame();
        }
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

    //Tamir King

    private void initHumanPlayers(int totalPlayers, Engine.Settings gameSettings) {
        int humanPlayers = UI.getHumanPlayers(totalPlayers);
        while (humanPlayers < 0 || humanPlayers > totalPlayers) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(0, totalPlayers);
            humanPlayers = UI.getHumanPlayers(totalPlayers);
        }
        gameSettings.setHumanPlayers(humanPlayers);
    }

    //Tamir King

    private void initColorNumber(int totalPlayers, Engine.Settings gameSettings) {
        int numOfMaxColorsForEach = MAX_PLAYERS / totalPlayers;
        int colorNumber = UI.getColorNumberForEach(totalPlayers, numOfMaxColorsForEach);;
        while (colorNumber < 1 || colorNumber > numOfMaxColorsForEach) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(1, numOfMaxColorsForEach);
            colorNumber = UI.getColorNumberForEach(totalPlayers, numOfMaxColorsForEach);
        }
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
        start = ChineseCheckersFactory.createSavedPoint(usedPoints.get(0), gameEngine.getGameBoard());
        end = ChineseCheckersFactory.createSavedPoint(usedPoints.get(1), gameEngine.getGameBoard());
        UI.showPlayerAiMove(start, end);
    }

    private void doPlayerIteration(Player curPlayer) {
        Point start, end;
        int userChoice = UI.isUserWannaQuit(curPlayer.getName());
        while (userChoice < 1 || userChoice > 3) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(1, 3);
            userChoice = UI.isUserWannaQuit(curPlayer.getName());
        }
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

    //Tamir King

    private Point getValidStartingPoint(Player curPlayer) {
        Point start;
        Point playerStart;
        ArrayList<Point> possibleMoves;
        HashMap<Point, ArrayList<Point>> playerPointsMap = curPlayer.getPossibleMoves();
        ArrayList<Point> moveablePoints = getNotEmptyKeys(playerPointsMap);
        moveablePoints = convertBoardPointsToPoints(moveablePoints);
        do {
            playerStart = validatePoint(curPlayer, moveablePoints, START);
            start = EngineFactory.createGamePoint(playerStart, gameEngine.getGameBoard());
            possibleMoves = gameEngine.getPossibleMovesForCurPlayerInPoint(start);
            if (possibleMoves == null) {
                UI.printInvalidPoint(playerStart);
            }
        } while (possibleMoves == null);
        possibleMoves = convertBoardPointsToPoints(possibleMoves);
        UI.showPossiblePointsToPick(possibleMoves);

        return start;
    }

    //Tamir King

    private Point validatePoint(Player curPlayer, ArrayList<Point> playerP, int whichPoint) {
        Point playerStart;
        boolean validPlayerStart;
        do {
            if (whichPoint == START) {
                playerStart = UI.getStartPoint(curPlayer, playerP);
            } else {
                playerStart = UI.getEndPoint(curPlayer);
            }
            validPlayerStart = playerStart.x < 1 || playerStart.x > 17 || playerStart.y < 1 || playerStart.y > 17;
            if (validPlayerStart) {
                UI.printInvalidPoint(playerStart);
            }
        } while (validPlayerStart);

        return playerStart;
    }

    //Tamir King

    private Point getValidEndPoint(Point start, Player curPlayer) {
        Point end;
        Point playerEnd = null;
        do {
            playerEnd = validatePoint(curPlayer, null, END);
            end = EngineFactory.createGamePoint(playerEnd, gameEngine.getGameBoard());
            if (!gameEngine.isPossibleMove(start, end)) {
                UI.printInvalidPoint(end);
            }
        } while (!gameEngine.isPossibleMove(start, end));
        return end;
    }

    //Tamir King

    public int getTotalPlayers() {
        int totalPlayers = UI.getTotalPlayers();
        while (totalPlayers > 6 || totalPlayers < 2) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(MIN_PLAYERS, MAX_PLAYERS);
            totalPlayers = UI.getTotalPlayers();
        }
        return totalPlayers;
    }

    private void clearPlayerPointsFromBoard(Player curPlayer) {
        UI.clearPlayerPointsFromBoard(curPlayer.getPoints(), gameEngine.getGameBoard());
        gameEngine.removePlayer(curPlayer);
    }

    private ArrayList<Point> convertBoardPointsToPoints(ArrayList<Point> possibleMoves) {
        ArrayList<Point> result = new ArrayList<>();
        for (Point possibleMove : possibleMoves) {
            result.add(ChineseCheckersFactory.createSavedPoint(possibleMove, gameEngine.getGameBoard()));

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

    private ArrayList<Point> getNotEmptyKeys(HashMap<Point, ArrayList<Point>> playerPointsMap) {
        ArrayList<Point> notEmptyKeys = new ArrayList<>();

        for (Point key : playerPointsMap.keySet()) {
            if (!playerPointsMap.get(key).isEmpty()) {
                notEmptyKeys.add(key);
            }
        }
        return notEmptyKeys;
    }

}
