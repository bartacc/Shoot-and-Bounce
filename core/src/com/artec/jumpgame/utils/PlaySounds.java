package com.artec.jumpgame.utils;


import com.badlogic.gdx.math.MathUtils;

/**
 * Created by bartek on 26.06.16.
 */
public class PlaySounds
{
    private static int propellerSoundsPlaying;
    private static boolean propellerSoundPlayed;
    private static long propellerSoundID;

    private static BackgroundMusic backgroundMusic = BackgroundMusic.MENU;
    private static boolean backgroundMusicPlaying = true;
    private static boolean soundsPlaying = true;

    public static void init()
    {
        if (propellerSoundPlayed)
        {
            Assets.instance.sounds.propeller.stop(propellerSoundID);
            propellerSoundsPlaying = 0;
            propellerSoundPlayed = false;
        }
    }

    public static void landing(float volume)
    {
        if(!soundsPlaying) return;
        Assets.instance.sounds.landing.play(volume);
    }

    public static void landingSticky(float volume)
    {
        if(!soundsPlaying) return;
        Assets.instance.sounds.landingSticky.play(volume);
    }

    public static void click(float volume)
    {
        if(!soundsPlaying) return;
        Assets.instance.sounds.click.play(volume);
    }

    public static void pickUpBullet(float volume)
    {
        if(!soundsPlaying) return;
        Assets.instance.sounds.pickUpBullet.play(volume, MathUtils.random(0.8f, 1.1f), 0);
    }

    public static void bulletShot(float volume)
    {
        if(!soundsPlaying) return;
        Assets.instance.sounds.bulletShot.play(volume, MathUtils.random(0.8f, 1.5f), 0);
    }

    public static void pickUpCoin(float volume)
    {
        if(!soundsPlaying) return;
        Assets.instance.sounds.pickUpCoin.play(volume);
    }

    public static void entityExplosion(float volume)
    {
        if(!soundsPlaying) return;
        if (MathUtils.random() > 0.5f)
            Assets.instance.sounds.entityExplosion.play(volume, MathUtils.random(0.8f, 1.5f), 0);
        else
            Assets.instance.sounds.entityExplosion2.play(volume, MathUtils.random(0.8f, 1.5f), 0);
    }

    public static void addPropellerSoundPlayer()
    {
        propellerSoundsPlaying++;
    }

    public static void removePropellerSoundPlayer()
    {
        propellerSoundsPlaying--;
    }

    public static void propellerSoundLoopUpdate(float volume)
    {
        if (!propellerSoundPlayed && propellerSoundsPlaying > 0 && soundsPlaying)
        {
            propellerSoundID = Assets.instance.sounds.propeller.loop(volume);
            propellerSoundPlayed = true;
        }

        if (propellerSoundsPlaying <= 0 && propellerSoundPlayed)
        {
            Assets.instance.sounds.propeller.stop(propellerSoundID);
            propellerSoundPlayed = false;
        }

    }

    public static void boughtItem(float volume)
    {
        if(!soundsPlaying) return;
        Assets.instance.sounds.buyItem.play(volume);
    }
    public static void playGameplayBackgroundMusic()
    {
        if (backgroundMusic != BackgroundMusic.GAMEPLAY) backgroundMusic = BackgroundMusic.GAMEPLAY;
        reloadBackgroundMusic();
    }

    public static void playMenuBackgroundMusic()
    {
        if(backgroundMusic != BackgroundMusic.MENU) backgroundMusic = BackgroundMusic.MENU;
        reloadBackgroundMusic();
    }

    public static void reloadBackgroundMusic()
    {
        if(!backgroundMusicPlaying)
        {
            if(Assets.instance.sounds.menuBackgroundMusic.isPlaying()) Assets.instance.sounds.menuBackgroundMusic.pause();
            if(Assets.instance.sounds.gameplayBackgroundMusic.isPlaying()) Assets.instance.sounds.gameplayBackgroundMusic.pause();
            return;
        }
        if(backgroundMusic == BackgroundMusic.MENU && !Assets.instance.sounds.menuBackgroundMusic.isPlaying())
        {
            Assets.instance.sounds.menuBackgroundMusic.setLooping(true);
            Assets.instance.sounds.menuBackgroundMusic.setVolume(1f);
            if(Assets.instance.sounds.gameplayBackgroundMusic.isPlaying()) Assets.instance.sounds.gameplayBackgroundMusic.stop();
            Assets.instance.sounds.menuBackgroundMusic.play();
        }
        if(backgroundMusic == BackgroundMusic.GAMEPLAY && !Assets.instance.sounds.gameplayBackgroundMusic.isPlaying())
        {
            Assets.instance.sounds.gameplayBackgroundMusic.setLooping(true);
            Assets.instance.sounds.gameplayBackgroundMusic.setVolume(0.45f);
            if(Assets.instance.sounds.menuBackgroundMusic.isPlaying())Assets.instance.sounds.menuBackgroundMusic.pause();
            Assets.instance.sounds.gameplayBackgroundMusic.play();
        }
    }

    public static void backgroundMusicPlayback(boolean isPlaying) {backgroundMusicPlaying = isPlaying;}
    public static boolean isBackgroundMusicPlaying() {return backgroundMusicPlaying;}

    public static void soundPlayback(boolean isPlaying) {soundsPlaying = isPlaying;}
    public static boolean isSoundPlaying() {return soundsPlaying;}

    public enum BackgroundMusic {MENU, GAMEPLAY;}
}
