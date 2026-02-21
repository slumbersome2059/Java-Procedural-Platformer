package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.mygdx.game.Map.tileSquareLength;

public class Box extends Sprite{
    private Player player;
    private int[][] board;
    private int gridX;
    private int gridY;
    private ArrayList<Box> boxes;
    private String boxCollidingWall;
    private int previousTileVal;
    public Box(int gridX, int gridY, int x, int y, Player player, int[][] board, Sprite sprite){
        super(sprite);
        setX(x);
        setY(y);
        this.player = player;
        this.board = board;
        this.gridX = gridX;
        this.gridY = gridY;
        boxCollidingWall = "Player not colliding with box";
        previousTileVal = 2;
    }
    public void draw(SpriteBatch batch) throws InterruptedException {

        super.draw(batch);
    }
    public boolean collidingWithWall(int changeX, int changeY) throws InterruptedException, FileNotFoundException {
        int[] nextCell = {gridX + changeX, gridY + changeY};
        if(board[nextCell[1]][nextCell[0]] == 1 || board[nextCell[1]][nextCell[0]] == 7){//tiles associated with inner or outer tiles
            return true;
        }else{
            if(board[nextCell[1]][nextCell[0]] == 5 || board[nextCell[1]][nextCell[0]] == 9){//box tile in board array
                if(findBox(nextCell[0], nextCell[1]).collidingWithWall(changeX, changeY)){
                    //findBox function returns a box and you want to see if that box collides with the wall
                    //This is to ensure that you can move 2 or 3 boxes in a row successfully
                    return true;
                }
            }
        }
        //In the board when you move box to a new position, old position will still be 5
        //you need to set this to it's initial or original value
        if(board[gridY][gridX] == 9){
            board[gridY][gridX] = 3;
        }else{
            board[gridY][gridX] = previousTileVal;//set to initial value
        }
        gridX = nextCell[0];
        gridY = nextCell[1];//nextCell is an array that has coordinates of new position(calculated by change in player position)
        previousTileVal = board[gridY][gridX];//for each box it stores initial value of square that it is about to move to
        if(previousTileVal == 9 || previousTileVal == 3){
            board[gridY][gridX] = 9;
        }
        else{
            board[gridY][gridX] = 5;
        }

        //for(Star star: player.getMap().getStars()) {
        //star.collidingWithBox(gridX, gridY);
        //}
        player.generateStarCount();//goes through every star and see if it is below every box and updates star count from that
        System.out.println(player.getStarCount());
        setX(gridX*Map.tileSquareLength);
        setY(getY() - Map.tileSquareLength*changeY);
        player.getMap().writeToMap(board);
        return false;
    }

    public void setBoxes(ArrayList<Box> boxes) {
        this.boxes = boxes;
    }
    private Box findBox(int gridX, int gridY){
        for(Box box:boxes){
            if(box.getGridX() == gridX && box.getGridY() == gridY){
                return box;
            }
        }
        System.out.println("Box not found");
        return boxes.get(0);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}
