package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import javax.xml.datatype.Duration;
import java.awt.geom.Line2D;
import java.io.FileNotFoundException;
import java.security.Key;
import java.util.ArrayList;

public class Player extends Sprite{
    private int squareLength;
    private int[][] board;
    private TextureRegion playerImg;
    private int gridX;
    private int gridY;
    private int oldGridY;
    private int oldGridX;
    private Map map;
    private int starCount;
    private String levelName;
    private int velocityX;
    private int objectY;
    private int objectX;
    private double GRAVITY;
    private Line2D line;
    private int COLLISION_OFFSET;
    public static final int PLAYER_X_OFFSET = 10;
    public static final int PLAYER_Y_OFFSET = 10;
    private float HORIZONTAL_SPEED;
    private float JUMP_SPEED;
    private double velocityY;
    public Rectangle playerRect;
    private boolean canJump;
    private boolean left;
    private boolean right;
    private float airSpeed;
    private ArrayList<deadPlayer> deadPlayers;
    private int initX;
    private int initY;
    private Rectangle deadPlayerRect;
    private Texture deadPlayerImg = new Texture(Gdx.files.internal("platform.png"));
    private Boolean isCollidingDeadPlayer;
    private Boolean displayTorch;
    private int mouseX, mouseY;
    private boolean checkLight;
    private insideLevel insideLevel;
    private boolean boxPosChanged;
    private Level currentLevel;
    private boolean moveDone;
    private boolean tabPressed;
    public Player(int initX, int initY, int initGridX, int initGridY, int[][] board, int squareLength, Sprite sprite, Map map){
        super(sprite);
        deadPlayers = new ArrayList<>();
        this.initX = initX;
        this.initY= initY;
        COLLISION_OFFSET = 1;
        HORIZONTAL_SPEED = 2.5f;
        JUMP_SPEED = 3.375f;
        setX(initX);
        setY(initY);
        gridX = initGridX;
        gridY = initGridY;
        oldGridY = initGridY;
        oldGridX = initGridX;
        this.board = board;
        this.squareLength = squareLength;
        this.map = map;
        this.insideLevel = insideLevel;
        starCount = 0;
        velocityX = 0;
        velocityY = 0;
        GRAVITY = 0.06f;
        levelName = "Outside";//Please set initial level
        left = false;
        right = false;
        airSpeed = 0.0f;
        playerRect = new Rectangle(getX()+PLAYER_X_OFFSET, getY()+PLAYER_Y_OFFSET, squareLength-2*PLAYER_X_OFFSET, squareLength-2*PLAYER_Y_OFFSET);
        canJump = false;
        moveDone = false;
        isCollidingDeadPlayer = false;
        displayTorch = false;
        checkLight = false;
        boxPosChanged = false;
        tabPressed = false;
    }
    public void draw(SpriteBatch batch) throws InterruptedException, FileNotFoundException{
        if(levelName.equals("Tower Level")){
            updateGravity(Gdx.graphics.getDeltaTime());
        }else{
            updateGrid();
        }
        super.draw(batch);
    }
    private void updateGrid() throws InterruptedException, FileNotFoundException {
        int moveDist = squareLength;
        setX(gridX*Map.tileSquareLength);//using gridX to determine x coordinates on screen
        setY(getY() - Map.tileSquareLength*(gridY - oldGridY));//same calculation as above but it is done a bit differently because the
        //y-axis is in a different direction(for the screen the higher you go the greater the y-value but in the grid it's the opposite)
        oldGridX = gridX;
        oldGridY = gridY;
        if(!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)){
            moveDone = false;//basically if you keep pressing you won't be able to move
        }
        tabPressed = Gdx.input.isKeyPressed(Input.Keys.TAB);
        if(!moveDone || tabPressed){//I think with all the controls there is a better way to do it with applicationListener so do more research
            //don't think you need to use Gdx.input
            if(Gdx.input.isKeyPressed(Input.Keys.A)){//moving left
                if(!colliding(gridX - 1, gridY, -1, 0)){
                    moveDone = true;
                    gridX -= 1;
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.D)){//moving right
                if(!colliding((gridX + 1), gridY, 1, 0)){
                    moveDone = true;
                    gridX += 1;
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S)){
                if(levelName.equals("Outside")){
                    for(Level level: map.getLevels()){
                        level.levelUpdate(map, this);
                    }
                }
                if(!colliding(gridX, gridY + 1, 0, 1)){//x and y coordinates in the format required by the array are feeded in
                    moveDone = true;//can be used if you want to implement any kind of delay after a key is pressed
                    gridY += 1;//As you are moving down the higher your y-coordinate gets
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.W)){
                if(!colliding(gridX, gridY-1, 0, -1)){
                    moveDone = true;
                    gridY -= 1;
                }
            }
        }
        ////////////////////////
        if(levelName.equals("Sokoban Level")){
            if(starCount == getCurrentLevel().getMaxStarCount()){

                starCount = 0;
                //Gdx.graphics.setWindowedMode(1200, 800);
                map.enteringOriginalMap();
            }
        }

    }
    private boolean colliding(int newGridX, int newGridY, int changeX, int changeY) throws InterruptedException, FileNotFoundException {
        newGridY -= 1;//MIGHT DO SOMETHING WITH MOVING AT END SQUARES
        if(newGridX < 0 || newGridX >= board.length || newGridY < 0 || newGridY >= board[0].length){
            return true;
        }
        if(board[newGridY][newGridX] == 5 || board[newGridY][newGridX] == 9){//checks for a collision with a box
            for(Box box: insideLevel.getBoxes()){//loops through all the boxes from boxes array in insideLevel
                if(box.getGridX() == newGridX && box.getGridY() == newGridY){
                    boolean boolVal = box.collidingWithWall(changeX, changeY);//checks if the box is going to collide when the player pushes it
                    boxPosChanged = true;//think this is a useless variable
                    return boolVal;
                }
            }
        }
        if(board[newGridY][newGridX] == 1 || board[newGridY][newGridX] == 7){
            //newGridY and newGridX are parameters for gridX and gridY values
            return true;//shows that you are colliding with a brick tile
        }else{
            return false;
        }
    }

    public int getGridX() {
        return gridX;
    }
    public void setGridX(int gridX) {
        this.gridX = gridX;
    }
    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public int getGridY() {
        return gridY;
    }
    public int getOldGridX() {
        return oldGridX;
    }
    public int getOldGridY() {
        return oldGridY;
    }
    public Map getMap(){
        return map;
    }
    public int getStarCount() {
        return starCount;
    }
    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }
    public void generateStarCount(){
        starCount = 0;
        for(Star star: insideLevel.getStars()){
            for(Box box:insideLevel.getBoxes()){
                //the two loops make it so that every combination of box and star is checked
                if(star.isCollidingWithBox(box.getGridX(), box.getGridY())){
                    starCount++;//star count is initialised to 0 then adding up is done
                }
            }
        }
    }
    public String getLevelName() {
        return levelName;
    }
    public boolean getDisplayTorch(){
        return displayTorch;
    }
    public void setLevelName(String level) {
        this.levelName = level;
    }
    private void updateGravity(float delta){
//        Velocity.x = 0
        velocityX = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            left = true;
        }else{
            left = false;
        }
