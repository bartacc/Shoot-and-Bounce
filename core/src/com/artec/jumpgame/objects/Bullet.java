package com.artec.jumpgame.objects;

import com.artec.jumpgame.entities.Enemy;
import com.artec.jumpgame.entities.Player;
import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.overlays.ChromaticAberration;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.Fading;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 01.06.16.
 */
public class Bullet
{
    private boolean active;
    private int collisionsCount;

    public Vector2 position; //Middle
    private Vector2 lastFramePosition; // Middle
    private Vector2 relativePosition; //Position relative to entity
    public Vector2 velocity;
    public Enums.InsideEntity insideEntity;
    private Level level;

    //Entity having this bullet, could be NULL
    private Enemy enemy;
    private Player player;

    private BulletTrail bulletTrail;

    public BulletAnimation bulletAnimation; //Bullet animation holds info about current radius of bullet and Animation
    private TextureRegion currentRegion; //Current region to draw
    private float elapsedSeconds;
    private Rectangle inEntitySafeZone;

    Fading fading; //Holds info about current alpha to draw

    //STATS
    private int enemiesKilled;

    public Bullet(Vector2 position, Level level, BulletAnimation animation)
    {
        this.insideEntity = Enums.InsideEntity.OUT;
        init(position, level, animation);
    }

    public Bullet(Vector2 position, Level level, BulletAnimation animation, Player player, boolean fade)
    {
        this.insideEntity = Enums.InsideEntity.IN_PLAYER;
        this.player = player;
        fading = new Fading(Constants.BULLET_FADE_IN_TIME, Fading.Fade.IN);
        if(!fade) fading.toggleActive();
        init(position, level, animation);
    }

    public Bullet(Vector2 position,  Level level, BulletAnimation animation, Enemy enemy, boolean fade)
    {
        this.insideEntity = Enums.InsideEntity.IN_ENEMY;
        this.enemy = enemy;
        fading = new Fading(Constants.BULLET_FADE_IN_TIME, Fading.Fade.IN);
        if(!fade) fading.toggleActive();
        init(position, level, animation);
    }

    private void init(Vector2 position, Level level, BulletAnimation animation)
    {
        enemiesKilled = 0;
        active = true;
        collisionsCount = 0;

        this.position = new Vector2(position);
        this.lastFramePosition = new Vector2(position);
        this.velocity = new Vector2();
        this.level = level;

        this.bulletAnimation = animation;
        this.currentRegion = bulletAnimation.getAnimation().getKeyFrame(0);
        elapsedSeconds = 0;
        this.bulletAnimation.setCurrentSize(elapsedSeconds);
        bulletTrail = new BulletTrail(bulletAnimation);

        relativePosition = new Vector2();
        inEntitySafeZone = new Rectangle();

        if(insideEntity == Enums.InsideEntity.IN_PLAYER)
        {
            velocity.set(
                    MathUtils.random(Constants.BULLET_IN_PLAYER_MIN_VELOCITY ,Constants.BULLET_IN_PLAYER_MAX_VELOCITY),
                    MathUtils.random(Constants.BULLET_IN_PLAYER_MIN_VELOCITY ,Constants.BULLET_IN_PLAYER_MAX_VELOCITY));

            inEntitySafeZone.set(player.getSafeZone());

            relativePosition.x = this.position.x - inEntitySafeZone.x;
            relativePosition.y = this.position.y - inEntitySafeZone.y;

        }

        if(insideEntity == Enums.InsideEntity.IN_ENEMY)
        {
            velocity.set(
                    MathUtils.random(Constants.BULLET_IN_PLAYER_MIN_VELOCITY ,Constants.BULLET_IN_PLAYER_MAX_VELOCITY),
                    MathUtils.random(Constants.BULLET_IN_PLAYER_MIN_VELOCITY ,Constants.BULLET_IN_PLAYER_MAX_VELOCITY));

            relativePosition.x = this.position.x - inEntitySafeZone.x;
            relativePosition.y = this.position.y - inEntitySafeZone.y;
        }
    }

    public void reload()
    {
        bulletTrail.reload();
    }


