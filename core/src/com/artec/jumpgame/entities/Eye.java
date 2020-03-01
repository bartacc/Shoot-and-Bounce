package com.artec.jumpgame.entities;

import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by bartek on 10.07.16.
 */
public class Eye
{
    private State state;

    private TextureRegion whiteEye;
    private Enums.EntityType entityType;
    private Vector2 whiteEyePosition; //Center
    private Vector2 blackEyePosition; //Center
    private float currentAngle;

    private float elapsedTime;
    private float blinkInterval; //Interval between blinks

    public Eye(Enums.EntityType entityType)
    {
        this.entityType = entityType;
        this.whiteEye = Assets.instance.eyeAssets.eyeWhite;
        whiteEyePosition = new Vector2();
        blackEyePosition = new Vector2();
    }
    public void init()
    {
        elapsedTime = 0;
        blinkInterval = MathUtils.random(Constants.EYE_BLINK_INTERVAL_MIN, Constants.EYE_BLINK_INTERVAL_MAX);
        whiteEyePosition.setZero();
        blackEyePosition.setZero();
        state = State.NORMAL;
        currentAngle = 0;
    }

    public void update(Vector2 newWhiteEyePosition, Vector2 lookedAtPosition)
    {
        this.whiteEyePosition.set(newWhiteEyePosition);

        float angle = (float) Math.atan2(lookedAtPosition.y - whiteEyePosition.y,
                lookedAtPosition.x - whiteEyePosition.x);

        if (angle < 0) angle += MathUtils.PI2;

        float distanceX = (float) Math.cos(angle) * entityType.EYE_SMALL_OFFSET_MAX;
        float distanceY = (float) Math.sin(angle) * entityType.EYE_SMALL_OFFSET_MAX;


        blackEyePosition.set(whiteEyePosition.x + distanceX, whiteEyePosition.y + distanceY);
        currentAngle = angle * MathUtils.radiansToDegrees;

        updateBlink();
    }

    private void updateBlink()
    {
        elapsedTime += Gdx.graphics.getDeltaTime();
        if(state == State.NORMAL)
        {
            if(elapsedTime > blinkInterval)
            {
                state = State.BLINK;
                elapsedTime = 0;
                blinkInterval = MathUtils.random(Constants.EYE_BLINK_INTERVAL_MIN, Constants.EYE_BLINK_INTERVAL_MAX);
            }
        }

        if(state == State.BLINK)
        {
            if(elapsedTime > Constants.EYE_BLINK_DURATION)
            {
                state = State.NORMAL;
                elapsedTime = 0;
            }
        }
    }

    public void render(SpriteBatch batch, float xOffset)
    {
        this.whiteEye = Assets.instance.eyeAssets.eyeWhite;

        if(state == State.NORMAL)
        {
            Utils.drawTextureRegion(batch, whiteEye, whiteEyePosition.x - entityType.EYE_BIG_SIZE / 2 + xOffset, whiteEyePosition.y - entityType.EYE_BIG_SIZE / 2,
                   entityType.EYE_BIG_SIZE, entityType.EYE_BIG_SIZE, false, 0, 0, 0);
            Utils.drawTextureRegion(batch, entityType.eyeSmall, blackEyePosition.x - entityType.EYE_SMALL_SIZE / 2 + xOffset, blackEyePosition.y - entityType.EYE_SMALL_SIZE / 2,
                    entityType.EYE_SMALL_SIZE,entityType.EYE_SMALL_SIZE, false, entityType.EYE_SMALL_SIZE / 2, entityType.EYE_SMALL_SIZE / 2, currentAngle);
        }
        else if(state == State.BLINK)
        {
            Utils.drawTextureRegion(batch, entityType.eyeBlink, whiteEyePosition.x - entityType.EYE_BIG_SIZE / 2 + xOffset, whiteEyePosition.y - entityType.EYE_BIG_SIZE / 2,
                    entityType.EYE_BIG_SIZE, entityType.EYE_BIG_SIZE, false, 0, 0, 0);
        }

    }

    public enum State
    {
        NORMAL, BLINK;
    }
}
