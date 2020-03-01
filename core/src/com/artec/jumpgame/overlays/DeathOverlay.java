package com.artec.jumpgame.overlays;

import com.artec.jumpgame.UIelements.OverlayButton;
import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.ScreenshotFactory;
import com.artec.jumpgame.utils.Slide;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 20.06.16.
 */
public class DeathOverlay extends GenericOverlay implements InputProcessor
{
    private Enums.DeathReason deathReason;

    private Slide buttonSlide;
    private OverlayButton restartButton;
    private OverlayButton menuButton;
    //private OverlayButton shareButton;

    private boolean clickable;

    public DeathOverlay(Viewport overlayViewport, GameScreen gameScreen)
    {
        super(overlayViewport, gameScreen);
        font = Assets.instance.fonts.comic40;
    }

    public void init(int score, Level.LevelState levelState, Enums.DeathReason deathReason)
    {
        super.init(score, levelState);
        this.deathReason = deathReason;

        float buttonWidth = Constants.DEATH_SCREEN_RESTART_BUTTON_WIDTH * overlayViewport.getWorldWidth();

        if(levelState == Level.LevelState.NORMAL)
        {
        menuButton = new OverlayButton(
                new Vector2(overlayViewport.getWorldWidth()*0.5f - buttonWidth
                        - Constants.OVERLAY_SPACE_BETWEEN_BUTTONS * overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight() * 0.1f),
                Assets.instance.uiElements.menuButtonWhite, Assets.instance.uiElements.menuButtonClickedWhite, buttonWidth);

            restartButton = new OverlayButton(
                    new Vector2(overlayViewport.getWorldWidth() * 0.5f
                            + Constants.OVERLAY_SPACE_BETWEEN_BUTTONS * overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight() * 0.1f),
                    Assets.instance.uiElements.restartButton, Assets.instance.uiElements.restartButtonClicked, buttonWidth);

            /*
            shareButton = new OverlayButton(
                    new Vector2(overlayViewport.getWorldWidth() * 0.5f + buttonWidth * 0.5f
                            + Constants.OVERLAY_SPACE_BETWEEN_BUTTONS * overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight() * 0.1f),
                    Assets.instance.uiElements.shareButton, Assets.instance.uiElements.shareButtonClicked, buttonWidth);
                    */
        }
        else
        {
            restartButton = new OverlayButton(
                    new Vector2(overlayViewport.getWorldWidth()*0.5f
                            + Constants.OVERLAY_SPACE_BETWEEN_BUTTONS * overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight() * 0.1f),
                    Assets.instance.uiElements.restartButton, Assets.instance.uiElements.restartButtonClicked, buttonWidth);

            menuButton = new OverlayButton(
                    new Vector2(overlayViewport.getWorldWidth()*0.5f - buttonWidth
                            - Constants.OVERLAY_SPACE_BETWEEN_BUTTONS * overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight() * 0.1f),
                    Assets.instance.uiElements.menuButtonWhite, Assets.instance.uiElements.menuButtonClickedWhite, buttonWidth);

            /*
            shareButton = new OverlayButton(
                    new Vector2(overlayViewport.getWorldWidth() * 0.5f + buttonWidth * 0.5f
                            + Constants.OVERLAY_SPACE_BETWEEN_BUTTONS * overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight() * 0.1f),
                    Assets.instance.uiElements.shareButton, Assets.instance.uiElements.shareButtonClicked, buttonWidth);
                    */
        }

        buttonSlide = new Slide(-restartButton.getHeight(), overlayViewport.getWorldHeight() * 0.1f, Slide.SlideState.BUTTONS_SLIDE.duration);

        clickable = false;
        Gdx.input.setInputProcessor(this);
    }

