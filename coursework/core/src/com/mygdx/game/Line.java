package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.atan;

public class Line{
    private float gradient;
    private ArrayList<float[]> points;
    private float origX;
    private float origY;
    public static int towerLevelWidth = TowerLevel.levelWidth * Map.tileSquareLength;
    public static int towerLevelHeight = TowerLevel.levelHeight * Map.tileSquareLength;
    private double angle;
    private float playerMidX;
    private float playerMidY;
    private ArrayList<Double> intAngle;
    private ArrayList<Double> intCalcAngle;
    private Player player;
    public Line(float origX, float origY, double angle, Player player){
        this.angle = setAngleBetweenPosAndNegPi(angle);
        this.player = player;
        gradient = (float)(Math.tan(angle));
        //System.out.println(gradient);
        this.origX = origX;
        this.origY = origY;
        this.intAngle = new ArrayList<>();
        this.intCalcAngle = new ArrayList<>();
        points = new ArrayList<>();
        calculateVerticalEdges();
        calculateIntersectionWithHorizontalEdges();

    }
    private void calculateIntersectionWithHorizontalEdges(){
        //this mainly calculates intersection with borders
        //the line will intersect forwards and backwards so you have to check both intersections at end
        float firstIntersectionX = origX - (origY/gradient);//from rearranging the formula y - y1 = m(x - x1) and y = 0
        if(firstIntersectionX>=0 &&firstIntersectionX<=towerLevelWidth){
            points.add(new float[]{firstIntersectionX, 0});
        }
        float secondIntersectionX = origX + ((towerLevelHeight - origY)/gradient);//same idea as above by y = height
        if(secondIntersectionX>=0 && secondIntersectionX<=towerLevelWidth){
            points.add(new float[]{secondIntersectionX, towerLevelHeight});
        }
    }
    private void calculateVerticalEdges(){
        float firstIntersectionY =gradient*(-origX) + origY;//from rearranging the formula y - y1 = m(x - x1) and x = 0
        if(firstIntersectionY>=0 && firstIntersectionY<=towerLevelHeight){
            points.add(new float[]{0, firstIntersectionY});
        }
        float secondIntersectionY =gradient*(towerLevelWidth-origX) + origY;//same idea as above by x = width
        if(secondIntersectionY>=0 && secondIntersectionY<=towerLevelHeight){
            points.add(new float[]{towerLevelWidth, secondIntersectionY});
        }
    }
    public float[] getPoints(){
        float[] retPoint = null;
        double minAngleDiff = 10;//there probably is a better way to do this subroutine, check page 51 of Iterative Stages 2 to see why it didn't work
        for(float[] point: points){//points refers to the forwards and backwards intersection
            double angleDiff = abs(angle - calcAngle(point[0], point[1]));//absolute function so it's like the modulus(gets positive)
            if(angleDiff < minAngleDiff){
                minAngleDiff = angleDiff;
                retPoint = point;//this returns the point that's in the direction of the angle(in the direction the torch is pointing)
            }
        }
        return retPoint;
    }
    public double getAngle(){//this is done badly because this method is used in light and line class
        //next time create an interface(just holds subroutines)
        return angle;
    }
    private double calcAngle(float x, float y){//this should be added to an interface so that both Light and Line can access this
        double angle = atan((y - origY)/(x - origX));
        if(y-origY > 0 && x - origX < 0){//see the same function in Light that explains why
            angle = angle+3.14159;
        }else if(y-origY < 0 && x - origX < 0){
            angle = angle-3.14159;
        }
        return angle;
    }
    private double setAngleBetweenPosAndNegPi(double angle){//some of the comparisons assume that the angles are between pos and neg PI so important
        if(angle > Math.PI){
            return angle - 2*Math.PI;
        }
        if(angle < -Math.PI){
            return angle + 2*Math.PI;
        }
        return angle;
    }


}
