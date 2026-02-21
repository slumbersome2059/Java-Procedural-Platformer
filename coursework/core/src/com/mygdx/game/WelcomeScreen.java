package com.mygdx.game;

import com.badlogic.gdx.Game;
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

import java.io.FileNotFoundException;

public class WelcomeScreen implements Screen {
    private Table table;
    private Skin skin;
    private Stage stage;
    private Label title;
    private TextButton textButton;
    private TextButton helpButton;
    private MyGdxGame game;
    private OrthographicCamera camera;
    public WelcomeScreen(MyGdxGame game) {
        Table table = new Table();//for help on table this tutorial is great https://github.com/EsotericSoftware/tablelayout?tab=readme-ov-file#fill
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table.setFillParent(true);
        stage.addActor(table);
        textButton = new TextButton("Play", skin);
        helpButton = new TextButton("Help", skin);
        title = new Label("Random Games", skin);
        title.setFontScale(3f);
        table.add(title).center().expand();
        table.row();
        table.add(textButton).center().width(200).height(75).expand();
        table.row();
        table.add(helpButton).center().width(200).height(75).expand();
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.2f, 0.5f, 1f, 1); // Blue background
        stage.draw();
        if(textButton.isPressed()){
            try {
                dispose();
                game.setScreen(new Map(game));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if(helpButton.isPressed()){
            dispose();
            game.setScreen(new HelpScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
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