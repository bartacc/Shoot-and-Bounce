package com.artec.jumpgame.entities;

import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.objects.Bullet;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.PlaySounds;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 02.07.16.
 */
public class FlyingEnemy extends Enemy
{
    private float initialHeight;
    private Vector2 velocity;
    private Rectangle safeZone;

    private float elapsedTime;
    private float interval;
    private DelayedRemovalArray<Bullet> bulletArray;

    private boolean soundStarted;

    public FlyingEnemy(Enums.EntityType entityType, float initialHeight, Level level)
    {
        super(entityType, level);
        this.initialHeight = initialHeight;
        super.centerPosition.set(MathUtils.random(0, level.getViewport().getWorldWidth() - width), initialHeight);
        velocity = new Vector2(Constants.ENEMY_FLYING_X_SPEED, Constants.ENEMY_FLYING_Y_SPEED);
        safeZone = new Rectangle();

        this.level = level;

        soundStarted = false;
        elapsedTime = 0;
        bulletArray = new DelayedRemovalArray<com.artec.jumpgame.objects.Bullet>();
        initBullets();
        interval = level.getDifficulty().ENEMY_SHOTS_INTERVAL_MIN *0.25f;
    }

    public void initBullets()
    {
        for(int i = 0; i < Constants.PLAYER_BULLETS_AT_START - bulletArray.size; i++)
        {
            Bullet bullet = new Bullet(
                    new Vector2(position.x + width/2, position.y + height/2), level,
                    new Bullet.BulletAnimation(entityType, Constants.ENEMY_BULLET_SMALL_RADIUS,
                            Constants.ENEMY_BULLET_MEDIUM_RADIUS, Constants.ENEMY_BULLET_BIG_RADIUS, entityType.particleEffectPool),
                    this, true);
            bulletArray.add(bullet);
        }
        interval = MathUtils.random(level.getDifficulty().ENEMY_SHOTS_INTERVAL_MIN, level.getDifficulty().ENEMY_SHOTS_INTERVAL_MAX);
    }

    public void update(float delta)
    {
        super.update(delta);
        if(!dead)
        {
            centerPosition.mulAdd(velocity, delta);
            isOutOfBounds();
            updateShots(delta);
            PlaySounds.propellerSoundLoopUpdate(Constants.SOUND_PROPELLER_VOLUME);
        }
    }

    public void reload()
    {
        bulletArray.clear();
        initBullets();
    }

    public void updateShots(float delta) //TODO: This method is exactly the same as ShootingEnemy.updateShots(float delta);
    {
        for(Bullet bullet : bulletArray)
            bullet.update(delta);

        if(soundStarted) elapsedTime += delta;

        if(position.y + height/2 < level.getChaseCam().getPositionCenter().y + level.getViewport().getWorldHeight()/2)
        {
            if(!soundStarted)
            {
                PlaySounds.addPropellerSoundPlayer();
                soundStarted = true;
            }
        }

        if(elapsedTime > interval && !dead) //Shoot the bullet
        {
            float shotVelocityY = MathUtils.random(Constants.ENEMY_FLYING_SHOT_VELOCITY_Y_MAX, Constants.ENEMY_FLYING_SHOT_VELOCITY_Y_MIN);

            Vector2 bulletVelocityTmp = Pools.obtain(Vector2.class).set(0, shotVelocityY);
            level.shootBullet(bulletArray.get(bulletArray.size - 1), bulletVelocityTmp);
            Pools.free(bulletVelocityTmp);
            bulletArray.removeIndex(bulletArray.size - 1);

            elapsedTime = 0;
            initBullets();
        }
    }

    private void isOutOfBounds()
    {
        if(centerPosition.x - width/2 < 0)
        {
            velocity.x = -velocity.x;
            centerPosition.x = width/2;
        }
        if(centerPosition.x + width/2 > level.getViewport().getWorldWidth())
        {
            velocity.x = -velocity.x;
            centerPosition.x = level.getViewport().getWorldWidth() - width/2;
        }

        if(centerPosition.y < initialHeight - Constants.ENEMY_FLYING_Y_AMPLITUDE)
        {
            velocity.y = -velocity.y;
            centerPosition.y = initialHeight - Constants.ENEMY_FLYING_Y_AMPLITUDE;
        }
        if(centerPosition.y > initialHeight + Constants.ENEMY_FLYING_Y_AMPLITUDE)
        {
            velocity.y = -velocity.y;
            centerPosition.y = initialHeight + Constants.ENEMY_FLYING_Y_AMPLITUDE;
        }
    }

    public void debugRender(ShapeRenderer renderer)
    {
        renderer.setColor(Color.BLUE);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.rect(position.x, position.y, width, height);
        renderer.rect(getSafeZone().x, getSafeZone().y, getSafeZone().width, getSafeZone().height);
        renderer.end();
    }

    public void render(SpriteBatch batch, float xOffset)
    {
        super.render(batch, xOffset);
        for(Bullet bullet : bulletArray)
            bullet.render(batch, xOffset);
    }

    public void die(boolean playSplashSound)
    {
        PlaySounds.removePropellerSoundPlayer();
        PlaySounds.propellerSoundLoopUpdate(Constants.SOUND_PROPELLER_VOLUME);
        super.die(playSplashSound);
    }

    public Rectangle getSafeZone()
    {
        safeZone.set(
                position.x + Constants.ENEMY_WIDTH * 0.25f,
                position.y + height * 0.15f,
                Constants.ENEMY_WIDTH * 0.5f,
                height * 0.35f);

        return safeZone;
    }
}
