package com.mygdx.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TowerLevel extends Level{
    private Random rand;
    public static final int levelWidth =20;
    public static final int levelHeight = 10;
    private int[][] levelArr;
    private ArrayList<Rectangle> towerRects;
    private ArrayList<int[]> towerPoints;
    private ArrayList<int[][]> towerEdges;
    public TowerLevel(){
        super("Tower Level");
        setWindowWidth(levelWidth);
        setWindowHeight(levelHeight);
        setInitPlayerPosGridX(1);
        setInitPlayerPosGridY(levelHeight-2);
        towerPoints = new ArrayList<>();
        towerEdges = new ArrayList<>();
        rand = new Random();
        levelArr = new int[levelHeight][levelWidth];
        for(int i = 0; i < levelHeight; i++){
            for(int j = 0; j < levelWidth; j++){
                levelArr[i][j] = 4;//background
            }
        }
        ArrayList<Integer> possHeights = new ArrayList<>();
        possHeights.add(7);
        possHeights.add(6);
        possHeights.add(5);
        possHeights.add(4);
        ArrayList<Integer> columns = new ArrayList<Integer>();
        columns.add(1);
        int randNum = 1;
        for(int i = possHeights.size() - 2; i > 0; i--){//code to generate the columns which contain platforms
            randNum = rand.nextInt((levelWidth - 1) - 2*(i+1) - (randNum + 2)) + randNum + 2;//this function only generates random values from 0 to a number
            //so here you add randNum + 2 at end, also the randNum + 2 is subtracted initially to avoid generating a random number that is outside the width of the array
            columns.add(randNum);
        }
        columns.add(levelWidth - 2);
        int randInd;
        towerRects = new ArrayList<>();
        for(int j = 1; j < levelWidth; j++){
            if(columns.contains(j)){//columns contains all the columns that have been chosen to be ones containing platforms
                randInd = rand.nextInt(possHeights.size());
                for(int i = levelHeight - 1; i > (levelHeight - 1) - possHeights.get(randInd); i--){//fills up platform tiles for the random height
                    levelArr[i][j] = 7;
                }
                towerRects.add(new Rectangle(j*Map.tileSquareLength, 0, Map.tileSquareLength,possHeights.get(randInd)*Map.tileSquareLength));
                possHeights.remove(randInd);
            }
            else{
                levelArr[levelHeight - 1][j] = 6;//adds a triangle tile
                towerPoints.add((new int[]{(int)((j+0.5)*Map.tileSquareLength), Map.tileSquareLength}));//top point of triangle
                for(int i = j; i <= j + 1; i++){
                    towerPoints.add(new int[]{i*Map.tileSquareLength, 0});//bottom points added with for loop
                    towerEdges.add(new int[][]{{i*Map.tileSquareLength, 0}, {(int)((j+0.5)*Map.tileSquareLength), Map.tileSquareLength}});
                    //adding an edge with top point and bottom two points
                }
            }

        }
        levelArr[levelHeight - 1][levelWidth - 1] = 3;//wormhole tile
        towerRects.add(new Rectangle(0, 0, levelWidth*Map.tileSquareLength, levelHeight*Map.tileSquareLength));
        super.setBoardArr(levelArr);
        levelInstructions = "Click on trackpad or mouse to activate torch\n\n" +
                "Move mouse or finger on trackpad to change direction of torch\n" +
                "If you fall on a triangle a dead player will spawn at that place\n\n" +
                "Touching dead player without torch shining on it makes you dead\n" +
                "The goal is to get to the star\n\n";
    }
    public int getLevelWidth(){
        return levelWidth;
    }

    public int getLevelLength() {
        return levelHeight;
    }

    public int[][] getLevelArr() {
        return levelArr;
    }
    public ArrayList<int[]> getTowerPoints() {//used for shadow casting

        for(Rectangle r : towerRects){
            towerPoints.add(new int[]{r.x, r.y});
            towerPoints.add(new int[]{r.x + r.width, r.y});
            towerPoints.add(new int[]{r.x, r.y+r.height});
            towerPoints.add(new int[]{r.x + r.width, r.y + r.height});
        }
        return towerPoints;
    }
    public ArrayList<int[][]> getTowerEdges() {//used to represent the rectangle edges(used for shadow casting)
        for(Rectangle r : towerRects){
            towerEdges.add(new int[][]{{r.x, r.y}, {r.x, r.y+r.height}});
            towerEdges.add(new int[][]{{r.x + r.width, r.y},{r.x + r.width, r.y + r.height} });
            towerEdges.add(new int[][]{{r.x, r.y+r.height}, {r.x + r.width, r.y + r.height}});
            towerEdges.add(new int[][]{{r.x, r.y}, {r.x + r.width, r.y}});
        }
        return towerEdges;
    }

}
