package com.artec.jumpgame.overlays;

import com.artec.jumpgame.UIelements.DialogWindow;
import com.artec.jumpgame.UIelements.OverlayButton;
import com.artec.jumpgame.UIelements.ToggleButton;
import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.Fading;
import com.artec.jumpgame.utils.PlaySounds;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 22.07.16.
 */
public class MenuOverlay extends InputAdapter
{
    private DialogWindow dialogWindow;
    private BitmapFont font;
    private GlyphLayout layout;
    private Fading fading;
    private boolean visible;
    private Vector2 position; // Position of faded background (usually 0, 0)

    private Enums.BlinkingState blinkingState;

    private OverlayButton howToPlayButton;
    private OverlayButton gameChangersButton;
    private OverlayButton aboutButton;
    //private OverlayButton achievementsButton;
    //private OverlayButton rankingsButton;

    private ToggleButton soundToggle;
    private ToggleButton musicToggle;
    private OverlayButton exitButton;

    private Viewport overlayViewport;
    private GameScreen gameScreen;

    public MenuOverlay(Viewport overlayViewport, GameScreen gameScreen)
    {
        dialogWindow = new DialogWindow();
        this.font = Assets.instance.fonts.comic40;
        this.layout = new GlyphLayout();
        fading = new Fading(Constants.DEATH_SCREEN_FADE_IN_TIME, Fading.Fade.OUT);
        fading.setActive(false);
        this.position = new Vector2(0, 0);
        this.overlayViewport = overlayViewport;
        this.gameScreen = gameScreen;
    }

    public void init()
    {
        if(!Enums.Upgrade.playedTutorial)
        {
            dialogWindow.init("Welcome to [#50be4b]Shoot and Bounce[#ffffff]. Would you like to [#50be4b]learn[#ffffff] how to play?",
                    new Vector2(overlayViewport.getWorldWidth()*0.05f, overlayViewport.getWorldHeight()*0.3f), overlayViewport.getWorldWidth()*0.9f,
                    overlayViewport.getWorldHeight()*0.4f, overlayViewport);
            dialogWindow.setVisible(true);
        }
        float currentHeight = overlayViewport.getWorldHeight() * 0.8f;

        float buttonWidth = Constants.MENU_SCREEN_HOW_TO_PLAY_BUTTON_WIDTH * overlayViewport.getWorldWidth();

        howToPlayButton = new OverlayButton(
                new Vector2(overlayViewport.getWorldWidth()/2 - buttonWidth/2,
                currentHeight), Assets.instance.uiElements.howToPlayButton, Assets.instance.uiElements.howToPlayButtonClicked,
                buttonWidth);

        currentHeight -= overlayViewport.getWorldHeight() * 0.1f;
        buttonWidth = buttonWidth * 1.25f;

        gameChangersButton = new OverlayButton(new Vector2(overlayViewport.getWorldWidth()/2 - (buttonWidth)/2,
                currentHeight), Assets.instance.uiElements.gameChangersButton, Assets.instance.uiElements.gameChangersButtonClicked,
                buttonWidth);

        currentHeight -= overlayViewport.getWorldHeight() * 0.15f;
        buttonWidth = overlayViewport.getWorldWidth() * Constants.MENU_SCREEN_ABOUT_BUTTON_WIDTH;

        aboutButton = new OverlayButton(new Vector2(overlayViewport.getWorldWidth() * 0.5f - buttonWidth/2, currentHeight),
                Assets.instance.uiElements.aboutButton, Assets.instance.uiElements.aboutButtonClicked, buttonWidth);

        /*
        achievementsButton = new OverlayButton(new Vector2(overlayViewport.getWorldWidth() * 0.5f - buttonWidth/2, currentHeight),
                Assets.instance.uiElements.achievementsButton, Assets.instance.uiElements.achievementsButtonClicked, buttonWidth);

        rankingsButton = new OverlayButton(new Vector2(overlayViewport.getWorldWidth() * 0.8f - buttonWidth/2, currentHeight),
                Assets.instance.uiElements.rankingsButton, Assets.instance.uiElements.rankingsButtonClicked, buttonWidth);
                */


        currentHeight -= overlayViewport.getWorldHeight() * 0.08f;
        buttonWidth = overlayViewport.getWorldWidth() * Constants.MENU_SCREEN_TOGGLE_BUTTON_WIDTH;

        soundToggle = new ToggleButton(new Vector2(overlayViewport.getWorldWidth()*0.3f - buttonWidth/2, currentHeight),
                Assets.instance.uiElements.soundOn, Assets.instance.uiElements.soundOff, buttonWidth,
                ToggleButton.State.getStateBasedOnBoolean(PlaySounds.isSoundPlaying()));

        musicToggle = new ToggleButton(new Vector2(overlayViewport.getWorldWidth()*0.5f - buttonWidth/2, currentHeight),
                Assets.instance.uiElements.musicOn, Assets.instance.uiElements.musicOff, buttonWidth,
                ToggleButton.State.getStateBasedOnBoolean(PlaySounds.isBackgroundMusicPlaying()));

        currentHeight += overlayViewport.getWorldHeight() * 0.01f;
        buttonWidth = buttonWidth * 0.7f;
        exitButton = new OverlayButton(new Vector2(overlayViewport.getWorldWidth()*0.7f - buttonWidth/2, currentHeight),
                Assets.instance.uiElements.exit, Assets.instance.uiElements.exit, buttonWidth);


        position.set(0, 0);
        Gdx.input.setInputProcessor(this);
        fading.init(Constants.DEATH_SCREEN_FADE_IN_TIME, Fading.Fade.OUT);
        fading.setActive(false);
        visible = true;

        blinkingState = Enums.BlinkingState.VISIBLE;
        Enums.BlinkingState.initialTime = TimeUtils.nanoTime();
    }

