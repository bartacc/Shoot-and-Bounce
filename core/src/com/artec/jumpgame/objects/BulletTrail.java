package com.artec.jumpgame.objects;

import com.artec.jumpgame.utils.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by bartek on 04.07.16.
 */
public class BulletTrail
{
    private Bullet.BulletAnimation bulletAnimation;
    private ParticleEffect particleEffect;
    private ParticleEmitter particleEmitter;

    private boolean started;

    public BulletTrail(Bullet.BulletAnimation bulletAnimation)
    {
        this.bulletAnimation = bulletAnimation;
        this.particleEffect = bulletAnimation.getParticleEffectPool().obtain();

        started = false;
        particleEffect.loadEmitterImages(Assets.instance.getParticleAtlas());
        particleEmitter = particleEffect.findEmitter("trail-emitter");
    }

    public void update(float delta, Vector2 bulletPositionCenter, Vector2 bulletVelocity)
    {
        particleEffect.setPosition(bulletPositionCenter.x, bulletPositionCenter.y);

        float newAngle = (float) Math.atan2(bulletVelocity.y * delta, bulletVelocity.x * delta);

        newAngle = newAngle * MathUtils.radiansToDegrees;
        if(newAngle < 0) newAngle = 360 - (-newAngle);
        newAngle = newAngle - 180;
        if(newAngle < 0) newAngle = 360 - (-newAngle);


        ParticleEmitter.ScaledNumericValue angle = particleEmitter.getAngle();
        angle.setHigh(newAngle);

        ParticleEmitter.ScaledNumericValue particleVelocity = particleEmitter.getVelocity();
        particleVelocity.setHigh((Math.abs(bulletVelocity.x) + Math.abs(bulletVelocity.y)) * 0.3f);

        if(!started)
        {
            started = true;
        }
    }

    public void render(SpriteBatch batch)
    {
        if(started)
        {
            particleEffect.draw(batch, Gdx.graphics.getDeltaTime());
        }
    }

    public void reload()
    {
        particleEffect.dispose();
        particleEffect.loadEmitterImages(Assets.instance.getParticleAtlas());
    }

    public void die()
    {
        bulletAnimation.getParticleEffectPool().free((ParticleEffectPool.PooledEffect) particleEffect);
    }
    public BoundingBox getBoundingRectangle() {return particleEffect.getBoundingBox();}
}
