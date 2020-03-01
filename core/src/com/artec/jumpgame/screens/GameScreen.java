package com.artec.jumpgame.screens;

import com.artec.jumpgame.GameMain;
import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.objects.Shockwave;
import com.artec.jumpgame.overlays.ChromaticAberration;
import com.artec.jumpgame.overlays.CreditsOverlay;
import com.artec.jumpgame.overlays.DeathOverlay;
import com.artec.jumpgame.overlays.GameChangersShopOverlay;
import com.artec.jumpgame.overlays.MenuOverlay;
import com.artec.jumpgame.overlays.PauseOverlay;
import com.artec.jumpgame.overlays.TutorialFinishedOverlay;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.GLScripts;
import com.artec.jumpgame.utils.PlaySounds;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by bartek on 01.06.16.
 */
public class GameScreen extends ScreenAdapter
{
    private GameMain gameMain;
    private Level level;


    private TextureRegion fboRegion;
    private FrameBuffer fbo; //our POT frame buffer with a color texture attached
    private ShaderProgram shaderProgram;
    private Shockwave shockwave;


    private DeathOverlay deathOverlay;
    private PauseOverlay pauseOverlay;
    private MenuOverlay menuOverlay;
    private TutorialFinishedOverlay tutorialFinishedOverlay;
    private GameChangersShopOverlay gameChangersShopOverlay;
    private CreditsOverlay creditsOverlay;

    private ShapeRenderer renderer;
    private SpriteBatch batch;

    private InputMultiplexer inputMultiplexer;

    private Enums.GameState gameState;


    public GameScreen(GameMain gameMain, SpriteBatch batch, ShapeRenderer renderer)
    {
        this.gameMain = gameMain;
        this.renderer = renderer;
        this.batch = batch;


        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);

        shockwave = new Shockwave();
        //ShaderProgram.pedantic = false;

