package Model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {

    private final Type type;
    private final String name;
    private final ArrayList<Color> colors = new ArrayList<>();
    private final ArrayList<Point> targets = new ArrayList<>();
    private final ArrayList<Point> points = new ArrayList<>();
    private HashMap<Point, ArrayList<Point>> possibleMoves;

    public Player(String name, Type type) {
        this.name = name;
        this.type = type;

    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public HashMap<Point, ArrayList<Point>> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(HashMap<Point, ArrayList<Point>> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    public ArrayList<Point> getTargets() {
        return targets;
    }

    public enum Type {

        Player,
        COMPUTER
    }
}