    public void update(float delta)
    {
        lastFramePosition.set(position);
        if(insideEntity == Enums.InsideEntity.IN_PLAYER)
        {
            elapsedSeconds = 0;
            inEntitySafeZone.set(player.getSafeZone());

            position.x = inEntitySafeZone.x + relativePosition.x;
            position.y = inEntitySafeZone.y + relativePosition.y;
            updateInEntity(delta);
        }
        else if(insideEntity == Enums.InsideEntity.IN_ENEMY)
        {
            elapsedSeconds = 0;
            inEntitySafeZone.set(enemy.getSafeZone());

            position.x = inEntitySafeZone.x + relativePosition.x;
            position.y = inEntitySafeZone.y + relativePosition.y;
            updateInEntity(delta);
        }
        else if(insideEntity == Enums.InsideEntity.OUT)
        {
            if(Constants.bulletBouncingOffWalls) isOutOfBounds();
            fading.setOpaque();
            elapsedSeconds += delta;

            velocity.y -= Constants.GRAVITY_ACCELERATION*0.3f * delta;
            position.mulAdd(velocity, delta);
        }
    }

    public boolean checkPlatformCollision(Platform platform)
    {
        if(!platform.isCollisionActive() || insideEntity != Enums.InsideEntity.OUT ||
                collisionsCount >= Constants.BULLET_MAX_BOUNCES || player == null) return false;
        boolean colliding = false;
        if(lastFramePosition.y >= platform.top && position.y < platform.top && position.x > platform.left && position.x  < platform.left + platform.width)
        {
            position.y = platform.top + bulletAnimation.getCurrentSize();
            velocity.y = -velocity.y;
            collisionsCount++;
        }
        return colliding;
    }

    private void updateInEntity(float delta)
    {
        checkCollisionInEntity();
        relativePosition.mulAdd(velocity, delta);

        fading.update(delta);
    }

    private void isOutOfBounds()
    {
        if(collisionsCount >= Constants.BULLET_MAX_BOUNCES) return;

        if(position.x - bulletAnimation.getCurrentSize() < 0)
        {
            position.x = bulletAnimation.getCurrentSize();
            velocity.x = -velocity.x;
            collisionsCount++;
        }
        if(position.x + bulletAnimation.getCurrentSize() > level.getViewport().getWorldWidth())
        {
            position.x = level.getViewport().getWorldWidth() - bulletAnimation.getCurrentSize();
            velocity.x = -velocity.x;
            collisionsCount++;
        }
    }

    private void checkCollisionInEntity()
    {
        if(relativePosition.x - bulletAnimation.getCurrentSize() < 0)
        {
            velocity.x = -velocity.x;
            relativePosition.x = bulletAnimation.getCurrentSize();
        }
        if(relativePosition.x + bulletAnimation.getCurrentSize() > inEntitySafeZone.width)
        {
            velocity.x = -velocity.x;
            relativePosition.x = inEntitySafeZone.width - bulletAnimation.getCurrentSize();
        }
        if(relativePosition.y - bulletAnimation.getCurrentSize() < 0)
        {
            velocity.y = -velocity.y;
            relativePosition.y = bulletAnimation.getCurrentSize();
        }
        if(relativePosition.y + bulletAnimation.getCurrentSize() > inEntitySafeZone.height)
        {
            velocity.y = -velocity.y;
            relativePosition.y = inEntitySafeZone.height - bulletAnimation.getCurrentSize();
        }
    }

    public void checkCollisionWithBullet(Bullet bullet, float delta)
    {
        if(position.dst(bullet.position) < bulletAnimation.getCurrentSize() + bullet.bulletAnimation.getCurrentSize())
        {
            float size1 = bulletAnimation.getCurrentSize() * 2;
            float size2 = bullet.bulletAnimation.getCurrentSize() * 2;

            float newVelX1 = (velocity.x * (size1 - size2) + (2 * size2 * bullet.velocity.x)) / (size1 + size2);
            float newVelY1 = (velocity.y * (size1 - size2) + (2 * size2 * bullet.velocity.y)) / (size1 + size2);
            float newVelX2 = (bullet.velocity.x * (size2 - size1) + (2 * size1 * velocity.x)) / (size1 + size2);
            float newVelY2 = (bullet.velocity.y * (size2 - size1) + (2 * size1 * velocity.y)) / (size1 + size2);

            velocity.set(newVelX1, newVelY1);
            bullet.velocity.set(newVelX2, newVelY2);

                relativePosition.mulAdd(velocity, delta * 3); // delta * 1 doesn't move the bullet enough
                bullet.relativePosition.mulAdd(bullet.velocity, delta * 3);
        }
    }

