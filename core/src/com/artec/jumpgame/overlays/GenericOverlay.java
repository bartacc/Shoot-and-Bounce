package com.artec.jumpgame.overlays;

import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.Fading;
import com.artec.jumpgame.utils.Slide;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 29.07.16.
 */
public class GenericOverlay
{
    protected Level.LevelState levelState;

    protected Slide.SlideState slideState;

    protected Slide scoreTextSlide;
    private Slide scoreValueSlide;
    private Slide iconSlide;
    private Slide bestScoreSlide;

    protected BitmapFont font;
    protected GlyphLayout layout;
    protected Fading fading;
    protected Vector2 position; // Position of faded background (usually 0, 0)

    protected Viewport overlayViewport;
    protected GameScreen gameScreen;

    protected int score;
    protected int bestScore;

    public GenericOverlay(Viewport overlayViewport, GameScreen gameScreen)
    {
        this.layout = new GlyphLayout();
        fading = new Fading(Constants.DEATH_SCREEN_FADE_IN_TIME, Fading.Fade.IN);
        this.position = new Vector2(0, 0);
        this.overlayViewport = overlayViewport;
        this.gameScreen = gameScreen;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void init(Level.LevelState levelState)
    {
        init(0, levelState);
    }

    public void init(int score, Level.LevelState levelState)
    {
        Slide.SlideState.reload();
        slideState = Slide.SlideState.SCORE_SLIDE;
        float iconSize = Constants.DEATH_SCREEN_ICON_SIZE * overlayViewport.getWorldWidth();
        iconSlide = new Slide(0 - iconSize, overlayViewport.getWorldWidth() * 0.25f - iconSize / 2, Slide.SlideState.BEST_SCORE_SLIDE.duration);
        bestScoreSlide = new Slide(overlayViewport.getWorldWidth(), overlayViewport.getWorldWidth() / 2, Slide.SlideState.BEST_SCORE_SLIDE.duration);

        scoreTextSlide = new Slide(overlayViewport.getWorldHeight(), overlayViewport.getWorldHeight() * 0.77f, Slide.SlideState.SCORE_SLIDE.duration);
        scoreValueSlide = new Slide(0, overlayViewport.getWorldHeight() * 0.72f, Slide.SlideState.SCORE_SLIDE.duration);

        this.levelState = levelState;

        position.set(0, 0);
        this.score = score;
        this.bestScore = Enums.Upgrade.currentUpgrade.highScore;
        fading.init(Constants.DEATH_SCREEN_FADE_IN_TIME, Fading.Fade.IN);
    }

    public void render(SpriteBatch batch)
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

        font.getData().setScale(1f);
        font.setColor(1, 1, 1, fading.getAlpha());
        layout.setText(font, "Score");
        font.draw(batch, "Score", (overlayViewport.getWorldWidth() - layout.width) / 2,
                scoreTextSlide.getPosition(Slide.SlideState.SCORE_SLIDE.elapsedSeconds) + layout.height);

        font.setColor(80 / 255f, 190 / 255f, 75 / 255f, fading.getAlpha());
        layout.setText(font, String.valueOf(score));
        font.draw(batch, String.valueOf(score), (overlayViewport.getWorldWidth() - layout.width) / 2,
                scoreValueSlide.getPosition(Slide.SlideState.SCORE_SLIDE.elapsedSeconds));

        float iconSize = Constants.DEATH_SCREEN_ICON_SIZE * overlayViewport.getWorldWidth();
        Vector2 iconPosition = Pools.obtain(Vector2.class).set(iconSlide.getPosition(Slide.SlideState.BEST_SCORE_SLIDE.elapsedSeconds),
                overlayViewport.getWorldHeight() * 0.55f - iconSize);
        Utils.drawTextureRegion(batch, Enums.Upgrade.currentUpgrade.icon, iconPosition, iconSize, iconSize, false);
        Pools.free(iconPosition);

        font.setColor(1, 1, 1, fading.getAlpha());
        font.getData().setScale(0.65f);

        layout.setText(font, String.valueOf(bestScore));
        font.draw(batch, "Best\n" + String.valueOf(bestScore), bestScoreSlide.getPosition(Slide.SlideState.BEST_SCORE_SLIDE.elapsedSeconds),
                overlayViewport.getWorldHeight() * 0.55f, overlayViewport.getWorldWidth() / 2,
                Align.center, false);
    }

    public void resize(int width, int height)
    {
        overlayViewport.update(width, height, true);
    }

    public void update(float delta)
    {
        fading.update(delta);
    }

    public void reload()
    {
        font = Assets.instance.fonts.comic40;
    }

    public GameScreen getGameScreen() {return gameScreen;}
}


