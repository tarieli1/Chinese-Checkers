package Model;

import Model.Player.Type;
import generatedClasses.Board;
import generatedClasses.Cell;
import generatedClasses.ChineseCheckers;
import generatedClasses.Color;
import generatedClasses.ColorType;
import generatedClasses.ColorType.Target;
import generatedClasses.PlayerType;
import generatedClasses.Players;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class ChineseCheckersFactory {

    public static ChineseCheckers createSavedGameObject(Engine engine) {
        ChineseCheckers savedGame = new ChineseCheckers();
        savedGame.setCurrentPlayer(engine.getCurrentPlayer().getName());
        savedGame.setPlayers(createSavedGamePlayersList(engine));
        savedGame.setBoard(createSaveGameBoard(engine));
        return savedGame;
    }

    public static Point createSavedGamePoint(Point p, Model.Board board) {
        int counter = 0;
        for (int i = 0; i <= p.y; i++) {
            Model.Color color = board.getColorByPoint(new Point(p.x, i));
            if (color != Model.Color.TRANSPARENT) {
                counter++;
            }
        }
        return new Point(p.x + 1, counter);
    }

    private static Players createSavedGamePlayersList(Engine engine) {
        Players playersObj = new Players();
        List<Players.Player> playersList = playersObj.getPlayer();
        for (Player player : engine.getPlayers()) {
            playersList.add(convertGamePlayerToSaveGamePlayer(player));
        }
        return playersObj;
    }

    private static Players.Player convertGamePlayerToSaveGamePlayer(Player player) {
        Players.Player savedPlayer = new Players.Player();
        savedPlayer.setName(player.getName());
        savedPlayer.setType(createSavedGameType(player.getType()));
        List<ColorType> colorList = savedPlayer.getColorDef();
        ArrayList<Point> targets = player.getTargets();
        ArrayList<Model.Color> colors = player.getColors();

        for (int i = 0; i < player.getColors().size(); i++) {
            Model.Color color = colors.get(i);
            Point target = targets.get(i);
            colorList.add(createSavedGameColorType(color, target));
        }

        return savedPlayer;
    }

    private static Color createSavedColorFromColor(Model.Color color) {
        Color res = null;
        switch (color) {
            case BLACK:
                res = Color.BLACK;
                break;
            case BLUE:
                res = Color.BLUE;
                break;
            case GREEN:
                res = Color.GREEN;
                break;
            case RED:
                res = Color.RED;
                break;
            case WHITE:
                res = Color.WHITE;
                break;
            case YELLOW:
                res = Color.YELLOW;
                break;
        }
        return res;
    }

    private static ColorType createSavedGameColorType(Model.Color color, Point target) {
        ColorType savedColor = new ColorType();
        savedColor.setTarget(createSavedGameTarget(target));
        savedColor.setColor(createSavedColorFromColor(color));

        return savedColor;
    }

    private static Target createSavedGameTarget(Point target) {
        Target savedGameTarget = new Target();

        savedGameTarget.setCol(target.y);//TODO
        savedGameTarget.setRow(target.x);//TODO
        return savedGameTarget;
    }

    private static PlayerType createSavedGameType(Type type) {
        if (type == Player.Type.COMPUTER) {
            return PlayerType.COMPUTER;
        } else {
            return PlayerType.HUMAN;
        }
    }

    private static Cell createSavedCell(int i, int j, Model.Color curColor, Model.Board board) {
        Cell savedCell = new Cell();
        Point savedPoint = createSavedGamePoint(new Point(i, j), board);
        savedCell.setRow(savedPoint.x);
        savedCell.setCol(savedPoint.y);
        savedCell.setColor(createSavedColorFromColor(curColor));
        return savedCell;
    }

    private static Board createSaveGameBoard(Engine engine) {
        Board savedBoard = new Board();
        Model.Board gameBoard = engine.getGameBoard();
        List<Cell> savedCellsList = savedBoard.getCell();
        for (int i = 0; i < Model.Board.ROWS; i++) {
            for (int j = 0; j < Model.Board.COLS; j++) {
                Model.Color curColor = gameBoard.getColorByPoint(new Point(i, j));
                if (engine.isMarble(curColor)) {
                    savedCellsList.add(createSavedCell(i, j, curColor, gameBoard));
                }
            }
        }
        return savedBoard;
    }

}
