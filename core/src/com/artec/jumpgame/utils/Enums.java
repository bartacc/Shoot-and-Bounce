package com.artec.jumpgame.utils;

import com.artec.jumpgame.AndroidHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


/**
 * Created by bartek on 01.06.16.
 */
public class Enums
{
    public enum JumpState
    {
        JUMPING,
        FALLING,
        GROUNDED
    }

    public enum Direction
    {
        LEFT,
        RIGHT
    }

    public enum InsideEntity
    {
        IN_PLAYER,
        IN_ENEMY,
        OUT
    }

    public enum Owner
    {
        PLAYER,
        ENEMY
    }

    public enum GameState
    {
        PLAYING,
        DEAD,
        PAUSE,
        MENU,
        FINISHED_TUTORIAL,
        GAME_CHANGERS_SHOP,
        CREDITS;
    }

    public enum PlatformType
    {
        START(Assets.instance.platformAssets.startPlatform, Assets.instance.platformAssets.startPlatform),
        PURPLE_METAL(Assets.instance.platformAssets.stonePlatform, Assets.instance.platformAssets.stonePlatformBroken),
        GOLD_METAL(Assets.instance.platformAssets.floatingPlatform, Assets.instance.platformAssets.floatingPlatform),
        SNOWY(Assets.instance.platformAssets.snowPlatform, Assets.instance.platformAssets.snowPlatformBroken);

        public Array<TextureAtlas.AtlasRegion> platformTextures;
        public Array<TextureAtlas.AtlasRegion> brokenPlatformTextures;

        PlatformType(Array<TextureAtlas.AtlasRegion> platformTextures, Array<TextureAtlas.AtlasRegion> brokePlatformTextures)
        {
            this.platformTextures = platformTextures;
            this.brokenPlatformTextures = brokePlatformTextures;
        }

        public static void reload()
        {
            START.platformTextures = Assets.instance.platformAssets.startPlatform;
            START.brokenPlatformTextures = Assets.instance.platformAssets.startPlatform;

            PURPLE_METAL.platformTextures = Assets.instance.platformAssets.stonePlatform;
            PURPLE_METAL.brokenPlatformTextures = Assets.instance.platformAssets.stonePlatformBroken;

            GOLD_METAL.platformTextures = Assets.instance.platformAssets.floatingPlatform;
            GOLD_METAL.brokenPlatformTextures = Assets.instance.platformAssets.floatingPlatform;

            SNOWY.platformTextures = Assets.instance.platformAssets.snowPlatform;
            SNOWY.brokenPlatformTextures = Assets.instance.platformAssets.snowPlatformBroken;
        }
    }

    public enum DeathReason
    {
        OUT_OF_BULLETS, KILLED_BY_ENEMY, FALLEN;
    }

    public enum LevelUpdateState {UPDATING, STOPPED}

    public enum BlinkingState
    {
        VISIBLE(Constants.MENU_SCREEN_TAP_TO_SHOOT_BLINK_VISIBLE_TIME), NOT_VISIBLE(Constants.MENU_SCREEN_TAP_TO_SHOOT_BLINK_NOT_VISIBLE_TIME);

        public static long initialTime;
        public final float stateDuration;

        BlinkingState(float stateDuration)
        {
            this.stateDuration = stateDuration;
        }

        public BlinkingState getNextState()
        {
            if(this.equals(VISIBLE)) return NOT_VISIBLE;
            else return VISIBLE;
        }
    }

    public enum EntityType
    {
        PLAYER_GREEN(Assets.instance.playerAssets.jumpAnimation, Assets.instance.playerAssets.deathAnimation, Assets.instance.splashAssets.greenSplash,
                Assets.instance.bulletAssets.greenBulletAnimation.getKeyFrame(0), Assets.instance.bulletAssets.greenBulletAnimation, 24,
                28, Assets.instance.eyeAssets.eyeBlackSmall, Assets.instance.eyeAssets.eyeBlinkGreen, PoolManager.getGreenTrailEffectPool()),

