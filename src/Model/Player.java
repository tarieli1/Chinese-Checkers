package Model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    
    private final Type type;
    private final String name;
    private ArrayList<Color> colors = new ArrayList<>();
    private ArrayList<Point> points = new ArrayList<>();
    private HashMap<Point,ArrayList<Point>> possibleMoves;

    public HashMap<Point, ArrayList<Point>> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(HashMap<Point, ArrayList<Point>> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }
    
    public Player(String name, Type type)
    {
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

    public void setColors(ArrayList<Color> colors) {
        this.colors = colors;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }
    
    
    public enum Type
    {
        Player,
        COMPUTER
    }
}
