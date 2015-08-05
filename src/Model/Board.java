package Model;

import java.util.*;

 class Board {
    private static final int COLS = 25;
    private static final int ROWS = 17;
    private static final char EMPTY = 'E';
    private static final char END = ' ';
    
    private Cell[][] gameBoard;

    public Cell[][] getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(Cell[][] gameBoard) {
        this.gameBoard = gameBoard;
    }
    
    public Board(){
       gameBoard = createFullBoard();   
    }

    private Cell[][] createFullBoard() {
        Cell[][] board = null;
        try{
           ArrayList<String> boardLines =  FileManager.readLinesFromFile("TODO Path");
           board = createBoardFromLines(boardLines);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return board;
      
    }

    private void printBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                //todo                
            }
        }
    }

    private Cell[][] createBoardFromLines(ArrayList<String> boardLines) {
        Cell[][] board = new Cell[ROWS][];
        for (int i = 0; i < ROWS; i++) {
            board[i] = getCellsFromString(boardLines.get(i));
        }
        return board;
    }

    private Cell[] getCellsFromString(String line) {
        Cell[] cellLine = new Cell[COLS];
        int[] charValues = line.chars().toArray();
        for (int j = 0; j < charValues.length; j++) {
            cellLine[j] = new Cell(charValues[j]);
        }
        return cellLine;
    }

    private static class Cell {
        
        private Color color;

        private Cell(int charValue) {
            this.color = getColorByChar(charValue);
        }

        private Color getColorByChar(int charValue) {
            Color myColor = null;
            switch(charValue)
            {
                case 'G':
                    myColor = Color.GREEN;
                    break;
                case 'R':
                    myColor = Color.RED;
                    break;
                case 'B':
                    myColor = Color.BLUE;
                    break;
                case 'Y':
                    myColor = Color.YELLOW;
                    break;
                case 'W':
                    myColor = Color.WHITE;
                    break;
                case 'K':
                    myColor = Color.BLACK;
                    break;
                case EMPTY:
                    myColor = Color.EMPTY;
                case END:
                    myColor = Color.TRANSPARENT;
            }
         return myColor;   
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }
}
