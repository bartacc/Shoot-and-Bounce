package com.artec.jumpgame;

/**
 * Created by bartek on 26.08.16.
 */
public enum Achievements
{
    GETTING_SOMEWHERE, ABSOLUTE_MASTERY, BASIC_SURVIVAL, BULLSEYE, PACIFIST, I_CAN_FLY, GREEDY, FAST_RIDE;

    //STATS THIS GAME
    public static int enemiesKilled;
    public static int platformsTouched;
    public static int coinsCollected;
    public static boolean shotUp;


    public static void resetStats()
    {
        enemiesKilled = 0;
        platformsTouched = 0;
        coinsCollected = 0;
        shotUp = false;
    }
}
