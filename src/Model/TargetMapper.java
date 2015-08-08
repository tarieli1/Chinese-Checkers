/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class TargetMapper {
    final HashMap<Point, ArrayList<Point>> vertexToSet = new HashMap<>();
    final HashMap<Color,Point> colorToVertex = new HashMap<>();
    
    public TargetMapper() {
        initTargetMap();
    }    

    private void initTargetMap() {
        vertexToSet.put(new Point(1,1), new ArrayList<>());
        vertexToSet.put(new Point(5,1), new ArrayList<>());
        vertexToSet.put(new Point(5,13), new ArrayList<>());
        vertexToSet.put(new Point(13,1), new ArrayList<>());
        vertexToSet.put(new Point(13,13), new ArrayList<>());
        vertexToSet.put(new Point(17,1), new ArrayList<>());
    }   
    
    void updateTargetMap(Color setColor, ArrayList<Point> setOfPoints,Board gameBoard){
        Set<Point> targets = vertexToSet.keySet();
        for (Point target : targets) {
            Point gamePoint = EngineFactory.createGamePoint(target, gameBoard);
            if (setOfPoints.contains(gamePoint)) {
                vertexToSet.put(target, setOfPoints);
                colorToVertex.put(setColor, target);
            }
        }
    }
}
