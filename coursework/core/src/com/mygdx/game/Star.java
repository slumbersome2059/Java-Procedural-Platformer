package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.io.FileNotFoundException;

public class Star extends Sprite {
    private Player player;
    private boolean collisionOccurred;
    private int[][] board;
    private int gridX;
    private int gridY;
    private int x;
    private int y;
    private boolean collidingWithBox;
    boolean collisionBox = false;



    public Star(int gridX, int gridY, int x, int y, Player player, int[][] board, Sprite sprite){
        super(sprite);
        setX(x);//refers to x and y given as parameters when new object instantiated
        setY(y);//these are needed for displaying on the screen, while gridX and gridY are used for calculations
        this.y = y;
        this.x = x;
        this.player = player;
        this.board = board;
        collisionOccurred = false;//checks for collision with player
        this.gridX = gridX;
        this.gridY = gridY;
        collidingWithBox = false;
    }
    public void draw(SpriteBatch batch) throws InterruptedException, FileNotFoundException {
        if(!collisionOccurred){
            update();
            super.draw(batch);
        }
    }
    private void update() throws InterruptedException, FileNotFoundException {
        if(!player.getLevelName().equals("Sokoban Level")){
            collidingWithPlayer();
        }
    }
    private void collidingWithPlayer() throws FileNotFoundException {
        if(!player.getLevelName().equals("Outside") && player.getGridX() == gridX && player.getGridY() == (gridY+1)){
            collisionOccurred = true;
        }
        if(player.getLevelName().equals("Tower Level")){
            if(player.playerRect.overlaps(new Rectangle(x, y, Map.tileSquareLength, Map.tileSquareLength))){
                collisionOccurred = true;
                System.out.println("Collision Detected");
                player.getMap().enteringOriginalMap();
            }
        }
    }

    public boolean isCollidingWithBox(int boxGridX, int boxGridY) {
        return (boxGridX == gridX && boxGridY == gridY);
    }
    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}
