package com.artec.jumpgame.utils;

import com.artec.jumpgame.overlays.ChromaticAberration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by bartek on 04.06.16.
 */
public class Utils
{
    public static void drawRotatedTextureRegion(SpriteBatch batch, TextureRegion region, Vector2 position, float width, float height,
                                                boolean customAlpha, float originX, float originY, float rotation)
    {
        drawTextureRegion(batch, region, position.x, position.y, width, height, customAlpha, originX, originY, rotation, false, false);
    }

    public static void drawTextureRegion(SpriteBatch batch, TextureRegion region, Vector2 position, float width, float height, boolean customAlpha,
                                         boolean flipX, boolean flipY)
    {
        drawTextureRegion(batch, region, position.x, position.y, width, height, customAlpha, 0, 0, 0, flipX, flipY);
    }
    public static void drawTextureRegion(SpriteBatch batch, TextureRegion region, Vector2 position, float width, float height, boolean customAlpha)
    {
        drawTextureRegion(batch, region, position.x, position.y, width, height, customAlpha, 0, 0, 0, false, false);
    }

    public static void drawTextureRegion(SpriteBatch batch, TextureRegion region, float x, float y, float width, float height, boolean customAlpha,
                                         float originX, float originY, float rotation)
    {
        drawTextureRegion(batch, region, x, y, width, height, customAlpha, originX, originY, rotation, false, false);
    }

    public static void drawTextureRegion(SpriteBatch batch, TextureRegion region, float x, float y, float width, float height, boolean customAlpha,
                                         float originX, float originY, float rotation, boolean flipX, boolean flipY)
    {
        if(!customAlpha && !ChromaticAberration.isActive())
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1);

        batch.draw(
                region.getTexture(),
                x,
                y,
                originX,
                originY,
                width,
                height,
                1,
                1,
                rotation,
                region.getRegionX(),
                region.getRegionY(),
                region.getRegionWidth(),
                region.getRegionHeight(),
                flipX,
                flipY);
    }

    public static float secondsSince(long timeNanos)
    {
        return MathUtils.nanoToSec * (TimeUtils.nanoTime() - timeNanos);
    }

    public static boolean checkCircleRectangleCollision(Circle circle, Rectangle rectangle)
    {
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

        return containsACorner || touchingAnEdge;

    }
}
