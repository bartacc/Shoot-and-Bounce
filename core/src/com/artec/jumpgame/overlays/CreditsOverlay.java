package com.artec.jumpgame.overlays;

import com.artec.jumpgame.UIelements.OverlayButton;
import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 15.08.16.
 */
public class CreditsOverlay implements InputProcessor
{
    private BitmapFont titleFont;
    private BitmapFont descriptionFont;
    private GlyphLayout layout;

    private Vector2 position; // Position of faded background (usually 0, 0)
    private Viewport overlayViewport;
    private GameScreen gameScreen;

    private OverlayButton backButton;

    private float maxScrollPosition;
    private float currentScrollOffsetPosition;

    private final String CREDITS_STRING =
            "Splash Screen background: Designed by Starline - Freepik.com\n\n"+
            "Click sound, Score font: KenneyNL\n\n" +
            "Entity explosion sound: http://opengameart.org/content/water-step-splashes-yo-frankie\n" +
            "\n" +
            "Jump landing sound: http://opengameart.org/content/falling-body\n" +
            "\n" +
            "Bullet shot sound: http://opengameart.org/content/sfxthrow\n" +
            "\n" +
            "Pick up bullet sound: http://opengameart.org/content/rpg-sound-pack (One of the slime sounds)\n" +
            "\n" +
            "Propeller sound: http://opengameart.org/content/helicopter-sounds\n" +
            "\n" +
            "Pick up coin sound: http://opengameart.org/content/10-8bit-coin-sounds\n\n" +
            "Coin texture: http://opengameart.org/content/spinning-coin\n" +
            "\n" +
            "Sound of buying an item: http://opengameart.org/content/inventory-sound-effects\n" +
            "\n" +
            "Gameplay background music: http://freepd.com/Electronic/Bit%20Bit%20Loop\n\n" +
            "Menu background music: http://freepd.com/Electronic/Orderly";

    public CreditsOverlay(Viewport viewport, GameScreen gameScreen)
    {
        layout = new GlyphLayout();
        position = new Vector2(0, 0);
        this.overlayViewport = viewport;
        this.gameScreen = gameScreen;
        init();
    }

    public void init()
    {
        reload();
        backButton = new OverlayButton(new Vector2(overlayViewport.getWorldWidth()*0.05f, overlayViewport.getWorldHeight()*0.92f),
                Assets.instance.uiElements.backButton, Assets.instance.uiElements.backButtonClicked, overlayViewport.getWorldWidth()*0.1f);

        maxScrollPosition = 0;
        titleFont.getData().setScale(0.4f);
        layout.setText(titleFont, "Shoot and Bounce", Color.WHITE, overlayViewport.getWorldWidth(), Align.center, true);
        maxScrollPosition += layout.height;

        descriptionFont.getData().setScale(0.3f);
        layout.setText(descriptionFont, "Programming and art:\n Bartek Szczecinski", Color.BLACK,  overlayViewport.getWorldWidth(), Align.center, true);
        maxScrollPosition += layout.height;

        descriptionFont.getData().setScale(0.22f);
        layout.setText(descriptionFont, CREDITS_STRING, Color.BLACK, overlayViewport.getWorldWidth() * 0.9f, Align.center, true);
        maxScrollPosition += layout.height + overlayViewport.getWorldHeight() *0.1f;
        maxScrollPosition -= overlayViewport.getWorldHeight();
        currentScrollOffsetPosition = -overlayViewport.getWorldHeight();
    }


    public void render(SpriteBatch batch)
    {
        if(currentScrollOffsetPosition < maxScrollPosition)
        currentScrollOffsetPosition += (Constants.CREDITS_OVERLAY_LETTERS_SPEED_PER_SEC * overlayViewport.getWorldHeight()) * Gdx.graphics.getDeltaTime();

        batch.begin();
        Utils.drawTextureRegion(batch, Assets.instance.uiElements.fadedBackground,
                position, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight(), false);

        titleFont.getData().markupEnabled = true;
        titleFont.getData().setScale(0.4f);
        float currentHeight = overlayViewport.getWorldHeight() + currentScrollOffsetPosition;
        layout.setText(titleFont, "Shoot and Bounce", Color.WHITE, overlayViewport.getWorldWidth(), Align.center, true);
        titleFont.draw(batch, "[#50be4b]Shoot and Bounce", 0, currentHeight, overlayViewport.getWorldWidth(), Align.center, true);

        currentHeight -= layout.height + overlayViewport.getWorldHeight() *0.03f;
        descriptionFont.getData().setScale(0.3f);
        descriptionFont.getData().markupEnabled = true;
        layout.setText(descriptionFont, "Programming and art:\n Bartek Szczecinski", Color.BLACK,  overlayViewport.getWorldWidth(), Align.center, true);
        descriptionFont.draw(batch, "Programming and art:\n Bartek Szczecinski", 0, currentHeight,
                overlayViewport.getWorldWidth(), Align.center, true);

        currentHeight -= layout.height + overlayViewport.getWorldHeight() *0.06f;
        descriptionFont.getData().setScale(0.22f);
        layout.setText(descriptionFont, CREDITS_STRING, Color.BLACK, overlayViewport.getWorldWidth() * 0.9f, Align.center, true);
        descriptionFont.draw(batch, CREDITS_STRING, overlayViewport.getWorldWidth() * 0.05f, currentHeight,
                overlayViewport.getWorldWidth() * 0.9f, Align.center, true);

        backButton.render(batch);
        batch.end();
    }

    public void resize(int width, int height)
    {
        overlayViewport.update(width, height, true);
    }

    public void reload()
    {
        titleFont = Assets.instance.fonts.comic40;
        descriptionFont = Assets.instance.fonts.descriptionFont;
        if(backButton != null) backButton.setRegions(Assets.instance.uiElements.backButton, Assets.instance.uiElements.backButtonClicked);
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if(keycode == Input.Keys.BACK)
        {
            gameScreen.menu(false);
            return true;
        }
        return false;
    }

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

        backButton.checkTouched(viewportCoordinates);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        if(backButton.checkTouched(viewportCoordinates) && backButton.getTouched())
        {
            gameScreen.menu(false);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = overlayViewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        backButton.checkTouched(viewportCoordinates);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {return false;}

    @Override
    public boolean scrolled(int amount) {return false;}
}
