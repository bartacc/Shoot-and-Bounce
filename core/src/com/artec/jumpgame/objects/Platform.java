package com.artec.jumpgame.objects;

import com.artec.jumpgame.level.LevelSpawner;
import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by bartek on 01.06.16.
 */
public class Platform
{
    private static int nextPlatformNr = 0;
    private int platformNr;

    public float left, top, width, height;
    protected Vector2 velocity;
    protected float nrOfTiles;

    private float initialFrictionVelocitySlowdown;
    private float frictionVelocitySlowdown;

    protected Enums.PlatformType platformType;
    protected Array<TextureAtlas.AtlasRegion> regions;

    protected LevelSpawner.EnemySpawnedThisFrame enemyOnPlatform;
    protected Array<TextureAtlas.AtlasRegion> trailRegions;
    protected boolean sticky;

    private boolean active;
    protected boolean landed; //Did player land on this platform?
    protected boolean collisionActive; //Should we check player collision with platform?

    public Platform()
    {
        velocity = new Vector2();
        platformNr = nextPlatformNr;
        nextPlatformNr++;
    }

    public void init(float left, float top, int nrOfTiles, Enums.PlatformType platformType, float frictionVelocitySlowdown,
                     LevelSpawner.EnemySpawnedThisFrame enemyOnPlatform)
    {
        this.platformType = platformType;
        regions = platformType.platformTextures;

        this.enemyOnPlatform = enemyOnPlatform;


        active = true;
        landed = false;
        collisionActive = true;

        this.left = left;
        this.top = top;
        this.nrOfTiles = nrOfTiles;
        this.width = nrOfTiles * Constants.PLATFORM_TILE_WIDTH;
        this.height = Constants.PLATFORM_TILE_WIDTH * ((float)regions.get(0).getRegionHeight() / (float)regions.get(0).getRegionWidth());

        velocity.setZero();
        this.initialFrictionVelocitySlowdown = frictionVelocitySlowdown;
        this.frictionVelocitySlowdown = frictionVelocitySlowdown;

        setupTrail();
    }

    public void debugRender(ShapeRenderer renderer)
    {
        renderer.setColor(Color.LIGHT_GRAY);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.rect(left, top - height, width, height);
        renderer.end();
    }

    public void update(float delta)
    {

    }

    protected void setupTrail()
    {
        if(enemyOnPlatform == LevelSpawner.EnemySpawnedThisFrame.SHOOTING || enemyOnPlatform == LevelSpawner.EnemySpawnedThisFrame.NORMAL)
        {
            sticky = true;
            frictionVelocitySlowdown = Constants.PLATFORM_FRICTION_VELOCITY_SLOWDOWN * 3f;
        }
        else
        {
            sticky = false;
            frictionVelocitySlowdown = initialFrictionVelocitySlowdown;
        }

        switch (enemyOnPlatform)
        {
            case NORMAL:
                trailRegions = Assets.instance.platformAssets.trailBlue;
                break;
            case SHOOTING:
                trailRegions = Assets.instance.platformAssets.trailRed;
                break;
            default:
                trailRegions = null;
        }
    }

    public void render(SpriteBatch batch, float xOffset)
    {
        setupTrail();
        regions = platformType.platformTextures;
        Vector2 tilePositionTmp = Pools.obtain(Vector2.class).setZero();
        if(nrOfTiles == 1)
        {
            tilePositionTmp.set(left + xOffset, top - height);
            Utils.drawTextureRegion(batch, regions.get(0), tilePositionTmp, Constants.PLATFORM_TILE_WIDTH, height, false);
            if(sticky) Utils.drawTextureRegion(batch, trailRegions.get(0), tilePositionTmp, Constants.PLATFORM_TILE_WIDTH, height, false);
        }
        else
        {
            for(int i = 0; i < nrOfTiles; i++)
            {
                tilePositionTmp.set(left + i * Constants.PLATFORM_TILE_WIDTH + xOffset, top - height);
                int tileNr;

                if(i == 0)                   tileNr = 1;
                else if (i == nrOfTiles - 1) tileNr = 3;
                else                         tileNr = 2;

                Utils.drawTextureRegion(batch, regions.get(tileNr), tilePositionTmp, Constants.PLATFORM_TILE_WIDTH, height, false);
                if(sticky) Utils.drawTextureRegion(batch, trailRegions.get(tileNr), tilePositionTmp, Constants.PLATFORM_TILE_WIDTH, height, false);
            }
        }
        Pools.free(tilePositionTmp);
    }

    public boolean isSticky(){return sticky;}

    public void playerLanded() {this.landed = true;}
    public boolean isCollisionActive() {return collisionActive;}

    public float getFrictionVelocitySlowdown() {return frictionVelocitySlowdown;}
    public int getPlatformNr() {return platformNr;}
    public Vector2 getVelocity() {return velocity;}

    public static void resetPlatformNr() {nextPlatformNr = 0;}

    public boolean isActive() {return active;}
    public void deActivate()
    {
        active = false;
        //Pools.free(this);
    }
}
