package com.artec.jumpgame.utils;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by bartek on 01.06.16.
 */
public class Constants
{
    public static final float WORLD_SIZE = 512;
    public static final Color CLEAR_COLOR = new Color(43f/255f, 48f/255f, 32f/255f, 1);
    public static final String TEXTURE_ATLAS = "images/others/sprites.pack.atlas";
    public static final String TEXTURE_ATLAS_ENTITIES = "images/entities/entities.pack.atlas";

    public static final String TEXTURE_BACKGROUND_0 = "background/background-0.png";
    public static final String TEXTURE_BACKGROUND_1 = "background/background-1.png";
    public static final String TEXTURE_BACKGROUND_2 = "background/background-1-horizontal.png";
    public static final String TEXTURE_SPLASH_SCREEN_BACKGROUND = "background/background-splash-screen.png";
    public static final float STOP_TIME_AFTER_ENEMY_DEATH = 0.06f; //In seconds

    //Player
    public static float PLAYER_WIDTH = 80;
    public static float PLAYER_EYE_OFFSET_FROM_TOP = 28;
    public static final float PLAYER_MAX_JUMP_TIME = 0.5f;
    public static float PLAYER_JUMP_SPEED = 480;
    public static final float PLAYER_SHOT_SPEED = 680;
    public static int   PLAYER_BULLETS_AT_START = 6;
    public static float PLAYER_MAX_BULLETS_INSIDE = 8;
    public static final float PLAYER_FRAME_DURATION = 0.05f;
    public static float GRAVITY_ACCELERATION = 512;
    public static final float PLAYER_MAX_TIME_WITHOUT_BULLETS_UNTIL_DEATH = 1f;

    public static final float SPHERE_MAX_RADIUS = 125;
    public static final float SPHERE_SIZE_SHRINK_PER_SEC = 100;

    //Eye
    public static final float EYE_BIG_SIZE = 24;
    public static final float EYE_SMALL_SIZE = EYE_BIG_SIZE * 0.4f;
    public static final float EYE_SMALL_OFFSET_MAX = EYE_BIG_SIZE * 0.32f - EYE_SMALL_SIZE/2;

    public static final float EYE_BLINK_INTERVAL_MIN = 1f;
    public static final float EYE_BLINK_INTERVAL_MAX = 3f;
    public static final float EYE_BLINK_DURATION = 0.1f;

    public static final float SCORE_MULTIPLIER = 0.01f;

    //Platforms
    public static final float PLATFORM_TILE_WIDTH = 64;
    public static final float PLATFORMS_WITHOUT_ENEMY_FROM_BOTTOM = 2;

    public static final float PLATFORM_FRICTION_VELOCITY_SLOWDOWN = PLAYER_JUMP_SPEED * 1.2f; //Bigger the number, bigger slowdown
    public static final float PLATFORM_SNOW_FRICTION_VELOCITY_SLOWDOWN = PLAYER_JUMP_SPEED * 0.35f;

    public static final float PLATFORM_FLYING_X_SPEED_MIN = 50;
    public static final float PLATFORM_FLYING_X_SPEED_MAX = 70;
    public static final float PLATFORM_FLYING_Y_SPEED = 15;
    public static final float PLATFORM_FLYING_Y_AMPLITUDE = 7;

    public static final float PLATFORM_BROKEN_TIME_BEFORE_FALLING = 0.5f; //In seconds
    public static final float PLATFORM_BROKEN_FADE_OUT_TIME = 1f; //In seconds
    public static final float PLATFORM_BROKEN_X_OFFSET_MAX = 8f;
    public static final float PLATFORM_BROKEN_Y_OFFSET_MAX = 30f;
    public static final float PLATFORM_BROKEN_X_SPEED = 250;
    public static final float PLATFORM_BROKEN_Y_SPEED = -150;
    public static final float PLATFORM_BROKEN_ROTATION_CHANGE_PER_SEC_MAX = 70f;
    public static final float PLATFORM_BROKEN_ROTATION_CHANGE_PER_SEC_MIN = 10f;


