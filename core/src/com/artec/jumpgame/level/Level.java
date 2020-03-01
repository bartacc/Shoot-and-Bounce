package com.artec.jumpgame.level;

import com.artec.jumpgame.Achievements;
import com.artec.jumpgame.entities.Enemy;
import com.artec.jumpgame.entities.Player;
import com.artec.jumpgame.objects.Bullet;
import com.artec.jumpgame.objects.Coin;
import com.artec.jumpgame.objects.PickupBullet;
import com.artec.jumpgame.objects.Platform;
import com.artec.jumpgame.objects.Splash;
import com.artec.jumpgame.overlays.ChromaticAberration;
import com.artec.jumpgame.overlays.GameHUD;
import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.artec.jumpgame.utils.PlaySounds;
import com.artec.jumpgame.utils.PoolManager;
import com.artec.jumpgame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 01.06.16.
 */
public class Level
{
    private GameScreen gameScreen;
    private Color drawColor;

    private LevelState levelState;
    private Enums.LevelUpdateState levelUpdateState;
    private float killedEnemyElapsedTime;

    private Viewport viewport;
    private Viewport HUDviewport;

    private Player player;
    private ChaseCam chaseCam;

    private DelayedRemovalArray<Platform> platformArray;
    private DelayedRemovalArray<Bullet> bulletArray;
    private DelayedRemovalArray<Enemy> enemyArray;
    private DelayedRemovalArray<Splash> splashArray;
    private DelayedRemovalArray<PickupBullet> pickupBulletArray;
    private DelayedRemovalArray<Coin> coinArray;

    private PoolManager poolManager;

    private LevelSpawner levelSpawner;
    private Enums.Difficulty difficulty;
    private Background background;

    private TutorialAnimation tutorialAnimation;
    private GameHUD gameHUD;
    private int score;


    public Level(GameScreen gameScreen)
    {
        this.gameScreen = gameScreen;
        drawColor = new Color(1, 1, 1, 1);
        poolManager = new PoolManager(this);

        viewport = new ExtendViewport(Constants.WORLD_SIZE, Constants.WORLD_SIZE);
        HUDviewport = new ScreenViewport();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameHUD = new GameHUD();
        tutorialAnimation = new TutorialAnimation();

        background = new Background(viewport);
        chaseCam = new ChaseCam();
        bulletArray = new DelayedRemovalArray<Bullet>();
        pickupBulletArray = new DelayedRemovalArray<PickupBullet>();
        platformArray = new DelayedRemovalArray<Platform>();
        coinArray = new DelayedRemovalArray<Coin>();

        levelSpawner = new LevelSpawner(this);
    }

    public void init(LevelState levelState)
    {
        Achievements.resetStats();
        this.levelState = levelState;
        killedEnemyElapsedTime = 0;

        PlaySounds.init();
        score = 0;

        difficulty = levelState.difficulty;

        for(PickupBullet pickupBullet : pickupBulletArray) {pickupBullet.deActivate();}
        pickupBulletArray.clear();

        for(Bullet bullet : bulletArray) {bullet.die();}
        bulletArray.clear();

        coinArray.clear();

        player = new Player(new Vector2(viewport.getWorldWidth()/2 - Constants.PLAYER_WIDTH/2, 10), this);
        chaseCam.init(player, viewport, levelState);
        levelSpawner.init(this);

        tutorialAnimation.init(this);
        gameHUD.init(HUDviewport, gameScreen, levelState);
        background.init();

        if(levelState != Level.LevelState.ANIMATION_1 &&
                levelState != Level.LevelState.ANIMATION_2  && levelState != Level.LevelState.ANIMATION_3  &&
                levelState != Level.LevelState.ANIMATION_4)
        gameScreen.getInputMultiplexer().addProcessor(player);

        gameScreen.getInputMultiplexer().addProcessor(gameHUD);
    }

