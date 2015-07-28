package Model;

import java.util.*;

class Board {
    private static final int boardSize = 17;
    private final Cell[][] gameBoard = new Cell[boardSize][boardSize];
    
    public Board(int numberOfPlayers, int numberOfColorForPlayer) {
        initBoard(numberOfPlayers, numberOfColorForPlayer);
        printBoard();
    }

    private void initBoard(int numberOfPlayers, int numberOfColorForPlayer) {
        clearBoard();
        gameBoard[0][4].color = "c";
        gameBoard[1][4].color = "c";
        gameBoard[1][5].color = "c";
        gameBoard[2][4].color = "c";
        gameBoard[2][5].color = "c";
        gameBoard[2][6].color = "c";
        gameBoard[3][4].color = "c";
        gameBoard[3][5].color = "c";
        gameBoard[3][6].color = "c";
        gameBoard[3][7].color = "c";
        
        gameBoard[0][12].color = "b";
        gameBoard[1][12].color = "b";
        gameBoard[1][11].color = "b";
        gameBoard[2][12].color = "b";
        gameBoard[2][11].color = "b";
        gameBoard[2][10].color = "b";
        gameBoard[3][12].color = "b";
        gameBoard[3][11].color = "b";
        gameBoard[3][10].color = "b";
        gameBoard[3][9].color = "b";
        
        gameBoard[16][4].color = "r";
        gameBoard[15][4].color = "r";
        gameBoard[15][5].color = "r";
        gameBoard[14][4].color = "r";
        gameBoard[14][5].color = "r";
        gameBoard[14][6].color = "r";
        gameBoard[13][4].color = "r";
        gameBoard[13][5].color = "r";
        gameBoard[13][6].color = "r";
        gameBoard[13][7].color = "r";
        
        gameBoard[16][12].color = "g";
        gameBoard[15][12].color = "g";
        gameBoard[15][11].color = "g";
        gameBoard[14][12].color = "g";
        gameBoard[14][11].color = "g";
        gameBoard[14][10].color = "g";
        gameBoard[13][12].color = "g";
        gameBoard[13][11].color = "g";
        gameBoard[13][10].color = "g";
        gameBoard[13][9].color = "g";
        
        gameBoard[8][0].color = "p";
        gameBoard[7][1].color = "p";
        gameBoard[9][1].color = "p";
        gameBoard[6][2].color = "p";
        gameBoard[8][2].color = "p";
        gameBoard[10][2].color = "p";
        gameBoard[5][3].color = "p";
        gameBoard[7][3].color = "p";
        gameBoard[9][3].color = "p";
        gameBoard[11][3].color = "p";
        
        gameBoard[8][16].color = "y";
        gameBoard[7][15].color = "y";
        gameBoard[9][15].color = "y";
        gameBoard[6][14].color = "y";
        gameBoard[8][14].color = "y";
        gameBoard[10][14].color = "y";
        gameBoard[5][13].color = "y";
        gameBoard[7][13].color = "y";
        gameBoard[9][13].color = "y";
        gameBoard[11][13].color = "y";
        
    }

    private void clearBoard() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                gameBoard[row][col] = new Cell(row, col, " ");
            }
        }
    }

    private void printBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print(gameBoard[i][j].color);
                if (j == 16) {
                    System.out.println("\n");
                }
            }
            
        }
    }

    private static class Cell {
        
        private int row;
        private int col;
        private String color;

        private Cell(int row, int col, String color) {
            this.col = col;
            this.color = color;
            this.row = row;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}