    //Enemy
    public static final float ENEMY_WIDTH = 96;

    public static final float ENEMY_MOVEMENT_SPEED_MIN = 70;
    public static final float ENEMY_MOVEMENT_SPEED_MAX = 96;
    public static final float ENEMY_SHOT_SPEED = 250;
    public static final float ENEMY_SHOT_VELOCITY_Y_MAX = 256;
    public static final float ENEMY_SHOT_VELOCITY_Y_MIN = 0;
    public static final float ENEMY_BULLET_BIG_RADIUS = 14;
    public static final float ENEMY_BULLET_MEDIUM_RADIUS = 10;
    public static final float ENEMY_BULLET_SMALL_RADIUS = 6;

    //Flying Enemy
    public static final float ENEMY_FLYING_X_SPEED = 70;
    public static final float ENEMY_FLYING_Y_SPEED = 15;
    public static final float ENEMY_FLYING_Y_AMPLITUDE = 7;
    public static final float ENEMY_FLYING_SPAWN_HEIGHT_MIN = 0.1f; //Portion of space between platforms
    public static final float ENEMY_FLYING_SPAWN_HEIGHT_MAX = 0.3f; //Portion of space between platforms
    public static final float ENEMY_FLYING_SHOT_VELOCITY_Y_MIN = -64;
    public static final float ENEMY_FLYING_SHOT_VELOCITY_Y_MAX = -128;


    //PickBullet
    public static final float PICKUPBULLET_WIDTH = 16;
    public static final float PICKUPBULLET_VELOCITY_X_MAX = 150;
    public static final float PICKUPBULLET_VELOCITY_Y_MIN = 100;
    public static final float PICKUPBULLET_VELOCITY_Y_MAX = 300;
    public static final float PICKUPBULLET_MOVING_TO_PLAYER_VELOCITY = 450;
    public static final float PICKUPBULLET_FRICTION_VELOCITY_SLOWDOWN_MULTIPLIER = 0.4f;

    //Bullet
    public static final float BULLET_BIG_RADIUS = 18;
    public static final float BULLET_MEDIUM_RADIUS = 12;
    public static final float BULLET_SMALL_RADIUS = 6;
    public static final float BULLET_FRAME_DURATION = 0.10f;
    public static final float BULLET_FADE_IN_TIME = 1;
    public static final float BULLET_IN_PLAYER_MIN_VELOCITY = 10f;
    public static final float BULLET_IN_PLAYER_MAX_VELOCITY = 20f;

    //Splash
    public static final float SPLASH_BASE_WIDTH = 96;
    public static final float SPLASH_MIN_SIZE = 0.9f;
    public static final float SPLASH_MAX_SIZE = 1.1f;

    //Score
    public static final String PREFERENCES_SCORE = "Score";
    public static final String PREFERENCES_BEST_SCORE = "bestScore";
    public static final String PREFERENCES_PLAYED_TUTORIAL = "playedTutorial";

    //Overlays
    public static final float LOADING_SCREEN_DEALAY = 0.2f; //Time after loading, to showing game

    public static final float CREDITS_OVERLAY_LETTERS_SPEED_PER_SEC = 0.05f;

    public static final float DEATH_SCREEN_FADE_IN_TIME = 1;
    public static final float DEATH_SCREEN_UNCLICKABLE_TIME = 0.5f; //How much time has to pass, before player can click something in death overlay.
    public static final float DEATH_SCREEN_RESTART_BUTTON_WIDTH = 0.2f; //Portion of screen width
    public static final float DEATH_SCREEN_ICON_SIZE = 0.25f;
    public static final float DEATH_SCREEN_SPACE_BETWEEN_ICON_AND_BEST = 0.1f;

    public static final float DIALOG_WINDOW_BUTTON_SIZE = 0.3f;
    public static final float TEXT_PADDING_SIDES = 0.05f;
    public static final float TEXT_PADDING_UP = 0.05f;

