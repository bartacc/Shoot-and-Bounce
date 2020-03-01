package com.artec.jumpgame.overlays;

import com.artec.jumpgame.UIelements.OverlayButton;
import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
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
 * Created by bartek on 29.07.16.
 */
public class TutorialFinishedOverlay extends GenericOverlay implements InputProcessor
{
    private OverlayButton menuButton;

    public TutorialFinishedOverlay(Viewport overlayViewport, GameScreen gameScreen)
    {
        super(overlayViewport, gameScreen);
        this.font = Assets.instance.fonts.descriptionFont;
    }

    public void init()
    {
        super.init(Level.LevelState.TUTORIAL_4);

        float buttonWidth = Constants.DEATH_SCREEN_RESTART_BUTTON_WIDTH * overlayViewport.getWorldWidth();
        menuButton = new OverlayButton(
                new Vector2(overlayViewport.getWorldWidth()*0.5f - buttonWidth/2, overlayViewport.getWorldHeight() * 0.2f),
                Assets.instance.uiElements.menuButtonWhite, Assets.instance.uiElements.menuButtonClickedWhite, buttonWidth);

        Gdx.input.setInputProcessor(this);
    }

    public void render(SpriteBatch batch)
    {
        batch.setColor(1, 1, 1, fading.getAlpha());
        batch.begin();
        Utils.drawTextureRegion(batch, Assets.instance.uiElements.fadedBackground, position,
                overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight(), true);

        font.setColor(1, 1, 1, fading.getAlpha());
        font.getData().setScale(0.4f);
        font.getData().markupEnabled = true;

        layout.setText(font, "Great Job!\nNow you can use your skills to reach the[#50be4b]top [#ffffff].\nGood luck!",
                Color.WHITE, overlayViewport.getWorldWidth()*0.85f, Align.center, true);
        font.draw(batch, "Great Job!\nNow you can use your skills to reach the [#50be4b]top[#ffffff].\nGood luck!", overlayViewport.getWorldWidth()*0.075f,
                overlayViewport.getWorldHeight() * 0.95f, overlayViewport.getWorldWidth()*0.85f, Align.center, true);

        menuButton.render(batch);
        batch.end();
    }

    public void reload()
    {
        super.reload();
        font = Assets.instance.fonts.descriptionFont;
        if(menuButton != null) menuButton.setRegions(Assets.instance.uiElements.menuButtonWhite, Assets.instance.uiElements.menuButtonClickedWhite);
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
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        menuButton.checkTouched(viewportCoordinates);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        if(menuButton.getTouched() && menuButton.checkTouched(viewportCoordinates))
            gameScreen.menu(true);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        menuButton.checkTouched(viewportCoordinates);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {return false;}

    @Override
    public boolean scrolled(int amount) {return false;}
}
