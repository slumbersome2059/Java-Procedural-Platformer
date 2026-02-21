package com.mygdx.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.mygdx.game.Map.tileSquareLength;

public class MyGdxGame extends Game {
	SpriteBatch batch;
	private Maze maze;
	private TmxMapLoader mapLoader;
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private Table table;
	private Skin skin;
	private Stage stage;
	private Label title;
	private TextButton textButton;
	private TextButton helpButton;


	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new WelcomeScreen(this));
		//for stuff UI things like buttons rather than creating sprites and sprite batches the same thing will happen
		//again and again so click button and so on so you use prebuilt buttons add it with other UI to stages which will be on a table
    }

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
	}
	public void pause(){
		super.pause();
	}
	public void resume(){
		super.resume();
	}
	public void createStars(int[][] board, ArrayList<Star> stars, ArrayList<Box> boxes, Player player, Texture starAtlas, Texture boxAtlas, int offsetX, int offsetY){
		//This method goes through the array and adds it to current stars, if outside it's passed each board and has to add the offset on to generate x and y
		int boardLength = Map.boardLength;
		int drawLowerByNum = 2;
		if(!player.getLevelName().equals("Outside")){
			boardLength = board.length;
			drawLowerByNum = 1;
		}
		for(int j = 0; j < board.length; j++){
			for(int i = 0; i < board[0].length; i++){
				if(board[j][i] == 3 || board[j][i] == 9){
					stars.add(new Star(i, j, (i+offsetX)*tileSquareLength, (boardLength - j - drawLowerByNum - offsetY)*tileSquareLength, player, board, new Sprite(starAtlas)));
				}
				if(board[j][i] == 5 || board[j][i] == 9){
					boxes.add(new Box(i, j, (i)*tileSquareLength, (boardLength - j - drawLowerByNum)*tileSquareLength, player, board, new Sprite(boxAtlas)));
				}
			}
		}
		for(Box box:boxes){
			box.setBoxes(boxes);
		}
	}

}
