package com.artec.jumpgame.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 31.07.16.
 */
public class TutorialAnimation
{
    private Level level;

    private float [] touchTimePosition; // Stores info about time, touchX and touchY in this order, last variable is always time to finish entire animation
    private int currentArrayIndex;
    private float elapsedTime;

    private boolean active;

    public void init(Level level)
    {
        this.level = level;

        if(level.getLevelState() != Level.LevelState.ANIMATION_1 && level.getLevelState() != Level.LevelState.ANIMATION_2
                && level.getLevelState() != Level.LevelState.ANIMATION_3 && level.getLevelState() != Level.LevelState.ANIMATION_4)
            active = false;
        else
            active = true;

        if(active)
        {
            this.touchTimePosition = level.getLevelState().animationTouchTimePosition;
            currentArrayIndex = 0;
            elapsedTime = 0;
        }
    }

    public void update(float delta)
    {
        if(!active) return;

        elapsedTime += delta;
        if(elapsedTime >= touchTimePosition[currentArrayIndex])
        {
            if(currentArrayIndex >= touchTimePosition.length-1)
            {
                level.getGameScreen().play(level.getLevelState());
            }
            else
            {
                Vector2 tempTouchPosition = Pools.obtain(Vector2.class).set(touchTimePosition[++currentArrayIndex], touchTimePosition[++currentArrayIndex]);
                level.getPlayer().shootBullet(tempTouchPosition);
                Pools.free(tempTouchPosition);

                currentArrayIndex++;
            }
        }

    }
}
