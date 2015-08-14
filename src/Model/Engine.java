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

    public TargetMapper getTargetMap() {
        return targetMap;
    }

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

    private void createPlayers(Settings gameSettings) {
        players.clear();
        List<String> playerNames = gameSettings.playerNames;
        int AINum = gameSettings.totalPlayers - gameSettings.humanPlayers;
        for (int i = 0; i < gameSettings.humanPlayers; i++) {
            players.add(new Player(playerNames.get(i), Type.PLAYER));
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

        for (int i = 0; i < colorsEach; i++) {
            for (int j = 0; j < numPlayer; j++) {
                initColorToPlayer(j);
            }
        }
    }

    private void initColorToPlayer(int j) {
        Player curPlayer = players.get(j);
        Pair<Color, Color> colorAndTarget = colorStack.pop();
        Color playerColor = colorAndTarget.getValue();
        Color playerTargetColor = colorAndTarget.getKey();
        curPlayer.getColors().add(playerColor);
        curPlayer.getTargets().add(targetMap.getColorToVertex().get(playerTargetColor));
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

    void setPossibleMovesForPlayer(Player player) {
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

    private void createTheColorStack() {
        colorStack.clear();
        colorStack.add(new Pair(Color.RED, Color.BLACK));
        colorStack.add(new Pair(Color.BLACK, Color.RED));
        colorStack.add(new Pair(Color.WHITE, Color.BLUE));
        colorStack.add(new Pair(Color.BLUE, Color.WHITE));
        colorStack.add(new Pair(Color.GREEN, Color.YELLOW));
        colorStack.add(new Pair(Color.YELLOW, Color.GREEN));
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

    public boolean doIteration(Point start, Point end) {
        boolean isGameOver;
        updateGameBoard(start, end);
        isGameOver = updatePlayerPoints(start, end);

        if (!isGameOver) {
            switchToNextPlayer();
        }
        return isGameOver;
    }

    private void updateGameBoard(Point start, Point end) {
        Color color = gameBoard.getColorByPoint(start);
        gameBoard.setColorByPoint(end, color);
        gameBoard.setColorByPoint(start, Color.EMPTY);
    }

    private boolean updatePlayerPoints(Point start, Point end) {
        Player player = getCurrentPlayer();
        player.getPoints().remove(start);
        player.getPoints().add(end);
        return checkIfGameOver(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Pair<Boolean,ArrayList<Point>> doAiIteration() {
        
        Player Ai = getCurrentPlayer();
        ArrayList<Point> aiMove = new ArrayList<>();

        HashMap<Point, ArrayList<Point>> possibleMoves = Ai.getPossibleMoves();
        Pair<Point, Point> bestMove = getBestAIMove(possibleMoves);

        Point start = bestMove.getKey();
        Point end = bestMove.getValue();
        Boolean res = doIteration(start, end);
        aiMove.add(start);
        aiMove.add(end);
        
        return new Pair<>(res,aiMove);
    }

    private Pair<Point, Point> getBestAIMove(HashMap<Point, ArrayList<Point>> possibleMoves) {
        Pair<Point, Point> bestAIMove = null;
        Double bestMoveScore = 0.0;
        Point target = null;
        if(!getCurrentPlayer().isFinish()){
            for (Point moveStart : possibleMoves.keySet()) {
                for (Point moveEnd : possibleMoves.get(moveStart)) {
                    target = getTargetPoint(moveStart);
                    Point gameTarget = EngineFactory.createGamePoint(target, gameBoard);
                    double curMoveScore = getMoveScore(moveStart, moveEnd, gameTarget);
                    if (curMoveScore > bestMoveScore) {
                        bestAIMove = new Pair<>(moveStart, moveEnd);
                        bestMoveScore = curMoveScore;
                    }
                }
            }
        }       
        if (bestAIMove == null) {
            bestAIMove = finish(possibleMoves);
        }
        return bestAIMove;
    }

    private Point getTargetPoint(Point moveStart) {
        Color pointColor = gameBoard.getColorByPoint(moveStart);
        int targetIndex = getCurrentPlayer().getColors().indexOf(pointColor);
        Point target = getCurrentPlayer().getTargets().get(targetIndex);
        return target;
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
        if (players.size() != 1) {
            setPossibleMovesForPlayer(getCurrentPlayer());
        }

        return players.size() == 1;
    }

    private ArrayList<Color> toList(Stack<Pair<Color, Color>> colorStack) {
        ArrayList<Color> colorsInStack = new ArrayList<>();
        for (Pair<Color, Color> pair : colorStack) {
            colorsInStack.add(pair.getKey());
        }
        return colorsInStack;
    }

    private void updateTarget(HashMap<Color, ArrayList<Point>> boardMap) {
        Set<Color> colors = boardMap.keySet();
        for (Color color : colors) {
            targetMap.updateTargetMap(color, boardMap.get(color), gameBoard);
        }
    }

    private void addPointToMap(int i, int j, HashMap<Color, ArrayList<Point>> boardMap) {
        Point curPoint = new Point(i, j);
        Color color = gameBoard.getColorByPoint(curPoint);
        if (isMarble(color)) {

            if (boardMap.get(color) == null) {
                boardMap.put(color, new ArrayList<>());
            }

            boardMap.get(color).add(curPoint);

        }

    }

    private boolean checkIfGameOver(Player player) {
        ArrayList<Point> targets;
        boolean isGameOver = false;
        int counter = 0;
        int size = player.getTargets().size();

        for (int i = 0; i < size; i++) {
            Point target = player.getTargets().get(i);
            targets = targetMap.getVertexToSet().get(target);
            for (int j = 0; j < 10; j++) {
                if (player.getPoints().contains(targets.get(j))) {
                    counter++;
                }
                if (counter == 10 * size) {
                    isGameOver = true;
                }

            }
        }

        return isGameOver;
    }

    private double getMoveScore(Point moveStart, Point moveEnd, Point target) {
        double moveScore;

        double startScore = moveStart.distance(target);
        double endScore = moveEnd.distance(target);

        moveScore = startScore - endScore;
        return moveScore;
    }

    private Pair<Point, Point> finish(HashMap<Point, ArrayList<Point>> possibleMoves) {
        Pair<Point, Point> bestAIMove = null;
        Double bestMoveScore = null;


        ArrayList<Point> playerPoints = getCurrentPlayer().getPoints();
        getCurrentPlayer().setIsFinish(true);
        for (Point startPoint : playerPoints) {
            Point target = getTargetPoint(startPoint);
            ArrayList<Point> targets = targetMap.getVertexToSet().get(target);
            if (!targets.contains(startPoint)) {
                Point newTarget = getEmptyTarget(targets, playerPoints);
                
                for (Point endPoint : possibleMoves.get(startPoint)) {
                    double curMoveScore = getMoveScore(startPoint, endPoint, newTarget);
                    if (bestMoveScore == null || bestMoveScore < curMoveScore) {
                        bestAIMove = new Pair<>(startPoint, endPoint);
                        bestMoveScore = curMoveScore;
                    }
                }
            }
        }

        return bestAIMove;
    }

    private Point getEmptyTarget(ArrayList<Point> targets, ArrayList<Point> playerPoints) {
        Point emptyTarget = null;

        for (Point target : targets) {
            if (emptyTarget == null || !playerPoints.contains(target)) {
                emptyTarget = target;
            }
        }

        return emptyTarget;
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
