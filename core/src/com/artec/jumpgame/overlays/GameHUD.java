package com.artec.jumpgame.overlays;

import com.artec.jumpgame.UIelements.OverlayButton;
import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.PlaySounds;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 19.06.16.
 */
public class GameHUD extends InputAdapter
{
    private Level.LevelState levelState;
    private GameScreen gameScreen;

    private boolean finishedDrawingText;
    private String tutorialCurrentText;
    private int tutorialTextCurrentLetter;
    private long lastLetterDrawTime;

    private Enums.BlinkingState blinkingState;
    private float coinPickupElapsedTime;

    private BitmapFont font;
    private BitmapFont tutorialDescriptionFont;
    private BitmapFont fpsCounterFont;
    private GlyphLayout layout;
    private OverlayButton pauseButton;
    private Viewport HUDviewport;

    private int score;

    public GameHUD()
    {
        layout = new GlyphLayout();
        fpsCounterFont = new BitmapFont();
    }

    public void init(Viewport HUDviewport, GameScreen gameScreen, Level.LevelState levelState)
    {
        if(this.levelState == null || !this.levelState.equals(levelState))
        {
            this.levelState = levelState;
            this.gameScreen = gameScreen;

            this.font = Assets.instance.fonts.comic40;
            this.tutorialDescriptionFont = Assets.instance.fonts.descriptionFont;
            this.HUDviewport = HUDviewport;

            tutorialCurrentText = new String();
            tutorialTextCurrentLetter = 0;
            lastLetterDrawTime = TimeUtils.nanoTime();
            finishedDrawingText = false;

            blinkingState = Enums.BlinkingState.VISIBLE;
            Enums.BlinkingState.initialTime = TimeUtils.nanoTime();
            coinPickupElapsedTime = Constants.TIME_TO_SHOW_COIN_STATE;
        }

        pauseButton = new OverlayButton(
                new Vector2(HUDviewport.getWorldWidth() - (Constants.HUD_PAUSE_BUTTON_WIDTH * HUDviewport.getWorldWidth() + HUDviewport.getWorldWidth() * 0.04f),
                        HUDviewport.getWorldHeight() - (Constants.HUD_PAUSE_BUTTON_WIDTH * HUDviewport.getWorldWidth() + HUDviewport.getWorldHeight() * 0.02f)),
                Assets.instance.uiElements.pauseButton, Assets.instance.uiElements.pauseButtonClicked,
                HUDviewport.getWorldWidth() * Constants.HUD_PAUSE_BUTTON_WIDTH);
    }

    public void update(int score)
    {
        this.score = score;
        update();
    }
    public void update()
    {
        if(levelState != Level.LevelState.NORMAL && Utils.secondsSince(lastLetterDrawTime) > Constants.TUTORIAL_TEXT_TIME_BETWEEN_DRAWING_LETTERS
                && !finishedDrawingText)
        {
            if(levelState.textLabel.charAt(tutorialTextCurrentLetter) == '[')
            {
                while(levelState.textLabel.charAt(tutorialTextCurrentLetter) != ']')
                    tutorialCurrentText += levelState.textLabel.charAt(tutorialTextCurrentLetter++);

                tutorialCurrentText += levelState.textLabel.charAt(tutorialTextCurrentLetter++);
            }
            tutorialCurrentText += levelState.textLabel.charAt(tutorialTextCurrentLetter++);
            if(tutorialTextCurrentLetter > levelState.textLabel.length()-1) finishedDrawingText = true;
            lastLetterDrawTime = TimeUtils.nanoTime();
        }

        if(Utils.secondsSince(Enums.BlinkingState.initialTime) > blinkingState.stateDuration)
        {
            blinkingState = blinkingState.getNextState();
            Enums.BlinkingState.initialTime = TimeUtils.nanoTime();
        }
        coinPickupElapsedTime += Gdx.graphics.getDeltaTime();
    }

