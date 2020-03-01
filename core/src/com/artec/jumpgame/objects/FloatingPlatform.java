package com.artec.jumpgame.objects;

import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.level.LevelSpawner;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by bartek on 14.07.16.
 */
public class FloatingPlatform extends Platform
{
    private Level level;
    private float initialHeight;


    public FloatingPlatform(Level level)
    {
        super();
        this.level = level;
    }

    public void init(float left, float top, int nrOfTiles, Enums.PlatformType platformType, float frictionVelocitySlowdown,
                     LevelSpawner.EnemySpawnedThisFrame enemyOnPlatform)
    {
        super.init(left, top, nrOfTiles, platformType, frictionVelocitySlowdown, enemyOnPlatform);
        initialHeight = top - height/2;
        velocity.set(MathUtils.random(Constants.PLATFORM_FLYING_X_SPEED_MIN, Constants.PLATFORM_FLYING_X_SPEED_MAX), 0);
    }

    public void update(float delta)
    {
        super.update(delta);
        left += velocity.x * delta;
        isOutOfBounds();
    }

    private void isOutOfBounds()
    {
        if(left < 0)
        {
            velocity.x = -velocity.x;
            left = 0;
        }
        if(left + width > level.getViewport().getWorldWidth())
        {
            velocity.x = -velocity.x;
            left = level.getViewport().getWorldWidth() - width;
        }

        /*
        if(top - height/2 < initialHeight - Constants.PLATFORM_FLYING_Y_AMPLITUDE)
        {
            velocity.y = -velocity.y;
            top = initialHeight - Constants.PLATFORM_FLYING_Y_AMPLITUDE + height/2;
        }
        if(top - height/2 > initialHeight + Constants.PLATFORM_FLYING_Y_AMPLITUDE)
        {
            velocity.y = -velocity.y;
            top = initialHeight + Constants.PLATFORM_FLYING_Y_AMPLITUDE + height/2;
        }
        */
    }
}
