package com.artec.jumpgame.objects;

import com.artec.jumpgame.level.LevelSpawner;
import com.artec.jumpgame.overlays.ChromaticAberration;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.Fading;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 15.07.16.
 */
public class BrokenPlatform extends Platform
{
    private BrokenPlatformState brokenPlatformState;
    private float elapsedStateTime;
    private Fading fading;

    private float xOffsetMax, yOffsetMax;
    private float initialLeftPosition;

    private float[] tilesRotation;

    public BrokenPlatform()
    {
        fading = new Fading();
    }

    @Override
    public void init(float left, float top, int nrOfTiles, Enums.PlatformType platformType, float frictionVelocitySlowdown,
                     LevelSpawner.EnemySpawnedThisFrame enemyOnPlatform)
    {
        super.init(left, top, nrOfTiles, platformType, frictionVelocitySlowdown, enemyOnPlatform);
        regions = platformType.brokenPlatformTextures;
        initialLeftPosition = left;
        fading.init(Constants.PLATFORM_BROKEN_FADE_OUT_TIME, Fading.Fade.OUT);
        brokenPlatformState = BrokenPlatformState.INITIAL;
        elapsedStateTime = 0;

        xOffsetMax = Constants.PLATFORM_BROKEN_X_OFFSET_MAX;
        yOffsetMax = Constants.PLATFORM_BROKEN_Y_OFFSET_MAX;

        tilesRotation = new float[nrOfTiles];
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        elapsedStateTime += delta;
        if(brokenPlatformState == BrokenPlatformState.SHAKING)
        {
            left += velocity.x * delta;
            if(Math.abs(left-initialLeftPosition) > xOffsetMax) {velocity.x = -velocity.x;}
            if(elapsedStateTime > brokenPlatformState.SECONDS_TO_NEXT_STATE)
            {
                brokenPlatformState = BrokenPlatformState.FALLING;
                velocity.set(0, Constants.PLATFORM_BROKEN_Y_SPEED);
                elapsedStateTime = 0;
                collisionActive = false;
                for(int i = 0; i < tilesRotation.length; i++)
                {
                    if(MathUtils.random() < 0.5f)
                        tilesRotation[i] -= Constants.PLATFORM_BROKEN_ROTATION_CHANGE_PER_SEC_MIN * delta;
                    else
                        tilesRotation[i] += Constants.PLATFORM_BROKEN_ROTATION_CHANGE_PER_SEC_MIN * delta;
                }
            }
        }
        if(brokenPlatformState == BrokenPlatformState.FALLING)
        {
            top += velocity.y * delta;
            fading.update(delta);
            for(int i = 0; i < tilesRotation.length; i++)
            {
                if(tilesRotation[i] < 0)
                    tilesRotation[i] -= MathUtils.random(Constants.PLATFORM_BROKEN_ROTATION_CHANGE_PER_SEC_MIN,
                            Constants.PLATFORM_BROKEN_ROTATION_CHANGE_PER_SEC_MAX) * delta;
                else
                    tilesRotation[i] += MathUtils.random(Constants.PLATFORM_BROKEN_ROTATION_CHANGE_PER_SEC_MIN,
                            Constants.PLATFORM_BROKEN_ROTATION_CHANGE_PER_SEC_MAX) * delta;
            }

            if(elapsedStateTime > brokenPlatformState.SECONDS_TO_NEXT_STATE)
                deActivate();
        }

    }

    @Override
    public void render(SpriteBatch batch, float xOffset)
    {
        setupTrail();
        regions = platformType.brokenPlatformTextures;
        if(!ChromaticAberration.isActive()) batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, fading.getAlpha());
        Vector2 tilePositionTmp = Pools.obtain(Vector2.class).setZero();
        if(nrOfTiles == 1)
        {
            tilePositionTmp.set(left + xOffset, top - height);
            Utils.drawRotatedTextureRegion(batch, regions.get(0), tilePositionTmp, Constants.PLATFORM_TILE_WIDTH, height, true,
                    Constants.PLATFORM_TILE_WIDTH/2, height/2, tilesRotation[0]);

            if(sticky)  Utils.drawRotatedTextureRegion(batch, trailRegions.get(0), tilePositionTmp, Constants.PLATFORM_TILE_WIDTH,
                    height, true, Constants.PLATFORM_TILE_WIDTH/2, height/2, tilesRotation[0]);
        }
        else
        {
            for(int i = 0; i < nrOfTiles; i++)
            {
                tilePositionTmp.set(left + i * Constants.PLATFORM_TILE_WIDTH + xOffset, top - height);
                int tileNr;

                if(i == 0)                   tileNr = 1;
                else if (i == nrOfTiles - 1) tileNr = 3;
                else                         tileNr = 2;

                Utils.drawRotatedTextureRegion(batch, regions.get(tileNr), tilePositionTmp, Constants.PLATFORM_TILE_WIDTH, height, true,
                        Constants.PLATFORM_TILE_WIDTH/2, height/2, tilesRotation[i]);

                if(sticky)  Utils.drawRotatedTextureRegion(batch, trailRegions.get(tileNr), tilePositionTmp, Constants.PLATFORM_TILE_WIDTH,
                        height, true, Constants.PLATFORM_TILE_WIDTH/2, height/2, tilesRotation[i]);
            }
        }
        Pools.free(tilePositionTmp);
    }

    @Override
    public void playerLanded()
    {
        if(!landed)
        {
            super.playerLanded();
            brokenPlatformState = BrokenPlatformState.SHAKING;
            elapsedStateTime = 0;
            velocity.set(Constants.PLATFORM_BROKEN_X_SPEED, 0);
        }
    }

    public enum BrokenPlatformState
    {
        INITIAL(0), SHAKING(Constants.PLATFORM_BROKEN_TIME_BEFORE_FALLING), FALLING(Constants.PLATFORM_BROKEN_FADE_OUT_TIME);

        public final float SECONDS_TO_NEXT_STATE;

        BrokenPlatformState(float SECONDS_TO_NEXT_STATE)
        {
            this.SECONDS_TO_NEXT_STATE = SECONDS_TO_NEXT_STATE;
        }
    }
}
