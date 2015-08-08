/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import generatedClasses.Board;
import generatedClasses.ChineseCheckers;
import generatedClasses.Players;
import java.awt.Point;
import java.util.List;


public abstract class EngineFactory {
    
    
    public static Engine createEngine(ChineseCheckers savedGame)
    {
        Engine.Settings savedGameSettings = createGameSettings(savedGame);
        Engine engine = new Engine(savedGameSettings);
        engine.setCurrentPlayerIndx(getCurrentPlayerIndx(savedGame));
        engine.setGameBoard(createGameBoard(savedGame.getBoard()));
        
        return engine;
    }
    
    public static Point createGamePoint(Point p, Engine engine) {
        int counter = 0;
        for (int i = p.y; i >= 0; i--) {
            Model.Color color = engine.getGameBoard().getColorByPoint(new Point(p.x, i));
            if (color != Model.Color.TRANSPARENT) {
                counter++;
            }
        }
        return new Point(p.x + 1, counter);
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

    private static int getCurrentPlayerIndx(ChineseCheckers savedGame) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static List createNamesList(List<Players.Player> players) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static Model.Board createGameBoard(Board board) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
