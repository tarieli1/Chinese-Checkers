package Model;

import java.util.*;

public class Logic {
    
    private Board gameBoard;
    private ArrayList<Player> players;
    private String currentPlayer;
    
    
    public Logic(Settings gameSettings) {
        gameBoard = new Board(6,1);
    }

    public static class Settings {
        private int totalPlayers;
        private int colorNumber;
        private int humanPlayers;
        private List playerNames;

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