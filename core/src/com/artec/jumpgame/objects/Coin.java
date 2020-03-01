package com.artec.jumpgame.objects;

import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 04.08.16.
 */
public class Coin
{
    private Level level;

    private Vector2 position; //Middle
    private Vector2 velocity;
    private float size;
    private float animationElapsedTime;

    private boolean active;

    public Coin(Vector2 position, Vector2 velocity, Level level)
    {
        this.level = level;
        this.position = new Vector2();
        this.velocity = new Vector2();
        init(position, velocity);
    }

    public void init(Vector2 position, Vector2 velocity)
    {
        this.position.set(position);
        this.velocity.set(velocity);
        animationElapsedTime = 0;
        size = Constants.COIN_SIZE;
        active = true;
    }

    public void render(SpriteBatch batch, float xOffset)
    {
        animationElapsedTime += Gdx.graphics.getDeltaTime();
        Vector2 cornerPosition = Pools.obtain(Vector2.class).set(position.x - size/2 + xOffset, position.y - size/2);
        Utils.drawTextureRegion(batch, Assets.instance.gameChangers.coinAnimation.getKeyFrame(animationElapsedTime),
                cornerPosition, size, size, false);
        Pools.free(cornerPosition);
    }

    public void update(float delta)
    {
        velocity.y -= Constants.GRAVITY_ACCELERATION *0.5f * delta;
        position.mulAdd(velocity, delta);
        isOutOfBounds();
    }

    private void isOutOfBounds()
    {
        if(position.x < size/2)
        {
            velocity.x = -velocity.x;
            position.x = size/2;
        }
        if(position.x > level.getViewport().getWorldWidth() - size/2)
        {
            velocity.x = -velocity.x;
            position.x = level.getViewport().getWorldWidth() - size/2;
        }
        if(position.y < level.getChaseCam().getPositionCenter().y - level.getViewport().getWorldHeight())
        {
            active = false;
        }
    }

    public Vector2 getPosition() {return position;}
    public float getSize() {return size;}

    public void setActive(boolean newActive) {active = newActive;}
    public boolean isActive() {return active;}
}