    public void resize(int width, int height)
    {
        overlayViewport.update(width, height, true);
    }

    public void update(float delta)
    {
        fading.update(delta);
        if(Utils.secondsSince(Enums.BlinkingState.initialTime) > blinkingState.stateDuration)
        {
            blinkingState = blinkingState.getNextState();
            Enums.BlinkingState.initialTime = TimeUtils.nanoTime();
        }
    }

    public void render(SpriteBatch batch)
    {
        batch.setColor(1, 1, 1, fading.getAlpha());
        batch.begin();
        Utils.drawTextureRegion(batch, Assets.instance.uiElements.fadedBackground, position,
                overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight(), true);

        if(blinkingState == Enums.BlinkingState.VISIBLE)
        {
            font.getData().setScale(0.3f);
            font.setColor(80 / 255f, 190 / 255f, 75 / 255f, fading.getAlpha());
            layout.setText(font, "Tap to shoot");
            font.draw(batch, "Tap to shoot", (overlayViewport.getWorldWidth() - layout.width) / 2, overlayViewport.getWorldHeight() * 0.05f+layout.height);
        }

        howToPlayButton.render(batch);
        gameChangersButton.render(batch);

        soundToggle.render(batch);
        musicToggle.render(batch);
        exitButton.render(batch);

        aboutButton.render(batch);
        //achievementsButton.render(batch);
        //rankingsButton.render(batch);

        if(dialogWindow.isVisible())
            dialogWindow.render(batch);

        batch.end();
    }

    public void reload()
    {
        font = Assets.instance.fonts.comic40;
        if(howToPlayButton != null) howToPlayButton.setRegions(Assets.instance.uiElements.howToPlayButton, Assets.instance.uiElements.howToPlayButtonClicked);
        if(gameChangersButton != null) gameChangersButton.setRegions(Assets.instance.uiElements.gameChangersButton, Assets.instance.uiElements.gameChangersButtonClicked);
        if(soundToggle != null) soundToggle.reload(Assets.instance.uiElements.soundOn, Assets.instance.uiElements.soundOff);
        if(musicToggle != null) musicToggle.reload(Assets.instance.uiElements.musicOn, Assets.instance.uiElements.musicOff);
        if(exitButton != null) exitButton.setRegions(Assets.instance.uiElements.exit, Assets.instance.uiElements.exit);
        if(aboutButton != null) aboutButton.setRegions(Assets.instance.uiElements.aboutButton, Assets.instance.uiElements.aboutButtonClicked);
        //if(achievementsButton != null) achievementsButton.setRegions(Assets.instance.uiElements.achievementsButton, Assets.instance.uiElements.achievementsButtonClicked);
        //if(rankingsButton != null) rankingsButton.setRegions(Assets.instance.uiElements.rankingsButton, Assets.instance.uiElements.rankingsButtonClicked);
        dialogWindow.reload();
    }