        ENEMY_RED(Assets.instance.enemyAssets.red, Assets.instance.enemyAssets.redDeath, Assets.instance.splashAssets.redSplash,
                Assets.instance.bulletAssets.redPickupBullet, Assets.instance.bulletAssets.redBulletAnimation, 24,
                25, Assets.instance.eyeAssets.eyeRedSmall, Assets.instance.eyeAssets.eyeBlinkRed, PoolManager.getRedTrailEffectPool()),

        ENEMY_BLUE(Assets.instance.enemyAssets.blue, Assets.instance.enemyAssets.blueDeath,Assets.instance.splashAssets.blueSplash,
                Assets.instance.bulletAssets.bluePickupBullet, Assets.instance.bulletAssets.blueBulletAnimation, 24,
                25, Assets.instance.eyeAssets.eyeBlueSmall, Assets.instance.eyeAssets.eyeBlinkBlue, PoolManager.getPurpleTrailEffectPool()),

        ENEMY_PURPLE(Assets.instance.enemyAssets.purple, Assets.instance.enemyAssets.purpleDeath, Assets.instance.splashAssets.purpleSplash,
                   Assets.instance.bulletAssets.purplePickupBullet, Assets.instance.bulletAssets.purpleBulletAnimation, 24,
                35, Assets.instance.eyeAssets.eyePurpleSmall, Assets.instance.eyeAssets.eyeBlinkPurple, PoolManager.getPurpleTrailEffectPool());

        public Animation entityAnimation;
        public Animation entityDeathAnimation;
        public TextureAtlas.AtlasRegion splashRegion;
        public TextureRegion pickupBulletRegion;
        public Animation bulletAnimation;

        public float EYE_BIG_SIZE;
        public float EYE_SMALL_SIZE;
        public float EYE_SMALL_OFFSET_MAX;

        public final float ENEMY_EYE_OFFSET_FROM_TOP;
        public TextureAtlas.AtlasRegion eyeSmall;
        public TextureAtlas.AtlasRegion eyeBlink;

        public ParticleEffectPool particleEffectPool;


        EntityType(Animation entityAnimation, Animation entityDeathAnimation, TextureAtlas.AtlasRegion splashRegion,
                   TextureRegion pickupBulletRegion, Animation bulletAnimation, float EYE_BIG_SIZE, float ENEMY_EYE_OFFSET_FROM_TOP,
                   TextureAtlas.AtlasRegion eyeSmall, TextureAtlas.AtlasRegion eyeBlink, ParticleEffectPool particleEffectPool)
        {
            this.entityAnimation = entityAnimation;
            this.entityDeathAnimation = entityDeathAnimation;
            this.splashRegion = splashRegion;
            this.pickupBulletRegion = pickupBulletRegion;
            this.bulletAnimation = bulletAnimation;

            setEyeSize(EYE_BIG_SIZE);

            this.ENEMY_EYE_OFFSET_FROM_TOP = ENEMY_EYE_OFFSET_FROM_TOP;
            this.eyeSmall = eyeSmall;
            this.eyeBlink = eyeBlink;
            this.particleEffectPool = particleEffectPool;
        }

        public void setEyeSize(float EYE_BIG_SIZE)
        {
            this.EYE_BIG_SIZE = EYE_BIG_SIZE;
            this.EYE_SMALL_SIZE = EYE_BIG_SIZE * 0.4f;
            this.EYE_SMALL_OFFSET_MAX = EYE_BIG_SIZE * 0.32f - EYE_SMALL_SIZE/2;
        }

