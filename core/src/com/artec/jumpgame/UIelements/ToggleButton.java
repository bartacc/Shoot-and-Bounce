package com.artec.jumpgame.UIelements;

import com.artec.jumpgame.utils.PlaySounds;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by bartek on 14.08.16.
 */
public class ToggleButton
{
    private Vector2 position;
    private float width, height;

    private Rectangle boundingRectangle;

    private TextureRegion regionOn;
    private TextureRegion regionOff;
    private TextureRegion currentRegion;
    private State state;

    public ToggleButton(Vector2 position, TextureRegion regionOn, TextureRegion regionOff, float width, State initialState)
    {
        this.position = position;

        this.regionOn = regionOn;
        this.regionOff = regionOff;

        this.width = width;
        height = ((float)regionOn.getRegionHeight() / (float)regionOn.getRegionWidth()) * width;

        init(initialState);
    }

    public void init(State initialState)
    {
        state = initialState;
        boundingRectangle = new Rectangle(position.x, position.y, width, height);
    }

    public void render(SpriteBatch batch)
    {
        if(state == State.ON) currentRegion = regionOn;
        else currentRegion = regionOff;

        Utils.drawTextureRegion(batch, currentRegion, position, width, height, true);
    }

    public boolean checkTouched(Vector2 touchPosition, boolean changeStateIfTouching)
    {
        if(boundingRectangle.contains(touchPosition))
        {
            if(changeStateIfTouching)
            {
                state = state.switchState();
                PlaySounds.click(1f);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public void reload(TextureRegion regionOn, TextureRegion regionOff)
    {
        this.regionOn = regionOn;
        this.regionOff = regionOff;
    }

    public State getState() {return state;}
    public float getWidth() {return width;}
    public float getHeight() {return height;}

    public enum State
    {
        ON, OFF;

        public State switchState()
        {
            if(this == ON) return OFF;
            else return ON;
        }

        public static State getStateBasedOnBoolean(boolean state)
        {
            if(state) return ON;
            else return OFF;
        }
    }
}
