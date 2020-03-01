package com.artec.jumpgame.level;

import com.artec.jumpgame.utils.Assets;
import com.artec.jumpgame.utils.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PooledLinkedList;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 23.06.16.
 */
public class Background
{
    private Array<Texture> backgroundTextures;
    private PooledLinkedList<Integer> texturesToDraw;
    private Viewport viewport;

    private float height;
    private float minYBackgroundPosition;
    private int initialTexture;

    public Background(Viewport viewport)
    {
        this.viewport = viewport;
        backgroundTextures = Assets.instance.backgroundAssets.backgroundTextures;
        texturesToDraw = new PooledLinkedList<Integer>(20);
        height = viewport.getWorldWidth() * (backgroundTextures.get(0).getHeight() / backgroundTextures.get(0).getWidth());
        initialTexture = 0;
    }

    public void init()
    {
        minYBackgroundPosition = -height;
        initialTexture = -1;
        texturesToDraw.clear();
        texturesToDraw.add(1);
        texturesToDraw.add(0);
        texturesToDraw.add(1);
        for(int i = 0; i < 4; i++)
            texturesToDraw.add(MathUtils.random(1, Constants.BACKGROUND_TEXTURES_MAX));
    }

    public void update(Vector2 cameraPosition)
    {
        if(cameraPosition.y > minYBackgroundPosition + height * 6)
        {
            minYBackgroundPosition += height;
            if(initialTexture == -1) initialTexture = 0;
            else if(initialTexture == 0) initialTexture = 1;
            else if(initialTexture == 1) initialTexture = 2;
            else if(initialTexture == 2) initialTexture = 1;

            texturesToDraw.iter();
            texturesToDraw.next();
            texturesToDraw.remove();
            texturesToDraw.add(MathUtils.random(1, Constants.BACKGROUND_TEXTURES_MAX));

        }
    }

    public void reload()
    {
        backgroundTextures = Assets.instance.backgroundAssets.backgroundTextures;
    }

    public void render(SpriteBatch batch)
    {
        batch.setColor(1, 1, 1, 1);
        batch.begin();

        /*
        int currentTexture = initialTexture;
            for(int i = 0; i < 3; i++)
            {
                boolean flipY = false;
                if(currentTexture == 2) flipY = true;

                Texture drawnTexture = backgroundTextures.get(currentTexture);
                batch.draw(drawnTexture, 0, (i * height) + minYBackgroundPosition, viewport.getWorldWidth(), height,
                        0, 0, drawnTexture.getWidth(), drawnTexture.getHeight(), false, flipY);

                if(currentTexture >= Constants.BACKGROUND_TEXTURES_MAX)
                    currentTexture = 1;
                else
                    currentTexture++;
            }
          */
        int currentTexture = initialTexture;
        texturesToDraw.iter();
        for(int i = 0; i < texturesToDraw.size(); i++)
        {
            boolean flipY = false;
            if(currentTexture == 2) flipY = true;

            Texture drawnTexture = backgroundTextures.get(texturesToDraw.next());

            if((i * height) + minYBackgroundPosition < viewport.getCamera().position.y + viewport.getWorldHeight()/2 ||
                    (i * height) + minYBackgroundPosition + height > viewport.getCamera().position.y - viewport.getWorldHeight()/2)
            {   //Draw background only if it's visible
                //if(currentTexture == -1)
                //    batch.draw(drawnTexture, 0, (i * height) + minYBackgroundPosition, viewport.getWorldWidth()/2, height/2,
                //            viewport.getWorldWidth(), height, 1, 1, 180, 0, 0, drawnTexture.getWidth(), drawnTexture.getHeight(), false, false);
                //    else
                batch.draw(drawnTexture, 0, (i * height) + minYBackgroundPosition, viewport.getWorldWidth(), height,
                        0, 0, drawnTexture.getWidth(), drawnTexture.getHeight(), false, flipY);
            }

            if(currentTexture >= Constants.BACKGROUND_TEXTURES_MAX)
                currentTexture = 1;
            else
                currentTexture++;
        }
        batch.end();
    }
}
