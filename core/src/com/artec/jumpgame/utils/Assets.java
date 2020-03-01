package com.artec.jumpgame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by bartek on 01.06.16.
 */
public class Assets implements Disposable, AssetErrorListener
{
    public static final String TAG = Assets.class.getName();
    public static final Assets instance = new Assets();

    private AssetManager assetManager;

    public PlayerAssets playerAssets;
    public EyeAssets eyeAssets;
    public BulletAssets bulletAssets;
    public PlatformAssets platformAssets;
    public EnemyAssets enemyAssets;
    public SplashAssets splashAssets;
    public UIelements uiElements;
    public BackgroundAssets backgroundAssets;
    public Fonts fonts;
    public Particles particles;
    public Sounds sounds;
    public GameChangers gameChangers;
    public SplashScreenAssets splashScreenAssets;

    private Assets(){}

    public void queueLoading(AssetManager assetManager)
    {
        //if(this.assetManager != null)
        //    this.assetManager.dispose();

        this.assetManager = assetManager;
        assetManager.setErrorListener(this);
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        assetManager.load(Constants.TEXTURE_SPLASH_SCREEN_BACKGROUND, Texture.class);

        FreetypeFontLoader.FreeTypeFontLoaderParameter size1Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size1Params.fontParameters.incremental = true;
        size1Params.fontFileName = "fonts/kenvector_future.ttf";
        size1Params.fontParameters.size = (int)(Gdx.graphics.getHeight() * 0.12);
        assetManager.load("comic40.ttf", BitmapFont.class, size1Params);
        assetManager.finishLoading();

        splashScreenAssets = new SplashScreenAssets(assetManager);

        loadSound(assetManager);


        FreetypeFontLoader.FreeTypeFontLoaderParameter descriptionFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        descriptionFontParams.fontParameters.incremental = true;
        descriptionFontParams.fontFileName = "fonts/descriptionBd.ttf";
        descriptionFontParams.fontParameters.size = (int)(Gdx.graphics.getHeight() * 0.12);
        assetManager.load("description.ttf", BitmapFont.class, descriptionFontParams);


        assetManager.load(Constants.TEXTURE_ATLAS, TextureAtlas.class);
        assetManager.load(Constants.TEXTURE_ATLAS_ENTITIES, TextureAtlas.class);

        assetManager.load(Constants.TEXTURE_BACKGROUND_0, Texture.class);
        assetManager.load(Constants.TEXTURE_BACKGROUND_1, Texture.class);
        assetManager.load(Constants.TEXTURE_BACKGROUND_2, Texture.class);

        ParticleEffectLoader.ParticleEffectParameter particleParam = new ParticleEffectLoader.ParticleEffectParameter();
        particleParam.atlasFile = Constants.TEXTURE_ATLAS;
        assetManager.load(Constants.PARTICLES_TRAIL_GREEN, ParticleEffect.class, particleParam);
        assetManager.load(Constants.PARTICLES_TRAIL_PURPLE, ParticleEffect.class, particleParam);
        assetManager.load(Constants.PARTICLES_TRAIL_RED, ParticleEffect.class, particleParam);
    }

    public void init()
    {
        fonts = new Fonts(assetManager);
        sounds = new Sounds(assetManager);
        particles = new Particles(assetManager);

        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS);
        TextureAtlas atlasEntities = assetManager.get(Constants.TEXTURE_ATLAS_ENTITIES);