    public void render(SpriteBatch batch)
    {
        if(levelState == Level.LevelState.NORMAL)
        {
            font.getData().setScale(0.75f);
            font.setColor(80 / 255f, 190 / 255f, 75 / 255f, 1);
            layout.setText(font, String.valueOf(score));

            batch.setColor(1, 1, 1, 1);
            font.draw(batch, String.valueOf(score), (HUDviewport.getWorldWidth() - layout.width) / 2, HUDviewport.getWorldHeight() - layout.height / 2);
            //fpsCounterFont.draw(batch, "FPS:" + Gdx.graphics.getFramesPerSecond(), HUDviewport.getWorldWidth() * 0.02f, HUDviewport.getWorldHeight() * 0.98f);

            if(coinPickupElapsedTime < Constants.TIME_TO_SHOW_COIN_STATE)
            {
                font.getData().markupEnabled = true;
                float coinSize = HUDviewport.getWorldWidth() * (Constants.OVERLAY_COIN_SIZE * 0.6f);
                Vector2 tempPosition = Pools.obtain(Vector2.class).set(HUDviewport.getWorldWidth() * 0.05f, HUDviewport.getWorldHeight() * 0.96f - coinSize);

                Utils.drawTextureRegion(batch, Assets.instance.gameChangers.coinAnimation.getKeyFrame(0), tempPosition, coinSize, coinSize, false);
                Pools.free(tempPosition);

                font.getData().setScale(0.4f);
                layout.setText(font, "" + Enums.Upgrade.coins);
                font.draw(batch, "[#ffffff]" + Enums.Upgrade.coins, HUDviewport.getWorldWidth() * 0.05f + coinSize + HUDviewport.getWorldWidth() * 0.03f
                        , HUDviewport.getWorldHeight() * 0.96f);
            }
        }
        else
        {
            if(levelState == Level.LevelState.ANIMATION_1 || levelState == Level.LevelState.ANIMATION_2
                    || levelState == Level.LevelState.ANIMATION_3 || levelState == Level.LevelState.ANIMATION_4)
            {
                batch.setColor(1, 1, 1, 1f);
                Vector2 backgroundPosition = Pools.obtain(Vector2.class).set(0, 0);
                Utils.drawTextureRegion(batch, Assets.instance.uiElements.fadedBackground, backgroundPosition,
                        HUDviewport.getWorldWidth(), HUDviewport.getWorldHeight(), true);
                Pools.free(backgroundPosition);
            }
            tutorialDescriptionFont.setColor(1, 1, 1, 0.8f);
            tutorialDescriptionFont.getData().setScale(0.25f);
            tutorialDescriptionFont.getData().markupEnabled = true;
            float textHeight;
            if(levelState == Level.LevelState.TUTORIAL_1 || levelState == Level.LevelState.TUTORIAL_2 ||
                    levelState == Level.LevelState.TUTORIAL_3 || levelState == Level.LevelState.TUTORIAL_4)
                textHeight = HUDviewport.getWorldHeight()*0.95f;
            else
                textHeight = HUDviewport.getWorldHeight()*0.75f;

            layout.setText(tutorialDescriptionFont, tutorialCurrentText, Color.WHITE,  HUDviewport.getWorldWidth()*0.85f, Align.center, true);
            tutorialDescriptionFont.draw(batch, tutorialCurrentText, HUDviewport.getWorldWidth()*0.075f, textHeight,
                    HUDviewport.getWorldWidth()*0.85f, Align.center, true);

            font.setColor(80 / 255f, 190 / 255f, 75 / 255f, 0.8f);
            if(finishedDrawingText && levelState == Level.LevelState.TUTORIAL_1 || levelState == Level.LevelState.TUTORIAL_2 ||
                    levelState == Level.LevelState.TUTORIAL_3 || levelState == Level.LevelState.TUTORIAL_4)
            {
                font.getData().setScale(0.5f);
                font.draw(batch, levelState.getCompletedObjectives() + "/" + levelState.getObjectivesToComplete(), HUDviewport.getWorldWidth() * 0.05f,
                        HUDviewport.getWorldHeight() * 0.95f - layout.height - font.getLineHeight() * 0.25f, HUDviewport.getWorldWidth() * 0.9f, Align.center, true);
            }
            else if(finishedDrawingText && levelState != Level.LevelState.NORMAL && blinkingState == Enums.BlinkingState.VISIBLE)
            {
                font.getData().setScale(0.3f);
                font.draw(batch, "Tap to continue", HUDviewport.getWorldWidth() * 0.05f,
                        HUDviewport.getWorldHeight() * 0.9f, HUDviewport.getWorldWidth() * 0.9f, Align.center, true);
            }
        }

        pauseButton.render(batch);
    }

    public void reload()
    {
        font = Assets.instance.fonts.comic40;
        tutorialDescriptionFont = Assets.instance.fonts.descriptionFont;
        if(pauseButton != null) pauseButton.setRegions(Assets.instance.uiElements.pauseButton, Assets.instance.uiElements.pauseButtonClicked);
    }

    public void pickedUpCoin()
    {
        coinPickupElapsedTime = 0;
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if(keycode == Input.Keys.BACK)
        {
            pauseButton.init();
            gameScreen.pause(score, levelState);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = HUDviewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        if((levelState == Level.LevelState.ANIMATION_1 || levelState == Level.LevelState.ANIMATION_2 ||
                levelState == Level.LevelState.ANIMATION_3 || levelState == Level.LevelState.ANIMATION_4) && finishedDrawingText
                && !pauseButton.checkTouched(viewportCoordinates))
        {
            PlaySounds.click(1f);
            levelState.addCompletedObjective();
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = HUDviewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        if(pauseButton.checkTouched(viewportCoordinates))
        {
            pauseButton.init();
            gameScreen.pause(score, levelState);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        Vector2 viewportCoordinates = HUDviewport.unproject(screenCoordinatesTmp);
        Pools.free(screenCoordinatesTmp);

        pauseButton.checkTouched(viewportCoordinates);
        return true;
    }

    public boolean isButtonTouched(int screenX, int screenY)
    {
        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        boolean touched = pauseButton.checkTouched(HUDviewport.unproject(screenCoordinatesTmp));
        Pools.free(screenCoordinatesTmp);
        return touched;
    }
}
