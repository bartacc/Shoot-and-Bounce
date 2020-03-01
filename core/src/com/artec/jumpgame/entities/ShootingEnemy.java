package com.artec.jumpgame.entities;

import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.objects.Bullet;
import com.artec.jumpgame.objects.Platform;
import com.artec.jumpgame.utils.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;

/**
 * Created by bartek on 10.06.16.
 */
public class ShootingEnemy extends WalkingEnemy
{
    private boolean visible;
    private float elapsedTime;
    private float interval;
    private DelayedRemovalArray<Bullet> bulletArray;

    public ShootingEnemy(Platform platform, Level level, Enums.EntityType entityType)
    {
        super(platform, entityType, level);
        this.level = level;
        elapsedTime = 0;
        bulletArray = new DelayedRemovalArray<com.artec.jumpgame.objects.Bullet>();
        initBullets();
        interval = level.getDifficulty().ENEMY_SHOTS_INTERVAL_MIN *0.25f;
        visible = false;
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

    public void reload()
    {
        bulletArray.clear();
        initBullets();
    }

    public void update(float delta)
    {
        super.update(delta);
        for(Bullet bullet : bulletArray)
            bullet.update(delta);

        if(visible) elapsedTime += delta;

        if(position.y + height/2 < level.getChaseCam().getPositionCenter().y + level.getViewport().getWorldHeight()/2 && !visible)
            visible = true;

        if(elapsedTime > interval && !dead) //Shoot the bullet
        {
            float shotVelocityY = MathUtils.random(Constants.ENEMY_SHOT_VELOCITY_Y_MIN, Constants.ENEMY_SHOT_VELOCITY_Y_MAX);
            float shotVelocityX;

            if(level.getPlayer().position.x + Constants.PLAYER_WIDTH/2 < position.x + Constants.ENEMY_WIDTH/2) shotVelocityX = -Constants.ENEMY_SHOT_SPEED;
            else shotVelocityX = Constants.ENEMY_SHOT_SPEED;

            level.shootBullet(bulletArray.get(bulletArray.size - 1), new Vector2(shotVelocityX, shotVelocityY));
            bulletArray.removeIndex(bulletArray.size - 1);

            elapsedTime = 0;
            initBullets();
        }
    }

    public void render(SpriteBatch batch, float xOffset)
    {
        super.render(batch, xOffset);
        for(com.artec.jumpgame.objects.Bullet bullet : bulletArray)
            bullet.render(batch, xOffset);
    }

    public void debugRender(ShapeRenderer renderer)
    {
        super.debugRender(renderer);
        for(com.artec.jumpgame.objects.Bullet bullet : bulletArray)
            bullet.debugRender(renderer);
    }
}
