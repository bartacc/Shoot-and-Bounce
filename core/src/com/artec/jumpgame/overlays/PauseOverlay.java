package com.artec.jumpgame.overlays;

/**
 * Created by bartek on 06.07.16.
 */
import com.artec.jumpgame.UIelements.OverlayButton;
import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Slide;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PauseOverlay extends GenericOverlay implements InputProcessor
{
    private Slide buttonSlide;

    private OverlayButton continueButton;
    private OverlayButton restartButton;
    private OverlayButton menuButton;

    private BitmapFont descriptionFont;

    public PauseOverlay(Viewport overlayViewport, GameScreen gameScreen)
    {
        super(overlayViewport, gameScreen);
        font = Assets.instance.fonts.comic40;
    }

    public void init(int score, Level.LevelState levelState)
    {
        super.init(score, levelState);
        descriptionFont = Assets.instance.fonts.descriptionFont;
        if(levelState == Level.LevelState.ANIMATION_1 || levelState == Level.LevelState.ANIMATION_2
                || levelState == Level.LevelState.ANIMATION_3 || levelState == Level.LevelState.ANIMATION_4)
            fading.setActive(false);

        float buttonWidth = Constants.DEATH_SCREEN_RESTART_BUTTON_WIDTH * overlayViewport.getWorldWidth();

        continueButton = new OverlayButton(
                new Vector2(overlayViewport.getWorldWidth()*0.5f - buttonWidth/2, overlayViewport.getWorldHeight() * 0.1f),
                Assets.instance.uiElements.continueButton, Assets.instance.uiElements.continueButtonClicked, buttonWidth);

        restartButton = new OverlayButton(
                new Vector2(overlayViewport.getWorldWidth()*0.5f + buttonWidth *0.5f
                                + Constants.OVERLAY_SPACE_BETWEEN_BUTTONS * overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight() * 0.1f),
                Assets.instance.uiElements.restartButtonWhite, Assets.instance.uiElements.restartButtonWhiteClicked, buttonWidth);

        menuButton = new OverlayButton(
                new Vector2(overlayViewport.getWorldWidth()*0.5f - buttonWidth *1.5f
                - Constants.OVERLAY_SPACE_BETWEEN_BUTTONS * overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight() * 0.1f),
                Assets.instance.uiElements.menuButtonWhite, Assets.instance.uiElements.menuButtonClickedWhite, buttonWidth);

        buttonSlide = new Slide(-restartButton.getHeight(), overlayViewport.getWorldHeight() * 0.1f, Slide.SlideState.BUTTONS_SLIDE.duration);

        Gdx.input.setInputProcessor(this);
    }

    public void render(SpriteBatch batch)
    {
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

            descriptionFont.setColor(1, 1, 1, fading.getAlpha());
            descriptionFont.getData().setScale(0.3f);
            descriptionFont.getData().markupEnabled = true;

            layout.setText(descriptionFont, levelState.textLabel, Color.WHITE, overlayViewport.getWorldWidth()*0.85f, Align.center, true);
            descriptionFont.draw(batch, levelState.textLabel, overlayViewport.getWorldWidth()*0.075f, overlayViewport.getWorldHeight() * 0.8f,
                    overlayViewport.getWorldWidth()*0.85f, Align.center, true);
        }


        /*
        Vector2 tmpVector2 = Pools.obtain(Vector2.class).set(continueButton.getPosition().x, buttonSlide.getPosition(Slide.SlideState.BUTTONS_SLIDE.elapsedSeconds));
        continueButton.setPosition(tmpVector2);
        tmpVector2.set(restartButton.getPosition().x, buttonSlide.getPosition(Slide.SlideState.BUTTONS_SLIDE.elapsedSeconds));
        restartButton.setPosition(tmpVector2);
        tmpVector2.set(menuButton.getPosition().x, buttonSlide.getPosition(Slide.SlideState.BUTTONS_SLIDE.elapsedSeconds));
        menuButton.setPosition(tmpVector2);
        */

        continueButton.render(batch);
        restartButton.render(batch);
        menuButton.render(batch);

        batch.end();
    }

    public void reload()
    {
        super.reload();
        if(restartButton != null) restartButton.setRegions(Assets.instance.uiElements.restartButtonWhite, Assets.instance.uiElements.restartButtonWhiteClicked);
        if(menuButton != null) menuButton.setRegions(Assets.instance.uiElements.menuButtonWhite, Assets.instance.uiElements.menuButtonClickedWhite);
        if(continueButton != null) continueButton.setRegions(Assets.instance.uiElements.continueButton, Assets.instance.uiElements.continueButtonClicked);
        descriptionFont = Assets.instance.fonts.descriptionFont;
    }

    @Override
    public boolean keyUp(int keycode) {return false;}

    @Override
    public boolean keyTyped(char character) {return false;}

    @Override
    public boolean keyDown(int keycode)
    {
        if(keycode == Input.Keys.BACK)
        {
            gameScreen.unPause();
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

        continueButton.checkTouched(viewportCoordinates);
        restartButton.checkTouched(viewportCoordinates);
        menuButton.checkTouched(viewportCoordinates);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        if(continueButton.getTouched() && continueButton.checkTouched(viewportCoordinates))
            gameScreen.unPause();
        else if(restartButton.getTouched() && restartButton.checkTouched(viewportCoordinates))
            gameScreen.play(levelState);
        else if(menuButton.getTouched() && menuButton.checkTouched(viewportCoordinates))
            gameScreen.menu(true);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        continueButton.checkTouched(viewportCoordinates);
        restartButton.checkTouched(viewportCoordinates);
        menuButton.checkTouched(viewportCoordinates);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {return false;}

    @Override
    public boolean scrolled(int amount) {return false;}
}

