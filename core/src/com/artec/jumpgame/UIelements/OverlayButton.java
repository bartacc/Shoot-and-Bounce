package com.artec.jumpgame.UIelements;

import com.artec.jumpgame.utils.PlaySounds;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by bartek on 29.06.16.
 */
public class OverlayButton
{
    private State state;

    private Vector2 position;
    private float width, height;
    private boolean flipX, flipY;
    private Rectangle boundingRectangle;

    private TextureRegion normalRegion;
    private TextureRegion clickedRegion;
    private TextureRegion unActiveRegion;

    private TextureRegion currenRegion;

    private NinePatch normalNinePatch;
    private NinePatch clickedNinePatch;
    private NinePatch unActiveNinePatch;

    private NinePatch currentNinePatch;

    private boolean touched;
    private boolean active;

    public OverlayButton(Vector2 position, NinePatch normalNinePatch, NinePatch clickedNinePatch, NinePatch unActiveNinePatch,  float width, float height)
    {
        state = State.NINEPATCH;
        this.position = position;
        flipX = false;
        flipY = false;

        this.normalNinePatch = normalNinePatch;
        this.clickedNinePatch = clickedNinePatch;
        this.unActiveNinePatch = unActiveNinePatch;

        this.width = width;
        //height = (normalNinePatch.getTotalHeight() / normalNinePatch.getTotalWidth()) * width;
        this.height = height;

        init();
    }

    public OverlayButton(Vector2 position, TextureRegion normalRegion, TextureRegion clickedRegion, TextureRegion unActiveRegion, float width, boolean flipX, boolean flipY)
    {
        this(position, normalRegion, clickedRegion, width, flipX, flipY);
        this.unActiveRegion = unActiveRegion;
    }

    public OverlayButton(Vector2 position, TextureRegion normalRegion, TextureRegion clickedRegion, float width, boolean flipX, boolean flipY)
    {
        this(position, normalRegion, clickedRegion, width);
        this.flipX = flipX;
        this.flipY = flipY;
    }

    public OverlayButton(Vector2 position, TextureRegion normalRegion, TextureRegion clickedRegion, float width)
    {
        state = State.TEXUTURE_REGION;
        this.position = position;
        flipX = false;
        flipY = false;

        this.normalRegion = normalRegion;
        this.clickedRegion = clickedRegion;
        this.unActiveRegion = normalRegion;

        this.width = width;
        height = ((float)normalRegion.getRegionHeight() / (float)normalRegion.getRegionWidth()) * width;

        init();
    }

    public void init()
    {
        boundingRectangle = new Rectangle(position.x, position.y, width, height);
        if(state == State.TEXUTURE_REGION) currenRegion = normalRegion;
        else if(state == State.NINEPATCH) currentNinePatch = normalNinePatch;
        active = true;
    }

    public void render(SpriteBatch batch)
    {
        if(state == State.TEXUTURE_REGION)
            Utils.drawTextureRegion(batch, currenRegion, position, width, height, true, flipX, flipY);
        else
            currentNinePatch.draw(batch, position.x, position.y, width, height);
    }

    public boolean checkTouched(Vector2 touchPosition)
    {
        if(!active) return false;

        if(boundingRectangle.contains(touchPosition))
        {
            if(state == State.TEXUTURE_REGION) currenRegion = clickedRegion;
            else if(state == State.NINEPATCH) currentNinePatch = clickedNinePatch;
            if(!touched) PlaySounds.click(1f);
            touched = true;
            return true;
        }
        else
        {
            if(state == State.TEXUTURE_REGION) currenRegion = normalRegion;
            else if(state == State.NINEPATCH) currentNinePatch = normalNinePatch;
            touched = false;
            return false;
        }
    }

    public boolean getTouched() {return touched;}

    public void setActive(boolean newActive)
    {
        if(!active && newActive)
        {
            if(state == State.TEXUTURE_REGION) currenRegion = normalRegion;
            else if(state == State.NINEPATCH) currentNinePatch = normalNinePatch;
        }
        if(!newActive)
        {
            if(state == State.TEXUTURE_REGION) currenRegion = unActiveRegion;
            else if(state == State.NINEPATCH) currentNinePatch = unActiveNinePatch;
        }
        active = newActive;
        //touched = false;
    }
    public boolean isActive(){return active;}

    public void setRegions(TextureRegion normalRegion, TextureRegion clickedRegion)
    {
        if(state != State.TEXUTURE_REGION) return;

        this.normalRegion = normalRegion;
        this.clickedRegion = clickedRegion;
        currenRegion = normalRegion;
    }

    public void setNinePatches(NinePatch normalNinePatch, NinePatch clickedNinePatch, NinePatch unActiveNinePatch)
    {
        if(state != State.NINEPATCH) return;

        this.normalNinePatch = normalNinePatch;
        this.clickedNinePatch = clickedNinePatch;
        this.unActiveNinePatch = unActiveNinePatch;
        currentNinePatch = normalNinePatch;
    }

    public void setPosition(Vector2 newPosition)
    {
        position.set(newPosition);
    }
    public Vector2 getPosition() {return position;}

    public float getWidth() {return width;}
    public float getHeight() {return height;}

    private enum State {TEXUTURE_REGION, NINEPATCH}
}
