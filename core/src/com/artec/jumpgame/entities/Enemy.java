package com.artec.jumpgame.entities;

import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.objects.Bullet;
import com.artec.jumpgame.utils.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 01.06.16.
 */
public class Enemy
{
    protected Level level;

    public Vector2 position; //Left Corner
    protected Vector2 centerPosition; //Center
    protected Vector2 currentPlatformVelocity;
    private Eye eye;
    private Rectangle safeZone;

    public float width, height;
    private float baseWidth; //Width of initial region in pixels. Is used to get in-game width of other sprites within animation

    protected Enums.EntityType entityType;
    private TextureRegion currentRegion; //Current region to draw

    private float elapsedTime; //Current time of animation
    private boolean active; //If true this enemy gets deleted
    protected boolean dead; //If true death animation plays and collision between player is not checked anymore


    public Enemy(Enums.EntityType entityType, Level level)
    {
        this.level = level;
        this.position = new Vector2();
        centerPosition = new Vector2();
        currentPlatformVelocity = new Vector2();
        safeZone = new Rectangle();
        this.entityType = entityType;
        active = true;
        dead = false;

        elapsedTime = 0;
        currentRegion = entityType.entityAnimation.getKeyFrame(elapsedTime);

        width = Constants.ENEMY_WIDTH;
        baseWidth = (float)currentRegion.getRegionWidth();
        height = width * ((float)currentRegion.getRegionHeight()/ (float)currentRegion.getRegionWidth());

        eye = new Eye(entityType);
        eye.init();
    }

    public void update(float delta)
    {
        elapsedTime += delta;

        position.set(centerPosition.x - width / 2, centerPosition.y - height / 2);
        width = ((float) currentRegion.getRegionWidth() / baseWidth) * Constants.ENEMY_WIDTH;
        height = width * ((float) currentRegion.getRegionHeight() / (float) currentRegion.getRegionWidth());

        Vector2 eyeCenterTmp = Pools.obtain(Vector2.class).set(centerPosition.x, centerPosition.y + height / 2 - entityType.ENEMY_EYE_OFFSET_FROM_TOP);
        eye.update(eyeCenterTmp, level.getPlayer().getCenterPosition());
        Pools.free(eyeCenterTmp);
    }

    public void reload(){;}

    public boolean checkBulletCollision(Bullet bullet)
    {
        /*
        Circle circle = new Circle(bullet.position.x, bullet.position.y, bullet.bulletAnimation.getCurrentSize());
        Rectangle rectangle = Pools.obtain(Rectangle.class).set(position.x, position.y, width, height);
        boolean containsACorner = circle.contains(rectangle.x, rectangle.y) || // Bottom left
                circle.contains(rectangle.x + rectangle.width, rectangle.y) || // Bottom right
                circle.contains(rectangle.x + rectangle.width, rectangle.y + rectangle.height) || // Top Right
                circle.contains(rectangle.x, rectangle.y + rectangle.height); // Top left

        boolean inHorizontalInterval = rectangle.x < circle.x && circle.x < rectangle.x + rectangle.width;
        boolean inVerticalInterval = rectangle.y < circle.y && circle.y < rectangle.y + rectangle.height;

        boolean inHorizontalNeighborhood = rectangle.x - circle.radius < circle.x && circle.x < rectangle.x + rectangle.width + circle.radius;
        boolean inVerticalNeighborhood = rectangle.y - circle.radius < circle.y && circle.y < rectangle.y + rectangle.height + circle.radius;

        boolean touchingAnEdge = inHorizontalInterval && inVerticalInterval ||
                inHorizontalNeighborhood && inVerticalNeighborhood;

        Pools.free(rectangle);
        return containsACorner || touchingAnEdge;
        */

        if(centerPosition.dst(bullet.position) < width/2 + bullet.bulletAnimation.getCurrentSize())
        {
            if(Constants.bulletBouncingOffEnemies && bullet.getBounceCount() <= Constants.BULLET_MAX_BOUNCES)
            {
                float size1 = width;
                float size2 = bullet.bulletAnimation.getCurrentSize() * 2;

                float newVelX2 = (bullet.velocity.x * (size2 - size1) + (2 * size1 * 1)) / (size1 + size2);
                float newVelY2 = (bullet.velocity.y * (size2 - size1) + (2 * size1 * 1)) / (size1 + size2);

                newVelX2 = newVelX2 * Constants.bouncingBallVelocityMultiplier;
                newVelY2 = newVelY2 * Constants.bouncingBallVelocityMultiplier;

                bullet.velocity.set(newVelX2, newVelY2);
                bullet.bounced();
            }
            return true;
        }
        return false;
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
        if(dead)
        {
            currentRegion = entityType.entityDeathAnimation.getKeyFrame(elapsedTime);
            if(entityType.entityDeathAnimation.isAnimationFinished(elapsedTime)) active = false;
        }
        else
        {
            currentRegion = entityType.entityAnimation.getKeyFrame(elapsedTime);
        }
        Vector2 tmpPosition = Pools.obtain(Vector2.class).set(position.x + xOffset, position.y);
        Utils.drawTextureRegion(batch, currentRegion, tmpPosition, width, height, false);
        Pools.free(tmpPosition);
        if(!dead)eye.render(batch, xOffset);
    }

    public void die(boolean playSplashSound)
    {
        if(playSplashSound)
        PlaySounds.entityExplosion(0.6f);

        dead = true;
        elapsedTime = 0;
    }

    public float getHeight(){return height;}
    public Enums.EntityType getEntityType() {return entityType;}

    public Rectangle getSafeZone()
    {
        safeZone.set(
                position.x + Constants.ENEMY_WIDTH * 0.25f,
                position.y ,
                Constants.ENEMY_WIDTH * 0.5f,
                height * 0.4f);

        return safeZone;
    }

    public Vector2 getCenterPosition() {return centerPosition;}
    public boolean isActive(){return active;}
    public boolean isDead(){return dead;}
}
