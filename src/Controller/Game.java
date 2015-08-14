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
import javafx.util.Pair;

public class Game {

    private Engine gameEngine;
    private final UserInterface UI = new UserInterface();
    private static final int MAX_PLAYERS = 6;
    private static final int MIN_PLAYERS = 2;
    private static final int PLAY_TURN = 1;
    private static final int QUIT = 3;
    private static final int SAVE = 2;
    private static final int SAVE_AS = 1;
    private static final int START = 1;
    private static final int END = 2;
    private static final int NEW_GAME = 1;
    private static final int LOAD_GAME = 2;
    private static final int FIRST_PLAYER = 0;
    private static final int MIN_HUMAN = 0;
    private static final int MAX_ROW = 17;
    private static final int MAX_COL = 13;
    private static final int MIN_COL_ROW = 1;
    private static final int MIN_COLOR_NUM = 1;
    private String saveGamePath = null;

    public void Run() {
        int userChoice = UI.checkIfUserWantToLoadGameOrPlayNewGame();
        while (userChoice < NEW_GAME || userChoice > LOAD_GAME) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(NEW_GAME, LOAD_GAME);
            userChoice = UI.checkIfUserWantToLoadGameOrPlayNewGame();
        }
        if (userChoice == NEW_GAME) {
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
        boolean isGameOver = false;

        while (!isGameOver) {
            isGameOver = doIteration();
        }
        if(isGameOver){
            UI.printBoard(gameEngine.getGameBoard());
            printWinnerName();
        }
    }

    private void printWinnerName() {
        String winnerName;
        if(gameEngine.getPlayers().size() == 1)
            winnerName = gameEngine.getPlayers().get(FIRST_PLAYER).getName();
        else
            winnerName = gameEngine.getCurrentPlayer().getName();
        UI.printWinnerGame(winnerName);
    }