    public void reload()
    {
        gameHUD.reload();
        Enums.PlatformType.reload();
        poolManager.reload();
        Enums.EntityType.reload();
        player.reload();
        for(Enemy enemy : enemyArray)
            enemy.reload();
        for(Bullet bullet : bulletArray)
        {
            ParticleEffectPool particleEffectPool;
            if(bullet.getPlayer() == null) particleEffectPool = bullet.bulletAnimation.getEntityType().particleEffectPool;
            else particleEffectPool = Enums.EntityType.PLAYER_GREEN.particleEffectPool;

            bullet.initBulletTrail(bullet.bulletAnimation.getEntityType(), bullet.bulletAnimation.getSizeSmall(),
                    bullet.bulletAnimation.getSizeMedium(), bullet.bulletAnimation.getSizeBig(), particleEffectPool);
        }
        Enums.Upgrade.reload();
        background.reload();
    }

    public void update(float delta)
    {
        if(levelState.isObjectiveComplete() && Utils.secondsSince(levelState.getObjectiveCompletionTime()) > levelState.TUTORIAL_TIME_AFTER_COMPLETION_TO_NEXT_TUTORIAL)
        {
            if(levelState.getNextTutorial() != LevelState.NORMAL)
                gameScreen.play(levelState.getNextTutorial());
            else
                gameScreen.finishedTutorial();
        }

        chaseCam.update(delta);
        if(levelUpdateState == Enums.LevelUpdateState.STOPPED)
        {
            killedEnemyElapsedTime += delta;
            if(killedEnemyElapsedTime > Constants.STOP_TIME_AFTER_ENEMY_DEATH) levelUpdateState = Enums.LevelUpdateState.UPDATING;
            else return;
        }

        levelSpawner.update();
        background.update(chaseCam.getPositionCenter());
        tutorialAnimation.update(delta);

        //Updates Player
        if(!player.isDead()) //Only if player is alive
        {
            player.update(delta);

            for (Platform platform : platformArray) player.checkPlatformCollision(platform);

            bulletArray.begin();
            for (int i = 0; i < bulletArray.size; i++)
            {
                if (bulletArray.get(i).getPlayer() == null && player.checkBulletCollision(bulletArray.get(i)))
                {   //Player dies
                    bulletArray.removeIndex(i);
                    gameOver(true, Enums.DeathReason.KILLED_BY_ENEMY);
                }
            }
            bulletArray.end();

            for (Enemy enemy : enemyArray)
            {
                if (!enemy.isDead() && player.checkEnemyCollision(enemy)) //Player dies
                    gameOver(true, Enums.DeathReason.KILLED_BY_ENEMY);
            }
        }



        //Updates enemy
        for (Enemy enemy : enemyArray)
            enemy.update(delta);


        for (Enemy enemy : enemyArray)
        {
            for (Bullet bullet : bulletArray)
            {
                if(!enemy.isDead() && (bullet.getEnemy() == null || !bullet.getEnemy().equals(enemy))
                        && enemy.checkBulletCollision(bullet))
                //TODO: Change this IF - every bullet should have a variable about where it belongs
                {   //Enemy dies here
                    splashArray.add(new Splash(enemy.position, enemy.getEntityType()));

                    for(int k = 0; k < MathUtils.random(difficulty.ENEMY_DROPPED_PICKBULLETS_MIN, difficulty.ENEMY_DROPPED_PICKBULLETS_MAX); k++)
                    {
                        PickupBullet pickupBullet = new PickupBullet(this);//Pools.obtain(PickupBullet.class);
                        pickupBullet.init(
                                new Vector2(enemy.getCenterPosition().x, enemy.getCenterPosition().y),
                                new Vector2(MathUtils.random(-Constants.PICKUPBULLET_VELOCITY_X_MAX, Constants.PICKUPBULLET_VELOCITY_X_MAX),
                                        MathUtils.random(Constants.PICKUPBULLET_VELOCITY_Y_MIN, Constants.PICKUPBULLET_VELOCITY_Y_MAX)),
                                enemy.getEntityType());
                        pickupBulletArray.add(pickupBullet);
                    }
                    if(MathUtils.random() < difficulty.CHANCE_FOR_DROPPING_COIN)
                    {
                        Coin spawnedCoin = new Coin(
                                new Vector2(enemy.getCenterPosition().x, enemy.getCenterPosition().y),
                                new Vector2(MathUtils.random(-Constants.PICKUPBULLET_VELOCITY_X_MAX, Constants.PICKUPBULLET_VELOCITY_X_MAX),
                                        MathUtils.random(Constants.PICKUPBULLET_VELOCITY_Y_MIN, Constants.PICKUPBULLET_VELOCITY_Y_MAX)), this);
                        coinArray.add(spawnedCoin);
                    }

                    gameScreen.startShockwave(new Vector2(enemy.getCenterPosition().x/viewport.getWorldWidth(), enemy.getCenterPosition().y/viewport.getWorldHeight()));
                    ChromaticAberration.start();
                    chaseCam.shake(Constants.CAMERA_SHAKE_AFTER_DEATH_LENGTH);
                    enemy.die(true);
                    if(gameScreen.getGameMain().gameOnAndroid && Enums.Upgrade.currentUpgrade == Enums.Upgrade.BOUNCING_OFF_WALLS)
                    {
                        if(bullet.killedEnemy()) gameScreen.getGameMain().androidHandler.unlockAchievement(Achievements.BULLSEYE);
                    }
                    if(!Constants.bulletBouncingOffEnemies) bullet.die();

                    if(levelState == LevelState.TUTORIAL_3) levelState.addCompletedObjective();
                    killedEnemyElapsedTime = 0;
                    levelUpdateState = Enums.LevelUpdateState.STOPPED;

                    if(bullet.getEnemy() == null) Achievements.enemiesKilled++;
                    if(gameScreen.getGameMain().gameOnAndroid && Enums.Upgrade.currentUpgrade == Enums.Upgrade.NORMAL && Achievements.enemiesKilled >= 35)
                        gameScreen.getGameMain().androidHandler.unlockAchievement(Achievements.BASIC_SURVIVAL);
                }
            }
        }
        enemyArray.begin();
        for(int i = 0; i < enemyArray.size; i++)
        {
            if(!enemyArray.get(i).isActive())
                enemyArray.removeIndex(i);
        }
        enemyArray.end();

        platformArray.begin();
        for(int i = 0; i < platformArray.size; i++)
        {
            if(!platformArray.get(i).isActive())
                platformArray.removeIndex(i);
        }
        platformArray.end();


        //Updates bullet

        for(Bullet bullet : bulletArray)
        {
            bullet.update(delta);
            if(Constants.bulletBouncingOffPlatforms)
            {
                for (Platform platform : platformArray)
                    bullet.checkPlatformCollision(platform);
            }

            if(bullet.isOutOfScreen())
                bullet.die();
        }
        bulletArray.begin();
        for(int i = 0; i < bulletArray.size; i++)
        {
            if(!bulletArray.get(i).isActive())
                bulletArray.removeIndex(i);
        }
        bulletArray.end();


        for(Platform platform : platformArray)
            platform.update(delta);


        pickupBulletArray.begin();
        for(int i = 0; i < pickupBulletArray.size; i++)
        {
            if(pickupBulletArray.get(i).isInsidePlayerSafeZone(player.getSafeZone()))
            {
                player.addBullet(pickupBulletArray.get(i));
                pickupBulletArray.get(i).deActivate();
                if(levelState == LevelState.TUTORIAL_4) levelState.addCompletedObjective();
            }

            if(!pickupBulletArray.get(i).isActive())
                pickupBulletArray.removeIndex(i);
        }
        pickupBulletArray.end();

        for(PickupBullet pickupBullet : pickupBulletArray)
        {
            pickupBullet.update(delta);

            if (pickupBullet.getOwner() == Enums.Owner.ENEMY)
                player.checkPickupBulletCollision(pickupBullet);
        }

        for(PickupBullet pickupBullet : pickupBulletArray)
        {
            for (Platform platform : platformArray)
                pickupBullet.checkPlatformCollision(platform);
        }

        for(Coin coin : coinArray)
        {
            if(player.checkCoinCollision(coin))
            {
                PlaySounds.pickUpCoin(1f);
                Enums.Upgrade.coins++;
                Enums.Upgrade.save();
                coin.setActive(false);
                gameHUD.pickedUpCoin();
                Achievements.coinsCollected++;
                if(gameScreen.getGameMain().gameOnAndroid && Enums.Upgrade.currentUpgrade == Enums.Upgrade.SMALLER
                        && Achievements.coinsCollected >= 10)
                {
                    gameScreen.getGameMain().androidHandler.unlockAchievement(Achievements.GREEDY);
                }
            }
            coin.update(delta);
        }

        coinArray.begin();
        for(int i = 0; i < coinArray.size; i++)
        {
            if(!coinArray.get(i).isActive()) coinArray.removeIndex(i);
        }
        coinArray.end();
    }