//        IF GDX.input.isKeyPressed(“A”) THEN
//
//        Velocity.x = -HORIZONTAL_SPEED
//
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            right = true;
        }else{
            right = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q)){
            if(canJump){
                spawnDeadPlayer();
            }
        }
        if(Gdx.input.justTouched()){
            displayTorch = !displayTorch;
        }
//        IF GDX.input.isKeyPressed(“D”) THEN
//
//        Velocity.x = -HORIZONTAL_SPEED
//
//
//        Velocity.y -= delta*GRAVITY
//
//        IF GDX.input.isKeyPressed(“W”) THEN
//
//        IF(canJump)
//
//        Velocity.y = JUMP_SPEED
        if(Gdx.input.isKeyPressed(Input.Keys.W)&&canJump){
            velocityY = JUMP_SPEED;
            canJump = false;
        }//make sure checking canJump all the time
        if(left || right || !canJump){
            if(!(left && right)){
                if(left){
                    updateXPos(-HORIZONTAL_SPEED);
                }
                if(right){
                    updateXPos(HORIZONTAL_SPEED);
                }
            }
            if(!canJump){
                updateYPos();
            }else{
                canJump = isEntityOnFloor((int)(playerRect.getX()), (int)(playerRect.getY() - 2));
            }

        }
        if(isCollidingDeadPlayer){
            if(!insideLevel.getLight().getContainsRects()){
                spawnDeadPlayer();
            }
        }
        setX(playerRect.getX() - PLAYER_X_OFFSET);
        setY(playerRect.getY() - PLAYER_Y_OFFSET);
    }
    private void updateXPos(float speed){
        int x = (int)(playerRect.getX() + (speed));
        if(canMoveHere(x, (int)(playerRect.getY()))){
            playerRect.setX(x);
        }else{
            if(isCollidingDeadPlayer){//you may collide with dead player which doesn't have the set grid coordinates as the walls
                //this should be speed
                setXNextToDeadPlayer(velocityX, deadPlayerRect.getX(), deadPlayerRect.getX() + deadPlayerRect.getWidth());
            }else{
                setXNextToWall(speed);
            }
        }
    }
    private void updateYPos(){
        int y = (int)(playerRect.getY() + (velocityY));
        if(canMoveHere((int)(playerRect.getX()), y)){
            velocityY -= GRAVITY;
            playerRect.setY(y);
        }else{
            if(isCollidingDeadPlayer){
                setYNextToDeadPlayer((velocityY), deadPlayerRect.getY(), deadPlayerRect.getY() + deadPlayerRect.getHeight());
            }else{
                setYNextToWall((velocityY));
            }
            //isCollidingDeadPlayer = false;
        }
    }
    private boolean canMoveHere(int x, int y){
        //this is some validation so that you don't go out of bounds
        if(x < 0 ||
                y < 0 ||
                y > (currentLevel.getWindowHeight() - 1)*Map.tileSquareLength + 10 ||//currentLevel refers to a Level object and shows you the window height there
                x+playerRect.getWidth()+10 > (currentLevel.getWindowWidth())*Map.tileSquareLength){//tileSquareLength is length of each tile from tiledMap
            return false;
        };
        int bottomLeft = getObjectAtCell(x,y);//the line below checks if the tile at each corner of the image is an ok one
        if((bottomLeft != 1 && bottomLeft != 6) && bottomLeft != 7){//1, 6, 7 are brick or floor tiles
            int bottomRight = getObjectAtCell((int)(x + playerRect.getWidth()),y);
            if(bottomRight != 1 && bottomRight != 6 && bottomRight != 7){
                int topRight = getObjectAtCell(x,(int)(y + playerRect.getHeight()));
                if(topRight != 1 && topRight != 6 && topRight != 7){
                    int topLeft = getObjectAtCell((int)(x + playerRect.getWidth()),(int)(y + playerRect.getHeight()));
                    if(topLeft != 1 && topLeft != 6 && topLeft != 7){
                        if(!collidesWithDeadPlayer(new Rectangle(x, y, playerRect.getWidth(), playerRect.getHeight()))){
                            isCollidingDeadPlayer = false;
                            checkLight = false;
                            return true;
                        }else{
                            isCollidingDeadPlayer = true;
                            checkLight = true;
                        }
                    }
                    if(topLeft == 6){
                        spawnDeadPlayer();
                    }
                }
                if(topRight == 6){
                    spawnDeadPlayer();
                }
            }
            if(bottomRight == 6){
                spawnDeadPlayer();
            }
        }
        if(bottomLeft == 6){
            spawnDeadPlayer();
        }
        return false;
    }
    private boolean isEntityOnFloor(int x, int y){
        if(y < 0){
            return true;
        }
        int bottomLeft = getObjectAtCell(x,y);
        if((bottomLeft == 1 || bottomLeft == 6) || bottomLeft == 7){
            int bottomRight = getObjectAtCell((int)(x + playerRect.getWidth()),y);
            if(bottomRight == 1 || bottomRight == 6 || bottomRight == 7){
                return true;
            }
        }
        return false;
    }
    private void setXNextToWall(float xSpeed){
        int cellX = (int)(playerRect.getX()/squareLength);
        if(xSpeed > 0){
            float diff = (cellX + 1)*squareLength - playerRect.getX() - playerRect.getWidth();
            playerRect.setX(playerRect.getX() + diff - 1);
        }
        if(xSpeed < 0){
            float diff = playerRect.getX() - (cellX)*squareLength;
            playerRect.setX(playerRect.getX() - diff + 1);
        }
    }
    private void setYNextToWall(double ySpeed){
        int cellY = (int)((playerRect.getY()/Map.tileSquareLength));//the int does floor division
        if(ySpeed > 0){//jumping
            float diff = (cellY + 1)*squareLength - playerRect.getY();
            playerRect.setY(playerRect.getY() + diff - 1);
        }
        if(ySpeed < 0){//falling
            canJump = true;//will be next to floor so you should be able to jump
            velocityY = 0;
            float diff = playerRect.getY() - (cellY)*squareLength;
            playerRect.setY(playerRect.getY() - diff + 1);
        }
    }
    private int getObjectAtCell(int x, int y){
        return board[board.length-(y/Map.tileSquareLength) - 1][x/Map.tileSquareLength];
    }
    private boolean genCanJump(){
        int[] sides = {getObjectBottomLeft(), getObjectBottomRight()};
        for(int num:sides){
            if(num == 1 || num == 7){
                return true;
            }
        }
        return false;
    }
    private int getObjectBottomRight(){//refers to bottom right corner of player Rectangle
        return getObjectAtCell((int)(playerRect.getX()+ playerRect.getWidth()-COLLISION_OFFSET), (int)(playerRect.getY()+COLLISION_OFFSET));
        //collision offset makes sure object's don't overlap
    }
    private int getObjectBottomLeft(){//refers to bottom left corner of player Rectangle
        return getObjectAtCell((int)(playerRect.getX()+COLLISION_OFFSET), (int)(playerRect.getY()+COLLISION_OFFSET));
        //collision offset makes sure object's don't overlap
    }
    private int getObjectTopLeft(){//refers to top left corner of player Rectangle
        return getObjectAtCell((int)(playerRect.getX()+COLLISION_OFFSET), (int)(playerRect.getY()+playerRect.getHeight() - COLLISION_OFFSET));
        //collision offset makes sure object's don't overlap
    }
    private int getObjectTopRight(){//refers to top right corner of player Rectangle
        return getObjectAtCell((int)(playerRect.getX()+ playerRect.getWidth()-COLLISION_OFFSET), (int)(playerRect.getY()+playerRect.getHeight() - COLLISION_OFFSET));
        //collision offset makes sure object's don't overlap
    }
    private void spawnDeadPlayer(){
        deadPlayers.add(new deadPlayer(new Sprite(deadPlayerImg), playerRect));
        resetPlayerPos();
    }
    private void resetPlayerPos(){
        playerRect.setX(Map.tileSquareLength);
        playerRect.setY(Line.towerLevelHeight - Map.tileSquareLength);
        left = false;
        right = false;
        canJump = false;
        velocityX = 0;
        velocityY = 0;
        airSpeed = 0.0f;
    }
    public ArrayList<deadPlayer> getDeadPlayers(){
        return deadPlayers;
    }
    public Rectangle getDeadPlayerRect(){

        return deadPlayerRect;
    }
    public boolean collidesWithDeadPlayer(Rectangle newPlayerRect){
        for(deadPlayer dp : deadPlayers){
            if(dp.collidingWithPlayer(newPlayerRect)){
                deadPlayerRect = dp.getDeadPlayerRect();
                return true;
            }
        }
        return false;
    }
    private void setYNextToDeadPlayer(double ySpeed, float bottomDeadPlayer, float topDeadPlayer){
        if(ySpeed > 0){//you can't use the setY nextToWall because this will appear in random positions and not tiled ones
            float diff =  bottomDeadPlayer- playerRect.getY()-playerRect.getHeight();//you should die even if you hit bottom
            playerRect.setY(playerRect.getY() + diff - 1);
        }
        if(ySpeed < 0){
            canJump = true;//it's as though you are on the floor
            velocityY = 0;//you have to stay at rest
            float diff = playerRect.getY() - topDeadPlayer;
            playerRect.setY(playerRect.getY() - diff + 1);
        }
    }
    private void setXNextToDeadPlayer(float xSpeed, float leftDeadPlayer, float rightDeadPlayer){
        if(xSpeed > 0){
            float diff = leftDeadPlayer - playerRect.getX() - playerRect.getWidth();
            playerRect.setX(playerRect.getX() + diff - 1);
        }
        if(xSpeed < 0){
            float diff = playerRect.getX() - rightDeadPlayer;
            playerRect.setX(playerRect.getX() - diff + 1);
        }
    }
    public float getPlayerMidX(){
        return playerRect.getX() + (playerRect.getWidth() / 2);
    }
    public float getPlayerMidY(){
        return playerRect.getY() + (playerRect.getHeight() / 2);
    }

    public int getMouseX() {
        int x = Gdx.input.getX();
        return Gdx.input.getX();
    }
    public int getMouseY() {
        int y = Gdx.input.getY();
        return Gdx.input.getY();
    }
    public void setDisplayTorch(boolean displayTorch){
        this.displayTorch = displayTorch;
    }
    public void setBoard(int[][] board){
        this.board = board;
    }
    public void setInsideLevel(insideLevel screen){
        this.insideLevel = screen;
    }
    public insideLevel getInsideLevel(){
        return insideLevel;
    }

    public void setOldGridX(int oldGridX) {
        this.oldGridX = oldGridX;
    }

    public void setOldGridY(int oldGridY) {
        this.oldGridY = oldGridY;
    }

    public boolean isBoxPosChanged() {
        return boxPosChanged;
    }

    public void setBoxPosChanged(boolean boxPosChanged) {
        this.boxPosChanged = boxPosChanged;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }
    public void clearDeadPlayers() {
        deadPlayers.clear();
    }
    public Rectangle getPlayerRect(){
        return playerRect;
    }
//    //BUGS
//    - When adding levels sometimes it keeps printing 5,5,5,5,5 or 6,6,6,6 this might something to do with the level placement
//    algorithm being wrong.
//- Moving into levels on map(you need people entering maze through other directly on map
//            - You can't move to the right end of a Sokoban Level sometimes
    //Index -1 out of bounds in Path class line 19
    //Index 0 out of bounds line 80 Path class
    //need add dead players shadowcasting
    //add animations
    //need to make a more accurate shape around the player(sometimes sword goes in like a 1/4 and torch doesn't point but player lives)
    //stars randomly disappearing sometimes???
}
