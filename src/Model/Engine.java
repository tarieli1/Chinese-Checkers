package Model;

import static Model.Board.COLS;
import static Model.Board.ROWS;
import Model.Player.Type;
import java.awt.Point;
import java.util.*;
import javafx.util.Pair;

public class Engine {

    private Board gameBoard;
    private final ArrayList<Player> players = new ArrayList<>();
    private int currentPlayerIndx;
    private final Stack<Pair<Color, Color>> colorStack = new Stack<>();
    private final TargetMapper targetMap = new TargetMapper();

    public Engine(Settings gameSettings) {
        init(gameSettings);
    }

    private void init(Settings gameSettings) {
        currentPlayerIndx = 0;
        createGameObjects(gameSettings);
        initGameObjects(gameSettings);
        setPossibleMovesForPlayer(getCurrentPlayer());
    }

    private void createGameObjects(Settings gameSettings) {
        createPlayers(gameSettings);
        gameBoard = new Board();
        createTheColorStack();
    }

    private void createTheColorStack() {
        colorStack.clear();
        colorStack.add(new Pair(Color.RED, Color.BLACK));
        colorStack.add(new Pair(Color.WHITE, Color.BLUE));
        colorStack.add(new Pair(Color.GREEN, Color.YELLOW));
    }

    private void createPlayers(Settings gameSettings) {
        players.clear();
        List<String> playerNames = gameSettings.playerNames;
        int AINum = gameSettings.totalPlayers - gameSettings.humanPlayers;
        for (int i = 0; i < gameSettings.humanPlayers; i++) {
            players.add(new Player(playerNames.get(i), Type.Player));
        }
        //Create em AIs
        for (int i = 0; i < AINum; i++) {
            players.add(new Player("AI" + i, Type.COMPUTER));
        }
    }

    private void initGameObjects(Settings gameSettings) {
        clearPlayersPoints();
        initializeTargetMapper();
        initColorToPlayers(gameSettings);

        gameBoard.removeColors(toList(colorStack));
        initPointsToPlayers();
    }

    private void clearPlayersPoints() {
        for (Player player : players) {
            player.getColors().clear();
            player.getPoints().clear();
            player.getTargets().clear();
        }
    }

    private void initColorToPlayers(Settings gameSettings) {

        int numPlayer = gameSettings.totalPlayers;
        int colorsEach = gameSettings.colorNumber;
        Pair<Color, Color> targetsColors;
        Color firstColor, secondColor;
        Player firstPlayer, secondPlayer;

        for (int i = 0; i < numPlayer * colorsEach; i += 2) {
            targetsColors = colorStack.pop();
            firstColor = targetsColors.getKey();
            secondColor = targetsColors.getValue();
            int j = i % numPlayer;
            firstPlayer = players.get(j);
            j = (i + 1) % numPlayer;
            secondPlayer = players.get(j);

            firstPlayer.getColors().add(firstColor);
            firstPlayer.getTargets().add(targetMap.colorToVertex.get(secondColor));
            secondPlayer.getColors().add(secondColor);
            secondPlayer.getTargets().add(targetMap.colorToVertex.get(firstColor));

        }
    }

