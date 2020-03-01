package com.artec.jumpgame.utils;

/**
 * Created by bartek on 20.06.16.
 */
public class Fading
{
    private float fadeTimeAlpha;
    private float fadeTime;
    private Fade fade;
    private boolean active;

    public Fading(){}

    public Fading(float fadeTime, Fade fade)
    {
        init(fadeTime, fade);
    }

    public void init(float fadeTime, Fade fade)
    {
        active = true;
        this.fadeTime = fadeTime;
        this.fade = fade;
        if (fade == Fade.IN) fadeTimeAlpha = 0;
        else fadeTimeAlpha = 1;
    }

    public void setOpaque(){fadeTimeAlpha = 1;}
    public void toggleActive(){active = !active;}
    public void setActive(boolean newActive) {active = newActive;}

    public boolean isFinished()
    {
        if(fade == Fade.IN && fadeTimeAlpha >=1) return true;
        if(fade == Fade.OUT && fadeTimeAlpha <=0) return true;
        return false;
    }

    public void update(float delta)
    {
        if(!active) return;

        if(fade == Fade.IN)
        {
            if (fadeTimeAlpha < 1)
                fadeTimeAlpha += delta / fadeTime;
            if (fadeTimeAlpha >= 1)
                fadeTimeAlpha = 1;
        }
        else
        {
            if (fadeTimeAlpha > 0)
                fadeTimeAlpha -= delta / fadeTime;
            if (fadeTimeAlpha <= 0)
                fadeTimeAlpha = 0;
        }
    }

    public float getAlpha()
    {
        if(!active) return 1;
        else
            return fadeTimeAlpha;
    }

    public enum Fade {IN, OUT}
}