        public static void reload()
        {
            ENEMY_RED.entityAnimation = Assets.instance.enemyAssets.red;
            ENEMY_RED.entityDeathAnimation = Assets.instance.enemyAssets.redDeath;
            ENEMY_RED.splashRegion = Assets.instance.splashAssets.redSplash;
            ENEMY_RED.pickupBulletRegion = Assets.instance.bulletAssets.redPickupBullet;
            ENEMY_RED.bulletAnimation = Assets.instance.bulletAssets.redBulletAnimation;
            ENEMY_RED.eyeSmall = Assets.instance.eyeAssets.eyeRedSmall;
            ENEMY_RED.eyeBlink = Assets.instance.eyeAssets.eyeBlinkRed;
            ENEMY_RED.particleEffectPool = PoolManager.getRedTrailEffectPool();

            ENEMY_BLUE.entityAnimation = Assets.instance.enemyAssets.blue;
            ENEMY_BLUE.entityDeathAnimation = Assets.instance.enemyAssets.blueDeath;
            ENEMY_BLUE.splashRegion = Assets.instance.splashAssets.blueSplash;
            ENEMY_BLUE.pickupBulletRegion = Assets.instance.bulletAssets.bluePickupBullet;
            ENEMY_BLUE.bulletAnimation = Assets.instance.bulletAssets.blueBulletAnimation;
            ENEMY_BLUE.eyeSmall = Assets.instance.eyeAssets.eyeBlueSmall;
            ENEMY_BLUE.eyeBlink = Assets.instance.eyeAssets.eyeBlinkBlue;
            ENEMY_BLUE.particleEffectPool = PoolManager.getPurpleTrailEffectPool();

            ENEMY_PURPLE.entityAnimation = Assets.instance.enemyAssets.purple;
            ENEMY_PURPLE.entityDeathAnimation = Assets.instance.enemyAssets.purpleDeath;
            ENEMY_PURPLE.splashRegion = Assets.instance.splashAssets.purpleSplash;
            ENEMY_PURPLE.pickupBulletRegion = Assets.instance.bulletAssets.purplePickupBullet;
            ENEMY_PURPLE.bulletAnimation = Assets.instance.bulletAssets.purpleBulletAnimation;
            ENEMY_PURPLE.eyeSmall = Assets.instance.eyeAssets.eyePurpleSmall;
            ENEMY_PURPLE.eyeBlink = Assets.instance.eyeAssets.eyeBlinkPurple;
            ENEMY_PURPLE.particleEffectPool = PoolManager.getPurpleTrailEffectPool();

            PLAYER_GREEN.entityAnimation = Assets.instance.playerAssets.jumpAnimation;
            PLAYER_GREEN.entityDeathAnimation = Assets.instance.playerAssets.deathAnimation;
            PLAYER_GREEN.splashRegion = Assets.instance.splashAssets.greenSplash;
            PLAYER_GREEN.pickupBulletRegion = Assets.instance.bulletAssets.greenBulletAnimation.getKeyFrame(0);
            PLAYER_GREEN.bulletAnimation = Assets.instance.bulletAssets.greenBulletAnimation;
            PLAYER_GREEN.eyeSmall = Assets.instance.eyeAssets.eyeBlackSmall;
            PLAYER_GREEN.eyeBlink = Assets.instance.eyeAssets.eyeBlinkGreen;
            PLAYER_GREEN.particleEffectPool = PoolManager.getGreenTrailEffectPool();
        }
    }

    public enum Difficulty
    {
        //           1     2    3   4  5   6     7     8      9  10  11   12   13 14   15    16     17    18    19  20
        DIFFICULTY_0(7  , 140, 190, 5, 8, 0.3f, 0f   , 0    , 2, 2, 1.5f, 3f  , 7, 9, 0.15f,  0f  ,  0f  , 0f   , 5, 8),
        DIFFICULTY_1(25 , 140, 190, 5, 8, 0.3f, 0.4f , 0f   , 2, 2, 1.5f, 3f  , 7, 9, 0.25f,  0.2f,  0f  , 0.25f, 4, 7),
        DIFFICULTY_2(50 , 160, 200, 4, 7, 0.3f, 0.25f, 0.25f, 2, 2, 1.5f, 3f  , 6, 8,  0.3f,  0.2f,  0.1f, 0.35f, 4, 6),
        DIFFICULTY_3(100, 170, 210, 3, 6, 0.3f, 0.3f , 0.3f , 2, 2, 1f  , 2.5f, 5, 7, 0.35f,  0.3f,  0.2f, 0.35f, 3, 6),
        DIFFICULTY_4(150, 180, 220, 3, 6, 0.4f, 0.35f, 0.35f, 2, 2, 1f  , 2f  , 4, 6, 0.35f,  0.4f,  0.3f, 0.5f , 3, 6),
        DIFFICULTY_5(200, 190, 220, 3, 6, 0.5f, 0.45f, 0.4f , 2, 2, 1f  , 1.5f, 4, 6,  0.4f,  0.4f,  0.4f, 0.5f , 3, 6),
        DIFFICULTY_6(300, 190, 220, 3, 5, 0.6f, 0.45f, 0.5f , 2, 2, 1f  , 1.5f, 3, 5,  0.5f, 0.45f, 0.45f, 0.8f , 4, 6),

