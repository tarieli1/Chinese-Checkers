package View;

import Controller.gameOption;
import Model.Board;
import static Model.Board.COLS;
import static Model.Board.ROWS;
import Model.Color;
import Model.Player;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    
    private final Scanner scanner = new Scanner(System.in);
    private boolean isRunning = true;
    
    public int getTotalPlayers() {
        System.out.println("Enter the number of players 2-6");
        return scanner.nextInt();
    }

    public int getColorNumberForEach(int totalPlayers) {
        System.out.println("Enter number of colors each player want to use");
        return scanner.nextInt();
    }

    public int getHumanPlayers(int totalPlayers) {
        System.out.format(
                "Enter the number of human players 0-%d%n", totalPlayers);
        return scanner.nextInt();
    }

    public List getNames(int totalPlayers) {
        ArrayList<String> playerNames = new ArrayList<>(totalPlayers);
        for (int i = 1; i <= totalPlayers; i++) {
            System.out.format("Please enter the %d' player name: ", i);
            playerNames.add(scanner.next());
        }
        
        return playerNames;
    }

    public gameOption getGameOption() {
        System.out.println("Pick your option:");
        gameOption[] gameOpt = gameOption.values();
        
        for (int i = 0; i < gameOpt.length; i++) 
            System.out.println(i + "." + gameOpt[i].name());
        
        return gameOpt[getValidChoice(gameOpt.length)];
    }

    public Point getStartPoint(Player curPlayer) {
        greet(curPlayer);
        
        return getPointFromUser();
    }
    
    private Point getPointFromUser(){
        System.out.println("Enter row number: ");
        int rowNum = scanner.nextInt();
        System.out.println("Enter col number: ");
        int colNum = scanner.nextInt();
        return new Point(rowNum,colNum);
    } 

    private void greet(Player curPlayer) {
        System.out.println("You are: " + curPlayer.getName() + "\nYour colors: ");
        
        curPlayer.getColors().forEach((curColor)-> System.out.print(curColor.name() + ", "));
        
        System.out.println("\nEnter the marble you want to play with:(G5)\n");        
    }

    public void showPlayerAiMove(Point start, Point end) {
        
        System.out.format("Computer moved from (%s,%s) to (%s,%s) \n", start.x,start.y,end.x,end.y);
    }

    public boolean isRunning() {
        return isRunning;
    }

    private int getValidChoice(int gameOptLength) {
        int userChoice = -1;
        
        do {
            if(scanner.hasNextInt())
                userChoice = scanner.nextInt();
           } while (userChoice < 0 && userChoice > gameOptLength);
        
        return userChoice;       
    }

    public Point getEndPoint(Player curPlayer) {
        System.out.println("Where you want to go?(8G)\n ");
        return getPointFromUser();

    }
    
    public void printBoard(Board gameBoard) {
        printNavigationLetters();
        printRows(gameBoard);
    }

    private void printNavigationLetters() {
        System.out.print("  ");
        for (int i = 0; i < COLS; i++) {
            System.out.print((char)('A' +i));
        }
        System.out.println("");
    }

    private void printRows(Board gameBoard) {
        for (int i = 0; i < ROWS; i++) 
            printRow(i, gameBoard);
    }

    private void printRow(int i, Board gameBoard) {
        Color color;
        System.out.print("\u001B[30m");
        System.out.print((i+1));
        if(i < 9)
            System.out.print(" ");

        for (int j = 0; j < COLS; j++) {
            color = gameBoard.getColorByPoint(new Point(i, j));
            char chToPrint = getCharByColor(color);
            printChar(chToPrint,color);
        }
        System.out.println("");
    }

    private char getCharByColor(Color color) {
        char ch;
        
        switch(color)
        {
            case TRANSPARENT:
                ch = ' ';
                break;
            case EMPTY:
                ch = '\u25cb';//Empty circle
                break;
            default:
                ch = '\u25cf';//Full circle
                break;
        }
        return ch;
    }

    private void printChar(char chToPrint,Color color) {
        String coloredChar = getColoredChar(chToPrint,color);
        System.out.print(coloredChar);
    }

    private String getColoredChar(char chToPrint, Color color) {
        int colorValue = getColorValue(color);
        return "\u001B[" + colorValue + "m" + chToPrint;
    }

    private int getColorValue(Color color) {
        int value;
        switch(color)
        {
            case BLUE:
                value = 34;
                break;
            case WHITE:
                value = 37;
                break;
            case GREEN:
                value = 32;
                break;
            case YELLOW:
                value = 33;
                break;
            case RED:
                value = 31;
                break;
            default:
                value = 30;//no color
                break;
        }
        return value;
    }

    public void showPossibleMoves(ArrayList<Point> possibleMoves) {
        System.out.println("Your possible moves are:");
        possibleMoves.forEach((point)->System.out.format("{%s,%s},",point.x,point.y));
        System.out.println("");
    }


}
