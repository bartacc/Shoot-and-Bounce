package com.artec.jumpgame.screens;

import com.artec.jumpgame.GameMain;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 27.07.16.
 */
public class LoadingScreen extends ScreenAdapter
{
    private GameMain game;
    private Viewport viewport;
    private LoadingState loadingState;
    private long initialTime;

    private GlyphLayout glyphLayout;
    private final String fullString = "Shoot\nand\nBounce";
    private String currentString;
    private boolean delayAfterCompletion = true;
    private float timeElapsedAfterCompletion;

    public LoadingScreen(GameMain game)
    {
        this.game = game;
        viewport = new ScreenViewport();
        glyphLayout = new GlyphLayout();
    }

    @Override
    public void show()
    {
        timeElapsedAfterCompletion = 0;
        loadingState = LoadingState.LOADING;
        initialTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(Constants.CLEAR_COLOR.r, Constants.CLEAR_COLOR.g, Constants.CLEAR_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(loadingState == LoadingState.LOADING)
        {
            if(!Assets.instance.update() || (delayAfterCompletion && timeElapsedAfterCompletion < Constants.LOADING_SCREEN_DEALAY))
            {
                if(Assets.instance.update())timeElapsedAfterCompletion += delta;
                currentString = "";
                for(int i = 0; i < Assets.instance.getProgress()*fullString.length(); i++)
                {
                    currentString += fullString.charAt(i);
                }
                viewport.apply();
                game.getSpriteBatch().setProjectionMatrix(viewport.getCamera().combined);

                game.getSpriteBatch().begin();
                game.getSpriteBatch().draw(Assets.instance.splashScreenAssets.backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

                BitmapFont font = Assets.instance.splashScreenAssets.titleFont;
                font.getData().setScale(0.5f);
                glyphLayout.setText(font, currentString);
                font.draw(game.getSpriteBatch(), "[#ffffffaa]"+currentString, viewport.getWorldWidth() * 0.1f, viewport.getWorldHeight() * 0.6f,
                        viewport.getWorldWidth() * 0.8f, Align.center, true);

                game.getSpriteBatch().end();
                System.out.println(Assets.instance.getProgress());
            }
            else
            {
                System.out.println("SECONDS: " + Utils.secondsSince(initialTime));
                loadingState = LoadingState.FINISHED;
                Assets.instance.init();
                game.initGameScreen();
            }
        }
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height, true);
    }

    public void setDelayAfterCompletion(boolean isDelayed) {delayAfterCompletion = isDelayed;}

    public LoadingState getLoadingState() {return loadingState;}

    public enum LoadingState {LOADING, FINISHED}
}