        //                    1     2   3   4  5     6   7     8    9  10  11   12   13 14  15   16     17    18    19  20
        DIFFICULTY_TUTORIAL_1(0 , 140, 190, 5, 8, 0f  , 0f  , 0  , 20, 0, 1.5f, 3f  , 7, 9, 0f,  0f  ,  0f  , 0f   , 5, 8),
        DIFFICULTY_TUTORIAL_2(0 , 160, 200, 5, 8, 0f  , 0f  , 0  , 20, 0, 1.5f, 3f  , 7, 9, 0f,  0f  ,  0f  , 0f   , 5, 8),
        DIFFICULTY_TUTORIAL_3(0 , 140, 200, 5, 8, 0f  , 0f  , 0  , 20, 1, 1.5f, 3f  , 7, 9, 0f,  0f  ,  0f  , 0f   , 5, 8),
        DIFFICULTY_TUTORIAL_4(0 , 140, 190, 5, 8, 0.3f, 0f  , 0  , 2 , 1, 1.5f, 3f  , 5, 7, 0f,  0f  ,  0f  , 0f   , 5, 8);

        public final int POINTS_TO_NEXT_DIFFICULTY;      //#1
        public final float SPACE_BETWEEN_PLATFORMS_MIN;  //#2
        public final float SPACE_BETWEEN_PLATFORMS_MAX;  //#3
        public final int PLATFORM_MIN_NR_OF_TILES;       //#4
        public final int PLATFORM_MAX_NR_OF_TILES;       //#5

        //From 0 to 1
        public final float SPAWN_RATE_ENEMY;             //#6
        public final float SPAWN_RATE_SHOOTING_ENEMY;    //#7
        public final float SPAWN_RATE_FLYING_ENEMY;      //#8

        public final float MAX_PLATFORMS_WITHOUT_ENEMY;  //#9
        public final int MAX_ENEMIES_IN_ROW;             //#10
        public final float ENEMY_SHOTS_INTERVAL_MIN;     //#11
        public final float ENEMY_SHOTS_INTERVAL_MAX;     //#12
        public final float ENEMY_DROPPED_PICKBULLETS_MIN;//#13
        public final float ENEMY_DROPPED_PICKBULLETS_MAX;//#14
        public final float CHANCE_FOR_DROPPING_COIN;     //#15

        //From 0 to 1
        public final float SPAWN_RATE_SNOW_PLATFORM;     //#16
        public final float SPAWN_RATE_FLOATING_PLATFORM; //#17
        public final float CHANCE_FOR_BROKEN_PLATFORM;   //#18
        public final int   MIN_PLATFORMS_IN_ROW;         //#19
        public final int   MAX_PLATFORMS_IN_ROW;         //#20

        public final float HEIGHT_TO_START_DELETING_PLATFORMS = 1500; //Height of player above which platforms can be deleted