    private void newGame(Engine.Settings gameSettings) {
        GameOption option = getValidGameOption();

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

    private GameOption getValidGameOption() {
        int userChoice = getValidUserChoice();
        GameOption[] options = GameOption.values();
        GameOption option = options[userChoice - 1];
        return option;
    }

    private int getValidUserChoice() {
        int userChoice = UI.getGameOption();
        while (userChoice < START || userChoice > QUIT) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(START,QUIT);
            userChoice = UI.getGameOption();
        }
        return userChoice;
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
        int humanPlayers = UI.getHumanPlayers(totalPlayers);
        while (humanPlayers < MIN_HUMAN || humanPlayers > totalPlayers) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(MIN_HUMAN, totalPlayers);
            humanPlayers = UI.getHumanPlayers(totalPlayers);
        }
        gameSettings.setHumanPlayers(humanPlayers);
    }

    private void initColorNumber(int totalPlayers, Engine.Settings gameSettings) {
        int numOfMaxColorsForEach = MAX_PLAYERS / totalPlayers;
        int colorNumber = UI.getColorNumberForEach(totalPlayers, numOfMaxColorsForEach);;
        while (colorNumber < MIN_COLOR_NUM || colorNumber > numOfMaxColorsForEach) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(MIN_COLOR_NUM, numOfMaxColorsForEach);
            colorNumber = UI.getColorNumberForEach(totalPlayers, numOfMaxColorsForEach);
        }
        gameSettings.setColorNumber(colorNumber);
    }

    private boolean doIteration() {
        boolean isGameOver = false;
        UI.printBoard(gameEngine.getGameBoard());
        Player curPlayer = gameEngine.getCurrentPlayer();
        if (curPlayer.getType() == Type.COMPUTER) {
            isGameOver = doAiIteration();
        } else if (doPlayerIteration(curPlayer)) {
            isGameOver = true;
        }

        return isGameOver;
    }

    private boolean doAiIteration() {
        Point start, end;
        Pair<Boolean,ArrayList<Point>> iterationResult;
        iterationResult = gameEngine.doAiIteration();
        ArrayList<Point> usedPoints = iterationResult.getValue();
        start = ChineseCheckersFactory.createSavedGamePoint(usedPoints.get(0), gameEngine.getGameBoard());
        end = ChineseCheckersFactory.createSavedGamePoint(usedPoints.get(1), gameEngine.getGameBoard());
        UI.showPlayerAiMove(start, end);
        return iterationResult.getKey();
    }

    private boolean doPlayerIteration(Player curPlayer) {
        int userChoice = UI.isUserWannaQuit(curPlayer.getName());
        while (userChoice < PLAY_TURN || userChoice > QUIT) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(PLAY_TURN, QUIT);
            userChoice = UI.isUserWannaQuit(curPlayer.getName());
        }
        return makeTheUserChoice(userChoice, curPlayer);
    }

    private boolean makeTheUserChoice(int userChoice, Player curPlayer) {
        boolean isGameOver = false;
        if (userChoice == PLAY_TURN) {
            isGameOver = getPointsAndStartIteration(curPlayer);
        } else if (userChoice == QUIT) {
            isGameOver = gameEngine.userQuited(curPlayer);
        } else {
            saveGame();
        }

        return isGameOver;
    }

    private boolean getPointsAndStartIteration(Player curPlayer) {
        Point start;
        Point end;
        start = getValidStartingPoint(curPlayer);

        end = getValidEndPoint(start, curPlayer);
        return gameEngine.doIteration(start, end);
    }

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

    private Point validatePoint(Player curPlayer, ArrayList<Point> playerP, int whichPoint) {
        Point playerStart;
        boolean isValidRow, isValidCol, validPlayerStart;
        do {
            if (whichPoint == START) {
                playerStart = UI.getStartPoint(curPlayer, playerP);
            } else {
                playerStart = UI.getEndPoint(curPlayer);
            }
            isValidRow = playerStart.x < MIN_COL_ROW || playerStart.x > MAX_ROW;
            isValidCol = playerStart.y < MIN_COL_ROW || playerStart.y > MAX_COL;
            validPlayerStart = isValidCol || isValidRow;
            if (validPlayerStart) {
                UI.printInvalidPoint(playerStart);
            }
        } while (validPlayerStart);

        return playerStart;
    }

    private Point getValidEndPoint(Point start, Player curPlayer) {
        Point end;
        Point playerEnd;
        do {
            playerEnd = validatePoint(curPlayer, null, END);
            end = EngineFactory.createGamePoint(playerEnd, gameEngine.getGameBoard());
            if (!gameEngine.isPossibleMove(start, end)) {
                UI.printInvalidPoint(playerEnd);
            }
        } while (!gameEngine.isPossibleMove(start, end));
        return end;
    }

    public int getTotalPlayers() {
        int totalPlayers = UI.getTotalPlayers();
        while (totalPlayers > MAX_PLAYERS || totalPlayers < MIN_PLAYERS) {
            UI.printErrorMsgToUserAboutInvalidNumberInput(MIN_PLAYERS, MAX_PLAYERS);
            totalPlayers = UI.getTotalPlayers();
        }
        return totalPlayers;
    }

    private ArrayList<Point> convertBoardPointsToPoints(ArrayList<Point> possibleMoves) {
        ArrayList<Point> result = new ArrayList<>();
        for (Point possibleMove : possibleMoves) {
            result.add(ChineseCheckersFactory.createSavedGamePoint(possibleMove, gameEngine.getGameBoard()));

        }
        return result;
    }

    private void saveGame() {
        int userChoice;
        boolean isSavedGame = false;
        String path = null;

        userChoice = getValidUserChoiceToSave();

        while (!isSavedGame) {
            if (userChoice == SAVE_AS || !isSaveGamePathExists()) {
                path = UI.getPathFromUser();
            } else {
                path = saveGamePath;
            }
            isSavedGame = tryToSaveGame(path);
        }
    }

    private int getValidUserChoiceToSave() {
        int userChoice;
        do {
            userChoice = UI.checkIfUserWantToSaveAsOrJustSave();
        } while (userChoice < SAVE_AS || userChoice > SAVE);
        return userChoice;
    }

    private boolean tryToSaveGame(String path) {
        boolean isFileSaved = true;

        try {
            ChineseCheckers savedGame = ChineseCheckersFactory.createSavedGameObject(gameEngine);
            FileManager.saveGame(path, savedGame);
        } catch (Exception e) {
            UI.showErrorToUser("Could not save game, Please try again with diffrent path.");
            isFileSaved = false;
        }

        if (isFileSaved) {
            saveGamePath = path;
        }

        return isFileSaved;
    }

    public boolean isSaveGamePathExists() {
        return saveGamePath != null;
    }

    private void loadGame(String path) {
        boolean isFileLoaded = false;

        while (!isFileLoaded) {
            isFileLoaded = tryToLoadGame(path);
            if (!isFileLoaded) {
                path = UI.getPathFromUser();
            }
        }
        play();
    }

    private boolean tryToLoadGame(String path) {
        boolean isFileLoaded;
        try {
            ChineseCheckers savedGame = FileManager.loadGame(path);
            gameEngine = EngineFactory.createEngine(savedGame);
            isFileLoaded = true;
        } catch (Exception ex) {
            String msg;
            if (ex.getMessage() == null) {
                msg = "Invalid path, try again";
            } else {
                msg = ex.getMessage();
            }
            UI.showErrorToUser(msg);
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
