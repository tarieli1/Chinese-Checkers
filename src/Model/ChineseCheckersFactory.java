/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Model.Player.Type;
import generatedClasses.Board;
import generatedClasses.Cell;
import generatedClasses.ChineseCheckers;
import generatedClasses.Color;
import generatedClasses.ColorType;
import generatedClasses.PlayerType;
import generatedClasses.Players;
import java.awt.Point;
import java.util.List;


public abstract class ChineseCheckersFactory {
    
    public static ChineseCheckers createSavedGameObject(Engine engine) {
        ChineseCheckers savedGame = new ChineseCheckers();
        savedGame.setCurrentPlayer(engine.getCurrentPlayer().getName());
        savedGame.setPlayers(createSavedGamePlayersList(engine));
        savedGame.setBoard(createSaveGameBoard(engine));
        return savedGame;
    }
    
    public static Point createSavedPoint(Point p, Engine engine) {
        int counter = 0;
        for (int i = 0; i <= p.y; i++) {
            Model.Color color = engine.getGameBoard().getColorByPoint(new Point(p.x, i));
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
        for (Model.Color color : player.getColors()) 
            colorList.add(createSavedGameColorType(color));
        
        return savedPlayer;
    }

    private static  void convertTargetToSaveGameTarget(ColorType savedColor) {
        ColorType.Target tgt = savedColor.getTarget();
        tgt.setCol(1);//TODO
        tgt.setRow(2);//TODO
    }

    private static  Color createSavedColorFromColor(Model.Color color) {
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

    private static ColorType createSavedGameColorType(Model.Color color) {
        ColorType savedColor = new ColorType();
        savedColor.setColor(createSavedColorFromColor(color));
        convertTargetToSaveGameTarget(savedColor);
        return savedColor;
    }

    private static PlayerType createSavedGameType(Type type) {
        if (type == Player.Type.COMPUTER) {
            return PlayerType.COMPUTER;
        } else {
            return PlayerType.HUMAN;
        }
    }

    private static Cell createSavedCell(int i, int j, Model.Color curColor,Engine engine){
        Cell savedCell = new Cell();
        Point savedPoint = createSavedPoint(new Point(i, j),engine);
        savedCell.setRow(savedPoint.x);
        savedCell.setCol(savedPoint.y);
        savedCell.setColor(createSavedColorFromColor(curColor));
        return savedCell;
    }

    private static Board createSaveGameBoard(Engine engine) {
        Board savedBoard = new Board();
        List<Cell> savedCellsList = savedBoard.getCell();
        for (int i = 0; i < Model.Board.ROWS; i++) {
            for (int j = 0; j < Model.Board.COLS; j++) {
                Model.Color curColor = engine.getGameBoard().getColorByPoint(new Point(i, j));
                if (engine.isMarble(curColor)) {
                    savedCellsList.add(createSavedCell(i, j, curColor,engine));
                }
            }
        }
        return savedBoard;
    }
    
}