        shaderProgram = new ShaderProgram(GLScripts.VERT_BASIC, GLScripts.FRAG_BASIC);// Gdx.files.internal("GLScripts/FRAG.glsl").readString());
        //shaderProgram.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (!shaderProgram.isCompiled()) {
            System.err.println(shaderProgram.getLog());
            System.exit(0);
        }
        if (shaderProgram.getLog().length()!=0)
            System.out.println(shaderProgram.getLog());
        //batch.setShader(shaderProgram);
        batch.setShader(shaderProgram);


        Gdx.input.setCatchBackKey(true);
        inputMultiplexer = new InputMultiplexer();
        level = new Level(this);
        deathOverlay = new DeathOverlay(new ScreenViewport(), this);
        pauseOverlay = new PauseOverlay(new ScreenViewport(), this);
        menuOverlay = new MenuOverlay(new ScreenViewport(), this);
        menuOverlay.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        tutorialFinishedOverlay = new TutorialFinishedOverlay(new ScreenViewport(), this);
        gameChangersShopOverlay = new GameChangersShopOverlay(new ScreenViewport(), this);
        creditsOverlay = new CreditsOverlay(new ScreenViewport(), this);
        //gameChangersShopOverlay.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Enums.Upgrade.load();
        Enums.Upgrade.save();
        Enums.Upgrade.currentUpgrade.applyValues();

        menu(true);

        //To clear best score uncomment following
        //Gdx.app.getPreferences(Constants.PREFERENCES_SCORE).clear();
    }

    @Override
    public void show()
    {
        reload();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(Constants.CLEAR_COLOR.r, Constants.CLEAR_COLOR.g, Constants.CLEAR_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(gameState != Enums.GameState.PAUSE && gameState != Enums.GameState.FINISHED_TUTORIAL) level.update(delta);


        //Gdx.gl20.glBlendFuncSeparate(GL20.GL_SRC_ALPHA,
        //        GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);


        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setShader(null);

        //fbo.begin();
        level.render(batch);
        //fbo.end();

        //fboRegion.setTexture(fbo.getColorBufferTexture());

        //System.out.println("X:"+shockwave.getPosition().x+",  Y:"+shockwave.getPosition().y+",  T:"+shockwave.getElapsedTime());
        //shaderProgram.setUniformf("center", shockwave.getPosition());
        //shaderProgram.setUniformf("shockParams", shockwave.getShockWaveParams());
        //shaderProgram.setUniformf("time", shockwave.getElapsedTime());
        //batch.setShader(shaderProgram);


        /*
        batch.begin();

            batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
            //batch.setColor(1, 1, 1, 1f);
            batch.draw(fboRegion, 0, 0);

        batch.end();
        */

        batch.setColor(1, 1, 1, 1f);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setShader(null);

        shockwave.update(delta);
        ChromaticAberration.update(delta);


        //level.debugRender(renderer);
        if(gameState == Enums.GameState.DEAD)
        {
            deathOverlay.update(delta);
            deathOverlay.render(batch);
        }
        if(gameState == Enums.GameState.PAUSE)
        {
            pauseOverlay.update(delta);
            pauseOverlay.render(batch);
        }
        if(gameState == Enums.GameState.MENU || menuOverlay.isVisible())
        {
            menuOverlay.update(delta);
            menuOverlay.render(batch);
        }
        if(gameState == Enums.GameState.FINISHED_TUTORIAL)
        {
            tutorialFinishedOverlay.update(delta);
            tutorialFinishedOverlay.render(batch);
        }
        if(gameState == Enums.GameState.GAME_CHANGERS_SHOP)
        {
            gameChangersShopOverlay.update(delta);
            gameChangersShopOverlay.render(batch);
        }
        if(gameState == Enums.GameState.CREDITS)
        {
            creditsOverlay.render(batch);
        }
    }

    @Override
    public void resize(int width, int height)
    {
        level.resize(width, height);
        deathOverlay.resize(width, height);
        pauseOverlay.resize(width, height);
        menuOverlay.resize(width, height);
        tutorialFinishedOverlay.resize(width, height);
        gameChangersShopOverlay.resize(width, height);
        creditsOverlay.resize(width, height);
    }

    @Override
    public void hide()
    {

    }

    @Override
    public void pause()
    {
        if(gameState == Enums.GameState.PLAYING)
        pause(level.getCurrentScore(), level.getLevelState());
    }

    @Override
    public void dispose()
    {

    }

    public void showCredits()
    {
        Gdx.input.setInputProcessor(creditsOverlay);
        creditsOverlay.init();
        gameState = Enums.GameState.CREDITS;
        if(gameMain.gameOnAndroid) gameMain.androidHandler.showAds(false);
    }

    public void gameChangersShop()
    {
        Gdx.input.setInputProcessor(gameChangersShopOverlay);
        gameChangersShopOverlay.init();
        gameState = Enums.GameState.GAME_CHANGERS_SHOP;
        if(gameMain.gameOnAndroid) gameMain.androidHandler.showAds(true);
    }

    public void playFromMenu(int screenX, int screenY)
    {
        PlaySounds.playGameplayBackgroundMusic();
        gameState = Enums.GameState.PLAYING;
        Gdx.input.setInputProcessor(inputMultiplexer);
        level.getPlayer().touchDown(screenX, screenY,0 ,0);
        if(gameMain.gameOnAndroid) gameMain.androidHandler.showAds(false);
    }
    public void play(Level.LevelState levelState)
    {
        if(gameMain.gameOnAndroid) gameMain.androidHandler.showAds(false);
        if(levelState != Level.LevelState.NORMAL)
        {
            Enums.Upgrade.NORMAL.applyValues();
        }
        reloadLevel(levelState);
        Gdx.input.setInputProcessor(inputMultiplexer);
        gameState = Enums.GameState.PLAYING;
    }

    public void reloadLevel(Level.LevelState levelState)
    {
        inputMultiplexer.clear();
        level.init(levelState);
    }

    public void menu(boolean initializeLevel)
    {
        PlaySounds.playMenuBackgroundMusic();
        if(initializeLevel) play(Level.LevelState.NORMAL);
        gameState = Enums.GameState.MENU;
        menuOverlay.init();
        Gdx.input.setInputProcessor(menuOverlay);
        if(gameMain.gameOnAndroid) gameMain.androidHandler.showAds(true);
    }

    public void pause(int currentScore, Level.LevelState levelState)
    {
        gameState = Enums.GameState.PAUSE;
        //PlaySounds.init();
        pauseOverlay.init(currentScore, levelState);
        if(gameMain.gameOnAndroid) gameMain.androidHandler.showAds(true);
    }

    public void unPause()
    {
        Gdx.input.setInputProcessor(inputMultiplexer);
        gameState = Enums.GameState.PLAYING;
        if(gameMain.gameOnAndroid) gameMain.androidHandler.showAds(false);
    }

    public void gameOver(int score, Enums.DeathReason deathReason)
    {
        gameState = Enums.GameState.DEAD;
        deathOverlay.init(score, level.getLevelState(), deathReason);
        if(gameMain.gameOnAndroid) gameMain.androidHandler.showAds(true);
    }

    public void finishedTutorial()
    {
        gameState = Enums.GameState.FINISHED_TUTORIAL;
        tutorialFinishedOverlay.init();
        if(gameMain.gameOnAndroid) gameMain.androidHandler.showAds(false);
    }

    public void reload()
    {
        this.deathOverlay.reload();
        this.menuOverlay.reload();
        this.pauseOverlay.reload();
        this.tutorialFinishedOverlay.reload();
        this.gameChangersShopOverlay.reload();
        this.creditsOverlay.reload();
        level.reload();
        PlaySounds.reloadBackgroundMusic();
    }


    public void startShockwave(Vector2 coordinates)
    {
        shockwave.start(coordinates);
    }


    public GameMain getGameMain() {return gameMain;}
    public Enums.GameState getGameState() {return gameState;}
    public InputMultiplexer getInputMultiplexer() {return inputMultiplexer;}
}
