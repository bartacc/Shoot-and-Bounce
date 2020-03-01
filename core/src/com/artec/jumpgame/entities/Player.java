package com.artec.jumpgame.entities;

import com.artec.jumpgame.Achievements;
import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.objects.Bullet;
import com.artec.jumpgame.objects.Coin;
import com.artec.jumpgame.objects.PickupBullet;
import com.artec.jumpgame.objects.Platform;
import com.artec.jumpgame.objects.Sphere;
import com.artec.jumpgame.objects.Splash;
import com.artec.jumpgame.objects.TouchGesture;
import com.artec.jumpgame.utils.*;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by bartek on 01.06.16.
 */
public class Player extends InputAdapter
{
    private Level level;
    private Enums.EntityType entityType;

    public Vector2 position; //Left corner
    private Vector2 positionCenter;
    private Rectangle safeZone;
    private float height, width, baseWidth;
    private Vector2 lastFramePosition; //Left corner
    private Vector2 touchPosition; //Coordinates of users touch

    private TouchGesture touchGesture;
    private Sphere sphere;

    private Vector2 velocity; //Current velocity affected by gravity
    private Vector2 targetVelocity; //If jumping velocity is set to targetVelocity(not affected by gravity)

    private float currentPlatformFrictionSlowdown; //Slowdown on X axis when landed on platform
    private Vector2 currentPlatformVelocity; //Velocity of current platform

    private long lastBulletShotTime; //Time in milis, when last bullet was shot

    private Eye eye;
    private CurrentAnimation currentAnimation;
    private long animationStartTime; //Start time of currentAnimation above

    private Enums.JumpState jumpState;
    private long jumpStartTime;
    private boolean landedThisFrame; //If player landed this frame

    private DelayedRemovalArray<Bullet> bulletArray; //Array of bullets inside player

    private State state;

    public Player(Vector2 position, Level level)
    {
        this.entityType = Enums.EntityType.PLAYER_GREEN;
        eye = new Eye(entityType);

        lastFramePosition = new Vector2();
        this.level = level;
        this.position = new Vector2();
        this.positionCenter = new Vector2();
        currentPlatformVelocity = new Vector2();
        safeZone = new Rectangle();
        velocity = new Vector2();
        touchPosition = new Vector2();
        touchGesture = new TouchGesture();
        targetVelocity = new Vector2();
        bulletArray = new DelayedRemovalArray<Bullet>();
        init(position);
    }

    public void init(Vector2 position)
    {
        eye.init();
        state = State.ALIVE;

        this.position.set(position);
        velocity.setZero();
        currentPlatformVelocity.setZero();
        touchPosition.setZero();

        if(level.getLevelState() == Level.LevelState.ANIMATION_1 || level.getLevelState() == Level.LevelState.ANIMATION_2 ||
                level.getLevelState() == Level.LevelState.ANIMATION_3 || level.getLevelState() == Level.LevelState.ANIMATION_4)
            touchGesture.setActive(true);
        else
            touchGesture.setActive(false);

        sphere = new Sphere();
        targetVelocity.setZero();

        jumpState = Enums.JumpState.FALLING;
        currentAnimation = CurrentAnimation.JUMPING;
        landedThisFrame = false;

        width = Constants.PLAYER_WIDTH;
        height = width * ((float)getCurrentFrame().getRegionHeight()/ (float)getCurrentFrame().getRegionWidth());
        baseWidth = Assets.instance.playerAssets.deathAnimation.getKeyFrame(0).getRegionWidth();

        initBullets();
    }