    public void shootBullet(Vector2 velocity)
    {
        insideEntity = Enums.InsideEntity.OUT;
        this.velocity.set(velocity);
        elapsedSeconds = 0;
    }

    public boolean isOutOfScreen()
    {
        if(position.y + bulletAnimation.getCurrentSize() + bulletTrail.getBoundingRectangle().getHeight()
                < level.getViewport().getCamera().position.y - level.getViewport().getWorldHeight()/2) return true;
        //if(position.y - bulletAnimation.getCurrentSize() > level.getViewport().getCamera().position.y + level.getViewport().getWorldHeight()/2) return true;
        if(position.x + bulletAnimation.getCurrentSize() + bulletTrail.getBoundingRectangle().getWidth() < 0) return true;
        if(position.x - bulletAnimation.getCurrentSize() - bulletTrail.getBoundingRectangle().getWidth()
                > level.getViewport().getWorldWidth()) return true;
        return false;
    }

    public void debugRender(ShapeRenderer renderer)
    {
        renderer.setColor(Color.RED);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.rect(inEntitySafeZone.x, inEntitySafeZone.y, inEntitySafeZone.width, inEntitySafeZone.height);
        renderer.circle(position.x, position.y, bulletAnimation.getCurrentSize());
        renderer.end();
    }

    public void render(SpriteBatch batch, float xOffset)
    {
        bulletAnimation.setCurrentSize(elapsedSeconds);
        currentRegion = bulletAnimation.getAnimation().getKeyFrame(elapsedSeconds);
        if(insideEntity == Enums.InsideEntity.OUT) bulletTrail.update(Gdx.graphics.getDeltaTime(), position, velocity);

        Vector2 cornerPosition = Pools.obtain(Vector2.class).set(
                position.x - bulletAnimation.getCurrentSize() + xOffset, position.y - bulletAnimation.getCurrentSize());

        if(!ChromaticAberration.isActive())
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, fading.getAlpha());

        bulletTrail.render(batch);
        Utils.drawTextureRegion(batch, currentRegion,
                cornerPosition, bulletAnimation.getCurrentSize() * 2, bulletAnimation.getCurrentSize() * 2, true);

        Pools.free(cornerPosition);


    }

    public void initBulletTrail(Enums.EntityType entityType, float small, float medium, float big, ParticleEffectPool particleEffectPool)
    {
        this.bulletAnimation = new BulletAnimation(entityType, small, medium, big, particleEffectPool);
        this.bulletTrail = new BulletTrail(bulletAnimation);
    }

    public boolean killedEnemy()
    {
        enemiesKilled++;
        return enemiesKilled >= 2;
    }

    public void die()
    {
        active = false;
        bulletTrail.die();
    }

    public void bounced() {collisionsCount++;}
    public int getBounceCount() {return collisionsCount;}
    public boolean isActive(){return active;}

    public void setVelocity(float x, float y){this.velocity.set(x, y);}

    public Enemy getEnemy(){return enemy;}
    public Player getPlayer(){return player;}

    /**
     * Created by bartek on 05.06.16.
     */
    public static class BulletAnimation
    {
        private Enums.EntityType entityType;
        private ParticleEffectPool particleEffectPool;

        private float sizeSmall;
        private float sizeMedium;
        private float sizeBig;
        private float currentSize;

        public BulletAnimation(Enums.EntityType entityType, float small, float medium, float big, ParticleEffectPool particleEffectPool)
        {
            this.entityType = entityType;
            this.sizeSmall = small;
            this.sizeMedium = medium;
            this.sizeBig = big;
            currentSize = sizeSmall;
            this.particleEffectPool = particleEffectPool;
        }

        public float getCurrentSize()
        {
            return currentSize;
        }

        public void setCurrentSize(float timeElapsed)
        {
            if(entityType.bulletAnimation.getKeyFrameIndex(timeElapsed) == 0) this.currentSize = sizeSmall;
            else if(entityType.bulletAnimation.getKeyFrameIndex(timeElapsed) == 1) this.currentSize = sizeMedium;
            else this.currentSize = sizeBig;
        }

        public ParticleEffectPool getParticleEffectPool() {return particleEffectPool;}
        public Animation getAnimation() {return entityType.bulletAnimation;}
        public Enums.EntityType getEntityType() {return entityType;}

        public float getSizeSmall() {return sizeSmall;}
        public float getSizeMedium() {return sizeMedium;}
        public float getSizeBig() {return sizeBig;}
    }
}
