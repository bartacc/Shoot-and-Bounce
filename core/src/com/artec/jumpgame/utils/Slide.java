package com.artec.jumpgame.utils;

/**
 * Created by bartek on 07.08.16.
 */
public class Slide
{
    private float startPosition, endPosition;
    private float duration;
    private float velocityPerSecond;

    public Slide(float startPosition, float endPosition, float duration)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.duration = duration;

        velocityPerSecond = (endPosition - startPosition)/duration;
    }

    public float getVelocity()
    {
        return velocityPerSecond;
    }

    public float getPosition(float elapsedSeconds)
    {
        if(!isSlideFinished(elapsedSeconds))
            return startPosition + (velocityPerSecond * elapsedSeconds);
        else return endPosition;
    }

    public boolean isSlideFinished(float elapsedSeconds)
    {
        if(velocityPerSecond > 0)
        return startPosition + (velocityPerSecond * elapsedSeconds) > endPosition;
        else return startPosition + (velocityPerSecond * elapsedSeconds) < endPosition;
    }

    public enum SlideState
    {
        SCORE_SLIDE(0.4f, 0), BEST_SCORE_SLIDE(0.4f, 0.25f), BUTTONS_SLIDE(0.3f, 0.38f), FINISHED(0, 0);

        public final float delay;
        public float elapsedSeconds = 0;
        public float elapsedDelay = 0;
        public final float duration;

        SlideState(float duration, float delay)
        {
            this.duration = duration;
            this.delay = delay;
        }

        public SlideState getNextSlideState()
        {
            switch(this)
            {
                case SCORE_SLIDE: return BEST_SCORE_SLIDE;

                case BEST_SCORE_SLIDE: return BUTTONS_SLIDE;

                case BUTTONS_SLIDE: return FINISHED;

                default: return FINISHED;
            }
        }

        public static void reload()
        {
            SCORE_SLIDE.elapsedSeconds = 0;
            SCORE_SLIDE.elapsedDelay = 0;

            BEST_SCORE_SLIDE.elapsedSeconds = 0;
            BEST_SCORE_SLIDE.elapsedDelay = 0;

            BUTTONS_SLIDE.elapsedSeconds = 0;
            BUTTONS_SLIDE.elapsedDelay = 0;

            FINISHED.elapsedSeconds = 0;
            FINISHED.elapsedDelay = 0;
        }
    }
}