    public void update(float delta)
    {
        //Height is based on Constants.WIDTH and aspect ratio of texture
        TextureRegion region = getCurrentFrame();
        width = ((float) region.getRegionWidth() / baseWidth) * Constants.PLAYER_WIDTH;
        height = width * ((float) region.getRegionHeight() / (float) region.getRegionWidth());

        if (!landedThisFrame && jumpState == Enums.JumpState.GROUNDED) //If jumpState says GROUNDED, but player didn't land this frame
        {   //Starts animation of jump and sets jumpState to FALLING
            jumpState = Enums.JumpState.FALLING;
            currentAnimation = CurrentAnimation.JUMPING;
            animationStartTime = TimeUtils.nanoTime();
            currentPlatformVelocity.setZero();
        }
        landedThisFrame = false; //Flag resets before every frame

        lastFramePosition.set(position);

        isOutOfBullets();
        //Aplies gravitational force to player (falling down)
        if(Constants.reversedGravity && velocity.y < Constants.REVERSED_GRAVITY_MAX_SPEED_Y && level.getGameScreen().getGameState() == Enums.GameState.PLAYING)
            velocity.y += Constants.GRAVITY_ACCELERATION * 0.75f * delta;
        else if(!Constants.reversedGravity || level.getGameScreen().getGameState() != Enums.GameState.PLAYING)
            velocity.y -= Constants.GRAVITY_ACCELERATION * delta;

        if(jumpState == Enums.JumpState.GROUNDED) //If player is on the ground makes him slide a little bit before completely slowing down
        {
            if(velocity.x > currentPlatformFrictionSlowdown * delta) velocity.x -= currentPlatformFrictionSlowdown * delta;
            else if(velocity.x < -currentPlatformFrictionSlowdown * delta) velocity.x += currentPlatformFrictionSlowdown * delta;
            else velocity.x = 0;
        }
        //Player moves by factor of velocity
        position.mulAdd(velocity, delta);
        position.x += currentPlatformVelocity.x * delta;
        if(state != State.DEAD || state != State.DEAD_UNACTIVE) positionCenter.set(position.x + width/2, position.y + height/2);

        isOutOfBounds();

        //Continues jump
        if(jumpState == Enums.JumpState.JUMPING)
            continueJump();

        for(Bullet bullet : bulletArray)
            bullet.update(delta);

        Vector2 eyeCenterTmp = Pools.obtain(Vector2.class).set(positionCenter.x - velocity.x * 0.01f,
                position.y + height - Constants.PLAYER_EYE_OFFSET_FROM_TOP - velocity.y * 0.01f);
        if(level.getClosestEnemy(position.y + height/2) == null)
            eye.update(eyeCenterTmp, new Vector2(level.getViewport().getWorldWidth()/2, level.getChaseCam().getPositionCenter().y + level.getViewport().getWorldHeight()));
        else
            eye.update(eyeCenterTmp, level.getClosestEnemy(position.y + height/2).getCenterPosition());
        Pools.free(eyeCenterTmp);

        level.updateScore((int)(position.y * Constants.SCORE_MULTIPLIER));

        if(Constants.sphereAroundPlayer) sphere.update(delta, positionCenter);
    }
    ///////////////////////////////////JUMPING/////////////////////////////////////////////////
    private void startJump()
    {
        if(Constants.reversedGravity && targetVelocity.y < velocity.y && targetVelocity.y > 0)
        {
            targetVelocity.set(targetVelocity.x, velocity.y);
        }
        velocity.set(targetVelocity);

        if(jumpState == Enums.JumpState.GROUNDED && targetVelocity.y < 0) return;

            jumpState = Enums.JumpState.JUMPING;
            currentPlatformVelocity.setZero();
            jumpStartTime = TimeUtils.nanoTime();
            continueJump();
    }

    private void continueJump()
    {
        if (jumpState == Enums.JumpState.JUMPING)
        {
            float jumpDuration = MathUtils.nanoToSec * (TimeUtils.nanoTime() - jumpStartTime);
            if (jumpDuration < Constants.PLAYER_MAX_JUMP_TIME)
            {
                velocity.set(targetVelocity);
            }
            else
                endJump();
        }
    }

