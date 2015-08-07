package Model;

import static Model.Board.COLS;
import static Model.Board.ROWS;
import Model.Player.Type;
import java.awt.Point;
import java.util.*;

public class Engine {
    
    private Board gameBoard;
    private final ArrayList<Player> players = new ArrayList<>();
    private int currentPlayerIndx = 0;
    private final Stack<Color> colorStack = new Stack<>();
    
    public Engine(Settings gameSettings) {
        init(gameSettings);
    }

    private void init(Settings gameSettings) {
        createGameObjects(gameSettings);
        initGameObjects(gameSettings);
        setPossibleMovesForPlayer(getCurrentPlayer());
    }

    private void createGameObjects(Settings gameSettings) {
        createPlayers(gameSettings.totalPlayers, gameSettings.playerNames);
        gameBoard = new Board();
        createTheColorStack();
    }
    
    private void createPlayers(int totalPlayers, List<String> playerNames) {
        players.clear();
        int AINum = totalPlayers - playerNames.size();
        
        playerNames.forEach((name)->players.add(new Player(name,Type.Player)));
        //Create em AIs
        for (int i = 0; i < AINum; i++)
            players.add(new Player("AI" + i,Type.Computer));        
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndx);
    }

    private void switchToNextPlayer(){
        currentPlayerIndx = (currentPlayerIndx + 1) % players.size();
        setPossibleMovesForPlayer(getCurrentPlayer());
    }

    private void setPossibleMovesForPlayer(Player player) {
        ArrayList<Point> points = player.getPoints();
        HashMap<Point,ArrayList<Point>> possibleMoves = new HashMap<>();
        
        for (Point point : points) 
            possibleMoves.put(point, getPossibleMoves(point));      
        player.setPossibleMoves(possibleMoves);
    }

    public void restart(Settings gameSettings) {
        init(gameSettings);
    }
    
    private void initGameObjects(Settings gameSettings) {    
        clearPlayersPoints();
        initColorToPlayers(gameSettings);
        gameBoard.removeColors(colorStack);
        initPointsToPlayer();
    }

    private void initColorToPlayers(Settings gameSettings) {
        for (int i = 0; i < gameSettings.colorNumber; i++) {
            for (int j = 0; j < gameSettings.totalPlayers; j++) {
                Color color = colorStack.pop();
                players.get(j).getColors().add(color);
            }
        }
    }
    
    private void initPointsToPlayer() {
        Color color;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                color = gameBoard.getColorByPoint(new Point(i, j));
                addPointToPlayer(color, i, j);
            }
        }
    }

    private void addPointToPlayer(Color color, int i, int j) {
        for (Player player : players)
            if (player.getColors().contains(color))
                player.getPoints().add(new Point(i, j));
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public ArrayList<Point> getPossibleMovesForCurPlayerInPoint(Point start) {
        ArrayList<Point> result = null;
        Player curPlayer = getCurrentPlayer();
        HashMap<Point,ArrayList<Point>> possibleMoves = curPlayer.getPossibleMoves();
        if(possibleMoves.containsKey(start) && !possibleMoves.get(start).isEmpty())
            result = possibleMoves.get(start);
        return result;
    }

    public boolean isPossibleMove(Point start, Point moveTo) {
        
        boolean result = false;
        Player curPlayer = getCurrentPlayer();
        HashMap<Point,ArrayList<Point>> possibleMoves = curPlayer.getPossibleMoves();
        if(possibleMoves.containsKey(start))
            if (possibleMoves.get(start).contains(moveTo)) {
                result = true;
            }
        return result;
    }
    
    public ArrayList<Point> getPossibleMoves(Point start) {
        
        ArrayList<Point> possibleMoves = new ArrayList<>();
        
        possibleMoveHelperJumper(start, possibleMoves);
        possibleMoveHelperSingle(start, possibleMoves);
        return possibleMoves;
     }

    private void possibleMoveHelperJumper(Point start, ArrayList<Point> possibleMoves) {      
        Direction[] directions = Direction.values();  
        for (Direction direction : directions) {
            Point nextPoint = getNextPointByDirection(start, direction);
            if (validPoint(nextPoint)) {
                Color color = gameBoard.getColorByPoint(nextPoint);       
                if (isMarble(color)) {
                    nextPoint = getNextPointByDirection(nextPoint, direction);
                    if(validPoint(nextPoint)){
                        color = gameBoard.getColorByPoint(nextPoint); 
                        if (color == Color.EMPTY && !possibleMoves.contains(nextPoint)) {
                            possibleMoves.add(nextPoint);
                            possibleMoveHelperJumper(nextPoint, possibleMoves);
                        }
                    }
                }
            }
        }
    }
   
    private void possibleMoveHelperSingle(Point start, ArrayList<Point> possibleMoves) {
        Direction[] directions = Direction.values();  
        for (Direction direction : directions) {
            Point nextPoint = getNextPointByDirection(start, direction);
            if (validPoint(nextPoint)) {           
                Color color = gameBoard.getColorByPoint(nextPoint); 
                if (color == Color.EMPTY && !possibleMoves.contains(nextPoint)) 
                    possibleMoves.add(nextPoint);
            
            }
        }
    }
        
    public void doIteration(Point start, Point end) {
        updateGameBoard(start, end);
        updatePlayerPoints(start, end);
        switchToNextPlayer();
    }

    private void updateGameBoard(Point start, Point end) {
        Color color = gameBoard.getColorByPoint(start);
        gameBoard.setColorByPoint(end, color);
        gameBoard.setColorByPoint(start, Color.EMPTY);
    }

    private void updatePlayerPoints(Point start, Point end) {
        Player player = getCurrentPlayer();
        player.getPoints().remove(start);
        player.getPoints().add(end);
    }

    public boolean isGameOver() {
        return false;//TODO
    }

    public ArrayList<Point> doAiIteration() {
        Player Ai = getCurrentPlayer();
        ArrayList<Point> result = new ArrayList<>();
        ArrayList<Point> startPoints = Ai.getPoints();
        HashMap<Point,ArrayList<Point>> possibleMoves = Ai.getPossibleMoves();
        Point randStartingPoint;
        do {
            randStartingPoint = getRandomPointInList(startPoints);
        } while(possibleMoves.get(randStartingPoint).isEmpty());
        
        Point start = randStartingPoint;
        Point end = getRandomPointInList(possibleMoves.get(start));
        doIteration(start, end);
        result.add(start);
        result.add(end);
        return result;
    }

    private Point getRandomPointInList(ArrayList<Point> startPoints) {
        Point randStartingPoint;
        int randStartingPointIdx = (int) (Math.random()*999 % startPoints.size());
        randStartingPoint = startPoints.get(randStartingPointIdx);
        return randStartingPoint;
    }

    private Point getNextPointByDirection(Point start, Direction direction) {
        Point p = new Point(start);
        switch(direction)
        {
            case BotLeft:
            {
                p.setLocation(start.x + 1, start.y - 1);
                break;
            }
            case BotRight:
            {
                p.setLocation(start.x + 1, start.y + 1);
                break;
            }
            case TopLeft:
            {
                p.setLocation(start.x - 1, start.y - 1);
                break;
            }
            case TopRight:
            {
                p.setLocation(start.x - 1, start.y + 1);
                break;
            }
            case Left:
            {
                p.setLocation(start.x, start.y - 2);
                break;
            }
            case Right:
            {
                p.setLocation(start.x, start.y + 2);
                break;
            }
        }   
        return p;
    }

    private void createTheColorStack() {
        colorStack.clear();
        colorStack.add(Color.RED);
        colorStack.add(Color.BLACK);
        colorStack.add(Color.BLUE);
        colorStack.add(Color.WHITE);
        colorStack.add(Color.GREEN);
        colorStack.add(Color.YELLOW);       
    }

    private void clearPlayersPoints() {
        for (Player player : players) {
            player.getColors().clear();
            player.getPoints().clear();
        }
    }

    private boolean validPoint(Point nextPoint) {
        boolean validRow = nextPoint.x < ROWS && nextPoint.x >= 0;
        boolean validCol = nextPoint.y < COLS && nextPoint.y >= 0;
        
        return validRow && validCol;
    }

    private boolean isMarble(Color color) {
        return  (color != Color.EMPTY && color != Color.TRANSPARENT);
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