    public void updateScore(int newScore)
    {
        if(levelState != LevelState.NORMAL) return;

        if(newScore > score)
        {
            score = newScore;
            if(score > difficulty.POINTS_TO_NEXT_DIFFICULTY)
                difficulty = Enums.Difficulty.getNextDifficulty(difficulty);
        }

        if(score > Enums.Upgrade.currentUpgrade.highScore)
        {
            Enums.Upgrade.currentUpgrade.highScore = score;
            Enums.Upgrade.save();
        }

        if(score == 100 && gameScreen.getGameMain().gameOnAndroid && Enums.Upgrade.currentUpgrade == Enums.Upgrade.BIGGER
                && Achievements.enemiesKilled <= 10)
        {
            gameScreen.getGameMain().androidHandler.unlockAchievement(Achievements.PACIFIST);
        }

        if(score == 150 && gameScreen.getGameMain().gameOnAndroid && Enums.Upgrade.currentUpgrade == Enums.Upgrade.LOW_GRAVITY
                && Achievements.platformsTouched <= 1)
        {
            gameScreen.getGameMain().androidHandler.unlockAchievement(Achievements.I_CAN_FLY);
        }

        if(score == 75 && gameScreen.getGameMain().gameOnAndroid && Enums.Upgrade.currentUpgrade == Enums.Upgrade.REVERSED_GRAVITY
                && Achievements.shotUp == false)
        {
            gameScreen.getGameMain().androidHandler.unlockAchievement(Achievements.FAST_RIDE);
        }
    }

