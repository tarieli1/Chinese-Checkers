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
import java.util.List;

public abstract class EngineFactory {

    public static Engine createEngine(ChineseCheckers savedGame) {
        Engine.Settings savedGameSettings = createGameSettings(savedGame);
        Engine engine = new Engine(savedGameSettings);
        setEnginePlayers(savedGame, engine);
        engine.setCurrentPlayerIndx(getCurrentPlayerIndx(savedGame, engine.getPlayers()));
        engine.setGameBoard(createGameBoard(savedGame.getBoard()));
        engine.initPointsToPlayers();

        return engine;
    }

    public static Point createGamePoint(Point p, Model.Board board) {

        int counter = 1;
        int i = -1;
        while (counter <= p.y) {
            ++i;
            Model.Color color = board.getColorByPoint(new Point(p.x - 1, i));
            if (color != Model.Color.TRANSPARENT) {
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
        //gameSetting.setHumanPlayers(humanPlayers);
        //gameSetting.setColorNumber(colorNumber);
        return gameSetting;
    }

    private static int getCurrentPlayerIndx(ChineseCheckers savedGame, List<Player> players) {
        int indx = 0;
        String curPlayerString = savedGame.getCurrentPlayer();

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(curPlayerString)) {
                indx = i;
            }
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

    private static Model.Board createGameBoard(Board board) {
        Model.Board gameBoard = new Model.Board();
        gameBoard.makeEmpty();
        List<Cell> cells = board.getCell();
        for (Cell cell : cells) {
            Model.Color color = createColorFromSaveGameColor(cell.getColor());
            Point point = createGamePoint(new Point(cell.getRow(), cell.getCol()), gameBoard);
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
            gameType = Type.Player;
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
        return new Point(savedTarget.getRow(),savedTarget.getCol());
    }

}
