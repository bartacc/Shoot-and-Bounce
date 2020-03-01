package com.artec.jumpgame.overlays;

import com.artec.jumpgame.UIelements.*;
import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.Utils;
import com.artec.jumpgame.UIelements.GameChanger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 31.07.16.
 */
public class GameChangersShopOverlay extends GenericOverlay implements InputProcessor
{
    private Array<GameChanger> gameChangers;
    private GameChanger gameChangerWithDialogWindow;
    private DialogWindow dialogWindow;
    private float bottom;
    private OverlayButton backButton;
    private float buttonsBackgroundHeight;

    private float scrollPositionThisFrame, scrollPositionLastFrame, scrollVelocity;
    private float topDifference;
    private boolean startedScrolling;
    private float scrollStartHeight, scrollStartHeightViewportCoordinates;

    public GameChangersShopOverlay(Viewport overlayViewport, GameScreen gameScreen)
    {
        super(overlayViewport, gameScreen);
        font = Assets.instance.fonts.comic40;
        gameChangers = new Array<GameChanger>();
        dialogWindow = new DialogWindow();
    }

    public void init()
    {
        super.init(Level.LevelState.NORMAL);
        gameChangers.clear();

        buttonsBackgroundHeight = overlayViewport.getWorldHeight() * 0.1f;
        float currentHeight = overlayViewport.getWorldHeight()*0.8f;

        GameChanger lastGameChanger = new GameChanger(overlayViewport.getWorldWidth()*0.05f, currentHeight,
                overlayViewport.getWorldWidth()*0.9f, Enums.Upgrade.NORMAL, overlayViewport, this);
        gameChangers.add(lastGameChanger);


        currentHeight -= lastGameChanger.getHeight() + Constants.SHOP_SPACE_BETWEEN_OBJECTS * overlayViewport.getWorldHeight();
        lastGameChanger = new GameChanger(overlayViewport.getWorldWidth()*0.05f, currentHeight,
                overlayViewport.getWorldWidth()*0.9f, Enums.Upgrade.BOUNCING_OFF_WALLS, overlayViewport, this);
        gameChangers.add(lastGameChanger);


        currentHeight -= lastGameChanger.getHeight() + Constants.SHOP_SPACE_BETWEEN_OBJECTS * overlayViewport.getWorldHeight();
        lastGameChanger = new GameChanger(overlayViewport.getWorldWidth()*0.05f, currentHeight,
                overlayViewport.getWorldWidth()*0.9f, Enums.Upgrade.BIGGER, overlayViewport, this);
        gameChangers.add(lastGameChanger);


        currentHeight -= lastGameChanger.getHeight() + Constants.SHOP_SPACE_BETWEEN_OBJECTS * overlayViewport.getWorldHeight();
        lastGameChanger = new GameChanger(overlayViewport.getWorldWidth()*0.05f, currentHeight,
                overlayViewport.getWorldWidth()*0.9f, Enums.Upgrade.LOW_GRAVITY, overlayViewport, this);
        gameChangers.add(lastGameChanger);


        currentHeight -= lastGameChanger.getHeight() + Constants.SHOP_SPACE_BETWEEN_OBJECTS * overlayViewport.getWorldHeight();
        lastGameChanger = new GameChanger(overlayViewport.getWorldWidth()*0.05f, currentHeight,
                overlayViewport.getWorldWidth()*0.9f, Enums.Upgrade.SMALLER, overlayViewport, this);
        gameChangers.add(lastGameChanger);

        currentHeight -= lastGameChanger.getHeight() + Constants.SHOP_SPACE_BETWEEN_OBJECTS * overlayViewport.getWorldHeight();
        lastGameChanger = new GameChanger(overlayViewport.getWorldWidth()*0.05f, currentHeight,
                overlayViewport.getWorldWidth()*0.9f, Enums.Upgrade.REVERSED_GRAVITY, overlayViewport, this);
        gameChangers.add(lastGameChanger);

        bottom = lastGameChanger.getTop() - lastGameChanger.getHeight()
                - Constants.SHOP_SPACE_BETWEEN_OBJECTS * overlayViewport.getWorldHeight();

        backButton = new OverlayButton(new Vector2(overlayViewport.getWorldWidth()*0.05f, overlayViewport.getWorldHeight()*0.015f),
                Assets.instance.uiElements.backButton, Assets.instance.uiElements.backButtonClicked, overlayViewport.getWorldWidth()*0.1f);

        topDifference = 0;
        scrollPositionLastFrame= 0;
        scrollPositionThisFrame = 0;
        scrollVelocity = 0;
        startedScrolling = false;
        scrollStartHeight = 0;
        scrollStartHeightViewportCoordinates = 0;
    }

    @Override
    public void update(float delta)
    {
        if(topDifference < 0 - bottom && topDifference > 0) topDifference += scrollVelocity;
        if (topDifference > 0 - bottom) topDifference = 0 - bottom;
        if (topDifference < 0) topDifference = 0;


        boolean scrollVelocityCrossedZeroBarrier = false;
        if(scrollVelocity > 0)
        {
            scrollVelocity -= 50 * Gdx.graphics.getDeltaTime();
            if(scrollVelocity <= 0) scrollVelocityCrossedZeroBarrier = true;
        }
        else if(scrollVelocity < 0)
        {
            scrollVelocity += 50 * Gdx.graphics.getDeltaTime();
            if(scrollVelocity >= 0) scrollVelocityCrossedZeroBarrier = true;
        }
        if(scrollVelocityCrossedZeroBarrier) scrollVelocity = 0;

        for(GameChanger gameChanger : gameChangers)
            gameChanger.setTopDifference(topDifference);
    }