    public void gameOver(boolean playerDead, Enums.DeathReason deathReason)
    {
        if(playerDead && !player.isDead())
        {
            player.die();
            splashArray.add(player.getSplash());
            chaseCam.shake(Constants.CAMERA_SHAKE_AFTER_DEATH_LENGTH);
        }
        if(gameScreen.getGameState() != Enums.GameState.DEAD)
        {
            if(gameScreen.getGameMain().gameOnAndroid)
            {
                gameScreen.getGameMain().androidHandler.submitScore(score, Enums.Upgrade.currentUpgrade.getGameChangerID());
                if(score >= 100) gameScreen.getGameMain().androidHandler.unlockAchievement(Achievements.GETTING_SOMEWHERE);
                int scoresForABSOLUTE_MASTERY = 0;
                for(Enums.Upgrade upgrade : Enums.Upgrade.values())
                {
                    if(upgrade.highScore >= 200) scoresForABSOLUTE_MASTERY++;
                }
                if(scoresForABSOLUTE_MASTERY >= 6) gameScreen.getGameMain().androidHandler.unlockAchievement(Achievements.ABSOLUTE_MASTERY);
            }
            gameScreen.gameOver(score, deathReason);
        }
    }

    public void debugRender(ShapeRenderer renderer)
    {
        viewport.apply();
        renderer.setProjectionMatrix(viewport.getCamera().combined);

        for(Enemy enemy : enemyArray)
            enemy.debugRender(renderer);

        player.debugRender(renderer);

        for(Platform platform : platformArray)
            platform.debugRender(renderer);

        for(Bullet bullet : bulletArray)
            bullet.debugRender(renderer);
    }

