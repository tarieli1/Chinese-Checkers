package Model;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

class Player {
    
    private Type type;
    private String name;
    private ArrayList<Color> colors;
    private ArrayList<Point> points;
    
    public Player(String name, Type type)
    {
        this.name = name;
        this.type = type;
        
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(ArrayList<Color> colors) {
        this.colors = colors;
    }
    
    public enum Type
    {
        Player,
        Computer
    }
}
