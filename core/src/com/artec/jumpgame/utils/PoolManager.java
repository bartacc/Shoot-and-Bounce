package com.artec.jumpgame.utils;

import com.artec.jumpgame.level.Level;
import com.artec.jumpgame.objects.PickupBullet;
import com.artec.jumpgame.objects.Platform;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 12.07.16.
 */
public class PoolManager
{
    private static ParticleEffectPool greenTrailEffectPool;
    private static ParticleEffectPool purpleTrailEffectPool;
    private static ParticleEffectPool redTrailEffectPool;

    private Pool<PickupBullet> pickupBulletPool;
    private Pool<Platform> platformPool;

    private Pool<Vector2> vector2Pool;
    private Pool<Rectangle> rectanglePool;
    private Pool<Circle> circlePool;
    private int objectNR = 0;

    public PoolManager(final Level level)
    {
        greenTrailEffectPool = new ParticleEffectPool(Assets.instance.particles.greenTrail, 0, 20);
        purpleTrailEffectPool = new ParticleEffectPool(Assets.instance.particles.purpleTrail, 0, 20);
        redTrailEffectPool = new ParticleEffectPool(Assets.instance.particles.redTrail, 0, 20);

        pickupBulletPool = new Pool<PickupBullet>()
        {
            @Override
            protected PickupBullet newObject() {return new PickupBullet(level);}
        };

        platformPool = new Pool<Platform>()
        {
            @Override
            protected Platform newObject() {return new Platform();}
        };

        vector2Pool = new Pool<Vector2>()
        {
            @Override
            protected Vector2 newObject() {return new Vector2();}
        };

        rectanglePool = new Pool<Rectangle>()
        {
            @Override
            protected Rectangle newObject() {return new Rectangle();}
        };

        circlePool = new Pool<Circle>()
        {
            @Override
            protected Circle newObject()
            {
                return new Circle();
            }
        };

        Pools.set(PickupBullet.class, pickupBulletPool);
        Pools.set(Platform.class, platformPool);
        Pools.set(Vector2.class, vector2Pool);
        Pools.set(Rectangle.class, rectanglePool);
        Pools.set(Circle.class, circlePool);
    }

    public void reload()
    {
        greenTrailEffectPool = new ParticleEffectPool(Assets.instance.particles.greenTrail, 0, 20);
        purpleTrailEffectPool = new ParticleEffectPool(Assets.instance.particles.purpleTrail, 0, 20);
        redTrailEffectPool = new ParticleEffectPool(Assets.instance.particles.redTrail, 0, 20);
        greenTrailEffectPool.clear();
        purpleTrailEffectPool.clear();
        redTrailEffectPool.clear();
    }

    public static ParticleEffectPool getGreenTrailEffectPool() {return greenTrailEffectPool;}
    public static ParticleEffectPool getPurpleTrailEffectPool() {return purpleTrailEffectPool;}
    public static ParticleEffectPool getRedTrailEffectPool() {return redTrailEffectPool;}
}