    private void initializeTargetMapper() {
        HashMap<Color, ArrayList<Point>> boardMap = new HashMap<>();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                addPointToMap(i, j, boardMap);
            }
        }
        updateTarget(boardMap);
    }

    void initPointsToPlayers() {
        Color color;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                color = gameBoard.getColorByPoint(new Point(i, j));
                addPointToPlayer(color, i, j);
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndx);
    }

    private void switchToNextPlayer() {
        currentPlayerIndx = (currentPlayerIndx + 1) % players.size();
        setPossibleMovesForPlayer(getCurrentPlayer());
    }

    private void setPossibleMovesForPlayer(Player player) {
        ArrayList<Point> points = player.getPoints();
        HashMap<Point, ArrayList<Point>> possibleMoves = new HashMap<>();

        for (Point point : points) {
            possibleMoves.put(point, getPossibleMoves(point));
        }
        player.setPossibleMoves(possibleMoves);
    }

    public void restart(Settings gameSettings) {
        init(gameSettings);
    }

    private void addPointToPlayer(Color color, int i, int j) {
        for (Player player : players) {
            if (player.getColors().contains(color)) {
                player.getPoints().add(new Point(i, j));
            }
        }
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public ArrayList<Point> getPossibleMovesForCurPlayerInPoint(Point start) {
        ArrayList<Point> result = null;
        Player curPlayer = getCurrentPlayer();
        HashMap<Point, ArrayList<Point>> possibleMoves = curPlayer.getPossibleMoves();
        if (possibleMoves.containsKey(start) && !possibleMoves.get(start).isEmpty()) {
            result = possibleMoves.get(start);
        }
        return result;
    }

    public boolean isPossibleMove(Point start, Point moveTo) {

        boolean result = false;
        Player curPlayer = getCurrentPlayer();
        HashMap<Point, ArrayList<Point>> possibleMoves = curPlayer.getPossibleMoves();
        if (possibleMoves.containsKey(start)) {
            if (possibleMoves.get(start).contains(moveTo)) {
                result = true;
            }
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
                    if (validPoint(nextPoint)) {
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
                if (color == Color.EMPTY && !possibleMoves.contains(nextPoint)) {
                    possibleMoves.add(nextPoint);
                }

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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Point> doAiIteration() {
        Player Ai = getCurrentPlayer();
        ArrayList<Point> result = new ArrayList<>();
        ArrayList<Point> startPoints = Ai.getPoints();
        HashMap<Point, ArrayList<Point>> possibleMoves = Ai.getPossibleMoves();
        Point randStartingPoint;
        do {
            randStartingPoint = getRandomPointInList(startPoints);
        } while (possibleMoves.get(randStartingPoint).isEmpty());

        Point start = randStartingPoint;
        Point end = getRandomPointInList(possibleMoves.get(start));
        doIteration(start, end);
        result.add(start);
        result.add(end);
        return result;
    }

    private Point getRandomPointInList(ArrayList<Point> startPoints) {
        Point randStartingPoint;
        int randStartingPointIdx = getRandomValidPointIndx(startPoints.size());
        randStartingPoint = startPoints.get(randStartingPointIdx);
        return randStartingPoint;
    }

    private int getRandomValidPointIndx(int size) {

        int min = 0;
        int max = size - 1;
        int range = (max - min) + 1;
        int randStartingPointIdx = (int) (Math.random() * range);
        return randStartingPointIdx;
    }

    private Point getNextPointByDirection(Point start, Direction direction) {
        Point p = new Point(start);
        switch (direction) {
            case BotLeft: {
                p.setLocation(start.x + 1, start.y - 1);
                break;
            }
            case BotRight: {
                p.setLocation(start.x + 1, start.y + 1);
                break;
            }
            case TopLeft: {
                p.setLocation(start.x - 1, start.y - 1);
                break;
            }
            case TopRight: {
                p.setLocation(start.x - 1, start.y + 1);
                break;
            }
            case Left: {
                p.setLocation(start.x, start.y - 2);
                break;
            }
            case Right: {
                p.setLocation(start.x, start.y + 2);
                break;
            }
        }
        return p;
    }

    private boolean validPoint(Point nextPoint) {
        boolean validRow = nextPoint.x < ROWS && nextPoint.x >= 0;
        boolean validCol = nextPoint.y < COLS && nextPoint.y >= 0;

        return validRow && validCol;
    }

    public boolean isMarble(Color color) {
        return (color != Color.EMPTY && color != Color.TRANSPARENT);
    }

    public void removePlayer(Player curPlayer) {
        players.remove(curPlayer);
    }

    void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

    void setCurrentPlayerIndx(int currentPlayerIndx) {
        this.currentPlayerIndx = currentPlayerIndx;
    }

    public boolean userQuited(Player quitedPlayer) {
        for (Point pointToRemove : quitedPlayer.getPoints()) {
            gameBoard.setColorByPoint(pointToRemove, Color.EMPTY);
        }
        players.remove(quitedPlayer);
        setPossibleMovesForPlayer(getCurrentPlayer());

        return players.size() == 1;
    }

    private ArrayList<Color> toList(Stack<Pair<Color, Color>> colorStack) {
        ArrayList<Color> colorsInStack = new ArrayList<>();
        for (Pair<Color, Color> pair : colorStack) {
            colorsInStack.add(pair.getKey());
            colorsInStack.add(pair.getValue());
        }
        return colorsInStack;
    }

    private void updateTarget(HashMap<Color, ArrayList<Point>> boardMap) {
        Set<Color> colors = boardMap.keySet();
        for (Color color : colors) {
            targetMap.updateTargetMap(color, boardMap.get(color),gameBoard);
        }
    }

    private void addPointToMap(int i, int j, HashMap<Color, ArrayList<Point>> boardMap) {
        Point curPoint = new Point(i, j);
        Color color = gameBoard.getColorByPoint(curPoint);
        if (isMarble(color)){
            
            if (boardMap.get(color) == null) {
                boardMap.put(color, new ArrayList<>());}
            
            boardMap.get(color).add(curPoint);
            
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
