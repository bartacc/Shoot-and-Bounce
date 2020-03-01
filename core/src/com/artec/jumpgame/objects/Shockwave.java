package com.artec.jumpgame.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by bartek on 09.08.16.
 */
public class Shockwave
{
    private Vector2 position;
    private float elapsedTime;
    private Vector3 shockWaveParams;

    public Shockwave()
    {
        position = new Vector2(0,0);
        elapsedTime = 100;
        shockWaveParams = new Vector3(10f, 0.8f, 0.1f);
    }

    public void start(Vector2 screenPosition)
    {
        position.set(screenPosition);
        elapsedTime = 0;
    }

    public void update(float delta)
    {
        elapsedTime += delta;
    }

    public Vector2 getPosition() {return position;}
    public float getElapsedTime() {return elapsedTime;}
    public Vector3 getShockWaveParams() {return shockWaveParams;}
}
