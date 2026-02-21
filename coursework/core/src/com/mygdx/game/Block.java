package com.mygdx.game;

public class Block {
    private int backtrackX, backtrackY, x, y;
    private double distStart, distEnd;
    public Block(double distStart, double distEnd, int backtrackX, int backtrackY, int x, int y){
        this.distStart = distStart;
        this.distEnd = distEnd;
        this.backtrackX = backtrackX;
        this.backtrackY = backtrackY;
        this.x = x;
        this.y = y;
    }

    public double getDistEnd() {
        return distEnd;
    }
    public int getBacktrackX() {
        return backtrackX;
    }
    public int getBacktrackY() {
        return backtrackY;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public double getTotalDist(){
        return distStart + distEnd;
    }
}