    private void endJump()
    {
        if (jumpState == Enums.JumpState.JUMPING)
            jumpState = Enums.JumpState.FALLING;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    private boolean isOutOfBounds()
    {
        if(position.x < 0)
        {
            velocity.x = -velocity.x;
            targetVelocity.x = -targetVelocity.x;
            position.x = 0;
            return true;
        }
        if(position.x > level.getViewport().getWorldWidth() - Constants.PLAYER_WIDTH)
        {
            velocity.x = -velocity.x;
            targetVelocity.x = -targetVelocity.x;
            position.x = level.getViewport().getWorldWidth() - Constants.PLAYER_WIDTH;
            return true;
        }
        if(position.y + height > level.getViewport().getCamera().position.y + level.getViewport().getWorldHeight()/2 &&
                (level.getLevelState() == Level.LevelState.TUTORIAL_1 || level.getLevelState() == Level.LevelState.TUTORIAL_2 ||
                        level.getLevelState() == Level.LevelState.TUTORIAL_3 || level.getLevelState() == Level.LevelState.ANIMATION_1
                        || level.getLevelState() == Level.LevelState.ANIMATION_2 || level.getLevelState() == Level.LevelState.ANIMATION_3))
        {
            velocity.y = -velocity.y;
            targetVelocity.y = -targetVelocity.y;
            position.y = level.getViewport().getCamera().position.y + level.getViewport().getWorldHeight()/2 - height;
            return true;
        }
        return false;
    }

    //Check is player on the platform
    public boolean checkPlatformCollision(Platform platform)
    {
        if(!platform.isCollisionActive()) return false;
        boolean colliding = false;
        if(lastFramePosition.y >= platform.top && position.y < platform.top)
        {
            if(position.x + Constants.PLAYER_WIDTH * 0.3f > platform.left && position.x + Constants.PLAYER_WIDTH * 0.7f < platform.left + platform.width)
            {
                colliding = true;
                position.y = platform.top;
                velocity.y = 0;
                currentPlatformVelocity.set(platform.getVelocity());
                if(!landedThisFrame && jumpState != Enums.JumpState.GROUNDED)
                {
                    if(level.getLevelState() == Level.LevelState.TUTORIAL_2 && platform.getPlatformNr() == 3)
                        level.getLevelState().addCompletedObjective();

                    if(!platform.isSticky()) PlaySounds.landing(1f);
                    else PlaySounds.landingSticky(0.5f);

                    currentAnimation = CurrentAnimation.LANDING;
                    animationStartTime = TimeUtils.nanoTime();

                    currentPlatformFrictionSlowdown = platform.getFrictionVelocitySlowdown();
                    platform.playerLanded();
                    if(bulletArray.size == 0) lastBulletShotTime = TimeUtils.nanoTime();

                    Achievements.platformsTouched++;
                }
                landedThisFrame = true;
                jumpState = Enums.JumpState.GROUNDED;
            }
        }
        return colliding;
    }

    private void isOutOfBullets()
    {
        if(bulletArray.size == 0) //
        {
            boolean pickUpBulletsMoving = false;
            for(PickupBullet pickupBullet : level.getPickUpBulletArray())
                if(pickupBullet.isInTheAir()) pickUpBulletsMoving = true;

            if(!this.isMoving() && !pickUpBulletsMoving && state != State.OUT_OF_BULLETS
                    && (TimeUtils.nanoTime() - lastBulletShotTime)*MathUtils.nanoToSec > Constants.PLAYER_MAX_TIME_WITHOUT_BULLETS_UNTIL_DEATH)
            {
                state = State.OUT_OF_BULLETS;
                level.gameOver(false, Enums.DeathReason.OUT_OF_BULLETS); //Player ran out of bullets
            }
        }
    }

    public boolean checkEnemyCollision(Enemy enemy)
    {
        Rectangle playerRectangle = Pools.obtain(Rectangle.class).set(position.x, position.y, Constants.PLAYER_WIDTH, height);
        Rectangle enemyRectangle = Pools.obtain(Rectangle.class).set(enemy.position.x, enemy.position.y, enemy.width, enemy.height);

        boolean overlaps = playerRectangle.overlaps(enemyRectangle);

        Pools.free(playerRectangle); Pools.free(enemyRectangle);
        return overlaps;
    }

    public void checkPickupBulletCollision(PickupBullet pickupBullet)
    {
        if(bulletArray.size + PickupBullet.getPickUpBulletsInPlayerAmount() >= Constants.PLAYER_MAX_BULLETS_INSIDE
                || state != State.ALIVE) return;

        if(!Constants.sphereAroundPlayer)
        {
            Rectangle playerRectangle = Pools.obtain(Rectangle.class).set(position.x, position.y, Constants.PLAYER_WIDTH, height);
            Rectangle bulletRectangle = Pools.obtain(Rectangle.class).set(
                    pickupBullet.getPosition().x, pickupBullet.getPosition().y, pickupBullet.getWidth(), pickupBullet.getHeight());

            if (playerRectangle.overlaps(bulletRectangle))
                pickupBullet.setOwnedToPlayer();

            Pools.free(playerRectangle);
            Pools.free(bulletRectangle);
        }
        else
        {
            if(positionCenter.dst(pickupBullet.getPositionCenter()) < sphere.getMaxRadius() + pickupBullet.getWidth()/2)
                pickupBullet.setOwnedToPlayer();
        }
    }

    public boolean checkBulletCollision(Bullet bullet)
    {
        Circle circle = Pools.obtain(Circle.class);
        circle.set(bullet.position.x, bullet.position.y, bullet.bulletAnimation.getCurrentSize());
        Rectangle rectangle = Pools.obtain(Rectangle.class).set(position.x, position.y, Constants.PLAYER_WIDTH, height);

        boolean contains = Utils.checkCircleRectangleCollision(circle, rectangle);
        Pools.free(rectangle); Pools.free(circle);
        return contains;
    }

    public boolean checkCoinCollision(Coin coin)
    {
        return positionCenter.dst(coin.getPosition()) < width/2 + coin.getSize()/2;
    }


    private void initBullets()
    {
        int bulletsAtStart = Constants.PLAYER_BULLETS_AT_START;
        if(level.getLevelState() == Level.LevelState.ANIMATION_4) bulletsAtStart = 2;

        for(int i = 0; i < bulletsAtStart; i++)
            addRandomBullet(Assets.instance.bulletAssets.greenBulletAnimation);
    }

    //Add bullet with random position
    public void addRandomBullet(Animation animation)
    {
        getSafeZone();

        //System.out.print("X:"+safeZone.x + "  Y:"+safeZone.y+ "  W:"+safeZone.width+ "  H:"+safeZone.height);
        Vector2 positionTmp = Pools.obtain(Vector2.class).set(//safeZone.x + safeZone.width*0.5f, safeZone.y + safeZone.height* 0.5f);
                MathUtils.random(safeZone.x + Constants.BULLET_SMALL_RADIUS, (safeZone.x + safeZone.width) - Constants.BULLET_SMALL_RADIUS),
                MathUtils.random(safeZone.y + Constants.BULLET_SMALL_RADIUS, (safeZone.y + safeZone.height) - Constants.BULLET_SMALL_RADIUS));

       addBullet(positionTmp, entityType, true);
       Pools.free(positionTmp);
    }

    public void addBullet(PickupBullet pickupBullet)
    {
        if(bulletArray.size >= Constants.PLAYER_MAX_BULLETS_INSIDE) return;

        PlaySounds.pickUpBullet(0.5f);
        Vector2 positionTmp = Pools.obtain(Vector2.class).set(
                pickupBullet.getPosition().x + pickupBullet.getWidth()/2,
                pickupBullet.getPosition().y + pickupBullet.getHeight()/2);
        addBullet(positionTmp, pickupBullet.getEntityType(), false);
        Pools.free(positionTmp);

        pickupBullet.decrementPickUpBulletsInPlayerAmount();
    }

    private void addBullet(Vector2 position, Enums.EntityType bulletEntityType, boolean fade)
    {
        Bullet.BulletAnimation bulletAnimation = new Bullet.BulletAnimation(bulletEntityType, Constants.BULLET_SMALL_RADIUS,
                Constants.BULLET_MEDIUM_RADIUS, Constants.BULLET_BIG_RADIUS, entityType.particleEffectPool);

        Bullet bullet = new Bullet(position, level, bulletAnimation, this, fade);
        bulletArray.add(bullet);
    }

    public void reload()
    {
        DelayedRemovalArray<Bullet> tempBulletArray = new DelayedRemovalArray<Bullet>();
        int size = bulletArray.size;
        bulletArray.begin();
        for(int i = 0; i < size; i++)
        {
            Bullet.BulletAnimation bulletAnimation = new Bullet.BulletAnimation(bulletArray.get(i).bulletAnimation.getEntityType(),
                    Constants.BULLET_SMALL_RADIUS, Constants.BULLET_MEDIUM_RADIUS, Constants.BULLET_BIG_RADIUS, entityType.particleEffectPool);
            Bullet newBullet = new Bullet(bulletArray.get(i).position, level, bulletAnimation, this, false);
            bulletArray.removeIndex(i);
            tempBulletArray.add(newBullet);
        }
        bulletArray.end();
        bulletArray = tempBulletArray;
    }

    public void debugRender(ShapeRenderer renderer)
    {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.RED);

        renderer.rect(position.x, position.y, Constants.PLAYER_WIDTH, height);
        renderer.setColor(Color.GREEN);
        renderer.rect(position.x, position.y, Constants.PLAYER_WIDTH, height * (bulletArray.size / Constants.PLAYER_MAX_BULLETS_INSIDE));

        renderer.end();

        for(Bullet bullet : bulletArray)
            bullet.debugRender(renderer);
    }

