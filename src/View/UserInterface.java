package View;

import Controller.gameOption;
import Model.Board;
import static Model.Board.COLS;
import static Model.Board.ROWS;
import Model.Color;
import Model.Player;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;

public class UserInterface {

    private final Scanner scanner = new Scanner(System.in);
    private boolean isRunning = true;

    public gameOption getGameOption() {
        System.out.println("Pick your option:");
        gameOption[] gameOpt = gameOption.values();

        for (int i = 0; i < gameOpt.length; i++) {
            System.out.println(i + "." + gameOpt[i].name());
        }

        return gameOpt[getValidChoice(gameOpt.length)];
    }

    public Point getStartPoint(Player curPlayer, ArrayList<Point> playerP) {
        greet(curPlayer);
        showPossiblePointsToPick(playerP);
        
        return getPointFromUser();
    }

    public void showPlayerAiMove(Point start, Point end) {

        System.out.format("Computer moved from (%s,%s) to (%s,%s) \n", start.x, start.y, end.x, end.y);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void clearPlayerPointsFromBoard(ArrayList<Point> pointsToRemove, Board board) {
        for (Point pointToRemove : pointsToRemove) {
            board.setColorByPoint(pointToRemove, Color.EMPTY);
        }
    }

    public void showPossiblePointsToPick(ArrayList<Point> playerPoints) {
        System.out.println("Your possible points to pick are:");
        for (int i = 0; i < playerPoints.size(); i++) {
            if (i % 5 == 0) {
                System.out.println("");
            }
            System.out.format("{%s,%s},", playerPoints.get(i).x, playerPoints.get(i).y);
        }
        System.out.println("");
    }

    public int getTotalPlayers() {
        boolean isValid = false;
        System.out.println("Enter the number of players 2-6");

        checkIntValidation(isValid);
        return scanner.nextInt();
    }

    public Point getEndPoint(Player curPlayer) {
        System.out.println("Where you want to go?\n ");

        return getPointFromUser();
    }

    public void printBoard(Board gameBoard) {
        printRows(gameBoard);
    }

    public void alertUserThatPlayerNameIsNotUinque(String playerName) {
        System.out.println("The name " + playerName + " already exists");
        System.out.println("Please pick another one: ");
    }

    public int isUserWannaQuit(String playerName) {
        boolean isValid = false;

        printPlayerChoices(playerName);
        checkIntValidation(isValid);

        return scanner.nextInt();
    }

    public int getColorNumberForEach(int totalPlayers, int howManyColorsForEach) {
        boolean isValid = false;

        System.out.println("Each user can pick up to " + howManyColorsForEach + " colors");
        System.out.println("Enter number of colors each player want to use");
        checkIntValidation(isValid);

        return scanner.nextInt();
    }

    public int getHumanPlayers(int totalPlayers) {
        boolean isValid = false;

        System.out.format("Enter the number of human players 0-%d%n", totalPlayers);
        checkIntValidation(isValid);

        return scanner.nextInt();
    }

    public String getName(int playerNumber) {
        boolean isValid = false;

        System.out.format("Please enter the %d' player name: ", playerNumber);
        checkStringValidation(isValid);

        return scanner.next();
    }

    private void checkStringValidation(boolean isValid) {
        while (!isValid) {
            if (scanner.hasNext()) {
                isValid = true;
            } else {
                scanner.next();
            }
        }
    }

    private Point getPointFromUser() {
        int rowNum = 0, colNum = 0;
        rowNum = getRowNumFromUser(rowNum);
        colNum = getColNumFromUser(colNum);

        return new Point(rowNum, colNum);
    }

    private int getColNumFromUser(int colNum) {
        boolean isValid = false;

        System.out.println("Enter col number: ");
        checkIntValidation(isValid);

        return scanner.nextInt();
    }

    private int getRowNumFromUser(int rowNum) {
        boolean isValid = false;

        System.out.println("Enter row number: ");
        checkIntValidation(isValid);

        return scanner.nextInt();
    }

    private void greet(Player curPlayer) {
        System.out.println("You are: " + curPlayer.getName() + "\nYour colors: ");
        curPlayer.getColors().forEach((curColor) -> System.out.print(curColor.name() + ", "));
        System.out.println("\nEnter the marble you want to play with:");
    }

    private void printRows(Board gameBoard) {
        for (int i = 0; i < ROWS; i++) {
            printRow(i, gameBoard);
        }
    }

    private void printRow(int i, Board gameBoard) {
        Color color;
        System.out.print("\u001B[30m");
        System.out.print((i + 1));
        if (i < 9) {
            System.out.print(" ");
        }

        for (int j = 0; j < COLS; j++) {
            color = gameBoard.getColorByPoint(new Point(i, j));
            char chToPrint = getCharByColor(color);
            printChar(chToPrint, color);
        }
        System.out.println("");
    }

    private char getCharByColor(Color color) {
        char ch;

        switch (color) {
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

    private void printChar(char chToPrint, Color color) {
        String coloredChar = getColoredChar(chToPrint, color);
        System.out.print(coloredChar);
    }

    private String getColoredChar(char chToPrint, Color color) {
        int colorValue = getColorValue(color);
        return "\u001B[" + colorValue + "m" + chToPrint;
    }

    private int getColorValue(Color color) {
        int value;
        switch (color) {
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

    private void printPlayerChoices(String playerName) {
        System.out.println(playerName + " what do you want to do now?");
        System.out.println("1. Play your turn");
        System.out.println("2. Save game");
        System.out.println("3. Quit");
    }

    private int getValidChoice(int gameOptLength) {
        int userChoice = -1;

        do {
            if (scanner.hasNextInt()) {
                userChoice = scanner.nextInt();
            }
        } while (userChoice < 0 && userChoice > gameOptLength);

        return userChoice;
    }

    private void checkIntValidation(boolean isValid) {
        while (!isValid) {
            if (scanner.hasNextInt()) {
                isValid = true;
            } else {
                scanner.next();
            }
        }
    }

    public int checkIfUserWantToSaveAsOrJustSave() {
        boolean isValid = false;

        showUserSaveOptions();
        checkIntValidation(isValid);

        return scanner.nextInt();
    }

    private void showUserSaveOptions() {
        System.out.println("What do you want to do?");
        System.out.println("1. Save As");
        System.out.println("2. Save");
    }

    public String getPathFromUser() {
        System.out.println("Please enter the path where you want your game to be saved/load");
        System.out.println("like: C:/ChineseCheckers/SavedGame/Chinese.xml");

        return scanner.next();
    }

    public void showExceptionToUser(Exception ex) {
        System.out.println(ex.getMessage());
    }

    public int checkIfUserWantToLoadGameOrPlayNewGame() {
        boolean isValid = false;

        showUserStartGameOptions();
        checkIntValidation(isValid);

        return scanner.nextInt();
    }

    private void showUserStartGameOptions() {
        System.out.println("What do you want to do?");
        System.out.println("1. Play New Game");
        System.out.println("2. Load Game");
    }
}