        Difficulty(int POINTS_TO_NEXT_DIFFICULTY, float SPACE_BETWEEN_PLATFORMS_MIN, float SPACE_BETWEEN_PLATFORMS_MAX, int PLATFORM_MIN_NR_OF_TILES,
                   int PLATFORM_MAX_NR_OF_TILES, float SPAWN_RATE_ENEMY, float SPAWN_RATE_SHOOTING_ENEMY, float SPAWN_RATE_FLYING_ENEMY,
                   float MAX_PLATFORMS_WITHOUT_ENEMY, int MAX_ENEMIES_IN_ROW, float ENEMY_SHOTS_INTERVAL_MIN, float ENEMY_SHOTS_INTERVAL_MAX,
                   float ENEMY_DROPPED_PICKBULLETS_MIN, float ENEMY_DROPPED_PICKBULLETS_MAX, float CHANCE_FOR_DROPPING_COIN, float SPAWN_RATE_SNOW_PLATFORM,
                   float SPAWN_RATE_FLOATING_PLATFORM, float CHANCE_FOR_BROKEN_PLATFORM, int MIN_PLATFORMS_IN_ROW, int MAX_PLATFORMS_IN_ROW)
        {
            this.POINTS_TO_NEXT_DIFFICULTY = POINTS_TO_NEXT_DIFFICULTY;
            this.SPACE_BETWEEN_PLATFORMS_MIN = SPACE_BETWEEN_PLATFORMS_MIN;
            this.SPACE_BETWEEN_PLATFORMS_MAX = SPACE_BETWEEN_PLATFORMS_MAX;
            this.PLATFORM_MIN_NR_OF_TILES = PLATFORM_MIN_NR_OF_TILES;
            this.PLATFORM_MAX_NR_OF_TILES = PLATFORM_MAX_NR_OF_TILES;
            this.SPAWN_RATE_ENEMY = SPAWN_RATE_ENEMY;
            this.SPAWN_RATE_SHOOTING_ENEMY = SPAWN_RATE_SHOOTING_ENEMY;
            this.SPAWN_RATE_FLYING_ENEMY = SPAWN_RATE_FLYING_ENEMY;
            this.MAX_PLATFORMS_WITHOUT_ENEMY = MAX_PLATFORMS_WITHOUT_ENEMY;
            this.MAX_ENEMIES_IN_ROW = MAX_ENEMIES_IN_ROW;
            this.ENEMY_SHOTS_INTERVAL_MIN = ENEMY_SHOTS_INTERVAL_MIN;
            this.ENEMY_SHOTS_INTERVAL_MAX = ENEMY_SHOTS_INTERVAL_MAX;
            this.ENEMY_DROPPED_PICKBULLETS_MIN = ENEMY_DROPPED_PICKBULLETS_MIN;
            this.ENEMY_DROPPED_PICKBULLETS_MAX = ENEMY_DROPPED_PICKBULLETS_MAX;
            this.CHANCE_FOR_DROPPING_COIN = CHANCE_FOR_DROPPING_COIN;
            this.SPAWN_RATE_SNOW_PLATFORM = SPAWN_RATE_SNOW_PLATFORM;
            this.SPAWN_RATE_FLOATING_PLATFORM  = SPAWN_RATE_FLOATING_PLATFORM;
            this.CHANCE_FOR_BROKEN_PLATFORM = CHANCE_FOR_BROKEN_PLATFORM;
            this.MIN_PLATFORMS_IN_ROW = MIN_PLATFORMS_IN_ROW;
            this.MAX_PLATFORMS_IN_ROW = MAX_PLATFORMS_IN_ROW;
        }

        public static Difficulty getNextDifficulty(Difficulty currentDifficulty)
        {
            if(currentDifficulty.equals(DIFFICULTY_0)) return DIFFICULTY_1;
            if(currentDifficulty.equals(DIFFICULTY_1)) return DIFFICULTY_2;
            if(currentDifficulty.equals(DIFFICULTY_2)) return DIFFICULTY_3;
            if(currentDifficulty.equals(DIFFICULTY_3)) return DIFFICULTY_4;
            if(currentDifficulty.equals(DIFFICULTY_4)) return DIFFICULTY_5;
            if(currentDifficulty.equals(DIFFICULTY_5)) return DIFFICULTY_6;
            if(currentDifficulty.equals(DIFFICULTY_6)) return DIFFICULTY_6;

            return DIFFICULTY_0;
        }
    }