    public void render(SpriteBatch batch, float xOffset)
    {
        if(state == State.ALIVE && Constants.sphereAroundPlayer)
        {
            sphere.render(batch);
        }

        Vector2 tempPosition = Pools.obtain(Vector2.class).set(positionCenter.x - width/2 + xOffset, positionCenter.y - height/2);
        Utils.drawTextureRegion(batch, getCurrentFrame(), tempPosition, width, height, false);
        Pools.free(tempPosition);


        if(state == State.ALIVE || state == State.OUT_OF_BULLETS)
        {
            for (Bullet bullet : bulletArray)
                bullet.render(batch, xOffset);

            eye.render(batch, xOffset);
        }

        touchGesture.render(batch);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        if(level.getGameHUD().isButtonTouched(screenX, screenY)) return false;

        Vector2 screenCoordinatesTmp = Pools.obtain(Vector2.class).set(screenX, screenY);
        touchPosition = level.getViewport().unproject(screenCoordinatesTmp);
        shootBullet(touchPosition);
        Pools.free(screenCoordinatesTmp);
        return true;
    }

    public void shootBullet(Vector2 touchPosition)
    {
        if(bulletArray.size > 0)
        {
            touchGesture.start(touchPosition);
            //Calculates velocity of the player after touch
            float angle = (float) Math.atan2(touchPosition.y - (position.y + height/2),
                    touchPosition.x - (position.x + Constants.PLAYER_WIDTH/2));

            if (angle < 0) angle += MathUtils.PI2;

            float distanceX = (float) -(Math.cos(angle) * Constants.PLAYER_JUMP_SPEED);
            float distanceY = (float) -(Math.sin(angle) * Constants.PLAYER_JUMP_SPEED);

            float shotDistanceX = (float) Math.cos(angle) * Constants.PLAYER_SHOT_SPEED;
            float shotDistanceY = (float) Math.sin(angle) * Constants.PLAYER_SHOT_SPEED;

            //Start animation
            if(jumpState == Enums.JumpState.GROUNDED && distanceY > 0)
            {
                currentAnimation = CurrentAnimation.JUMPING;
                animationStartTime = TimeUtils.nanoTime();
            }
            if(distanceY < 0) Achievements.shotUp = true;

            int bulletShotIndex = MathUtils.random(bulletArray.size - 1);

            Vector2 bulletVelocityTmp = Pools.obtain(Vector2.class).set(shotDistanceX, shotDistanceY);
            level.shootBullet(bulletArray.get(bulletShotIndex), bulletVelocityTmp);
            Pools.free(bulletVelocityTmp);
            bulletArray.removeIndex(bulletShotIndex);

            if(bulletArray.size == 0 && jumpState == Enums.JumpState.GROUNDED) lastBulletShotTime = TimeUtils.nanoTime();

            PlaySounds.bulletShot(1f);

            targetVelocity.set(distanceX, distanceY);
            startJump();
            if(level.getLevelState() == Level.LevelState.TUTORIAL_1) Level.LevelState.TUTORIAL_1.addCompletedObjective();
        }
    }

