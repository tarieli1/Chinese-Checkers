package Controller;

import static Model.Board.COLS;
import Model.Color;
import Model.Engine;
import Model.Player;
import Model.Player.Type;
import View.UserInterface;
import java.awt.Point;
import java.util.*;

public class Game {

    private Engine gameLogic;
    private final UserInterface UI = new UserInterface();
    private static final int MAX_PLAYERS = 6;
    private static final int PLAY_TURN = 1;
    private static final int SAVE_GAME = 2;
    private static final int QUIT = 3;
    private static final int START = 1;
    private static final int END = 2;

    public void Run() {
        Engine.Settings gameSettings = getGameSettings();
        gameLogic = new Engine(gameSettings);
        playGame(gameSettings);
    }

    private void playGame(Engine.Settings gameSettings) {
        play();
        newGame(gameSettings);
    }

    private void play() {
        boolean isGameOver = false, isRunning = true;

        while (isRunning && !isGameOver) {
            isGameOver = gameLogic.isGameOver();
            isRunning = doIteration();
        }
    }

    private void newGame(Engine.Settings gameSettings) {
        gameOption option = UI.getGameOption();
        switch (option) {
            case NewGame:
                gameSettings = getGameSettings();
            case Restart:
                gameLogic.restart(gameSettings);
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
        UI.printBoard(gameLogic.getGameBoard());
        Player curPlayer = gameLogic.getCurrentPlayer();
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
        usedPoints = gameLogic.doAiIteration();
        start = convertBoardPointToPoint(usedPoints.get(0));
        end = convertBoardPointToPoint(usedPoints.get(1));
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
            gameLogic.doIteration(start, end);
        }
    }

    private Point getValidStartingPoint(Player curPlayer) {
        Point start;
        Point playerStart = null;
        ArrayList<Point> possibleMoves;
        ArrayList<Point> playerPoints;
        playerPoints = convertBoardPointsToPoints(curPlayer.getPoints());
        playerStart = validatePoint(playerStart, curPlayer, playerPoints, START);
        do {
            start = convertPointToBoardPoint(playerStart);
            possibleMoves = gameLogic.getPossibleMovesForCurPlayerInPoint(start);
        } while (possibleMoves == null);
        possibleMoves = convertBoardPointsToPoints(possibleMoves);
        UI.showPossiblePointsToPick(possibleMoves);
        return start;
    }

    private Point validatePoint(Point playerStart, Player curPlayer, ArrayList<Point> playerP, int whichPoint) {
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
            end = convertPointToBoardPoint(playerEnd);
        } while (!gameLogic.isPossibleMove(start, end));
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
        UI.clearPlayerPointsFromBoard(curPlayer.getPoints(), gameLogic.getGameBoard());
        gameLogic.removePlayer(curPlayer);
    }

    private ArrayList<Point> convertBoardPointsToPoints(ArrayList<Point> possibleMoves) {
        ArrayList<Point> result = new ArrayList<>();
        for (Point possibleMove : possibleMoves) {
            result.add(convertBoardPointToPoint(possibleMove));

        }
        return result;
    }

    private void saveGame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