    public enum Upgrade
    {
        NORMAL("Basic Blob", "[#ffffff]Your usual blob. Can hold onto [#50be4b]8 bullets[#ffffff] at maximum.",
                Assets.instance.gameChangers.iconNormal, 0),
        SMALLER("Smaller Blob", "[#ffffff]Smaller, faster, but can hold onto only [#50be4b]5 bullets[#ffffff].",
                Assets.instance.gameChangers.iconSmaller, 100),
        BIGGER("Bigger Blob", "[#ffffff]Bigger, slower, but can hold onto [#50be4b]12 bullets[#ffffff]!",
                Assets.instance.gameChangers.iconBigger, 60),
        BOUNCING_OFF_WALLS("Bouncy Bullet", "[#ffffff]Bullets bounce off the [#50be4b]walls[#ffffff], [#50be4b]enemies[#ffffff] and [#50be4b]platforms[#ffffff]! They can only bounce 4 times.",
                Assets.instance.gameChangers.iconBouncingOffWalls, 40),
        LOW_GRAVITY("Low Gravity", "[#ffffff]Gravity force becomes significantly [#50be4b]lower[#ffffff].",
                Assets.instance.gameChangers.iconLowGravity, 80),
        REVERSED_GRAVITY("Reversed Gravity", "[#ffffff]Gravity now pulls you [#50be4b]upwards[#ffffff], but everything else is being pulled [#50be4b]down[#ffffff]!",
                Assets.instance.gameChangers.iconReversedGravity, 120);

        public final String TITLE;
        public final String DESCRIPTION;
        public TextureRegion icon;
        public static Upgrade currentUpgrade;
        public static int coins;
        public int highScore;
        public boolean bought;
        public final int price;

        public static boolean playedTutorial = false;

        Upgrade(String title, String description, TextureRegion icon, int price)
        {
            this.TITLE = title;
            this.DESCRIPTION = description;
            this.icon = icon;
            this.price = price;
        }

        public void applyValues()
        {
            currentUpgrade = this;

            switch(this)
            {
                case NORMAL:
                    applyNormalValues();
                    break;
                case SMALLER:
                    applyNormalValues();
                    Constants.PLAYER_WIDTH = 70;
                    EntityType.PLAYER_GREEN.setEyeSize(21);
                    Constants.PLAYER_EYE_OFFSET_FROM_TOP = 24.5f;
                    Constants.PLAYER_JUMP_SPEED = 620;
                    Constants.PLAYER_BULLETS_AT_START = 4;
                    Constants.PLAYER_MAX_BULLETS_INSIDE = 5;
                    break;
                case BIGGER:
                    applyNormalValues();
                    Constants.PLAYER_WIDTH = 90;
                    EntityType.PLAYER_GREEN.setEyeSize(27);
                    Constants.PLAYER_EYE_OFFSET_FROM_TOP = 31.5f;
                    Constants.PLAYER_JUMP_SPEED = 400;
                    Constants.PLAYER_BULLETS_AT_START = 8;
                    Constants.PLAYER_MAX_BULLETS_INSIDE = 12;
                    break;
                case BOUNCING_OFF_WALLS:
                    applyNormalValues();
                    Constants.bulletBouncingOffWalls = true;
                    Constants.bulletBouncingOffEnemies = true;
                    Constants.bulletBouncingOffPlatforms = true;
                    break;
                case LOW_GRAVITY:
                    applyNormalValues();
                    Constants.GRAVITY_ACCELERATION = 100;
                    break;
                case REVERSED_GRAVITY:
                    applyNormalValues();
                    Constants.reversedGravity = true;
                    break;
            }
        }

        private void applyNormalValues()
        {
            Constants.PLAYER_WIDTH = 80;
            Constants.PLAYER_EYE_OFFSET_FROM_TOP = 28f;
            Constants.PLAYER_JUMP_SPEED = 500;
            Constants.PLAYER_BULLETS_AT_START = 6;
            Constants.PLAYER_MAX_BULLETS_INSIDE = 8;
            Constants.bulletBouncingOffWalls = false;
            Constants.bulletBouncingOffEnemies = false;
            Constants.bulletBouncingOffPlatforms = false;
            Constants.GRAVITY_ACCELERATION = 512;
            Constants.sphereAroundPlayer = false;
            Constants.reversedGravity = false;

            EntityType.PLAYER_GREEN.setEyeSize(24);
        }

