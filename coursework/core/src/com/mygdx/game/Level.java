package com.mygdx.game;

import java.io.FileNotFoundException;

public class Level {
    private int entranceLevelX;
    private int entranceLevelY;
    private int actualEntranceLevelX;
    private int actualEntranceLevelY;
    private int initPlayerPosGridX;
    private int initPlayerPosGridY;
    private int windowHeight;
    private int windowWidth;
    private boolean setPlayerSpriteBatchBegin;
    protected int maxStarCount;
    protected String levelInstructions;
    private String levelName;
    private Boolean doneStart;
    public int[][] getBoardArr() {
        return boardArr;
    }

    public void setBoardArr(int[][] boardArr) {
        this.boardArr = boardArr;
    }

    private int[][] boardArr;
    public int getEntranceLevelY() {
        return entranceLevelY;
    }

    public void setEntranceLevelY(int entranceLevelY) {
        if(this.entranceLevelY == -1){
            this.entranceLevelY = entranceLevelY;
        }else{
            this.entranceLevelY += entranceLevelY;
        }
    }
    //String levelName, int[][] boardArr
    public Level(String levelName){//All instances of Sokoban, Tower and Maze levels inherit from this
        this.levelName = levelName;
        entranceLevelX = -1;
        entranceLevelY = -1;
        this.boardArr = new int[][]{};//One main use of inheritance was to access all the boards of all the levels without knowing object type so maze/tower
        setPlayerSpriteBatchBegin = false;
        doneStart = false;//to do with rendering the text at start of level
    }
    public int getEntranceLevelX() {
        return entranceLevelX;
    }
    public void setEntranceLevelX(int entranceLevelX) {
        if(this.entranceLevelX == -1){//you should really have another function to add coordinates
            this.entranceLevelX = entranceLevelX;
            actualEntranceLevelX = entranceLevelX;
        }else{
            this.entranceLevelX += entranceLevelX;//adding coordinates that are relative to just inside the level to outer coordinates
        }
    }
    public void setActualEntranceLevelX(int actualEntranceLevelX) {
        this.actualEntranceLevelX = actualEntranceLevelX;
    }
    public void setActualEntranceLevelY(int actualEntranceLevelY) {
        this.actualEntranceLevelY = actualEntranceLevelY;
    }
    public int getActualEntranceLevelX(){
        return actualEntranceLevelX;
    }
    public int getActualEntranceLevelY(){
        return actualEntranceLevelY;
    }
    public void levelUpdate(Map map, Player player) throws FileNotFoundException {//only called when player intends to move down
        int checkY = entranceLevelY+1;

        if(player.getGridX() == entranceLevelX && (player.getGridY() == checkY)){
            enterLevel(player, map);
        }
    }
    public void enterLevel(Player player, Map map)throws FileNotFoundException{
        player.setLevelName(levelName);//I think this was old code before creating Level super class to tell what level each thing was in
        System.out.println("Map");
        player.setBoard(boardArr);
        player.setCurrentLevel(this);
        map.writeToMap(boardArr);//this wipes out map from screen
        map.reloadMap(this);
    }

    public int getInitPlayerPosGridX() {
        return initPlayerPosGridX;
    }

    public void setInitPlayerPosGridX(int initPlayerPosGridX) {
        this.initPlayerPosGridX = initPlayerPosGridX;
    }

    public int getInitPlayerPosGridY() {
        return initPlayerPosGridY;
    }

    public void setInitPlayerPosGridY(int initPlayerPosGridY) {
        this.initPlayerPosGridY = initPlayerPosGridY;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getMaxStarCount() {
        return maxStarCount;
    }

    public void setMaxStarCount(int maxStarCount) {
        this.maxStarCount = maxStarCount;
    }

    public Boolean getDoneStart() {
        return doneStart;
    }

    public void setDoneStart(Boolean doneStart) {
        this.doneStart = doneStart;
    }
}
