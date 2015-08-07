package Controller;
import static Model.Board.COLS;
import Model.Color;
import Model.Engine;
import Model.Player;
import Model.Player.Type;
import View.UserInterface;
import java.awt.Point;
import java.util.*;

public class Game {
    
    private Engine gameLogic;
    private final UserInterface UI = new UserInterface();
    
    public void Run(){
        Engine.Settings gameSettings = getGameSettings();
        gameLogic = new Engine(gameSettings);
        playGame(gameSettings);
    }

    private void playGame(Engine.Settings gameSettings) {
        play();      
        newGame(gameSettings);
    }
    
    private void play() {
        boolean isGameOver = false,isRunning = true;
        
        while(isRunning && !isGameOver)
        {
            isGameOver = gameLogic.isGameOver();
            isRunning = doIteration();
        }
    }

    private void newGame(Engine.Settings gameSettings) {
        gameOption option = UI.getGameOption();
        switch(option)
        {
            case NewGame:
                gameSettings = getGameSettings();
            case Restart:
                gameLogic.restart(gameSettings);
                playGame(gameSettings);
                break;
            default:
                break;
        }
    }
    
    private Engine.Settings getGameSettings() {
        int totalPlayers = UI.getTotalPlayers();
        Engine.Settings gameSettings = initSettings(totalPlayers);
        gameSettings.setTotalPlayers(totalPlayers);

        return gameSettings;
    }

    private Engine.Settings initSettings(int totalPlayers) {
        Engine.Settings gameSettings = new Engine.Settings();
        initColorNumber(totalPlayers, gameSettings);
        initHumanPlayers(totalPlayers, gameSettings);
        initPlayerNames(gameSettings);
        
        return gameSettings;
    }

    private void initPlayerNames(Engine.Settings gameSettings) {
        List playerNames = UI.getNames(gameSettings.getHumanPlayers());
        gameSettings.setPlayerNames(playerNames);
    }

    private void initHumanPlayers(int totalPlayers, Engine.Settings gameSettings) {
        int humanPlayers = UI.getHumanPlayers(totalPlayers);
        gameSettings.setHumanPlayers(humanPlayers);
    }

    private void initColorNumber(int totalPlayers, Engine.Settings gameSettings) {
        int colorNumber = UI.getColorNumberForEach(totalPlayers);
        gameSettings.setColorNumber(colorNumber);
    }

    private boolean doIteration() {
        UI.printBoard(gameLogic.getGameBoard());
        //gameLogic.doIteration(new Point(9,3) , new Point(8,4));
        //UI.printBoard(gameLogic.getGameBoard());
    
    
        Player curPlayer = gameLogic.getCurrentPlayer();
        if(curPlayer.getType() == Type.Computer)
             doAiIteration();
        else
             doPlayerIteration(curPlayer); 
        return UI.isRunning();
    }

    private void doAiIteration() {
        Point start = null,end = null;
        ArrayList<Point> usedPoints;
        usedPoints = gameLogic.doAiIteration();
        start = convertBoardPointToPoint(usedPoints.get(0));
        end = convertBoardPointToPoint(usedPoints.get(1));
        UI.showPlayerAiMove(start,end);
    }

    private void doPlayerIteration(Player curPlayer) {
        Point start,end;
        
        start = getValidStartingPoint(curPlayer);    
        if(UI.isRunning()){
                end = getValidEndPoint(start,curPlayer);
                gameLogic.doIteration(start,end);
        } 
        
    }

    private Point getValidStartingPoint(Player curPlayer) {
        Point start;
        ArrayList<Point> possibleMoves;
        do {
            Point playerStart = UI.getStartPoint(curPlayer);
            start = convertPointToBoardPoint(playerStart);
             possibleMoves = gameLogic.getPossibleMovesForCurPlayerInPoint(start);
        } while( possibleMoves == null);
        possibleMoves = convertBoardPointsToPoints(possibleMoves);
        UI.showPossibleMoves(possibleMoves); 
        return start;
    }

    private Point getValidEndPoint(Point start,Player curPlayer) {
        Point end;
        do {
            Point playerEnd = UI.getEndPoint(curPlayer);
            end = convertPointToBoardPoint(playerEnd);
        } while(!gameLogic.isPossibleMove(start,end));
        return end;
    }
    
    private ArrayList<Point> convertBoardPointsToPoints(ArrayList<Point> possibleMoves) {
        ArrayList<Point> result = new ArrayList<>();
        for (Point possibleMove : possibleMoves) {
            result.add(convertBoardPointToPoint(possibleMove));
            
        }
        return result;
    }

    private Point convertPointToBoardPoint(Point p){
        int counter = 0;
        Point res = null;
        for (int i = 0; i < COLS; i++) {
            Color color = gameLogic.getGameBoard().getColorByPoint(new Point(p.x-1,i));
            if (color != Color.TRANSPARENT) 
                counter++;
            if (counter == p.y){
                res = new Point(p.x - 1,i);       
                break;
            }
        }
        return res;
    }
    
    private Point convertBoardPointToPoint(Point p) {
        int counter = 0;
        for (int i = p.y; i >= 0; i--) {
            Color color = gameLogic.getGameBoard().getColorByPoint(new Point(p.x,i));
            if (color != Color.TRANSPARENT) 
                counter++;
        }
        return new Point(p.x + 1,counter);
    }
}
