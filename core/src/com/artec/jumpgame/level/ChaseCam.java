package com.artec.jumpgame.level;

import com.artec.jumpgame.entities.Player;
import com.artec.jumpgame.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 01.06.16.
 */
public class ChaseCam
{
    private Level.LevelState levelState;
    private Player player;
    private Viewport viewport;
    private Vector2 positionCenter;
    private boolean following;

    private float velocityY;
    private Vector2 offset;
    private boolean shaking;
    private float shakeLength;
    private float shakeElapsedTime;

    public ChaseCam()
    {
        positionCenter = new Vector2();
        offset = new Vector2();
    }

    public void init(Player player, Viewport viewport, Level.LevelState levelState)
    {
        this.player = player;
        this.viewport = viewport;
        this.levelState = levelState;

        shaking = false;
        shakeLength = 0;
        shakeElapsedTime = 0;
        velocityY = 0;
        positionCenter.setZero();
        offset.setZero();


        if(levelState == Level.LevelState.NORMAL || levelState == Level.LevelState.TUTORIAL_4 || levelState == Level.LevelState.ANIMATION_4 ||
                levelState == Level.LevelState.TUTORIAL_3 || levelState == Level.LevelState.ANIMATION_3
                || levelState == Level.LevelState.TUTORIAL_2 || levelState == Level.LevelState.ANIMATION_2)
            following = true;
        else
            following = false;

        if(levelState == Level.LevelState.TUTORIAL_1 || levelState == Level.LevelState.ANIMATION_1)
            positionCenter.y = viewport.getWorldHeight() * 0.45f;
    }

    public void update(float delta)
    {
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) following = !following;

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) positionCenter.y += 10;
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) positionCenter.y -= 10;

        positionCenter.x = viewport.getWorldWidth()/2;

        if(shaking)
        {
            shakeElapsedTime += delta;
            offset.y += velocityY * delta;
            if(offset.y > Constants.CAMERA_SHAKE_AMPLITUDE)
            {
                offset.y = Constants.CAMERA_SHAKE_AMPLITUDE;
                velocityY = -velocityY;
            }
            if(offset.y < -Constants.CAMERA_SHAKE_AMPLITUDE)
            {
                offset.y = -Constants.CAMERA_SHAKE_AMPLITUDE;
                velocityY = -velocityY;
            }
            if(shakeElapsedTime > shakeLength){shaking = false;}
        }

        if(following)
        {
            if((levelState == Level.LevelState.TUTORIAL_2 || levelState == Level.LevelState.ANIMATION_2 ||
                    levelState == Level.LevelState.TUTORIAL_3 || levelState == Level.LevelState.ANIMATION_3)
                    && player.position.y + player.getHeight()/2 > viewport.getWorldHeight()*0.5f)
                positionCenter.y = viewport.getWorldHeight() * 0.6f;

            else if(player.position.y + player.getHeight()/2 < viewport.getWorldHeight() * 0.25f)
                positionCenter.y = viewport.getWorldHeight() * 0.35f;
            else
                positionCenter.y = player.position.y + player.getHeight()/2 + viewport.getWorldHeight() * 0.1f;
        }
        viewport.getCamera().position.set(positionCenter.x + offset.x, positionCenter.y + offset.y, 1);
    }

    public void shake(float seconds)
    {
        shaking = true;
        shakeLength = seconds;
        shakeElapsedTime = 0;
        velocityY = Constants.CAMERA_SHAKE_VELOCITY;
    }

    public void setFollowing(boolean newFollowing) {following = newFollowing;}

    public Vector2 getPositionCenter() {return positionCenter;}
}
