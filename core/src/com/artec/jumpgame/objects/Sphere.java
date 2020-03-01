package com.artec.jumpgame.objects;

import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 30.07.16.
 */
public class Sphere
{
    private Vector2 position;
    private TextureAtlas.AtlasRegion currentRegion;
    private float currentRadius, maxRadius;
    private boolean maxRadiusGrowing;

    public Sphere()
    {
        position = new Vector2();
        currentRegion = Assets.instance.playerAssets.sphereRegion;
        maxRadius = Constants.SPHERE_MAX_RADIUS;
        currentRadius = maxRadius;
        maxRadiusGrowing = false;
    }

    public void update(float delta, Vector2 centerPosition)
    {
        position.set(centerPosition);

        currentRadius -= Constants.SPHERE_SIZE_SHRINK_PER_SEC * delta;
        if(currentRadius <= 0) currentRadius = maxRadius;

        if(maxRadius > Constants.SPHERE_MAX_RADIUS*1f)  maxRadiusGrowing = false;
        if(maxRadius <= Constants.SPHERE_MAX_RADIUS*0.9f) maxRadiusGrowing = true;

        if(maxRadiusGrowing) maxRadius += Constants.SPHERE_SIZE_SHRINK_PER_SEC * 0.25f * delta;
        else maxRadius -= Constants.SPHERE_SIZE_SHRINK_PER_SEC * 0.25f * delta;
    }

    public void render(SpriteBatch batch)
    {
        Vector2 cornerPosition = Pools.obtain(Vector2.class).set(position.x - currentRadius, position.y - currentRadius);
        Utils.drawTextureRegion(batch, currentRegion, cornerPosition, currentRadius*2, currentRadius*2, false);
        cornerPosition.set(position.x - maxRadius, position.y - maxRadius);
        Utils.drawTextureRegion(batch, currentRegion, cornerPosition, maxRadius*2, maxRadius*2, false);
        Pools.free(cornerPosition);
    }

    public float getMaxRadius(){return maxRadius;}
}