        public static void save()
        {
            Preferences scorePreferences = Gdx.app.getPreferences(Constants.PREFERENCES_SCORE);
            scorePreferences.putBoolean(Constants.PREFERENCES_PLAYED_TUTORIAL, playedTutorial);
            scorePreferences.putString("current-upgrade", currentUpgrade.name());

            String encodedString = Base64.encode(String.valueOf(coins).getBytes());
            scorePreferences.putString("coins", encodedString);

            encodedString = Base64.encode(String.valueOf(NORMAL.highScore).getBytes());
            scorePreferences.putString("normal-highScore", encodedString);
            scorePreferences.putBoolean("normal-bought", NORMAL.bought);

            encodedString = Base64.encode(String.valueOf(SMALLER.highScore).getBytes());
            scorePreferences.putString("smaller-highScore", encodedString);
            scorePreferences.putBoolean("smaller-bought", SMALLER.bought);

            encodedString = Base64.encode(String.valueOf(BIGGER.highScore).getBytes());
            scorePreferences.putString("bigger-highScore", encodedString);
            scorePreferences.putBoolean("bigger-bought", BIGGER.bought);

            encodedString = Base64.encode(String.valueOf(BOUNCING_OFF_WALLS.highScore).getBytes());
            scorePreferences.putString("bouncingOffWalls-highScore", encodedString);
            scorePreferences.putBoolean("bouncingOffWalls-bought", BOUNCING_OFF_WALLS.bought);

            encodedString = Base64.encode(String.valueOf(LOW_GRAVITY.highScore).getBytes());
            scorePreferences.putString("lowGravity-highScore", encodedString);
            scorePreferences.putBoolean("lowGravity-bought", LOW_GRAVITY.bought);

            encodedString = Base64.encode(String.valueOf(REVERSED_GRAVITY.highScore).getBytes());
            scorePreferences.putString("reversedGravity-highScore", encodedString);
            scorePreferences.putBoolean("reversedGravity-bought", REVERSED_GRAVITY.bought);

            scorePreferences.flush();
        }

