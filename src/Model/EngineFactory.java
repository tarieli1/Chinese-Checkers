package Model;

import Model.Player.Type;
import generatedClasses.Board;
import generatedClasses.Cell;
import generatedClasses.ChineseCheckers;
import generatedClasses.ColorType;
import generatedClasses.PlayerType;
import generatedClasses.Players;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EngineFactory {

    public static Engine createEngine(ChineseCheckers savedGame) throws Exception {
        Engine.Settings savedGameSettings = createGameSettings(savedGame);
        Engine engine = new Engine(savedGameSettings);
        initializeComponents(savedGame, engine);

        validateEngine(engine);
        return engine;
    }

    private static void initializeComponents(ChineseCheckers savedGame, Engine engine) throws Exception {
        setEnginePlayers(savedGame, engine);
        engine.setCurrentPlayerIndx(getCurrentPlayerIndx(savedGame, engine.getPlayers()));
        engine.setGameBoard(createGameBoard(savedGame.getBoard()));
        engine.initPointsToPlayers();

        engine.setPossibleMovesForPlayer(engine.getCurrentPlayer());
    }

    public static Point createGamePoint(Point p, Model.Board board) {

        int counter = 1;
        int i = -1;
        while (counter <= p.y && i < Model.Board.COLS - 1) {
            ++i;
            Model.Color color = board.getColorByPoint(new Point(p.x - 1, i));
            if (color != Color.TRANSPARENT) {
                counter++;
            }
        }
        return new Point(p.x - 1, i);
    }

    private static Engine.Settings createGameSettings(ChineseCheckers savedGame) {
        Engine.Settings gameSetting = new Engine.Settings();
        List<Players.Player> players = savedGame.getPlayers().getPlayer();
        gameSetting.setTotalPlayers(players.size());
        gameSetting.setPlayerNames(createNamesList(players));

        return gameSetting;
    }

    private static int getCurrentPlayerIndx(ChineseCheckers savedGame, List<Player> players) throws Exception {
        int indx = 0;
        boolean found = false;
        String curPlayerString = savedGame.getCurrentPlayer();

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(curPlayerString)) {
                indx = i;
                found = true;
            }
        }
        if (!found) {
            throw new Exception("Current player doesn't exist");
        }
        return indx;
    }

    private static List<String> createNamesList(List<Players.Player> players) {
        ArrayList<String> playersNames = new ArrayList<>();

        for (Players.Player player : players) {
            playersNames.add(player.getName());
        }

        return playersNames;
    }

    private static Model.Board createGameBoard(Board board) throws Exception {
        Model.Board gameBoard = new Model.Board();
        gameBoard.makeEmpty();
        List<Cell> cells = board.getCell();
        for (Cell cell : cells) {
            Model.Color color = createColorFromSaveGameColor(cell.getColor());
            Point point = createGamePoint(new Point(cell.getRow(), cell.getCol()), gameBoard);
            Model.Color gameColor = gameBoard.getColorByPoint(point);
            if (gameColor == Color.TRANSPARENT) {
                throw new Exception(String.format("invalid point {%d,%d}", cell.getRow(), cell.getCol()));
            }

            gameBoard.setColorByPoint(point, color);
        }

        return gameBoard;
    }

    private static void setEnginePlayers(ChineseCheckers savedGame, Engine engine) {
        ArrayList<Player> gamePlayers = engine.getPlayers();
        gamePlayers.clear();
        List<Players.Player> savedPlayers = savedGame.getPlayers().getPlayer();

        for (Players.Player savedPlayer : savedPlayers) {
            gamePlayers.add(createGamePlayer(savedPlayer));
        }
    }

    private static Player createGamePlayer(Players.Player savedPlayer) {
        String playerName = savedPlayer.getName();
        Type playerType = createTypeFromSavedType(savedPlayer.getType());
        Player gamePlayer = new Player(playerName, playerType);

        setPlayerColors(gamePlayer, savedPlayer);

        return gamePlayer;
    }

    private static Type createTypeFromSavedType(PlayerType type) {
        Type gameType;
        if (type.equals(PlayerType.COMPUTER)) {
            gameType = Type.COMPUTER;
        } else {
            gameType = Type.PLAYER;
        }
        return gameType;
    }

    private static void setPlayerColors(Player gamePlayer, Players.Player savedPlayer) {
        List<ColorType> savedColors = savedPlayer.getColorDef();
        ArrayList<Color> playerColors = gamePlayer.getColors();
        ArrayList<Point> playerTargets = gamePlayer.getTargets();
        playerColors.clear();
        playerTargets.clear();

        for (ColorType color : savedColors) {
            playerColors.add(createColorFromSaveGameColor(color.getColor()));
            playerTargets.add(createTargetPointFromSavedGameTarget(color.getTarget()));
        }
    }

    private static Color createColorFromSaveGameColor(generatedClasses.Color color) {
        Color gameColor;

        switch (color) {
            case BLACK:
                gameColor = Color.BLACK;
                break;
            case BLUE:
                gameColor = Color.BLUE;
                break;
            case GREEN:
                gameColor = Color.GREEN;
                break;
            case RED:
                gameColor = Color.RED;
                break;
            case WHITE:
                gameColor = Color.WHITE;
                break;
            case YELLOW:
                gameColor = Color.YELLOW;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return gameColor;
    }

    private static Point createTargetPointFromSavedGameTarget(ColorType.Target savedTarget) {
        return new Point(savedTarget.getRow(), savedTarget.getCol());
    }

    private static void validateEngine(Engine engine) throws Exception {
        eachColorHasTenMarbles(engine.getGameBoard());
        targetsAreLegit(engine);

    }

    private static void eachColorHasTenMarbles(Model.Board gameBoard) throws Exception {
        HashMap<Model.Color, AtomicInteger> colorCounterMap = createTheMap(gameBoard);
        validateMap(colorCounterMap);
    }

    private static HashMap<Model.Color, AtomicInteger> createTheMap(Model.Board gameBoard) {
        HashMap<Model.Color, AtomicInteger> colorCounterMap = new HashMap<>();

        int rows = Model.Board.ROWS;
        int cols = Model.Board.COLS;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                updateMap(gameBoard, new Point(i, j), colorCounterMap);
            }
        }
        return colorCounterMap;
    }

    private static void updateMap(Model.Board gameBoard, Point curPoint, HashMap<Color, AtomicInteger> colorCounterMap) {
        Model.Color curColor = gameBoard.getColorByPoint(curPoint);
        if (isMarble(curColor)) {
            if (!colorCounterMap.containsKey(curColor)) {
                colorCounterMap.put(curColor, new AtomicInteger(0));
            }

            colorCounterMap.get(curColor).getAndIncrement();
        }
    }

    private static void validateMap(HashMap<Color, AtomicInteger> colorCounterMap) throws Exception {
        for (Model.Color key : colorCounterMap.keySet()) {
            if (colorCounterMap.get(key).intValue() != 10) {
                throw new Exception("There is a color with more then 10 points!");
            }
        }
    }

    private static void targetsAreLegit(Engine engine) throws Exception {
        ArrayList<Player> players = engine.getPlayers();
        TargetMapper targetMap = engine.getTargetMap();
        Set<Point> validTargets = targetMap.getVertexToSet().keySet();
        for (Player player : players) {
            ArrayList<Point> targets = player.getTargets();
            for (Point target : targets) {
                if (!validTargets.contains(target)) {
                    throw new Exception("not valid target point");
                }
            }
        }
    }

    private static boolean isMarble(Color color) {
        return color != Color.TRANSPARENT && color != Color.EMPTY;
    }
}
