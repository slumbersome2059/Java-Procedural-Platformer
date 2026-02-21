package com.mygdx.game;

import com.badlogic.gdx.Gdx;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import static java.lang.Math.atan;
import static java.lang.Math.cos;

public class Light {
    private ArrayList<int[]> polygonPoints;
    private double angleRangeLight;
    private Player player;
    private float mouseX;
    private float mouseY;
    private boolean calculate;
    private ArrayList<int[]> towerPoints;
    private ArrayList<int[][]> towerEdges;
    private HashMap<int[], Double> hm;
    private double playerAngle;
    private ArrayList<Line> lines;
    private TreeMap<Double, float[]> trianglePoints;
    private float[][] trianglePointsArray;
    private ArrayList<Polygon> lightTriangles;
    private ArrayList<int[][]> deadPlayerEdges;
    private boolean containsRect;
    private int dpRectIndex;
    public Light(Player player){
        angleRangeLight = 0.1;
        playerAngle = 0;
        polygonPoints = new ArrayList<>();
        trianglePoints = new TreeMap<>();//tree map will automatically sort by angle so that when rendering the light you can do a full rotation
        lines = new ArrayList<>();
        hm = new HashMap<>();//stores the point and it's angle with the player -> useful for drawing the lines from player to point
        this.player = player;
        calculate = true;
        towerPoints = player.getMap().getTowerPoints();//these store vertices of the rectangles that represent the tower
        towerEdges = player.getMap().getTowerEdges();//these store all the edges of the rectangles that represent the tower
        lightTriangles = new ArrayList<>();//array with each of the triangles that makes up the torch
        deadPlayerEdges = new ArrayList<>();
        containsRect = false;
        dpRectIndex = 0;
    }
    public void update(){
        calculate = true;//changed when mouse is clicked
        mouseX = player.getMouseX();
        mouseY = Line.towerLevelHeight - player.getMouseY();
        if(mouseX < 1||mouseY < 1 || mouseX > (Line.towerLevelWidth -1) || mouseY > (Line.towerLevelHeight-1)){//validates for out of bounds of screen
            calculate = false;
        }
        if(calculate){
            //the player may have changed positions so this just automatically clears and updates again
            //-> for efficiency you can update only when there is a change in player position next time
            trianglePoints.clear();
            lines.clear();
            hm.clear();
            lightTriangles.clear();
            //New
            deadPlayerEdges.clear();
            generateDeadPlayerEdges();
            ArrayList<int[]> allPoints = new ArrayList<>(towerPoints);
            allPoints.addAll(getDeadPlayerCornerPoints());
            //
            calculateAllAngles(allPoints);
            createLines();
            createTrianglePoints();
            createTrianglePointsArray();
            createLightTriangle();
//            for(float[] arr:new Line(player.getPlayerMidX(), player.getPlayerMidY(), angle - angleRangeLight, player.getPlayerMidX(), player.getPlayerMidY()).getPoints()){
//                polygonPoints.add(new int[]{(int)arr[0], (int)arr[1]});
//            }
//            for(float[] arr:new Line(player.getPlayerMidX(), player.getPlayerMidY(), angle + angleRangeLight, player.getPlayerMidX(), player.getPlayerMidY()).getPoints()){
//                polygonPoints.add(new int[]{(int)arr[0], (int)arr[1]});
//            }
            //lightTriangles = createLightTriangle();
        }
    }
    private double getAngle(float x, float y){
        float origY = player.getPlayerMidY();
        float origX = player.getPlayerMidX();
        double angle = atan((y - player.getPlayerMidY())/(x - player.getPlayerMidX()));
        if(y-origY > 0 && x - origX < 0&& angle < 0){//The arctan function always give out negative for first and third quadrants, but the code outside thinks that first = positive angle, third = negative angle
            angle = angle+Math.PI;//so this fixes it for first quadrant
        }else if(y-origY < 0 && x - origX < 0){//function gives out positive for second and fourth quadrant
            angle = angle-Math.PI;//but you want negative for fourth quadrant
        }
        if(angle == 0 && x - origX < 0){
            return Math.PI;
        }
        return angle;
    }
    private void createTrianglePointsArray(){
        trianglePointsArray = new float[trianglePoints.values().size()][2];//initialises array
        getTrianglePoints().values().toArray(trianglePointsArray);//each array inside big array stores two coordinated which are used in rendering
        //A triangle will be rendered with each of the consecutive points by a shapeRenderer
    }
    public float[][] getTrianglePointsArray(){
        return trianglePointsArray;
    }

    private void createLightTriangle(){
        //this creates the polygon that controls all the collision detection
        for(int i = 0; i < trianglePointsArray.length - 1; i++){
            lightTriangles.add(new Polygon(new int[]{(int)(player.getPlayerMidX()), (int)(trianglePointsArray[i][0]), (int)(trianglePointsArray[i+1][0])},
                    new int[]{(int)(player.getPlayerMidY()), (int)(trianglePointsArray[i][1]), (int)(trianglePointsArray[i+1][1])}, 3));
        }
    }
    public ArrayList<Polygon> getLightTriangles(){
        return lightTriangles;
    }
    public boolean getContainsRects(){//used to detect if overlaps with dead players
        trianglePoints.clear();
        deadPlayerEdges.clear();
        generateDeadPlayerEdges();
        createTrianglePoints();
        if(lightTriangles == null || !player.getDisplayTorch()){//here for efficiency to avoid having to loop through lightTriangles
            return false;
        }
        if(!containsRect){
            System.out.println("interesting");
        }
        return containsRect;
    }
    private void calculateAllAngles(ArrayList<int[]> points){
        playerAngle = getAngle(mouseX, mouseY);//refers to x and y of cursor
        for(int[] point:points){
            double pointAngle = getAngle(point[0], point[1]);
            if(pointAngle > (playerAngle - angleRangeLight) && pointAngle < (playerAngle + angleRangeLight)){//makes that the torch has a range of visibility
                hm.put(point,pointAngle);//populates the hash map with point and associated angle
            }
        }
    }
    private ArrayList<float[]> get_line_intersection(float p0_x, float p0_y, float p1_x, float p1_y,
                                                   float p2_x, float p2_y, float p3_x, float p3_y)
    {//this is code that has been copied in from  https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect.
        ArrayList<float[]> intersections = new ArrayList<>();
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
        s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

        float s, t;
        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            // Collision detected

            float i_x = p0_x + (t * s1_x);
            float i_y = p0_y + (t * s1_y);
            intersections.add(new float[]{i_x, i_y});
            return intersections;
        }

