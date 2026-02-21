package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Delaunay.DEdge;
import com.mygdx.game.Delaunay.DPoint;
import com.mygdx.game.Delaunay.Delaunator;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Scanner;

public class Map implements Screen {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Maze maze;
    private ArrayList<DPoint> points;
    private Random rand;
    private int numPoints;
    private int[][] board;
    private ArrayList<Path> paths;
    private Delaunator del;
    private ArrayList<int[][]> objArrs;
    private ArrayList<int[]> objDims;
    private TowerLevel towerLevel;
    private String mapPath;
    private Player player;
    private SpriteBatch spriteBatch;
    private Texture playerAtlas;
    private TextureRegion playerImg;
    private Pixmap origPlayerAtlasPM;
    private Pixmap newPlayerAtlasPM;
    private ArrayList<Star> stars;
    private ArrayList<Level> levels;
    private Texture starAtlas;
    private ArrayList<Box> boxes;
    private Texture boxAtlas;
    public static final int tileSquareLength = 50;
    public static final float mapWindowWidth = (float)(0.8*1500);
    public static final float mapWindowHeight = (float)(0.8*1000);
    private int maxStarCount;
    private Light light;
    private int playerInitGridX;
    private int playerInitGridY;
    private TmxMapLoader mapLoader;
    private MyGdxGame game;
    public static final int boardLength = 70;
    private ArrayList<Sprite> sprites;
    private Boolean enterLevel;
    private int mapGridX;
    private int mapGridY;
    private BitmapFont font;
    SpriteBatch batch2;
    private SpriteBatch batch3;
    private boolean doneStart;
    public Map(MyGdxGame game) throws FileNotFoundException {
        this.game = game;
        spriteBatch = new SpriteBatch();
        stars = new ArrayList<>();
        boxes = new ArrayList<>();
        levels = new ArrayList<>();
        sprites = new ArrayList<>();
        mapPath = "./coursework/assets/coinCollectorMap.tmx";//relative file paths can maybe done with GDX.files.internal(Do research)
        //mapPath = "C://Users//a.nachiappan23//courseworkRepo//coursework//coursework//assets//coinCollectorMap.tmx";
        points = new ArrayList<>();
        rand = new Random();
        numPoints = 9;//this changes the number of levels in the game
        playerInitGridX = 0;
        playerInitGridY = 0;
        board = new int[boardLength][boardLength];
        objArrs = new ArrayList<>();
        objDims = new ArrayList<>();
        for(int i = 0; i <4;i++){
            SokobanLevel sL = new SokobanLevel(40);//the level numbers shows the number of levels on the levels file
            levels.add(sL);
        }
        maze = new Maze(9);//initialise a maze with length of 9
        levels.add(maze);
        for(int i = 0; i < numPoints - 6; i++){//the 6 is to do with the fact that 5 levels have been set already and a tower level is added below
            levels.add(new Maze(9));//fills remaining levels with mazes
        }
        towerLevel = new TowerLevel();
        levels.add(towerLevel);
        for(Level level:levels){
            objArrs.add(level.getBoardArr());//this stuff was created before I created the level class so they are unnecessary now because you can just this from levels array
            objDims.add(new int[]{level.getWindowWidth(), level.getWindowHeight()});
        }
        createPoints();
        createBoard(points);
        origPlayerAtlasPM = new Pixmap(Gdx.files.internal("player_sprites.png"));
        newPlayerAtlasPM = new Pixmap(300, 450, origPlayerAtlasPM.getFormat());//creating a new image with resized version
        newPlayerAtlasPM.drawPixmap(origPlayerAtlasPM,
                0, 0, origPlayerAtlasPM.getWidth(), origPlayerAtlasPM.getHeight(),
                0, 0, newPlayerAtlasPM.getWidth(), newPlayerAtlasPM.getHeight()
        );//making the old image contain the contents of the new image
        playerAtlas = new Texture(newPlayerAtlasPM);
        origPlayerAtlasPM = new Pixmap(Gdx.files.internal("box.jpg"));
        newPlayerAtlasPM = new Pixmap(50, 50, origPlayerAtlasPM.getFormat());
        newPlayerAtlasPM.drawPixmap(origPlayerAtlasPM,
                0, 0, origPlayerAtlasPM.getWidth(), origPlayerAtlasPM.getHeight(),
                0, 0, newPlayerAtlasPM.getWidth(), newPlayerAtlasPM.getHeight()
        );
        starAtlas = new Texture(Gdx.files.internal("object-star.png"));
        boxAtlas = new Texture(newPlayerAtlasPM);
        //playerAtlas = new Texture("C://Users//User//CourseworkRepo//courseworkAN//coursework//assets//player_sprites.png");
        playerImg = new TextureRegion(playerAtlas, 0, 0, 50, 50);
        origPlayerAtlasPM.dispose();
        newPlayerAtlasPM.dispose();//getting the old images out of memory
        //player = new Player(tileSquareLength*sokobanLevel.getPlayerPosGridX(), tileSquareLength*(sokobanLevel.getLevelLength()-1-sokobanLevel.getPlayerPosGridY()), sokobanLevel.getPlayerPosGridX(), sokobanLevel.getPlayerPosGridY(), sokobanLevel.getSokLevelArr(), tileSquareLength, new Sprite(playerImg), this);
        //In the line of code below the inital grid x and y positions are changed so that they are displayed on top of the tower level
        player = new Player(playerInitGridX*tileSquareLength, (boardLength - playerInitGridY) * tileSquareLength, playerInitGridX, playerInitGridY, board, tileSquareLength, new Sprite(playerImg), this);
        //System.out.println("(" + String.valueOf((points.get(0).x) - (int)(levelLength/2)) + ", " + String.valueOf((int)(points.get(0).y) - (int)(levelLength/2) - 1) + ")");
        //System.out.println("(" + String.valueOf((points.get(5).x) - (int)(levelLength/2)) + ", " + String.valueOf((int)(points.get(5).y) - (int)(levelLength/2) - 1) + ")");
        paths = new ArrayList<>();
        for(DEdge edge: del.getEdges()){
            paths.add(new Path((int)(edge.getPointsA()[0]) - (int)(edge.a.getLevelWidth()/2), (int)(edge.getPointsA()[1]) - (int)(edge.a.getLevelLength()/2) - 1, (int)(edge.getPointsB()[0])- (int)(edge.b.getLevelWidth()/2), (int)(edge.getPointsB()[1])- (int)(edge.b.getLevelLength()/2) - 1, boardLength, board));
        }
        createEntranceLevels();
        try {
            writeToMap(board);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int counter = 0;
        for(int[][] arr:objArrs){
            game.createStars(arr, stars, boxes, player, starAtlas, boxAtlas, levels.get(counter).getActualEntranceLevelX(), levels.get(counter).getActualEntranceLevelY());
            counter += 1;
        }
        mapLoader = new TmxMapLoader();
        //C://Users//User//CourseworkRepo//courseworkAN//coursework//assets//coinCollectorMap.tmx
        tiledMap = mapLoader.load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch2 = new SpriteBatch();
        font = new BitmapFont();
        doneStart = true;
        playerInitGridY = levels.get(2).getActualEntranceLevelY()+1;
        playerInitGridX = levels.get(2).getActualEntranceLevelX();
    }
    @Override
    public void show() {//this is the function that get's called when a screen is set everytime
        // (the constructor is called when something new called and then most of the time never again because new not mentioned
        enterLevel = false;
        player.setGridX(playerInitGridX);
        player.setGridY(playerInitGridY);
        player.setOldGridY(playerInitGridY);//chose to do directions by subtracting oldX rather than using booleans for left, right...
        player.setY((boardLength - playerInitGridY) * tileSquareLength);
        player.setX(playerInitGridX * tileSquareLength);
        Gdx.graphics.setWindowedMode((int)(0.8*1500), (int)(0.8*1000));
        camera = new OrthographicCamera();
        camera.setToOrtho(false,(int)(0.8*1500), (int)(0.8*1000));
        camera.viewportWidth = boardLength*tileSquareLength;
        camera.viewportHeight = boardLength*tileSquareLength;
        camera.update();
        if(batch3 != null){
            batch3.dispose();
        }
        batch3 = new SpriteBatch();

//        try {//for some reason changing screens has to be called here
//            levels.get(2).enterLevel(player, this);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }

    }

    @Override
    public void render(float delta) {

        if(!doneStart) {//executes only for the first time map is shown
            if(!Gdx.input.isTouched()) {
                batch2.begin();
                font.draw(batch2, "WASD controls exist\n" +
                        "Press D when you get to a door to get into a level\n", //the message
                        (int) (0.4 * mapWindowWidth), (int) (0.4 * mapWindowHeight));//centres with centre of board
                batch2.end();//sprite batch to render this, rendering everything in one sprite batch doesn't seem to work
            }else{
                doneStart = true;
            }
        }else{
            sprites.clear();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch3.setProjectionMatrix(camera.combined);
            batch3.begin();
            spriteBatch.begin();
            try {
                player.draw(spriteBatch);
                //for(deadPlayer dps:player.getDeadPlayers()){
                    //dps.draw(batch3);
                //}
                for(Star star:stars){
                    star.draw(batch3);
                }
                //for(Box box:boxes){
                //box.draw(batch3);
                //}
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            if(player.getLevelName().equals("Outside")){
                camera.position.set(player.getX(), player.getY(), 0);
            }
            camera.update();
            renderer.setView(camera);
            renderer.render();
            spriteBatch.end();
            batch3.end();
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();

    }
    @Override
    public void dispose() {
        font.dispose();
    }
    private void createPoints() {
        //These are the four fixed points
        //All points are made so that they are in the middle of the level
        points.add(new DPoint((int)(objDims.get(0)[0]/2) + 1, boardLength - (int)((objDims.get(0)[1]/2 + 1))));//bottom-left
        points.add(new DPoint((int)(objDims.get(1)[0]/2) + 1, (int)((objDims.get(1)[1]/2) + 2)));//top-left
        points.add(new DPoint(boardLength - (int)(objDims.get(2)[0]/2 + 1), ((int)(objDims.get(2)[1]/2) + 2)));//top-right
        points.add(new DPoint(boardLength - (int)(objDims.get(3)[0]/2 + 1), boardLength - (int)(objDims.get(3)[1]/2 + 1)));//bottom-right
        int randX = 0, randY = 0;
        boolean canAdd;
        int objCount = 4;//index is 4 because 0 to 3 are occupied by the fixed points
        for(int b = 4; b < numPoints;b++){//selects elements to insert
            canAdd = false;
            while(!canAdd){
                canAdd = true;//set to true so that if no changes happen you will break out
                randX = rand.nextInt(boardLength - 2*((int)(objDims.get(objCount)[0]/2)+1) + 1) + ((int)((objDims.get(objCount)[0]/2))) ;
                randY = rand.nextInt(boardLength - 2*((int)((objDims.get(objCount)[1]/2))+1) + 1) + ((int)((objDims.get(objCount)[1]/2))) ;
                int c = 0;

                while(c < b){//these are the items being compared with(first item added does not compared because of the strict sign
                    //The c allows comparison of all items previously added
                    int diffX = (int)(Math.sqrt(Math.pow(randX - (int)(points.get(c).x), 2)));//gets magnitude of distance
                    int diffY = (int)(Math.sqrt(Math.pow((randY - (int)(points.get(c).y)), 2)));
                    if(diffX <= (objDims.get(objCount)[0]) && diffY <= ((objDims.get(objCount)[1]))){
                        canAdd = false;//changes the boolean variable which means the previous code will not run again
                        c = b;
                    }
                    c++;
                }
            }
            points.add(new DPoint(randX, randY));//here it will be a valid point(as it would have been changed in while loop) so added
            objCount += 1;
        }
        objCount = 0;
        for(DPoint point: points){
            point.setLevelWidth(objDims.get(objCount)[0]);
            point.setLevelLength(objDims.get(objCount)[1]);
            objCount += 1;
        }
        del = new Delaunator(points);//initialising the delaunay triangles
        //call path with all the points by getting all the edges


    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
    }

    private void createBoard(ArrayList<DPoint> points){
        for(int i = 0; i < boardLength; i++){
            for(int j = 0; j < boardLength; j++){
                board[j][i] = 1;
            }
        }
        int objCount = 0;
        for(DPoint p : points){
            //the points from Delaunay triangulation has been generated so that they represent the middle of each level
            //In hindsight I should have made them generate the points at the entrance to the levels
            int endValX = 0;//this variable will contain the value for which you should stop writing the level inside the board array
            int endValY = 0;
            int intLW = (int)(objDims.get(objCount)[0]/2);//gets half of level width
            int intLL = (int)(objDims.get(objCount)[1]/2);
            if(objDims.get(objCount)[0] % 2 == 1){//checks if width is odd
                endValX =  (int)(p.x + intLW + 1);//if it is middle is 1 + floor divide operation
            }
            else{
                endValX =  (int)(p.x + intLW);
            }
            this.playerInitGridX = endValX - objDims.get(objCount)[0];
            levels.get(objCount).setEntranceLevelX(playerInitGridX);
            levels.get(objCount).setActualEntranceLevelX(playerInitGridX);
            if(objDims.get(objCount)[1] % 2 == 1){//does the same as above but for height
                endValY =  (int)(p.y + intLL + 1);
            }
            else{
                endValY =  (int)(p.y + intLL);
            }
            this.playerInitGridY = endValY - objDims.get(objCount)[1];
            levels.get(objCount).setEntranceLevelY(playerInitGridY - 1);
            levels.get(objCount).setActualEntranceLevelY(playerInitGridY - 1);
            int zI = 0;
            int zJ = 0;
            for(int i = (int)(p.y - intLL); i < endValY; i++){
                zJ = 0;//These extra variables are used instead of just i and j because the other level arrays are start from 0,0
                //while in the map board the level starts from sum random x coordinate and y coordinate
                for(int j = (int)(p.x - intLW); j < endValX; j++){
                    board[i][j] = objArrs.get(objCount)[zI][zJ];//writing map board with the level information
                    zJ += 1;
                }
                zI += 1;
            }

            objCount += 1;
        }

    }
    public void writeToMap(int[][] board) throws FileNotFoundException {
        File testFile = new File(mapPath);
        Scanner myReader = new Scanner(testFile);
        StringBuilder fileContent = new StringBuilder();
        int boardWidth = board[0].length;
        int boardLength = board.length;//maze is always supposed to be a square that's why width = length
        //Code belowis called xml
        //It maps tile numbers to images. It can be interpreted by the tile map renderer.
        String prevText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<map version=\"1.10\" tiledversion=\"1.11.0\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\""  + String.valueOf(boardWidth) + "\" height=\"" + String.valueOf(boardLength) + "\" tilewidth=\"" + String.valueOf(tileSquareLength) +"\" tileheight=\"" + String.valueOf(tileSquareLength) + "\" infinite=\"0\" nextlayerid=\"2\" nextobjectid=\"1\">\n" +
                " <tileset firstgid=\"1\" source=\"Brick.tsx\"/>\n" +
                " <tileset firstgid=\"2\" source=\"correctLadder.tsx\"/>\n" +
                " <tileset firstgid=\"3\" source=\"Star.tsx\"/>\n" +
                " <tileset firstgid=\"4\" source=\"Ladder.tsx\"/>\n" +
                " <tileset firstgid=\"5\" source=\"box.tsx\"/>\n" +
                " <tileset firstgid=\"6\" source=\"Triangle.tsx\"/>\n" +
                " <tileset firstgid=\"7\" source=\"innerTile.tsx\"/>\n" +
                " <tileset firstgid=\"8\" source=\"Entrance.tsx\"/>\n" +
                " <tileset firstgid=\"9\" source=\"box.tsx\"/>\n" +
                " <layer id=\"1\" name=\"Tile Layer 1\" width=\"" + String.valueOf(boardWidth) + "\" height=\"" + String.valueOf(boardLength) + "\">\n" +
                "  <data encoding=\"csv\">\n";
        fileContent.append(prevText);
        for(int y = 0; y<boardLength; y++){//boardLength
            for(int x = 0; x<boardWidth; x++){//boardLength
                fileContent.append(String.valueOf(board[y][x]) + ",");//fileContent is a string which will be written to file at end
            }
            fileContent.append("\n");//adding a new line to the file
        }
        fileContent.append("</data>\n" +
                " </layer>\n" +
                "</map>\n");
        myReader.close();//I think the reader stuff is redundant code
        //"C://Users//a.nachiappan23//courseworkRepo//coursework//coursework//assets//coinCollectorMap.tmx"
        //"C:/Users/User/CourseworkRepo/courseworkAN/coursework/assets/coinCollectorMap.tmx"
        try {
            FileWriter myWriter = new FileWriter(mapPath);//will write to file if file path given
            myWriter.write(String.valueOf(fileContent));
            myWriter.close();
        } catch (IOException e) {//exception handler if there is some wrong input
            e.printStackTrace();
        }
    }


    public ArrayList<Box> getBoxes() {
        return boxes;
    }
    public ArrayList<Star> getStars(){
        return stars;
    }
    public int getMaxStarCount(){
        return maxStarCount;
    }
    public int getBoardLength(){
        return boardLength;
    }
    public Light getLight(){
        return light;
    }
    public ArrayList<int[]> getTowerPoints(){
        return towerLevel.getTowerPoints();
    }
    public ArrayList<int[][]> getTowerEdges() {
        return towerLevel.getTowerEdges();
    }
    private void createEntranceLevels(){
        for(Level level:levels){
            board[level.getEntranceLevelY()][level.getEntranceLevelX()] = 8;//this is the door tile

        }
    }
    public void reloadMap(Level level){//what is called when you enter a level to reload the map file and change screens
        enterLevel = true;
        playerInitGridX = player.getGridX();
        playerInitGridY = player.getGridY();
        if(level.getLevelName().equals("Tower Level")){
            playerInitGridY -= 1;
            player.clearDeadPlayers();//should really have been using getDeadPlayers() in player
        }
        game.setScreen(new insideLevel(player, game, level));
        //Gdx.graphics.setWindowedMode(Line.towerLevelWidth, Line.towerLevelHeight);
    }
    public void enteringOriginalMap() throws FileNotFoundException {//called from insideLevel when player exits or finishes level
        player.setLevelName("Outside");
        player.setBoard(board);//needed for collision detection
        player.setGridX(playerInitGridX);//this is the coordinates that the player previously was in before entering level
        player.setGridY(playerInitGridY);
        writeToMap(board);
        game.setScreen(this);//look up on disposing resources and stuff -> doesn't seem to make any difference here, but important for memory management
        //batch3.end();
    }

    public Texture getStarAtlas() {
        return starAtlas;
    }

    public Texture getBoxAtlas() {
        return boxAtlas;
    }

    public int[][] getBoard() {
        return board;
    }
}