    public void render(SpriteBatch batch)
    {

        if(!clickable && slideState == Slide.SlideState.FINISHED)
            clickable = true;

        batch.setColor(1, 1, 1, fading.getAlpha());
        batch.begin();
        Utils.drawTextureRegion(batch, Assets.instance.uiElements.fadedBackground, position,
                overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight(), true);

        if(levelState == Level.LevelState.NORMAL)
        {
            super.render(batch);
        }
        else
        {
            if (slideState.elapsedSeconds < slideState.duration)
            {
                if(slideState.elapsedDelay < slideState.delay)
                    slideState.elapsedDelay += Gdx.graphics.getDeltaTime();
                else
                    slideState.elapsedSeconds += Gdx.graphics.getDeltaTime();
            }
            else
            {
                slideState = slideState.getNextSlideState();
            }

            font.setColor(1, 1, 1, fading.getAlpha());
            font.getData().setScale(0.5f);
            font.getData().markupEnabled = true;
            switch(deathReason)
            {
                case KILLED_BY_ENEMY:
                    layout.setText(font, "You were killed by [#106cf4ff]enemy[#ffffff].\nTry again.", Color.WHITE,
                            overlayViewport.getWorldWidth()*0.85f, Align.center, true);
                    font.draw(batch, "You were killed by [#106cf4ff]enemy[#ffffff].\nTry again.",
                            overlayViewport.getWorldWidth()*0.075f, overlayViewport.getWorldHeight() * 0.85f,
                            overlayViewport.getWorldWidth()*0.85f, Align.center, true);
                    break;
                case OUT_OF_BULLETS:
                    layout.setText(font, "You ran out of [#50be4b]bullets[#ffffff].\nTry again.", Color.WHITE,
                            overlayViewport.getWorldWidth()*0.85f, Align.center, true);
                    font.draw(batch, "You ran out of [#50be4b]bullets[#ffffff].\nTry again.",
                            overlayViewport.getWorldWidth()*0.075f, overlayViewport.getWorldHeight() * 0.85f,
                            overlayViewport.getWorldWidth()*0.85f, Align.center, true);
                    break;
                case FALLEN:
                    layout.setText(font, "You have fallen to [#50be4b]death[#ffffff].\nTry again.", Color.WHITE,
                            overlayViewport.getWorldWidth()*0.85f, Align.center, true);
                    font.draw(batch, "You have fallen to [#50be4b]death[#ffffff].\nTry again.",
                            overlayViewport.getWorldWidth()*0.075f, overlayViewport.getWorldHeight() * 0.85f,
                            overlayViewport.getWorldWidth()*0.85f, Align.center, true);
                    break;
            }
        }

        Vector2 tmpVector2 = Pools.obtain(Vector2.class).set(restartButton.getPosition().x, buttonSlide.getPosition(Slide.SlideState.BUTTONS_SLIDE.elapsedSeconds));
        restartButton.setPosition(tmpVector2);

        tmpVector2.set(menuButton.getPosition().x, buttonSlide.getPosition(Slide.SlideState.BUTTONS_SLIDE.elapsedSeconds));
        menuButton.setPosition(tmpVector2);

        //tmpVector2.set(shareButton.getPosition().x, buttonSlide.getPosition(Slide.SlideState.BUTTONS_SLIDE.elapsedSeconds));
        //shareButton.setPosition(tmpVector2);
        //Pools.free(tmpVector2);

        restartButton.render(batch);
        menuButton.render(batch);
        //if(levelState == Level.LevelState.NORMAL) shareButton.render(batch);

        batch.end();
    }

    public void reload()
    {
        super.reload();
        if(restartButton != null) restartButton.setRegions(Assets.instance.uiElements.restartButton, Assets.instance.uiElements.restartButtonClicked);
        if(menuButton != null) menuButton.setRegions(Assets.instance.uiElements.menuButtonWhite, Assets.instance.uiElements.menuButtonClickedWhite);
        //if(shareButton != null) shareButton.setRegions(Assets.instance.uiElements.shareButton, Assets.instance.uiElements.shareButtonClicked);
    }

    @Override
    public boolean keyDown(int keycode) {return false;}

    @Override
    public boolean keyUp(int keycode) {return false;}

    @Override
    public boolean keyTyped(char character) {return false;}

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        if(!clickable) return true;

        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

            restartButton.checkTouched(viewportCoordinates);
            menuButton.checkTouched(viewportCoordinates);
            //if(levelState == Level.LevelState.NORMAL) shareButton.checkTouched(viewportCoordinates);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        if(!clickable) return true;

        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        if(restartButton.getTouched() && restartButton.checkTouched(viewportCoordinates))
            gameScreen.play(levelState);
        else if(menuButton.getTouched() && menuButton.checkTouched(viewportCoordinates))
            gameScreen.menu(true);
        /*else if(levelState == Level.LevelState.NORMAL && shareButton.getTouched() && shareButton.checkTouched(viewportCoordinates))
        {
            if(gameScreen.getGameMain().gameOnAndroid)
            {
                System.out.println(gameScreen.getGameMain().androidHandler.getCachePath());
                ScreenshotFactory.saveScreenshot(gameScreen.getGameMain().androidHandler.getCachePath());
                gameScreen.getGameMain().androidHandler.share(score);
            }
        }*/
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        if(!clickable) return true;

        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        restartButton.checkTouched(viewportCoordinates);
        menuButton.checkTouched(viewportCoordinates);
        //if(levelState == Level.LevelState.NORMAL) shareButton.checkTouched(viewportCoordinates);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {return false;}

    @Override
    public boolean scrolled(int amount) {return false;}
}
