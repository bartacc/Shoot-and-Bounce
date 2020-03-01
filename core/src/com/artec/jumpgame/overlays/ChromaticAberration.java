package com.artec.jumpgame.overlays;

import com.artec.jumpgame.utils.Constants;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by bartek on 11.08.16.
 */
public class ChromaticAberration
{
    private static boolean active;
    private static float velocityPerSec;

    private static float blueOffset;
    private static float redOffset;

    public final static int srcBlending = GL20.GL_DST_COLOR;
    public final static int dstBlending = GL20.GL_ONE;

    public static void start()
    {
        active = true;
        velocityPerSec = Constants.CHROMATIC_ABERRATION_MAX_OFFSET / Constants.CHROMATIC_ABERRATION_DURATION;
        blueOffset = -Constants.CHROMATIC_ABERRATION_MAX_OFFSET;
        redOffset = Constants.CHROMATIC_ABERRATION_MAX_OFFSET;
    }

    public static void update(float delta)
    {
        if(active)
        {
            blueOffset += velocityPerSec * delta;
            redOffset -= velocityPerSec * delta;
            if(blueOffset > 0 || redOffset < 0)
            {
                active = false;
                blueOffset = 0;
                redOffset = 0;
            }
        }
    }

    public static float getOffset(int nr)
    {
        switch (nr)
        {
            case 0: return 0;
            case 1: return blueOffset;
            case 2: return 0;
            case 3: return redOffset;
            default: return 0;
        }
    }

    public static Color getOffsetColor(int nr, Color colorOut)
    {
        switch (nr)
        {
            case 0: colorOut.set(1, 1, 1, 1);
                break;
            case 1: colorOut.set(0, 0, 1, 1f);
                break;
            case 2: colorOut.set(0, 1, 0, 1f);
                break;
            case 3: colorOut.set(1, 0, 0, 1f);
                break;
            default: colorOut.set(1, 1, 1, 1f);
        }
        return colorOut;
    }
    public static float getBlueOffset() {return blueOffset;}
    public static float getRedOffset() {return redOffset;}
    public static boolean isActive() {return active;}
}