    public boolean isVisible(){return !fading.isFinished() && visible;}


    @Override
    public boolean keyDown(int keycode)
    {
        if(keycode == Input.Keys.BACK)
        {
            Enums.Upgrade.save();
            Gdx.app.exit();
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        if(!dialogWindow.isVisible())
        {
            if (!howToPlayButton.checkTouched(viewportCoordinates) && !gameChangersButton.checkTouched(viewportCoordinates) &&
                    !aboutButton.checkTouched(viewportCoordinates) &&
                    //!rankingsButton.checkTouched(viewportCoordinates) && !achievementsButton.checkTouched(viewportCoordinates) &&
                    !soundToggle.checkTouched(viewportCoordinates, false) &&
                    !musicToggle.checkTouched(viewportCoordinates, false) && !exitButton.checkTouched(viewportCoordinates))
            {
                //fading.init(Constants.DEATH_SCREEN_FADE_IN_TIME, Fading.Fade.OUT);
                fading.setActive(true);
                gameScreen.playFromMenu(screenX, screenY);
            }
        }
        else
        {
            dialogWindow.checkTouch(viewportCoordinates);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        if(!dialogWindow.isVisible())
        {
            if (howToPlayButton.getTouched() && howToPlayButton.checkTouched(viewportCoordinates))
            {
                visible = false;
                gameScreen.play(Level.LevelState.ANIMATION_1);
            }
            else if (gameChangersButton.getTouched() && gameChangersButton.checkTouched(viewportCoordinates))
            {
                visible = false;
                gameScreen.gameChangersShop();
            }
            else if(aboutButton.getTouched() && aboutButton.checkTouched(viewportCoordinates))
            {
                visible = false;
                gameScreen.showCredits();
            }
            /*
            else if(achievementsButton.getTouched() && achievementsButton.checkTouched(viewportCoordinates))
            {
                if(gameScreen.getGameMain().gameOnAndroid)
                {
                    gameScreen.getGameMain().androidHandler.showAchievements();
                }
            }
            else if(rankingsButton.getTouched() && rankingsButton.checkTouched(viewportCoordinates))
            {
                if(gameScreen.getGameMain().gameOnAndroid)
                {
                    gameScreen.getGameMain().androidHandler.showLeaderboards();
                }
            }
            */
            else if(soundToggle.checkTouched(viewportCoordinates, true))
            {
                switch (soundToggle.getState())
                {
                    case ON:
                        PlaySounds.soundPlayback(true);
                        PlaySounds.click(1f);
                        break;
                    case OFF:
                        PlaySounds.soundPlayback(false);
                        break;
                }
            }
            else if(musicToggle.checkTouched(viewportCoordinates, true))
            {
                switch(musicToggle.getState())
                {
                    case ON:
                        PlaySounds.backgroundMusicPlayback(true);
                        break;
                    case OFF:
                        PlaySounds.backgroundMusicPlayback(false);
                        break;
                }
                PlaySounds.reloadBackgroundMusic();
            }
            else if(exitButton.checkTouched(viewportCoordinates) && exitButton.getTouched())
            {
                Gdx.app.exit();
            }
        }
        else
        {
            Enums.Upgrade.playedTutorial = true;
            Enums.Upgrade.save();
            if(dialogWindow.checkTouchUp(viewportCoordinates) == 1)
            {
                visible = false;
                gameScreen.play(Level.LevelState.ANIMATION_1);
            }
            dialogWindow.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        if(!dialogWindow.isVisible())
        {
            howToPlayButton.checkTouched(viewportCoordinates);
            gameChangersButton.checkTouched(viewportCoordinates);

            aboutButton.checkTouched(viewportCoordinates);
            //achievementsButton.checkTouched(viewportCoordinates);
            //rankingsButton.checkTouched(viewportCoordinates);
        }
        else
        {
            dialogWindow.checkTouch(viewportCoordinates);
        }
        return true;
    }
}
