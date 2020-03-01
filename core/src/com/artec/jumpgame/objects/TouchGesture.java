package com.artec.jumpgame.objects;

import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by bartek on 23.07.16.
 */
public class TouchGesture
{
    private Vector2 position;
    private float width, height, baseWidth;

    private long animationInitialTime;
    private TextureRegion currentRegion;

    private boolean active;

    public TouchGesture()
    {
        position = new Vector2();
        currentRegion = Assets.instance.playerAssets.touchAnimation.getKeyFrame(0);

        width = Constants.TOUCH_GESTURE_WIDTH;
        baseWidth = (float)currentRegion.getRegionWidth();
        height = width * ((float)currentRegion.getRegionHeight()/ (float)currentRegion.getRegionWidth());
    }

    public void start(Vector2 centerPosition)
    {
        this.position.set(centerPosition);
        animationInitialTime = TimeUtils.nanoTime();
        currentRegion = Assets.instance.playerAssets.touchAnimation.getKeyFrame(Utils.secondsSince(animationInitialTime));
    }

    public void render(SpriteBatch batch)
    {
        if(Assets.instance.playerAssets.touchAnimation.isAnimationFinished(Utils.secondsSince(animationInitialTime)) || !active) return;


        currentRegion = Assets.instance.playerAssets.touchAnimation.getKeyFrame(Utils.secondsSince(animationInitialTime));
        width = ((float) currentRegion.getRegionWidth() / baseWidth) * Constants.TOUCH_GESTURE_WIDTH;
        height = width * ((float) currentRegion.getRegionHeight() / (float) currentRegion.getRegionWidth());

        Vector2 cornerPosition = Pools.obtain(Vector2.class).set(position.x - width/2, position.y - height/2);
        Utils.drawTextureRegion(batch, currentRegion, cornerPosition, width, height, false);
        Pools.free(cornerPosition);
    }

    public void setActive(boolean newActive){active = newActive;}
}