    public static final float MENU_SCREEN_HOW_TO_PLAY_BUTTON_WIDTH = 0.7f; // Portion of screen width
    public static final float MENU_SCREEN_ABOUT_BUTTON_WIDTH = 0.25f;
    public static final float MENU_SCREEN_TOGGLE_BUTTON_WIDTH = 0.115f;
    public static final float MENU_SCREEN_TAP_TO_SHOOT_BLINK_VISIBLE_TIME = 0.5f;
    public static final float MENU_SCREEN_TAP_TO_SHOOT_BLINK_NOT_VISIBLE_TIME = 0.2f;

    public static final float SHOP_SPACE_BETWEEN_OBJECTS = 0.2f; //Portion of screen
    public static final float SHOP_MIN_LENGTH_OF_TOUCH_TO_SCROLL = 0.04f;
    public static final float SHOP_SCROLL_WIDTH = 0.03f;

    public static final float OVERLAY_SPACE_BETWEEN_BUTTONS = 0.1f; //Portion of screen width
    public static final float HUD_PAUSE_BUTTON_WIDTH = 0.08f; //Portion of screen width

    public static final float TUTORIAL_TEXT_TIME_BETWEEN_DRAWING_LETTERS = 0.04f; //In seconds


    public static final int BACKGROUND_TEXTURES_MAX = 2; //Number of different background textures

    //Camera
    public static final float CAMERA_SHAKE_VELOCITY = 300;
    public static final float CAMERA_SHAKE_AMPLITUDE = 10f;
    public static final float CAMERA_SHAKE_AFTER_DEATH_LENGTH = 0.4f;

    //Sounds
    public static final String MUSIC_GAMEPLAY_BACKGROUND = "sounds/background/gameplay-background.mp3";
    public static final String MUSIC_MENU_BACKGROUND = "sounds/background/menu-background.mp3";

    public static final String SOUND_PICK_UP_BULLET = "sounds/20500/pickUpBullet.ogg";
    public static final String SOUND_PICK_UP_COIN = "sounds/20500/coin.ogg";
    public static final String SOUND_BULLET_SHOT = "sounds/20500/bullet-shot.ogg";

    public static final String SOUND_LANDING = "sounds/20500/jumpland2.ogg";
    public static final String SOUND_LANDING_STICKY = "sounds/20500/jumpLandSticky.ogg";

    public static final String SOUND_ENTITY_EXPLOSION = "sounds/20500/entity-explosion1.ogg";
    public static final String SOUND_ENTITY_EXPLOSION_2 = "sounds/20500/entity-explosion2.ogg";

    public static final String SOUND_PROPELLER = "sounds/20500/propeller.ogg";
    public static final float SOUND_PROPELLER_VOLUME = 0.3f;

    public static final String SOUND_CLICK = "sounds/20500/click.ogg";
    public static final String SOUND_BUY_ITEM = "sounds/20500/buy-item.ogg";

    //Particles
    public final static String PARTICLES_TRAIL_GREEN = "particle effects/trail-green.p";
    public final static String PARTICLES_TRAIL_PURPLE = "particle effects/trail-purple.p";
    public final static String PARTICLES_TRAIL_RED = "particle effects/trail-red.p";


    //Upgrades
    public static final float COIN_SIZE = 35;
    public static final float TIME_TO_SHOW_COIN_STATE = 2.5f; //For how many seconds display amount of coins after picking one up.
    public static final float OVERLAY_COIN_SIZE = 0.08f;

    public static boolean bulletBouncingOffWalls = false;
    public static boolean bulletBouncingOffEnemies = false;
    public static boolean bulletBouncingOffPlatforms = false;
    public static final int BULLET_MAX_BOUNCES = 4;
    public static final float bouncingBallVelocityMultiplier = 1.6f;
    public static boolean sphereAroundPlayer = false;

    public static boolean reversedGravity = false;
    public static final float REVERSED_GRAVITY_MAX_SPEED_Y = 600;



    //Others
    public static final float CHROMATIC_ABERRATION_DURATION = 0.3f;
    public static final float CHROMATIC_ABERRATION_MAX_OFFSET = 30f;

    public static final float TOUCH_GESTURE_WIDTH = 30;
}