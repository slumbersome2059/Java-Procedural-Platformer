package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class deadPlayer extends Sprite {
    Rectangle deadPlayerRect;
    public deadPlayer(Sprite sprite, Rectangle playerRect) {
        super(sprite);//allows you to render player easily
        deadPlayerRect = new Rectangle(playerRect);//like lists in python being passed by reference, it is same thing with rectangles
        deadPlayerRect.x = makeRectanglesAsGrid(deadPlayerRect.x);
        deadPlayerRect.y = makeRectanglesAsGrid(deadPlayerRect.y);
        deadPlayerRect.setHeight(50);
        deadPlayerRect.setWidth(50);
        setX(deadPlayerRect.x);
        setY(deadPlayerRect.y);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);//this is the advantage of using inheritance
    }
    public boolean collidingWithPlayer(Rectangle playerRect){
        if(playerRect.overlaps(deadPlayerRect)){//a special method in the rectangle class
            System.out.println(deadPlayerRect.toString());
            return true;
        }
        return false;
    }
    public Rectangle getDeadPlayerRect() {
        return deadPlayerRect;
    }
    private float makeRectanglesAsGrid(float currentVal){
        int floorDivVal = (int)(currentVal/Map.tileSquareLength);
        if(currentVal - floorDivVal*Map.tileSquareLength < (float)(Map.tileSquareLength/2)){
            currentVal = floorDivVal*Map.tileSquareLength;
        }else{
            currentVal = (floorDivVal+1)*Map.tileSquareLength;
        }
        return currentVal;
    }
}