         // No collision
        return intersections;
    }
    private void createLines() {
        double angle = getAngle(mouseX, mouseY);
        lines.add(new Line(player.getPlayerMidX(), player.getPlayerMidY(), angle - angleRangeLight, player));
        lines.add(new Line(player.getPlayerMidX(), player.getPlayerMidY(), angle + angleRangeLight, player));
        for (Double val : hm.values()) {//accesses the angles from player to each point
            //here we create three lines from the player to each point with slightly different angles
            //makes it look like the light knows which way to go along(you don't work which direction the shadow will be)
            lines.add(new Line(player.getPlayerMidX(), player.getPlayerMidY(), val + 0.01, player));
            lines.add(new Line(player.getPlayerMidX(), player.getPlayerMidY(), val, player));
            lines.add(new Line(player.getPlayerMidX(), player.getPlayerMidY(), val - 0.01, player));
        }
    }
    private double calcPythDist(float[] point){
        //uses pythagoras distance formula
        return Math.sqrt(Math.pow(point[0]-player.getPlayerMidX(), 2) + Math.pow(point[1]-player.getPlayerMidY(), 2));
    }
    private void createTrianglePoints(){
        //these basically loops through each line and checks where the nearest intersections
        for(Line line : lines){
            float[] point = line.getPoints();
            float[] finalPoint = new float[]{};
            double minDist = 10000;//a high distance that's not attainable so that first distance overwrites it
            for(int[][]edge:towerEdges){
                ArrayList<float[]> intersections = get_line_intersection((float)(player.getPlayerMidX()), (float)(player.getPlayerMidY()), point[0], point[1],
                        (edge[0][0]), (edge[0][1]), (edge[1][0]), (edge[1][1])
                );
                if(!intersections.isEmpty()){
                    double dist = calcPythDist(intersections.get(0));
                    if(dist < minDist){//calculates shortest distance
                        minDist = dist;
                        finalPoint = intersections.get(0);
                    }
                    //pythagoras function
                    //add to tree map
                    //draw the triangles using tree map
                }
            }
            // NEW
            containsRect = false;
            int count =0;
            for(int[][]edge:deadPlayerEdges){
                ArrayList<float[]> intersections = get_line_intersection((float)(player.getPlayerMidX()), (float)(player.getPlayerMidY()), point[0], point[1],
                        (edge[0][0]), (edge[0][1]), (edge[1][0]), (edge[1][1])
                );
                if(!intersections.isEmpty()){//I think problem with intersections
                    if(count >= dpRectIndex && count <= dpRectIndex + 3){
                        containsRect = true;
                    }
                    double dist = calcPythDist(intersections.get(0));
                    if(dist < minDist){
                        minDist = dist;
                        finalPoint = intersections.get(0);
                    }
                }
                count++;
            }

            //
            trianglePoints.put(line.getAngle(), finalPoint);
        }
    }
    public TreeMap<Double, float[]> getTrianglePoints(){
        return trianglePoints;
    }
    private void generateDeadPlayerEdges() {
        int count = 0;
        boolean val = player.collidesWithDeadPlayer(player.getPlayerRect());
        for (deadPlayer dp : player.getDeadPlayers()) {
            com.badlogic.gdx.math.Rectangle rect = dp.getDeadPlayerRect();
            if(rect.equals(player.getDeadPlayerRect())){
                dpRectIndex = count;
            }
            int x = (int) rect.x;
            int y = (int) rect.y;
            int width = (int) rect.width;
            int height = (int) rect.height;

            // Create four edges for the rectangle (top, right, bottom, left)
            deadPlayerEdges.add(new int[][]{{x, y + height}, {x + width, y + height}}); // Top edge
            deadPlayerEdges.add(new int[][]{{x + width, y + height}, {x + width, y}}); // Right edge
            deadPlayerEdges.add(new int[][]{{x + width, y}, {x, y}}); // Bottom edge
            deadPlayerEdges.add(new int[][]{{x, y}, {x, y + height}}); // Left edge
            count += 4;
        }
    }
    private ArrayList<int[]> getDeadPlayerCornerPoints() {
        ArrayList<int[]> cornerPoints = new ArrayList<>();
        for (deadPlayer dp : player.getDeadPlayers()) {
            com.badlogic.gdx.math.Rectangle rect = dp.getDeadPlayerRect();
            int x = (int) rect.x;
            int y = (int) rect.y;
            int width = (int) rect.width;
            int height = (int) rect.height;

            // Add all four corners
            cornerPoints.add(new int[]{x, y}); // Bottom-left
            cornerPoints.add(new int[]{x + width, y}); // Bottom-right
            cornerPoints.add(new int[]{x, y + height}); // Top-left
            cornerPoints.add(new int[]{x + width, y + height}); // Top-right
        }
        return cornerPoints;
    }
}
