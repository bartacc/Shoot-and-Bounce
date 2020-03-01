package com.artec.jumpgame.entities;

import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.objects.Platform;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by bartek on 02.07.16.
 */
public class WalkingEnemy extends Enemy
{
    private Enums.Direction direction;
    public Platform platform; //Platform on which this enemy stands
    private float movementSpeed;

    public WalkingEnemy(Platform platform, Enums.EntityType entityType, Level level)
    {
        super(entityType, level);

        this.platform = platform;
        this.direction = Enums.Direction.RIGHT;
        this.centerPosition.set(platform.left + platform.width/2, platform.top + height/2);
        currentPlatformVelocity.set(platform.getVelocity());
        if(level.getLevelState() == Level.LevelState.ANIMATION_3 || level.getLevelState() == Level.LevelState.ANIMATION_4)
            movementSpeed = Constants.ENEMY_MOVEMENT_SPEED_MIN;
        else
            movementSpeed = MathUtils.random(Constants.ENEMY_MOVEMENT_SPEED_MIN, Constants.ENEMY_MOVEMENT_SPEED_MAX);
    }

    public void update(float delta)
    {
        super.update(delta);
        centerPosition.y = platform.top + height/2;
        if(!dead)
        {
            currentPlatformVelocity.set(platform.getVelocity());
            isEnemyOnEdge();
            if (direction == Enums.Direction.LEFT) centerPosition.x -= delta * movementSpeed;
            if (direction == Enums.Direction.RIGHT) centerPosition.x += delta * movementSpeed;
            if(direction == Enums.Direction.LEFT && currentPlatformVelocity.x < 0) centerPosition.mulAdd(currentPlatformVelocity, delta);
            if(direction == Enums.Direction.RIGHT && currentPlatformVelocity.x > 0) centerPosition.mulAdd(currentPlatformVelocity, delta);
        }
    }

    private void isEnemyOnEdge()
    {
        if(position.x < platform.left) direction = Enums.Direction.RIGHT;
        if(position.x + Constants.ENEMY_WIDTH > platform.left + platform.width) direction = Enums.Direction.LEFT;
    }
}