    public void render(SpriteBatch batch)
    {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        background.render(batch);


        int nrOfDrawings;
        if(ChromaticAberration.isActive()) nrOfDrawings = 4;
        else nrOfDrawings = 1;

        for(int i = 0; i < nrOfDrawings; i++)
        {
            float currentOffset = ChromaticAberration.getOffset(i);
            drawColor = ChromaticAberration.getOffsetColor(i, drawColor);

            if(i == 0) batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            else
                batch.setBlendFunction(ChromaticAberration.srcBlending, ChromaticAberration.dstBlending);

            batch.setColor(drawColor);
            batch.begin();

            for (Splash splash : splashArray)
                splash.render(batch, currentOffset);

            for (Platform platform : platformArray)
                platform.render(batch, currentOffset);

            for (PickupBullet pickupBullet : pickupBulletArray)
                pickupBullet.render(batch, currentOffset);

            for (Enemy enemy : enemyArray)
                enemy.render(batch, currentOffset);

            for (Coin coin : coinArray)
                coin.render(batch, currentOffset);

            if (player.isActive())
                player.render(batch, currentOffset);
            batch.end();
        }
        if(ChromaticAberration.isActive())  batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.setColor(1, 1, 1, 1);
        batch.begin();
        for (Bullet bullet : bulletArray)
            bullet.render(batch, 0);
        batch.end();

        batch.begin();
        HUDviewport.apply(true);
        batch.setProjectionMatrix(HUDviewport.getCamera().combined);

        if(gameScreen.getGameState() == Enums.GameState.PLAYING)
        {
            if(levelState == LevelState.NORMAL)
                gameHUD.update(score);
            else
                gameHUD.update();

            gameHUD.render(batch);
        }
        batch.end();
    }

    public void resize(int width, int height)
    {
        viewport.update(width, height);
        HUDviewport.update(width, height, true);
    }

    public Viewport getViewport(){return viewport;}

    public void shootBullet(Bullet bullet, Vector2 velocity)
    {
        bullet.shootBullet(velocity);
        bulletArray.add(bullet);
    }

    public GameHUD getGameHUD() {return gameHUD;}
    public int getCurrentScore() {return score;}

    public ParticleEffectPool getGreenTrailEffectPool(){return poolManager.getGreenTrailEffectPool();}
    public ParticleEffectPool getPurpleTrailEffectPool(){return poolManager.getPurpleTrailEffectPool();}
    public ParticleEffectPool getRedTrailEffectPool(){return poolManager.getRedTrailEffectPool();}

    public DelayedRemovalArray getBullets() {return bulletArray;}
    public void setBullets(DelayedRemovalArray<Bullet> array) {this.bulletArray = array;}

    public DelayedRemovalArray getPlatforms() {return platformArray;}
    public void setPlatforms(DelayedRemovalArray<Platform> array) {this.platformArray = array;}

    public DelayedRemovalArray<PickupBullet> getPickUpBulletArray() {return pickupBulletArray;}

    public Enemy getClosestEnemy(float height)
    {
        if(enemyArray.size == 0) return null;
        Enemy closestEnemy = enemyArray.get(0);
        float closestDistance = Math.abs(enemyArray.get(0).getCenterPosition().y - height);
        for(Enemy enemy : enemyArray)
        {
            if(Math.abs(enemy.getCenterPosition().y - height) < closestDistance)
            {
                closestEnemy = enemy;
                closestDistance = Math.abs(enemy.getCenterPosition().y - height);
            }
        }
        return closestEnemy;
    }
    public GameScreen getGameScreen() {return gameScreen;}

    public DelayedRemovalArray getEnemies() {return enemyArray;}
    public void setEnemies(DelayedRemovalArray<Enemy> array) {this.enemyArray = array;}

    public DelayedRemovalArray getSplashes() {return splashArray;}
    public void setSplashes(DelayedRemovalArray<Splash> array) {this.splashArray = array;}

    public Player getPlayer() {return player;}
    public void setPlayer(Player player) {this.player = player;}

    public Enums.Difficulty getDifficulty() {return difficulty;}

    public ChaseCam getChaseCam() {return chaseCam;}

