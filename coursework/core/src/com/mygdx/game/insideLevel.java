package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import py4j.GatewayServer;


public class insideLevel implements Screen{
    private Player player;
    private String mapPath;
    private SpriteBatch spriteBatchPlayer;
    private SpriteBatch dpBatch;
    private Light light;
    private ShapeRenderer shapeRenderer;
    private TmxMapLoader mapLoader;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMap tiledMap;
    private MyGdxGame game;
    private ArrayList<Sprite> thingsToRender;
    private ArrayList<Star> stars;
    private ArrayList<Box> boxes;
    private Level level;
    private SpriteBatch boxBatch;
    private BitmapFont font;
    private SpriteBatch batch2;
    private Boolean newSLLevel;
    private Boolean first;
    public insideLevel(Player player, MyGdxGame game, Level level){

        this.player = player;
        this.level = level;
        this.player.playerRect.setX((level.getInitPlayerPosGridX())*Map.tileSquareLength + Player.PLAYER_X_OFFSET);
        this.player.playerRect.setY((level.getInitPlayerPosGridY()+1)*Map.tileSquareLength + Player.PLAYER_Y_OFFSET);//the +1 is there because I think the initGridY corresponds to top right of image
        this.player.setX((level.getInitPlayerPosGridX())*Map.tileSquareLength);
        this.player.setY((level.getInitPlayerPosGridY()+1)*Map.tileSquareLength);//these gridY values are the display values and not the actual ones
        this.player.setGridY(level.getWindowHeight() - level.getInitPlayerPosGridY() - 1);//the -1 is there because window height just gives you the height and not the indexed one while the coordinate is indexed
        this.player.setGridX(level.getInitPlayerPosGridX());
        this.player.setOldGridY(level.getWindowHeight() - level.getInitPlayerPosGridY() - 1);
        this.player.setOldGridX(level.getInitPlayerPosGridX());
        System.out.println("Early");
        System.out.println(this.player.getX());
        System.out.println(this.player.getY());
        System.out.println(level.getWindowHeight());
        this.game = game;
        spriteBatchPlayer = new SpriteBatch();//sprite batches allow you to render the sprites
        dpBatch = new SpriteBatch();
        boxBatch = new SpriteBatch();
        stars = new ArrayList<>();
        boxes = new ArrayList<>();
        game.createStars(level.getBoardArr(), stars, boxes, this.player, player.getMap().getStarAtlas(), player.getMap().getBoxAtlas(), 0, 0);
        batch2 = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        //temporary code that should be removed after sokoban solver trained
        GatewayServer gatewayServer = new GatewayServer(this);
        gatewayServer.start();
        System.out.println("Server started");
        newSLLevel = false;
    }
    @Override
    public void show() {
        mapPath = "coursework/assets/coinCollectorMap.tmx";
        mapLoader = new TmxMapLoader();
        tiledMap = mapLoader.load(mapPath);
        shapeRenderer = new ShapeRenderer();
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        light = new Light(player);
        camera = new OrthographicCamera();
        //CHANGEF
        camera.setToOrtho(false,level.getWindowWidth()*Map.tileSquareLength, level.getWindowHeight()*Map.tileSquareLength);
        camera.update();
        Gdx.graphics.setWindowedMode(level.getWindowWidth()*Map.tileSquareLength, level.getWindowHeight()*Map.tileSquareLength);
        player.setInsideLevel(this);
        System.out.println("IN inside level");
    }

