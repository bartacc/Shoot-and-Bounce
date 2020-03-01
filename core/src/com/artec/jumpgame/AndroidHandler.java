package com.artec.jumpgame;

/**
 * Created by bartek on 08.08.16.
 */
public interface AndroidHandler
{
    final static int NORMAL_GAME_CHANGER = 0;
    final static int BOUNCY_BULLET_GAME_CHANGER = 1;
    final static int BIGGER_BLOB_GAME_CHANGER = 2;
    final static int LOW_GRAVITY_GAME_CHANGER = 3;
    final static int SMALLER_BLOB_GAME_CHANGER = 4;
    final static int REVERSED_GRAVITY_GAME_CHANGER = 5;

    void share(int score);
    String getCachePath();

    void signIn();
    void signOut();
    boolean isSignedIn();

    void showLeaderboards();
    void submitScore(int score, int gameChanger);

    void showAchievements();
    void unlockAchievement(Achievements achievement);

    void showAds(boolean show);
    void boughtGameChanger();
}