    public void render(SpriteBatch batch)
    {
        batch.begin();
        Utils.drawTextureRegion(batch, Assets.instance.uiElements.fadedBackground, position,
                overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight(), false);
        for(GameChanger gameChanger : gameChangers)
            gameChanger.render(batch);


        Vector2 tempPosition = Pools.obtain(Vector2.class);



        float scrollWidth = Constants.SHOP_SCROLL_WIDTH * overlayViewport.getWorldWidth();
        tempPosition.set(overlayViewport.getWorldWidth() - scrollWidth,
                overlayViewport.getWorldHeight() - buttonsBackgroundHeight - scrollWidth*8f - (topDifference / Math.abs(bottom) *
                        (overlayViewport.getWorldHeight() - buttonsBackgroundHeight - scrollWidth*8f) - buttonsBackgroundHeight));
        Utils.drawTextureRegion(batch, Assets.instance.uiElements.scroll, tempPosition, scrollWidth, scrollWidth*8f, false);



        tempPosition.set(0, 0);
        Utils.drawTextureRegion(batch, Assets.instance.uiElements.shopButtonBackground, tempPosition,
                overlayViewport.getWorldWidth(), buttonsBackgroundHeight, false);

        font.getData().setScale(0.7f);
        layout.setText(font, ""+ Enums.Upgrade.coins);
        font.draw(batch, "[#ffffff]"+Enums.Upgrade.coins, overlayViewport.getWorldWidth()*0.95f - layout.width, overlayViewport.getWorldHeight() * 0.015f + layout.height);

        float coinSize = overlayViewport.getWorldWidth() * Constants.OVERLAY_COIN_SIZE;
        tempPosition.set(overlayViewport.getWorldWidth()*0.95f - layout.width - (overlayViewport.getWorldWidth() * 0.03f) -
        coinSize, overlayViewport.getWorldHeight() * 0.015f);

        Utils.drawTextureRegion(batch, Assets.instance.gameChangers.coinAnimation.getKeyFrame(0), tempPosition, coinSize, coinSize, false);
        Pools.free(tempPosition);

        backButton.render(batch);

        if(dialogWindow.isVisible())
            dialogWindow.render(batch);
        batch.end();
    }

    public void reload()
    {
        super.reload();
        if(backButton != null) backButton.setRegions(Assets.instance.uiElements.backButton, Assets.instance.uiElements.backButtonClicked);
        for(GameChanger gameChanger : gameChangers)
            gameChanger.reload();
        dialogWindow.reload();
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
            gameScreen.menu(false);
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
            boolean buttonTouched = backButton.checkTouched(viewportCoordinates);
            viewportCoordinates.y -= topDifference;


            for (GameChanger gameChanger : gameChangers)
            {
                boolean touched = gameChanger.checkTouch(viewportCoordinates);
                if (!buttonTouched) buttonTouched = touched;
            }
            if (!buttonTouched)
            {
                startedScrolling = true;
                scrollStartHeight = viewportCoordinates.y;
                scrollStartHeightViewportCoordinates = viewportCoordinates.y + topDifference;
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
            if (backButton.checkTouched(viewportCoordinates) && backButton.getTouched())
            {
                gameScreen.menu(false);
            }

            viewportCoordinates.y -= topDifference;


            for (GameChanger gameChanger : gameChangers)
            {
                int touchUpResult = gameChanger.checkTouchUp(viewportCoordinates);
                if (touchUpResult == 1)
                {
                    gameScreen.reloadLevel(Level.LevelState.NORMAL);
                    break;
                }
                else if(touchUpResult == 2)
                {
                    gameChangerWithDialogWindow = gameChanger;
                }
            }


            if (startedScrolling)
            {
                if (Math.abs(scrollPositionThisFrame - scrollPositionLastFrame) > Constants.SHOP_MIN_LENGTH_OF_TOUCH_TO_SCROLL * overlayViewport.getWorldWidth())
                    scrollVelocity = (scrollPositionThisFrame - scrollPositionLastFrame) * 1f;
                else scrollVelocity = 0;

                startedScrolling = false;
            }
        }
        else
        {
           if(dialogWindow.checkTouchUp(viewportCoordinates) == 1)
               gameChangerWithDialogWindow.buy();
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
            backButton.checkTouched(viewportCoordinates);

            if (startedScrolling)
            {
                if ((topDifference >= 0 - bottom && viewportCoordinates.y - scrollStartHeightViewportCoordinates <= 0)
                        || (topDifference <= 0 && viewportCoordinates.y - scrollStartHeightViewportCoordinates >= 0)
                        || topDifference > 0 && topDifference < 0 - bottom)
                {
                    topDifference = viewportCoordinates.y - scrollStartHeight;
                    //System.out.println(viewportCoordinates.y - scrollStartHeightViewportCoordinates);
                    scrollPositionLastFrame = scrollPositionThisFrame;
                    scrollPositionThisFrame = viewportCoordinates.y;
                } else
                {
                    scrollPositionLastFrame = 0;
                    scrollPositionThisFrame = 0;
                }
            }
            viewportCoordinates.y -= topDifference;


            for (GameChanger gameChanger : gameChangers)
                gameChanger.checkTouch(viewportCoordinates);
        }
        else
        {
            dialogWindow.checkTouch(viewportCoordinates);
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {return false;}

    @Override
    public boolean scrolled(int amount) {return false;}

    public DialogWindow getDialogWindow() {return dialogWindow;}
}