        public static void load()
        {
            Preferences scorePreferences = Gdx.app.getPreferences(Constants.PREFERENCES_SCORE);
            playedTutorial = scorePreferences.getBoolean(Constants.PREFERENCES_PLAYED_TUTORIAL, false);
            String currentUpgradeName = scorePreferences.getString("current-upgrade", "NORMAL");

            System.out.println(currentUpgradeName);
            if(currentUpgradeName.equals("NORMAL")) currentUpgrade = NORMAL;
            if(currentUpgradeName.equals("SMALLER")) currentUpgrade = SMALLER;
            if(currentUpgradeName.equals("BIGGER")) currentUpgrade = BIGGER;
            if(currentUpgradeName.equals("BOUNCING_OFF_WALLS")) currentUpgrade = BOUNCING_OFF_WALLS;
            if(currentUpgradeName.equals("LOW_GRAVITY")) currentUpgrade = LOW_GRAVITY;
            if(currentUpgradeName.equals("REVERSED_GRAVITY")) currentUpgrade = REVERSED_GRAVITY;


            byte[] decodedValue;
            String readValue = scorePreferences.getString("coins", "0");
            if(!readValue.equals("0"))
            {
                decodedValue = Base64.decode(readValue);
                coins = Integer.parseInt(new String(decodedValue));
            }
            else coins = 0;

            readValue = scorePreferences.getString("normal-highScore", "0");
            if(!readValue.equals("0"))
            {
                decodedValue = Base64.decode(readValue);
                NORMAL.highScore = Integer.parseInt(new String(decodedValue));
            }
            else NORMAL.highScore = 0;
            NORMAL.bought = scorePreferences.getBoolean("normal-bought", true);

            readValue = scorePreferences.getString("smaller-highScore", "0");
            if(!readValue.equals("0"))
            {
                decodedValue = Base64.decode(readValue);
                SMALLER.highScore = Integer.parseInt(new String(decodedValue));
            }
            else SMALLER.highScore = 0;
            SMALLER.bought = scorePreferences.getBoolean("smaller-bought", false);

            readValue = scorePreferences.getString("bigger-highScore", "0");
            if(!readValue.equals("0"))
            {
                decodedValue = Base64.decode(readValue);
                BIGGER.highScore = Integer.parseInt(new String(decodedValue));
            }
            else BIGGER.highScore = 0;
            BIGGER.bought = scorePreferences.getBoolean("bigger-bought", false);

            readValue = scorePreferences.getString("bouncingOffWalls-highScore", "0");
            if(!readValue.equals("0"))
            {
                decodedValue = Base64.decode(readValue);
                BOUNCING_OFF_WALLS.highScore = Integer.parseInt(new String(decodedValue));
            }
            else BOUNCING_OFF_WALLS.highScore = 0;
            BOUNCING_OFF_WALLS.bought = scorePreferences.getBoolean("bouncingOffWalls-bought", false);

            readValue = scorePreferences.getString("lowGravity-highScore", "0");
            if(!readValue.equals("0"))
            {
                decodedValue = Base64.decode(readValue);
                LOW_GRAVITY.highScore = Integer.parseInt(new String(decodedValue));
            }
            else LOW_GRAVITY.highScore = 0;
            LOW_GRAVITY.bought = scorePreferences.getBoolean("lowGravity-bought", false);

            readValue = scorePreferences.getString("reversedGravity-highScore", "0");
            if(!readValue.equals("0"))
            {
                decodedValue = Base64.decode(readValue);
                REVERSED_GRAVITY.highScore = Integer.parseInt(new String(decodedValue));
            }
            else REVERSED_GRAVITY.highScore = 0;
            REVERSED_GRAVITY.bought = scorePreferences.getBoolean("reversedGravity-bought", false);


            //Uncomment to reset shop data
            /*
            coins = 0;
            NORMAL.bought = true;
            SMALLER.bought = false;
            BIGGER.bought = false;
            BOUNCING_OFF_WALLS.bought = false;
            LOW_GRAVITY.bought = false;
            REVERSED_GRAVITY.bought = false;

            NORMAL.highScore = 0;
            SMALLER.highScore = 0;
            BIGGER.highScore = 0;
            BOUNCING_OFF_WALLS.highScore = 0;
            LOW_GRAVITY.highScore = 0;
            REVERSED_GRAVITY.highScore = 0;
            */

            scorePreferences.flush();
        }

        public static void reload()
        {
            NORMAL.icon = Assets.instance.gameChangers.iconNormal;
            SMALLER.icon = Assets.instance.gameChangers.iconSmaller;
            BIGGER.icon = Assets.instance.gameChangers.iconBigger;
            BOUNCING_OFF_WALLS.icon = Assets.instance.gameChangers.iconBouncingOffWalls;
            LOW_GRAVITY.icon = Assets.instance.gameChangers.iconLowGravity;
            REVERSED_GRAVITY.icon = Assets.instance.gameChangers.iconReversedGravity;
        }

        public int getGameChangerID()
        {
            switch (this)
            {
                case NORMAL: return AndroidHandler.NORMAL_GAME_CHANGER;
                case BOUNCING_OFF_WALLS: return AndroidHandler.BOUNCY_BULLET_GAME_CHANGER;
                case BIGGER: return AndroidHandler.BIGGER_BLOB_GAME_CHANGER;
                case LOW_GRAVITY: return AndroidHandler.LOW_GRAVITY_GAME_CHANGER;
                case SMALLER: return AndroidHandler.SMALLER_BLOB_GAME_CHANGER;
                case REVERSED_GRAVITY: return AndroidHandler.REVERSED_GRAVITY_GAME_CHANGER;
                default: return AndroidHandler.NORMAL_GAME_CHANGER;
            }
        }
    }

}
