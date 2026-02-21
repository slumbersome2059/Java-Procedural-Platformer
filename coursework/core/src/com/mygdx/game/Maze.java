package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMap;


import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.in;

public class Maze extends Level{
    private ArrayList<ArrayList<Integer>> maze;
    private int gridLength;
    private int levelLength;
    private int levelWidth;
    private TiledMap map;
    public Maze(int gridLength) {
        super("Maze Level");
        this.gridLength = gridLength;
        levelLength = gridLength;
        levelWidth = gridLength;
        setWindowHeight(levelLength);
        setWindowWidth(levelWidth);
        setInitPlayerPosGridX(1);
        setInitPlayerPosGridY(levelLength - 1);
        maze = new ArrayList<>();
        setupGrid();
        carvePassage(3, 5);
        for(ArrayList<Integer> row : maze) {
            System.out.println(row);
        }
        super.setBoardArr(getMazeArr());
        levelInstructions = "When you move to a position of the star you will collect it\n\n" +
                "Try and collect all the stars\n\n";
    }
    private void setupGrid(){
        for (int i = 0; i < gridLength; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < gridLength; j++) {
                if(i % 2 != 0 && j % 2 != 0) {
                    row.add(2);
                }
                else{
                    row.add(7);
                }
            }
            maze.add(row);
        }
        //Creating the entrance of the maze
        ArrayList<Integer> row = maze.get(0);
        row.set(1, 3);//changes 2nd item of row to 3 to show star
        maze.set(0, row);
    }
    private void spaceToDraw(int cellX, int cellY, int endCellX, int endCellY){
        if(cellX > endCellX){
            for(int i = cellX; i >= endCellX; i--){
                maze.get(cellY).set(i, 3);
            }
        }else if(cellX < endCellX){
            for(int i = endCellX; i >= cellX; i--){
                maze.get(cellY).set(i, 3);
            }
        }else if(cellY > endCellY){
            for(int i = cellY; i >= endCellY; i--){
                maze.get(i).set(cellX, 3);
            }
        }else if(cellY < endCellY){
            for(int i = endCellY; i >= cellY; i--){
                maze.get(i).set(cellX, 3);
            }
        }
    }
    private ArrayList<ArrayList<Integer>> checkAvailableSquares(int cellX, int cellY){//this function checks the cells that are available 2 squares away
        ArrayList<ArrayList<Integer>> availableSquares = new ArrayList<>();
        ArrayList<Integer> row;
        int[] coordsToSee = {cellX+2, cellY, cellX - 2, cellY, cellX, cellY + 2, cellX, cellY - 2};//a 1D array that stores x and y next to each other
        for(int i = 0; i <= coordsToSee.length-2; i+=2){//loops till the final x index
            int xInd = i;
            int yInd = i+1;
            if(coordsToSee[xInd] < gridLength && coordsToSee[yInd] < gridLength && coordsToSee[xInd] >= 0 && coordsToSee[yInd] >= 0){
                //adds validation to make sure not out of bounds
                if(maze.get(coordsToSee[yInd]).get(coordsToSee[xInd]) == 2){//2 shows that a square has not been visited
                    row = new ArrayList<>();
                    row.add(coordsToSee[xInd]);
                    row.add(coordsToSee[yInd]);//row is a list containing the coordinates
                    availableSquares.add(row);
                }
            }
        }

        return availableSquares;
    }
    private ArrayList<Integer> genRandSquare(ArrayList<ArrayList<Integer>> possSquares){
        int randNum = (int) (Math.random()*possSquares.size());
        return possSquares.get(randNum);
    }
    private void carvePassage(int cellX, int cellY){
        ArrayList<ArrayList<Integer>> possSquares = new ArrayList<>();
        possSquares = checkAvailableSquares(cellX, cellY);
        while(!possSquares.isEmpty()){
            ArrayList<Integer> square = genRandSquare(possSquares);
            spaceToDraw(cellX,cellY,square.get(0),square.get(1));
            carvePassage(square.get(0),square.get(1));
            possSquares = checkAvailableSquares(cellX, cellY);
        }
    }

    public int getLevelWidth() {
        return levelWidth;
    }

    public int getLevelLength() {
        return levelLength;
    }
    public int[][] getMazeArr(){
        int[][] mazeArr = new int[gridLength][gridLength];
        for(int i = 0; i < gridLength; i++){
            for(int j = 0; j < gridLength; j++){
                mazeArr[i][j] = maze.get(i).get(j);
            }
        }
        return mazeArr;
    }
}

