package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class HelpScreen implements Screen {
    private MyGdxGame game;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Stage stage;
    private Table table;
    private Skin skin;
    private TextButton backButton;

    public HelpScreen(MyGdxGame game) {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Label title = new Label("Instructions", skin);
        title.setFontScale(2);
        Label bp1 = new Label("- Use WASD to navigate", skin);
        Label bp2 = new Label("- Enter through entrance door (get to this door and press down) to get inside a smaller level", skin);
        Label bp3 = new Label("- For maze level eat up all the stars then press E to exit", skin);
        Label bp4 = new Label("- For tower level, use mouse to click and activate torch and move mouse to move torch and if the torch touches dead player, player won't die and try to get player to star", skin);
        Label bp5 = new Label("- For the Sokoban level, move the boxes onto the stars", skin);
        backButton = new TextButton("Back", skin);
        table.add(title).center();
        table.row();
        table.add(bp1).center();
        table.row();
        table.add(bp2).center();
        table.row();
        table.add(bp3).center();
        table.row();
        table.add(bp4).center().expandX().fillX();
        table.row();
        table.add(bp5).center();
        table.row();
        table.add(backButton).left().expandY().bottom().height((float)(0.1 * Map.mapWindowHeight)).width((float)(0.05 * Map.mapWindowWidth));
        bp4.setWrap(true);
        stage.addActor(table);
        this.game = game;

    }
    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.3f, 0.5f, 1); // Dark blue background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        if(backButton.isPressed()){
            dispose();
            game.setScreen(new WelcomeScreen(game));
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
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}