    public float getHeight() {return height;}

    public Enums.JumpState getJumpState() {return jumpState;}

    public void setJumpState(Enums.JumpState state) {this.jumpState = state;}

    public void die()
    {
        state = State.DEAD;
        PlaySounds.entityExplosion(0.6f);
        animationStartTime = TimeUtils.nanoTime();
        currentAnimation = CurrentAnimation.DEATH;
    }
    public Splash getSplash() {return new Splash(position, entityType);}

    public boolean isMoving()
    {
        return velocity.x != 0 || velocity.y != 0;
    }
    public boolean isDead()
    {
        if(state == State.DEAD || state == State.DEAD_UNACTIVE) return true;
        else return false;
    }
    public boolean isActive()
    {
        if(state != State.UNACTIVE && state != State.DEAD_UNACTIVE) return true;
        else return false;
    }

    public TextureRegion getCurrentFrame()
    {
        Animation animation;
        switch(currentAnimation)
        {
            case JUMPING:
                animation = Assets.instance.playerAssets.jumpAnimation;
                break;
            case LANDING:
                animation = Assets.instance.playerAssets.landAnimation;
                break;
            default:
                animation = Assets.instance.playerAssets.deathAnimation;
        }
        float elapsedTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - animationStartTime);
        if(state == State.DEAD && animation.isAnimationFinished(elapsedTime)) state = State.DEAD_UNACTIVE;


        return animation.getKeyFrame(elapsedTime);
    }

    public Vector2 getCenterPosition() {return positionCenter;}

    public Rectangle getSafeZone()
    {
       safeZone.set(
                position.x + Constants.PLAYER_WIDTH * 0.15f,
                position.y + height * 0.1f,
                Constants.PLAYER_WIDTH * 0.70f,
                height * 0.5f);
        return safeZone;
    }

    private enum State{UNACTIVE, DEAD, DEAD_UNACTIVE , ALIVE, OUT_OF_BULLETS}
    private enum CurrentAnimation {JUMPING, LANDING, DEATH}
}
