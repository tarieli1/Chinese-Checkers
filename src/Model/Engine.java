package Model;

import Model.Player.Type;
import java.awt.Point;
import java.util.*;

public class Engine {
    
    private Board gameBoard;
    private ArrayList<Player> players;
    private int currentPlayerIndx = 0;
    private final Map<Color,ArrayList<Point>> colorMap = new HashMap();
    
    public Engine(Settings gameSettings) {
        init(gameSettings);
    }

    private void init(Settings gameSettings) {
        createGameObjects(gameSettings);
        initGameObjects(gameSettings);
    }

    private void createGameObjects(Settings gameSettings) {
        createPlayers(gameSettings.totalPlayers, gameSettings.playerNames);
        gameBoard = new Board();
    }
    
    private void createPlayers(int totalPlayers, List<String> playerNames) {
        players.clear();
        int AINum = totalPlayers - playerNames.size();
        
        playerNames.forEach((name)->players.add(new Player(name,Type.Player)));
        //Create em AIs
        for (int i = 0; i < AINum; i++)
            players.add(new Player("AI" + i,Type.Computer));        
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndx);
    }

    private void nextPlayer(){
        currentPlayerIndx = (currentPlayerIndx + 1) % players.size();
    }

    public boolean doIteration() {
        boolean isRunning = true;
        Player curPlayer = getCurrentPlayer();
        
        if(curPlayer.getType() == Type.Computer)
            isRunning = doAiIteration();
        else
            isRunning = doPlayerIteration();
        
        return isRunning;
    }

    public void restart(Settings gameSettings) {
        init(gameSettings);
    }

    private boolean doAiIteration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean doPlayerIteration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void initGameObjects(Settings gameSettings) {
        for (int i = 0; i < 10; i++) {
            
        }
    }
    
    public static class Settings {
        private int totalPlayers;
        private int colorNumber;
        private int humanPlayers;
        private List<String> playerNames;

        public int getTotalPlayers() {
            return totalPlayers;
        }

        public void setTotalPlayers(int totalPlayers) {
            this.totalPlayers = totalPlayers;
        }

        public int getColorNumber() {
            return colorNumber;
        }

        public void setColorNumber(int colorNumber) {
            this.colorNumber = colorNumber;
        }

        public int getHumanPlayers() {
            return humanPlayers;
        }

        public void setHumanPlayers(int humanPlayers) {
            this.humanPlayers = humanPlayers;
        }

        public List getPlayerNames() {
            return playerNames;
        }

        public void setPlayerNames(List playerNames) {
            this.playerNames = playerNames;
        }
        
    }
    
    
}