    public LevelState getLevelState(){return levelState;}
    public enum LevelState
    {
        NORMAL(Enums.Difficulty.DIFFICULTY_0, "", 0, new float[]{}, 0),
        ANIMATION_1(Enums.Difficulty.DIFFICULTY_TUTORIAL_1, "[#50be4b]Touch [#ffffff]the screen to shoot a bullet in a given direction.",
                1, new float[]{1f,200,200,   2f,400,400,   3f}, 0),
        TUTORIAL_1(Enums.Difficulty.DIFFICULTY_TUTORIAL_1, "Try shooting 3 times.",
                3, new float[]{}, 1f),
        ANIMATION_2(Enums.Difficulty.DIFFICULTY_TUTORIAL_2, "When you shoot you also [#50be4b]recoil [#ffffff]in the opposite direction.",
                1, new float[]{1f, 50, -50,  2f, 300, 0,  3f}, 0),
        TUTORIAL_2(Enums.Difficulty.DIFFICULTY_TUTORIAL_2, "Try to [#f2ac62]land [#ffffff]on that golden platform by shooting [#50be4b]down[#ffffff].",
                1, new float[]{}, 1f),
        ANIMATION_3(Enums.Difficulty.DIFFICULTY_TUTORIAL_3, "On your way to the top you will encounter [#106cf4ff]enemies[#ffffff].",
                1, new float[]{0.5f, 380, 300,  2f}, 0),
        TUTORIAL_3(Enums.Difficulty.DIFFICULTY_TUTORIAL_3, "Try [#106cf4ff]shooting [#ffffff]one of them!",
                1, new float[]{}, 1f),
        ANIMATION_4(Enums.Difficulty.DIFFICULTY_TUTORIAL_4, "Pick up [#106cf4ff]bullets [#ffffff]from fallen enemies, to keep shooting.",
                1, new float[]{0.5f, 380, 300,  1.5f,150,-50,   2.8f,250,500,  4f}, 0),
        TUTORIAL_4(Enums.Difficulty.DIFFICULTY_TUTORIAL_4, "Try gathering 8 [#106cf4ff]bullets[#ffffff] from enemies.",
                8, new float[]{}, 0f);

        public final float TUTORIAL_TIME_AFTER_COMPLETION_TO_NEXT_TUTORIAL;
        public final String textLabel;
        public final Enums.Difficulty difficulty;

        public final float [] animationTouchTimePosition;

        private boolean objectiveComplete;
        private int objectivesComplete;
        private int objectivesToComplete;
        private long objectiveCompletionTime;

        LevelState(Enums.Difficulty difficulty, String textLabel, int objectivesToComplete, float[] animationTouchTimePosition,
                   float timeAfterCompletionToNextTutorial)
        {
            this.difficulty = difficulty;
            this.textLabel = textLabel;
            this.objectivesToComplete = objectivesToComplete;
            this.animationTouchTimePosition = animationTouchTimePosition;
            this.TUTORIAL_TIME_AFTER_COMPLETION_TO_NEXT_TUTORIAL = timeAfterCompletionToNextTutorial;
            init();
        }

        public void init()
        {
            objectiveComplete = false;
            objectivesComplete = 0;
        }
        public LevelState getNextTutorial()
        {
            init();
            if(this.equals(ANIMATION_1)) return TUTORIAL_1;
            if(this.equals(TUTORIAL_1)) return ANIMATION_2;
            if(this.equals(ANIMATION_2)) return TUTORIAL_2;
            if(this.equals(TUTORIAL_2)) return ANIMATION_3;
            if(this.equals(ANIMATION_3)) return TUTORIAL_3;
            if(this.equals(TUTORIAL_3)) return ANIMATION_4;
            if(this.equals(ANIMATION_4)) return TUTORIAL_4;
            else return NORMAL;
        }

        public boolean isObjectiveComplete(){return objectiveComplete;}
        public int getCompletedObjectives(){return objectivesComplete;}
        public int getObjectivesToComplete(){return objectivesToComplete;}
        public long getObjectiveCompletionTime(){return objectiveCompletionTime;}

        public void addCompletedObjective()
        {
            objectivesComplete++;
            if(objectivesComplete == objectivesToComplete)
            {
                objectiveComplete = true;
                objectiveCompletionTime = TimeUtils.nanoTime();
            }
        }
    }
}
