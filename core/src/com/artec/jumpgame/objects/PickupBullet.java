package com.artec.jumpgame.objects;

import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 25.06.16.
 */
public class PickupBullet implements Pool.Poolable
{
    private Viewport viewport;
    private Level level;

    private Vector2 lastFramePosition;
    private Vector2 position; //Left corner
    private Vector2 positionCenter;
    private Vector2 velocity;
    private float velocityMultiplier;
    private float height;

    private boolean landedThisFrame; //Did this pickupBullet land on platform (if false it means that it's in the air)
    private Vector2 currentPlatformVelocity;
    private float currentPlatformFrictionSlowdown;

    private Enums.EntityType entityType;

    private Enums.Owner owner;
    private static int pickUpBulletsInPlayer = 0;
    private boolean active;

    public PickupBullet(Level level)
    {
        this.viewport = level.getViewport();
        this.level = level;

        this.position = new Vector2();
        positionCenter = new Vector2();
        lastFramePosition = new Vector2();
        this.velocity = new Vector2();
        currentPlatformVelocity = new Vector2();
    }

    public void init(Vector2 position, Vector2 velocity, Enums.EntityType entityType)
    {
        this.position.set(position);
        this.lastFramePosition.set(position);
        this.velocity.set(velocity);
        velocityMultiplier = 1;
        this.entityType = entityType;

        height = Constants.PICKUPBULLET_WIDTH * (float)(entityType.pickupBulletRegion.getRegionHeight() / entityType.pickupBulletRegion.getRegionWidth());

        owner = Enums.Owner.ENEMY;
        active = true;
        currentPlatformVelocity.setZero();
        landedThisFrame = false;
    }

    public void render(SpriteBatch batch, float xOffset)
    {
        Vector2 tmpPosition = Pools.obtain(Vector2.class).set(position.x + xOffset, position.y);
        Utils.drawTextureRegion(batch, entityType.pickupBulletRegion, tmpPosition, Constants.PICKUPBULLET_WIDTH, height, false);
        Pools.free(tmpPosition);
    }

    public void update(float delta)
    {
        lastFramePosition.set(position);
        positionCenter.set(position.x + getWidth()/2, position.y + height/2);

        height = Constants.PICKUPBULLET_WIDTH * (float)(entityType.pickupBulletRegion.getRegionHeight() / entityType.pickupBulletRegion.getRegionWidth());

        if(owner == Enums.Owner.PLAYER)
        {
                Rectangle playerSafeZone = level.getPlayer().getSafeZone();
                Vector2 targetPosition = Pools.obtain(Vector2.class).set(playerSafeZone.x + playerSafeZone.width / 2, playerSafeZone.y + playerSafeZone.height / 2);

                float angle = (float) Math.atan2(targetPosition.y - (position.y + height / 2),
                        targetPosition.x - (position.x + getWidth() / 2));

                if (angle < 0) angle += MathUtils.PI2;

                float distanceX = (float) Math.cos(angle) * Constants.PICKUPBULLET_MOVING_TO_PLAYER_VELOCITY * velocityMultiplier;
                float distanceY = (float) Math.sin(angle) * Constants.PICKUPBULLET_MOVING_TO_PLAYER_VELOCITY * velocityMultiplier;

                velocity.set(distanceX, distanceY);
                velocityMultiplier += 0.1f;

                Pools.free(targetPosition);
        }


        if(owner == Enums.Owner.ENEMY)
        {
            if(landedThisFrame)
            {
                if(velocity.x > currentPlatformFrictionSlowdown * delta) velocity.x -= currentPlatformFrictionSlowdown * delta;
                else if(velocity.x < -currentPlatformFrictionSlowdown * delta) velocity.x += currentPlatformFrictionSlowdown * delta;
                else velocity.x = 0;
            }


            velocity.y -= Constants.GRAVITY_ACCELERATION * delta;
            position.mulAdd(currentPlatformVelocity, delta);
            isOutOfBounds();
        }

        position.mulAdd(velocity, delta);

        landedThisFrame = false;
    }

    public void checkPlatformCollision(Platform platform)
    {
        if(owner == Enums.Owner.PLAYER || !platform.isCollisionActive()) return;

        if(lastFramePosition.y >= platform.top && position.y < platform.top)
        {
            if(position.x + Constants.PICKUPBULLET_WIDTH/2 > platform.left && position.x + Constants.PICKUPBULLET_WIDTH/2 < platform.left + platform.width)
            {
                position.y = platform.top;
                velocity.y = 0;
                currentPlatformVelocity.set(platform.getVelocity());
                currentPlatformFrictionSlowdown = platform.getFrictionVelocitySlowdown() * Constants.PICKUPBULLET_FRICTION_VELOCITY_SLOWDOWN_MULTIPLIER;
                landedThisFrame = true;
            }
        }
    }

    public boolean isInsidePlayerSafeZone(Rectangle safeZone)
    {
        if(position.x > safeZone.x && position.x + getWidth() < safeZone.x + safeZone.width &&
                position.y > safeZone.y && position.y + height < safeZone.y + safeZone.height)
            return true;
        else
            return false;
    }


    private void isOutOfBounds()
    {
        if(position.x < 0)
        {
            velocity.x = -velocity.x;
            position.x = 0;
        }
        if(position.x > viewport.getWorldWidth() - getWidth())
        {
            velocity.x = -velocity.x;
            position.x = viewport.getWorldWidth() - getWidth();
        }
        isVisible();
    }
    private void isVisible()
    {
        if(position.y < level.getChaseCam().getPositionCenter().y - viewport.getWorldHeight())
        {
            deActivate();
        }
    }

    public boolean isInTheAir() {return !landedThisFrame;}

    public void setOwnedToPlayer()
    {
        owner = Enums.Owner.PLAYER;
        pickUpBulletsInPlayer++;
    }
    public Enums.Owner getOwner() {return owner;}

    public static int getPickUpBulletsInPlayerAmount() {return pickUpBulletsInPlayer;}
    public void decrementPickUpBulletsInPlayerAmount() {pickUpBulletsInPlayer--;}

    public Vector2 getPosition() {return position;}
    public Vector2 getPositionCenter() {return positionCenter;}
    public Enums.EntityType getEntityType() {return entityType;}

    public float getWidth() {return Constants.PICKUPBULLET_WIDTH;}
    public float getHeight() {return height;}

    public void deActivate()
    {
        active = false;
        Pools.free(this);
    }
    public boolean isActive() {return active;}

    @Override
    public void reset()
    {
        this.position.setZero();
        this.lastFramePosition.setZero();
        this.velocity.setZero();

        owner = Enums.Owner.ENEMY;
        pickUpBulletsInPlayer = 0;
    }

}