    @Override
    public void render(float delta) {

        if(!level.getDoneStart()) {//ensures that you don't get the message displaying everytime you enter the level
            Gdx.gl.glClearColor(0, 0, 0, 1);//makes screen black
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if(!Gdx.input.isTouched()) {//checks if a trackpad hit and does below code if not
                batch2.begin();//batch passes in stuff to draw
                font.draw(batch2, level.levelInstructions, (int) (0.5*level.getWindowWidth()*Map.tileSquareLength - (double) font.getRegion().getRegionWidth() /2), (int) (0.5 * level.getWindowHeight()*Map.tileSquareLength + (double) font.getRegion().getRegionHeight() /2));
                batch2.end();
            }else{
                level.setDoneStart(true);
            }
        }else {

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                try {
                    player.setStarCount(0);
                    Gdx.graphics.setWindowedMode(1200, 800);
                    player.getMap().enteringOriginalMap();
                } catch (FileNotFoundException e) {//these file not found exceptions are there because of the file used in rendering the board
                    throw new RuntimeException(e);
                }
            }
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            spriteBatchPlayer.begin();
            dpBatch.begin();
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 1, 1);
            if (player.getLevelName().equals("Tower Level")) {//this is not great, what I should've done was make each Level inherit screen class then I wouldn't have needed these if statements in update
                if (player.getDisplayTorch()) {
                    light.update();
                    float[][] values = light.getTrianglePointsArray();
                    for (int i = 0; i < values.length - 1; i++) {
                        shapeRenderer.triangle(player.getPlayerMidX(), player.getPlayerMidY(), values[i][0], values[i][1], values[i + 1][0], values[i + 1][1]);
                    }//this repeats with stuff in map class so aim to reduce repetition next time
                }
                for (deadPlayer dps : player.getDeadPlayers()) {
                    dps.draw(dpBatch);
                }
            }
            for (Star star : stars) {
                try {
                    star.draw(game.batch);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                player.draw(spriteBatchPlayer);
            } catch (FileNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            //if(player.getLevel().equals("Outside")){
            //camera.position.set(player.getX(), player.getY(), 0);
            //}
            //camera.update();
            if (player.isBoxPosChanged()) {//for sokoban levels the board array will have to be changed when box is moved
                //so I thought I could use this to render change on screen rather than using draw method in Box
                tiledMap = mapLoader.load(mapPath);
                renderer = new OrthogonalTiledMapRenderer(tiledMap);
                player.setBoxPosChanged(false);
            }
            //you have to make sure that you end all the batches before setWindowed mode or entering new screens
            renderer.setView(camera);
            renderer.render();
            game.batch.end();
            spriteBatchPlayer.end();
            dpBatch.end();
            shapeRenderer.end();
            if(newSLLevel){
                tiledMap = mapLoader.load(mapPath);
                renderer = new OrthogonalTiledMapRenderer(tiledMap);
                camera.setToOrtho(false,level.getWindowWidth()*Map.tileSquareLength, level.getWindowHeight()*Map.tileSquareLength);
                camera.update();
                Gdx.graphics.setWindowedMode(level.getWindowWidth()*Map.tileSquareLength, level.getWindowHeight()*Map.tileSquareLength);
                newSLLevel = false;
            }

        }

    }
    public Light getLight(){
        return light;
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        font.dispose();
    }

    public ArrayList<Star> getStars() {
        return stars;
    }

    public void setStars(ArrayList<Star> stars) {
        this.stars = stars;
    }

    public ArrayList<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(ArrayList<Box> boxes) {
        this.boxes = boxes;
    }
    public void sayHello(){
        System.out.println("Hello");
    }
    public Player getPlayer() {
        return player;
    }
    public Level getLevel(){
        return level;
    }
    public void createNewLevel(int levelNum) throws FileNotFoundException {
        level = new SokobanLevel(levelNum);
        level.setDoneStart(true);
        this.player.playerRect.setX((level.getInitPlayerPosGridX())*Map.tileSquareLength + Player.PLAYER_X_OFFSET);
        this.player.playerRect.setY((level.getInitPlayerPosGridY())*Map.tileSquareLength + Player.PLAYER_Y_OFFSET);//the +1 is there because I think the initGridY corresponds to top right of image
        this.player.setX((level.getInitPlayerPosGridX())*Map.tileSquareLength);
        this.player.setY((level.getInitPlayerPosGridY())*Map.tileSquareLength);//these gridY values are the display values and not the actual ones
        this.player.setGridY(level.getWindowHeight() - level.getInitPlayerPosGridY());//this works but i think there should be a -1 like in the constructor don't understand what's different
        this.player.setGridX(level.getInitPlayerPosGridX());
        this.player.setOldGridY(level.getWindowHeight() - level.getInitPlayerPosGridY());
        this.player.setOldGridX(level.getInitPlayerPosGridX());
        player.setBoard(level.getBoardArr());
        player.setCurrentLevel(level);
        player.getMap().writeToMap(level.getBoardArr());
        stars = new ArrayList<>();
        boxes = new ArrayList<>();
        game.createStars(level.getBoardArr(), stars, boxes, this.player, player.getMap().getStarAtlas(), player.getMap().getBoxAtlas(), 0, 0);
        newSLLevel = true;
        this.player.setStarCount(0);
    }
}
