package com.artec.jumpgame.objects;

import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 12.06.16.
 */
public class Splash
{
    private Vector2 position;
    private float width, height;
    private float sizeScale;
    private float rotation;
    private Enums.EntityType entityType;

    public Splash(Vector2 position, Enums.EntityType entityType)
    {
        init(position, entityType);
        rotation = MathUtils.random(360);
    }

    private void init(Vector2 position, Enums.EntityType entityType)
    {
        this.position = new Vector2(position);
        this.entityType = entityType;

        sizeScale = MathUtils.random(Constants.SPLASH_MIN_SIZE, Constants.SPLASH_MAX_SIZE);
        width = Constants.SPLASH_BASE_WIDTH * sizeScale;
        height = width * ((float)entityType.splashRegion.getRegionHeight()/ (float)entityType.splashRegion.getRegionWidth());
    }


    public void render(SpriteBatch batch, float xOffset)
    {
        Vector2 tmpVector2 = Pools.obtain(Vector2.class).set(position.x + xOffset, position.y);
        Utils.drawRotatedTextureRegion(batch, entityType.splashRegion, tmpVector2, width, height, false, width/2, height/2, rotation);
        Pools.free(tmpVector2);
    }

    public Vector2 getPosition() {return position;}
}