        playerAssets = new PlayerAssets(atlasEntities, atlas);
        eyeAssets = new EyeAssets(atlasEntities);
        bulletAssets = new BulletAssets(atlas);
        platformAssets = new PlatformAssets(atlas);
        enemyAssets = new EnemyAssets(atlasEntities);
        splashAssets = new SplashAssets(atlas);
        uiElements = new UIelements(atlas);
        gameChangers = new GameChangers(atlas);
        backgroundAssets = new BackgroundAssets(assetManager);
    }

    public boolean update() {return assetManager.update();}
    public float getProgress() {return assetManager.getProgress();}

    private void loadSound(AssetManager assetManager)
    {
        assetManager.load(Constants.MUSIC_GAMEPLAY_BACKGROUND, Music.class);
        assetManager.load(Constants.MUSIC_MENU_BACKGROUND, Music.class);

        assetManager.load(Constants.SOUND_BULLET_SHOT, Sound.class);
        assetManager.load(Constants.SOUND_LANDING, Sound.class);
        assetManager.load(Constants.SOUND_LANDING_STICKY, Sound.class);
        assetManager.load(Constants.SOUND_ENTITY_EXPLOSION, Sound.class);
        assetManager.load(Constants.SOUND_ENTITY_EXPLOSION_2, Sound.class);
        assetManager.load(Constants.SOUND_PROPELLER, Sound.class);
        assetManager.load(Constants.SOUND_PICK_UP_BULLET, Sound.class);
        assetManager.load(Constants.SOUND_PICK_UP_COIN, Sound.class);
        assetManager.load(Constants.SOUND_CLICK, Sound.class);
        assetManager.load(Constants.SOUND_BUY_ITEM, Sound.class);
    }

        @Override
    public void error(AssetDescriptor asset, Throwable throwable)
        {Gdx.app.error(TAG, "Couldn't load asset: " + asset.fileName, throwable);}

    @Override
    public void dispose()
    {
        if(assetManager.getProgress() < 1f) assetManager.finishLoading();
        assetManager.dispose();
    }

    public TextureAtlas getParticleAtlas(){return assetManager.get(Constants.TEXTURE_ATLAS);}

    public class PlayerAssets
    {
        public final Animation landAnimation;
        public final Animation jumpAnimation;

        public final Animation deathAnimation;

        public final Animation touchAnimation;
        public final TextureAtlas.AtlasRegion sphereRegion;

        public PlayerAssets(TextureAtlas entityAtlas, TextureAtlas atlas)
        {
            Array<TextureAtlas.AtlasRegion> playerSprites = new Array<TextureAtlas.AtlasRegion>();
            playerSprites.add(entityAtlas.findRegion("HI-RES-player-1"));
            playerSprites.add(entityAtlas.findRegion("HI-RES-player-2"));
            playerSprites.add(entityAtlas.findRegion("HI-RES-player-3"));

            landAnimation = new Animation(Constants.PLAYER_FRAME_DURATION, playerSprites, Animation.PlayMode.NORMAL);
            jumpAnimation = new Animation(Constants.PLAYER_FRAME_DURATION, playerSprites, Animation.PlayMode.REVERSED);

            Array<TextureAtlas.AtlasRegion> deathSprites = new Array<TextureAtlas.AtlasRegion>();
            for(int i=1; i<=8; i++)
            deathSprites.add(entityAtlas.findRegion("player-death-"+i));

            deathAnimation = new Animation(Constants.PLAYER_FRAME_DURATION * 0.5f, deathSprites, Animation.PlayMode.NORMAL);

            Array<TextureAtlas.AtlasRegion> touchSprites = new Array<TextureAtlas.AtlasRegion>();
            for(int i=1; i<=8; i++)
                touchSprites.add(atlas.findRegion("touch-"+i));

            touchAnimation = new Animation(0.05f, touchSprites, Animation.PlayMode.NORMAL);
            sphereRegion = atlas.findRegion("touch-8");
        }
    }

    public class EyeAssets
    {
        public final TextureAtlas.AtlasRegion eyeWhite;
        public final TextureAtlas.AtlasRegion eyeBlackSmall;

        public final TextureAtlas.AtlasRegion eyeRedSmall;
        public final TextureAtlas.AtlasRegion eyeBlueSmall;
        public final TextureAtlas.AtlasRegion eyePurpleSmall;

        public final TextureAtlas.AtlasRegion eyeBlinkGreen;
        public final TextureAtlas.AtlasRegion eyeBlinkBlue;
        public final TextureAtlas.AtlasRegion eyeBlinkRed;
        public final TextureAtlas.AtlasRegion eyeBlinkPurple;


        public EyeAssets(TextureAtlas atlas)
        {
            eyeWhite = atlas.findRegion("eye-white");
            eyeBlackSmall = atlas.findRegion("eye-black");

            eyeRedSmall = atlas.findRegion("eye-red-small");
            eyeBlueSmall = atlas.findRegion("eye-blue-small");
            eyePurpleSmall = atlas.findRegion("eye-purple-small");

            eyeBlinkGreen = atlas.findRegion("eye-blink-green");
            eyeBlinkBlue = atlas.findRegion("eye-blink-blue");
            eyeBlinkRed = atlas.findRegion("eye-blink-red");
            eyeBlinkPurple = atlas.findRegion("eye-blink-purple");
        }
    }

    public class BulletAssets
    {
        public final Animation greenBulletAnimation;
        public final Animation redBulletAnimation;
        public final Animation blueBulletAnimation;
        public final Animation purpleBulletAnimation;

        public final TextureAtlas.AtlasRegion redPickupBullet;
        public final TextureAtlas.AtlasRegion bluePickupBullet;
        public final TextureAtlas.AtlasRegion purplePickupBullet;

        public BulletAssets(TextureAtlas atlas)
        {
            Array<TextureAtlas.AtlasRegion> greenBullets = new Array<TextureAtlas.AtlasRegion>();
            greenBullets.add(atlas.findRegion("bullet-green-small"));
            greenBullets.add(atlas.findRegion("bullet-green-medium"));
            greenBullets.add(atlas.findRegion("bullet-green-big"));
            greenBulletAnimation = new Animation(Constants.BULLET_FRAME_DURATION, greenBullets, Animation.PlayMode.NORMAL);


            redPickupBullet = atlas.findRegion("bullet-red-small");

            Array<TextureAtlas.AtlasRegion> redBullets = new Array<TextureAtlas.AtlasRegion>();
            redBullets.add(redPickupBullet);
            redBullets.add(atlas.findRegion("bullet-red-medium"));
            redBullets.add(atlas.findRegion("bullet-red-big"));
            redBulletAnimation = new Animation(Constants.BULLET_FRAME_DURATION, redBullets, Animation.PlayMode.NORMAL);


            bluePickupBullet = atlas.findRegion("bullet-blue-small");

            Array<TextureAtlas.AtlasRegion> blueBullets = new Array<TextureAtlas.AtlasRegion>();
            blueBullets.add(bluePickupBullet);
            blueBullets.add(atlas.findRegion("bullet-blue-medium"));
            blueBullets.add(atlas.findRegion("bullet-blue-big"));
            blueBulletAnimation = new Animation(Constants.BULLET_FRAME_DURATION, blueBullets, Animation.PlayMode.NORMAL);


            purplePickupBullet = atlas.findRegion("bullet-purple-small");

            Array<TextureAtlas.AtlasRegion> purpleBullets = new Array<TextureAtlas.AtlasRegion>();
            purpleBullets.add(purplePickupBullet);
            purpleBullets.add(atlas.findRegion("bullet-purple-medium"));
            purpleBullets.add(atlas.findRegion("bullet-purple-big"));
            purpleBulletAnimation = new Animation(Constants.BULLET_FRAME_DURATION, purpleBullets, Animation.PlayMode.NORMAL);
        }
    }

    public class PlatformAssets
    {
        public final Array<TextureAtlas.AtlasRegion> stonePlatform;
        public final Array<TextureAtlas.AtlasRegion> floatingPlatform;
        public final Array<TextureAtlas.AtlasRegion> snowPlatform;
        public final Array<TextureAtlas.AtlasRegion> startPlatform;

        public final Array<TextureAtlas.AtlasRegion> stonePlatformBroken;
        public final Array<TextureAtlas.AtlasRegion> snowPlatformBroken;

        public final Array<TextureAtlas.AtlasRegion> trailBlue;
        public final Array<TextureAtlas.AtlasRegion> trailRed;

        public PlatformAssets(TextureAtlas atlas)
        {
            stonePlatform = new Array<TextureAtlas.AtlasRegion>();
            stonePlatform.add(atlas.findRegion("metal4-mid"));   //Tile 0 is one full tile
            stonePlatform.add(atlas.findRegion("metal4-left"));  //Tile 1 is tile on the left
            stonePlatform.add(atlas.findRegion("metal4-mid"));   //Tile 2 is tile in the middle
            stonePlatform.add(atlas.findRegion("metal4-right")); //Tile 3 is tile on the right

            floatingPlatform = new Array<TextureAtlas.AtlasRegion>();
            floatingPlatform.add(atlas.findRegion("metal3-mid"));  //Tile 0 is one full tile
            floatingPlatform.add(atlas.findRegion("metal3-left")); //Tile 1 is tile on the left
            floatingPlatform.add(atlas.findRegion("metal3-mid"));  //Tile 2 is tile in the middle
            floatingPlatform.add(atlas.findRegion("metal3-right"));//Tile 3 is tile on the right

            snowPlatform = new Array<TextureAtlas.AtlasRegion>();
            snowPlatform.add(atlas.findRegion("snow-mid"));    //Tile 0 is one full tile
            snowPlatform.add(atlas.findRegion("snow-left"));   //Tile 1 is tile on the left
            snowPlatform.add(atlas.findRegion("snow-mid"));    //Tile 2 is tile in the middle
            snowPlatform.add(atlas.findRegion("snow-right"));  //Tile 3 is tile on the right

            startPlatform = new Array<TextureAtlas.AtlasRegion>();
            startPlatform.add(atlas.findRegion("metal-start-mid"));    //Tile 0 is one full tile
            startPlatform.add(atlas.findRegion("metal-start-left"));   //Tile 1 is tile on the left
            startPlatform.add(atlas.findRegion("metal-start-mid"));    //Tile 2 is tile in the middle
            startPlatform.add(atlas.findRegion("metal-start-right"));  //Tile 3 is tile on the right


            stonePlatformBroken = new Array<TextureAtlas.AtlasRegion>();
            stonePlatformBroken.add(atlas.findRegion("metal-broken-mid"));
            stonePlatformBroken.add(atlas.findRegion("metal-broken-left"));
            stonePlatformBroken.add(atlas.findRegion("metal-broken-mid"));
            stonePlatformBroken.add(atlas.findRegion("metal-broken-right"));

            snowPlatformBroken = new Array<TextureAtlas.AtlasRegion>();
            snowPlatformBroken.add(atlas.findRegion("snow-broken-mid"));
            snowPlatformBroken.add(atlas.findRegion("snow-broken-left"));
            snowPlatformBroken.add(atlas.findRegion("snow-broken-mid"));
            snowPlatformBroken.add(atlas.findRegion("snow-broken-right"));


            trailBlue = new Array<TextureAtlas.AtlasRegion>();
            trailBlue.add(atlas.findRegion("enemy-trail-blue-mid"));
            trailBlue.add(atlas.findRegion("enemy-trail-blue-left"));
            trailBlue.add(atlas.findRegion("enemy-trail-blue-mid"));
            trailBlue.add(atlas.findRegion("enemy-trail-blue-right"));

            trailRed = new Array<TextureAtlas.AtlasRegion>();
            trailRed.add(atlas.findRegion("enemy-trail-red-mid"));
            trailRed.add(atlas.findRegion("enemy-trail-red-left"));
            trailRed.add(atlas.findRegion("enemy-trail-red-mid"));
            trailRed.add(atlas.findRegion("enemy-trail-red-right"));
        }
    }

    public class EnemyAssets
    {
        public final Animation blue;
        public final Animation blueDeath;

        public final Animation red;
        public final Animation redDeath;

        public final Animation purple;
        public final Animation purpleDeath;
        public EnemyAssets(TextureAtlas atlas)
        {
            //Blue Sprites
            Array<TextureRegion> blueSprites = new Array<TextureRegion>();
            for(int i = 1; i <= 7; i++)
                blueSprites.add(atlas.findRegion("enemy-blue-"+i));
            blue = new Animation(0.07f, blueSprites, Animation.PlayMode.LOOP_PINGPONG);

            Array<TextureRegion> blueDeathSprites = new Array<TextureRegion>();
            for(int i = 1; i <= 8; i++)
            blueDeathSprites.add(atlas.findRegion("enemy-blue-death-"+i));
            blueDeath = new Animation(0.03f, blueDeathSprites, Animation.PlayMode.NORMAL);


            //Red Sprites
            Array<TextureRegion> redSprites = new Array<TextureRegion>();
            for(int i = 1; i <= 7; i++)
                redSprites.add(atlas.findRegion("enemy-red-"+i));
            red = new Animation(0.07f, redSprites, Animation.PlayMode.LOOP_PINGPONG);

            Array<TextureRegion> redDeathSprites = new Array<TextureRegion>();
            for(int i = 1; i <= 8; i++)
                redDeathSprites.add(atlas.findRegion("enemy-red-death-"+i));
            redDeath = new Animation(0.03f, redDeathSprites, Animation.PlayMode.NORMAL);


            //Purple Sprites
            Array<TextureRegion> purpleSprites = new Array<TextureRegion>();
            for(int i = 1; i <= 6; i++)
            purpleSprites.add(atlas.findRegion("enemy-purple-"+i));
            purple = new Animation(0.01f, purpleSprites, Animation.PlayMode.LOOP_PINGPONG);

            Array<TextureRegion> purpleDeathSprites = new Array<TextureRegion>();
            for(int i = 1; i <= 8; i++)
                purpleDeathSprites.add(atlas.findRegion("enemy-purple-death-"+i));
            purpleDeath = new Animation(0.03f, purpleDeathSprites, Animation.PlayMode.NORMAL);
        }
    }

    public class SplashAssets
    {
        public final TextureAtlas.AtlasRegion redSplash;
        public final TextureAtlas.AtlasRegion blueSplash;
        public final TextureAtlas.AtlasRegion greenSplash;
        public final TextureAtlas.AtlasRegion purpleSplash;

        public SplashAssets(TextureAtlas atlas)
        {
            redSplash = atlas.findRegion("splash-red");
            blueSplash = atlas.findRegion("splash-blue");
            greenSplash = atlas.findRegion("splash-green");
            purpleSplash = atlas.findRegion("splash-purple");
        }
    }

    public class Fonts
    {
        public final BitmapFont comic40;
        public final BitmapFont descriptionFont;

        public Fonts (AssetManager manager)
        {
            comic40 = manager.get("comic40.ttf", BitmapFont.class);
            comic40.getData().markupEnabled = true;
            descriptionFont = manager.get("description.ttf", BitmapFont.class);
            descriptionFont.getData().markupEnabled = true;
        }
    }

    public class UIelements
    {
        public final TextureAtlas.AtlasRegion fadedBackground;
        public final TextureAtlas.AtlasRegion shopButtonBackground;

        public final TextureAtlas.AtlasRegion restartButton;
        public final TextureAtlas.AtlasRegion restartButtonClicked;
        public final TextureAtlas.AtlasRegion restartButtonWhite;
        public final TextureAtlas.AtlasRegion restartButtonWhiteClicked;

        public final TextureAtlas.AtlasRegion menuButtonWhite;
        public final TextureAtlas.AtlasRegion menuButtonClickedWhite;

        public final TextureAtlas.AtlasRegion continueButton;
        public final TextureAtlas.AtlasRegion continueButtonClicked;

        public final TextureAtlas.AtlasRegion pauseButton;
        public final TextureAtlas.AtlasRegion pauseButtonClicked;

        public final TextureAtlas.AtlasRegion shareButton;
        public final TextureAtlas.AtlasRegion shareButtonClicked;

        public final TextureAtlas.AtlasRegion howToPlayButton;
        public final TextureAtlas.AtlasRegion howToPlayButtonClicked;

        public final TextureAtlas.AtlasRegion gameChangersButton;
        public final TextureAtlas.AtlasRegion gameChangersButtonClicked;

        public final TextureAtlas.AtlasRegion aboutButton;
        public final TextureAtlas.AtlasRegion aboutButtonClicked;

        public final TextureAtlas.AtlasRegion achievementsButton;
        public final TextureAtlas.AtlasRegion achievementsButtonClicked;

        public final TextureAtlas.AtlasRegion rankingsButton;
        public final TextureAtlas.AtlasRegion rankingsButtonClicked;

        public final TextureAtlas.AtlasRegion soundOn;
        public final TextureAtlas.AtlasRegion soundOff;

        public final TextureAtlas.AtlasRegion musicOn;
        public final TextureAtlas.AtlasRegion musicOff;

        public final TextureAtlas.AtlasRegion exit;

        public final TextureAtlas.AtlasRegion backButton;
        public final TextureAtlas.AtlasRegion backButtonClicked;

        public final TextureAtlas.AtlasRegion yesButton;
        public final TextureAtlas.AtlasRegion yesButtonClicked;

        public final TextureAtlas.AtlasRegion noButton;
        public final TextureAtlas.AtlasRegion noButtonClicked;

        public final TextureAtlas.AtlasRegion scroll;

        public final NinePatch textBackground;

        public final NinePatch equipButton;
        public final NinePatch equipButtonClicked;
        public final NinePatch equipButtonUnActive;

        public final NinePatch buyButton;
        public final NinePatch buyButtonClicked;
        public final NinePatch buyButtonUnActive;

        public UIelements(TextureAtlas atlas)
        {
            fadedBackground = atlas.findRegion("faded-background");
            shopButtonBackground = atlas.findRegion("shop-button-background");

            restartButton = atlas.findRegion("button-restart-normal");
            restartButtonClicked = atlas.findRegion("button-restart-clicked");
            restartButtonWhite = atlas.findRegion("button-restart-normal-white");
            restartButtonWhiteClicked = atlas.findRegion("button-restart-clicked-white");

            menuButtonWhite = atlas.findRegion("button-menu-normal-white");
            menuButtonClickedWhite = atlas.findRegion("button-menu-clicked-white");

            continueButton = atlas.findRegion("button-continue-normal");
            continueButtonClicked = atlas.findRegion("button-continue-clicked");

            pauseButton = atlas.findRegion("button-pause");
            pauseButtonClicked = atlas.findRegion("button-pause-clicked");

            shareButton = atlas.findRegion("button-share-normal");
            shareButtonClicked = atlas.findRegion("button-share-clicked");

            howToPlayButton = atlas.findRegion("how-to-play-button-normal");
            howToPlayButtonClicked = atlas.findRegion("how-to-play-button-clicked");

            gameChangersButton = atlas.findRegion("game-changers-button-normal");
            gameChangersButtonClicked = atlas.findRegion("game-changers-button-clicked");

            aboutButton = atlas.findRegion("button-about-normal");
            aboutButtonClicked = atlas.findRegion("button-about-clicked");

            achievementsButton = atlas.findRegion("button-achievements-normal");
            achievementsButtonClicked = atlas.findRegion("button-achievements-clicked");

            rankingsButton = atlas.findRegion("button-rankings-normal");
            rankingsButtonClicked = atlas.findRegion("button-rankings-clicked");

            soundOn = atlas.findRegion("audioOn");
            soundOff = atlas.findRegion("audioOff");

            musicOn = atlas.findRegion("musicOn");
            musicOff = atlas.findRegion("musicOff");

            exit = atlas.findRegion("exit");

            backButton = atlas.findRegion("button-back-normal");
            backButtonClicked = atlas.findRegion("button-back-clicked");

            yesButton = atlas.findRegion("button-yes-normal");
            yesButtonClicked = atlas.findRegion("button-yes-clicked");

            noButton = atlas.findRegion("button-no-normal");
            noButtonClicked = atlas.findRegion("button-no-clicked");

            scroll = atlas.findRegion("scroll");

            textBackground = new NinePatch(atlas.findRegion("text-background"), 35, 35, 35, 35);

            equipButton = new NinePatch(atlas.findRegion("button-equip-normal"), 1, 1, 1, 1);
            equipButtonClicked = new NinePatch(atlas.findRegion("button-equip-clicked"), 1, 1, 1, 1);
            equipButtonUnActive = new NinePatch(atlas.findRegion("button-equip-unActive"), 1, 1, 1, 1);

            buyButton = new NinePatch(atlas.findRegion("button-buy-normal"), 0, 1, 1, 1);
            buyButtonClicked = new NinePatch(atlas.findRegion("button-buy-clicked"), 0, 1, 1, 1);
            buyButtonUnActive = new NinePatch(atlas.findRegion("button-buy-unActive"), 0, 1, 1, 1);
        }
    }

    public class BackgroundAssets
    {
        public final Array<Texture> backgroundTextures;

        public BackgroundAssets(AssetManager assetManager)
        {
            backgroundTextures = new Array<Texture>();

            backgroundTextures.add(assetManager.get(Constants.TEXTURE_BACKGROUND_0, Texture.class));
            backgroundTextures.add(assetManager.get(Constants.TEXTURE_BACKGROUND_1, Texture.class));
            backgroundTextures.add(assetManager.get(Constants.TEXTURE_BACKGROUND_2, Texture.class));
        }
    }

    public class Particles
    {
        public final ParticleEffect greenTrail;
        public final ParticleEffect purpleTrail;
        public final ParticleEffect redTrail;


        public Particles(AssetManager assetManager)
        {
            greenTrail = assetManager.get(Constants.PARTICLES_TRAIL_GREEN);
            purpleTrail = assetManager.get(Constants.PARTICLES_TRAIL_PURPLE);
            redTrail = assetManager.get(Constants.PARTICLES_TRAIL_RED);
        }
    }

    public class Sounds
    {
        public final Music gameplayBackgroundMusic;
        public final Music menuBackgroundMusic;

        public final Sound pickUpBullet;
        public final Sound pickUpCoin;

        public final Sound bulletShot;

        public final Sound landing;
        public final Sound landingSticky;

        public final Sound entityExplosion;
        public final Sound entityExplosion2;
        public final Sound propeller;

        public final Sound click;
        public final Sound buyItem;

        public Sounds(AssetManager assetManager)
        {
            gameplayBackgroundMusic = assetManager.get(Constants.MUSIC_GAMEPLAY_BACKGROUND);
            menuBackgroundMusic = assetManager.get(Constants.MUSIC_MENU_BACKGROUND);

            bulletShot = assetManager.get(Constants.SOUND_BULLET_SHOT);
            landing = assetManager.get(Constants.SOUND_LANDING);
            landingSticky = assetManager.get(Constants.SOUND_LANDING_STICKY);
            entityExplosion = assetManager.get(Constants.SOUND_ENTITY_EXPLOSION);
            entityExplosion2 = assetManager.get(Constants.SOUND_ENTITY_EXPLOSION_2);
            propeller = assetManager.get(Constants.SOUND_PROPELLER);
            pickUpBullet = assetManager.get(Constants.SOUND_PICK_UP_BULLET);
            pickUpCoin = assetManager.get(Constants.SOUND_PICK_UP_COIN);
            click = assetManager.get(Constants.SOUND_CLICK);
            buyItem = assetManager.get(Constants.SOUND_BUY_ITEM);
        }
    }

    public class GameChangers
    {
        public final Animation coinAnimation;

        public final TextureAtlas.AtlasRegion iconNormal;
        public final TextureAtlas.AtlasRegion iconSmaller;
        public final TextureAtlas.AtlasRegion iconBigger;
        public final TextureAtlas.AtlasRegion iconBouncingOffWalls;
        public final TextureAtlas.AtlasRegion iconLowGravity;
        public final TextureAtlas.AtlasRegion iconReversedGravity;

        public GameChangers(TextureAtlas atlas)
        {
            Array<TextureAtlas.AtlasRegion> coinSprites = new Array<TextureAtlas.AtlasRegion>();
            for(int i = 1; i <= 6; i++)
                coinSprites.add(atlas.findRegion("coin-"+i));
            coinAnimation = new Animation(1f/25f, coinSprites, Animation.PlayMode.LOOP);

            iconNormal = atlas.findRegion("gameChanger-normal");
            iconSmaller = atlas.findRegion("gameChanger-small");
            iconBigger = atlas.findRegion("gameChanger-big");
            iconBouncingOffWalls = atlas.findRegion("gameChanger-bouncingOffWalls");
            iconLowGravity = atlas.findRegion("gameChanger-lowGravity");
            iconReversedGravity = atlas.findRegion("gameChanger-reversedGravity");
        }
    }

    public class SplashScreenAssets
    {
        public final Texture backgroundTexture;
        public final BitmapFont titleFont;

        public SplashScreenAssets(AssetManager assetManager)
        {
            backgroundTexture = assetManager.get(Constants.TEXTURE_SPLASH_SCREEN_BACKGROUND, Texture.class);

            titleFont = assetManager.get("comic40.ttf", BitmapFont.class);
            titleFont.getData().markupEnabled = true;
        }
    }
}
