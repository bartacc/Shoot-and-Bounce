package com.artec.jumpgame.level;

import com.artec.jumpgame.entities.FlyingEnemy;
import com.artec.jumpgame.entities.Enemy;
import com.artec.jumpgame.entities.Player;
import com.artec.jumpgame.entities.ShootingEnemy;
import com.artec.jumpgame.entities.WalkingEnemy;
import com.artec.jumpgame.objects.BrokenPlatform;
import com.artec.jumpgame.objects.FloatingPlatform;
import com.artec.jumpgame.objects.Platform;
import com.artec.jumpgame.objects.Splash;
import com.artec.jumpgame.utils.Constants;
import com.artec.jumpgame.utils.Enums;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by bartek on 03.06.16.
 */
public class LevelSpawner
{
    private Level level;
    private Viewport viewport;
    private DelayedRemovalArray<com.artec.jumpgame.objects.Platform> platformArray;
    private DelayedRemovalArray<Enemy> enemyArray;
    private DelayedRemovalArray<Splash> splashArray;
    private Player player;
    private Enums.Difficulty difficulty;

    private float minHeight; //Height below witch platforms are removed and player dies
    private boolean ableToDeletePlatforms; //If false platforms are not deleted at all
    private float currentPlatformTop; //Where to spawn next platform on Y axis
    private float lastPlatformTop; //What's the top of last spawned platform

    private int platformsSpawnedThisGame;
    private float platformsWithoutEnemy; //Amount of spawned platforms in row that didn't have enemy on it
    private int enemiesInRow; //Amount of enemies spawned in a row
    private PlatformType platformType; //Stores info about last spawned platform type and how many of those types were spawned in a row
    private EnemySpawnedThisFrame enemySpawnedThisFrame;

    public LevelSpawner(Level level)
    {
        this.level = level;
    }

    public void init(Level level)
    {
        this.platformArray = level.getPlatforms();
        this.enemyArray = level.getEnemies();
        this.splashArray = level.getSplashes();
        this.player = level.getPlayer();
        this.difficulty = level.getDifficulty();
        this.viewport = level.getViewport();

        //Entities
        for(Platform platform : platformArray) {platform.deActivate();}
        platformArray.clear();

        enemyArray = new DelayedRemovalArray<Enemy>();
        splashArray = new DelayedRemovalArray<Splash>();

        platformsSpawnedThisGame = 0;
        platformsWithoutEnemy = 0;
        enemiesInRow = 0;

        Platform.resetPlatformNr();
        minHeight = 0;
        ableToDeletePlatforms = false;
        currentPlatformTop = 0;
        lastPlatformTop = 0;
        platformType = PlatformType.NORMAL;
        enemySpawnedThisFrame = EnemySpawnedThisFrame.NONE;
        //Initialize platforms
        Platform startingPlatform = new Platform();

        startingPlatform.init(0, currentPlatformTop, (int)(viewport.getWorldWidth() / Constants.PLATFORM_TILE_WIDTH),
                Enums.PlatformType.START, Constants.PLATFORM_FRICTION_VELOCITY_SLOWDOWN, EnemySpawnedThisFrame.NONE);

        platformArray.add(startingPlatform);


        currentPlatformTop += difficulty.SPACE_BETWEEN_PLATFORMS_MIN;
        if(level.getLevelState() == Level.LevelState.ANIMATION_1 || level.getLevelState() == Level.LevelState.ANIMATION_2)
        {
            platformArray.add(spawnNormalPlatform(50, 6));  currentPlatformTop += 140;
            platformArray.add(spawnNormalPlatform(190, 5)); currentPlatformTop += 160;
            platformArray.add(spawnNormalPlatform(100, 5)); currentPlatformTop += 150;
            platformArray.add(spawnNormalPlatform(100, 5)); currentPlatformTop += 120;
            platformArray.add(spawnNormalPlatform(300, 3)); currentPlatformTop += 170;
            platformArray.add(spawnNormalPlatform(250, 4)); currentPlatformTop += 130;
            platformArray.add(spawnNormalPlatform(80, 5));  currentPlatformTop += 150;
            platformArray.add(spawnNormalPlatform(0, 7));   currentPlatformTop += 160;
        }
        if(level.getLevelState() == Level.LevelState.TUTORIAL_2)
        {
            for(int i = 0; i < 2; i++)
                spawnPlatform();
            float left = MathUtils.random(
                    viewport.getWorldWidth() / Constants.PLATFORM_TILE_WIDTH - difficulty.PLATFORM_MIN_NR_OF_TILES) * Constants.PLATFORM_TILE_WIDTH;
            float nrOfTiles = MathUtils.random(difficulty.PLATFORM_MIN_NR_OF_TILES, Math.min(
                            difficulty.PLATFORM_MAX_NR_OF_TILES,
                            viewport.getWorldWidth() / Constants.PLATFORM_TILE_WIDTH - left / Constants.PLATFORM_TILE_WIDTH));
            Platform goldPlatform = new Platform();
            goldPlatform.init(left, currentPlatformTop, (int)nrOfTiles,
                    Enums.PlatformType.GOLD_METAL, Constants.PLATFORM_FRICTION_VELOCITY_SLOWDOWN,
                    enemySpawnedThisFrame);
            platformArray.add(goldPlatform);
            lastPlatformTop = currentPlatformTop;
            currentPlatformTop += MathUtils.random(difficulty.SPACE_BETWEEN_PLATFORMS_MIN, difficulty.SPACE_BETWEEN_PLATFORMS_MAX);
        }
        if(level.getLevelState() == Level.LevelState.TUTORIAL_3)
        {
            for(int i = 0; i < 2; i++)
                spawnPlatform();
            Platform enemyPlatform = spawnPlatform();
            enemyArray.add(new WalkingEnemy(enemyPlatform, Enums.EntityType.ENEMY_BLUE, level));
        }
        if(level.getLevelState() == Level.LevelState.ANIMATION_3 || level.getLevelState() == Level.LevelState.ANIMATION_4)
        {
            platformArray.add(spawnNormalPlatform(100, 6));
            currentPlatformTop += 160;
            Platform enemyPlatform = spawnNormalPlatform(180, 5);
            Enemy enemy = new WalkingEnemy(enemyPlatform, Enums.EntityType.ENEMY_BLUE, level);
            enemyArray.add(enemy);
            platformArray.add(enemyPlatform);
            currentPlatformTop += 180; platformArray.add(spawnNormalPlatform(50, 5));
            currentPlatformTop += 150; platformArray.add(spawnNormalPlatform(300, 3));
            lastPlatformTop = currentPlatformTop;
            currentPlatformTop += 150;
        }
        if(level.getLevelState() != Level.LevelState.TUTORIAL_2)
        {
            while (lastPlatformTop - platformArray.get(0).height < level.getChaseCam().getPositionCenter().y + viewport.getWorldHeight())
                spawnPlatform();
        }

        setValues();
    }

    public void update()
    {
        this.platformArray = level.getPlatforms();
        this.enemyArray = level.getEnemies();
        this.splashArray = level.getSplashes();
        this.player = level.getPlayer();
        this.difficulty = level.getDifficulty();
        this.viewport = level.getViewport();

        difficulty = level.getDifficulty();
        if(player.position.y > difficulty.HEIGHT_TO_START_DELETING_PLATFORMS) ableToDeletePlatforms = true;

        for(Platform platform : platformArray)
        {
            if(platform.top < level.getChaseCam().getPositionCenter().y - viewport.getWorldHeight() * 1.5f && ableToDeletePlatforms)
            {
                if(platform.top > minHeight) minHeight = platform.top;
                platform.deActivate();
            }
        }


        for(Enemy enemy : enemyArray)
        {
            if(enemy.position.y < level.getChaseCam().getPositionCenter().y - viewport.getWorldHeight())
                enemy.die(false);
        }


        splashArray.begin();
        for(int i = 0; i < splashArray.size; i++)
        {
            if(splashArray.get(i).getPosition().y < level.getChaseCam().getPositionCenter().y - viewport.getWorldHeight())
                splashArray.removeIndex(i);
        }
        splashArray.end();

        if(lastPlatformTop - platformArray.get(0).height < level.getChaseCam().getPositionCenter().y + viewport.getWorldHeight()/2 &&
                level.getLevelState() != Level.LevelState.TUTORIAL_2)
        {
            spawnPlatform();
        }
        isPlayerOutOfBounds();
    }

    private Platform spawnPlatform()
    {
        platformsSpawnedThisGame++;
        float left = MathUtils.random(
                viewport.getWorldWidth() / Constants.PLATFORM_TILE_WIDTH - difficulty.PLATFORM_MIN_NR_OF_TILES) * Constants.PLATFORM_TILE_WIDTH;
        float nrOfTiles = MathUtils.random(
                difficulty.PLATFORM_MIN_NR_OF_TILES,
                Math.min(
                        difficulty.PLATFORM_MAX_NR_OF_TILES,
                        viewport.getWorldWidth() / Constants.PLATFORM_TILE_WIDTH - left / Constants.PLATFORM_TILE_WIDTH));


        enemySpawnedThisFrame = EnemySpawnedThisFrame.NONE;
        float spaceBetweenPlatforms = MathUtils.random(difficulty.SPACE_BETWEEN_PLATFORMS_MIN, difficulty.SPACE_BETWEEN_PLATFORMS_MAX);
        if((MathUtils.random() < difficulty.SPAWN_RATE_ENEMY || platformsWithoutEnemy > difficulty.MAX_PLATFORMS_WITHOUT_ENEMY)
                && enemiesInRow < difficulty.MAX_ENEMIES_IN_ROW && platformsSpawnedThisGame > Constants.PLATFORMS_WITHOUT_ENEMY_FROM_BOTTOM)
        {
            float rolledSpawnRate = MathUtils.random();
            if(rolledSpawnRate < difficulty.SPAWN_RATE_SHOOTING_ENEMY)

                enemySpawnedThisFrame = EnemySpawnedThisFrame.SHOOTING;

            else if(rolledSpawnRate < difficulty.SPAWN_RATE_SHOOTING_ENEMY + difficulty.SPAWN_RATE_FLYING_ENEMY)
                enemySpawnedThisFrame = EnemySpawnedThisFrame.FLYING;

            else
                enemySpawnedThisFrame = EnemySpawnedThisFrame.NORMAL;


            platformsWithoutEnemy = 0;
            enemiesInRow++;
        }
        else
        {
            platformsWithoutEnemy++;
            enemiesInRow = 0;
        }

        float rolledPlatformSpawnRate = MathUtils.random();
        Platform platform;
        if(PlatformType.platformsInRow < PlatformType.maxPlatformsInRow)
        {
            switch(platformType)
            {
                case NORMAL:
                    platform = spawnNormalPlatform(left, nrOfTiles);
                    break;
                case FLOATING:
                    platform = spawnFloatingPlatform(left, nrOfTiles);
                    break;
                case SNOWY:
                    platform = spawnSnowyPlatform(left, nrOfTiles);
                    break;
                default:
                    platform = spawnNormalPlatform(left, nrOfTiles);
            }
            PlatformType.platformsInRow++;
        }
        else if(rolledPlatformSpawnRate < difficulty.SPAWN_RATE_SNOW_PLATFORM) //Spawn Snow platform
        {
            platformType = PlatformType.SNOWY;
            PlatformType.maxPlatformsInRow = MathUtils.random(difficulty.MIN_PLATFORMS_IN_ROW, difficulty.MAX_PLATFORMS_IN_ROW);
            PlatformType.platformsInRow = 1;

            platform = spawnSnowyPlatform(left, nrOfTiles);
        }
        else if(rolledPlatformSpawnRate < difficulty.SPAWN_RATE_SNOW_PLATFORM + difficulty.SPAWN_RATE_FLOATING_PLATFORM) //Spawn floating platform
        {
            platformType = PlatformType.FLOATING;
            PlatformType.maxPlatformsInRow = MathUtils.random(difficulty.MIN_PLATFORMS_IN_ROW, difficulty.MAX_PLATFORMS_IN_ROW);
            PlatformType.platformsInRow = 1;

            platform = spawnFloatingPlatform(left, nrOfTiles);
        }
        else //Spawn basic platform
        {
            platformType = PlatformType.NORMAL;
            PlatformType.maxPlatformsInRow = MathUtils.random(difficulty.MIN_PLATFORMS_IN_ROW, difficulty.MAX_PLATFORMS_IN_ROW);
            PlatformType.platformsInRow = 1;

            platform = spawnNormalPlatform(left, nrOfTiles);
        }

        platformArray.add(platform);

        if(level.getLevelState() == Level.LevelState.TUTORIAL_4 || level.getLevelState() == Level.LevelState.NORMAL)
        {
            switch (enemySpawnedThisFrame)
            {
                case NORMAL:
                    enemyArray.add(new WalkingEnemy(platform, Enums.EntityType.ENEMY_BLUE, level));
                    break;

                case FLYING:
                    float enemyInitialHeight = platform.top + MathUtils.random(
                            spaceBetweenPlatforms * Constants.ENEMY_FLYING_SPAWN_HEIGHT_MIN, spaceBetweenPlatforms * Constants.ENEMY_FLYING_SPAWN_HEIGHT_MAX);
                    enemyArray.add(new FlyingEnemy(Enums.EntityType.ENEMY_PURPLE, enemyInitialHeight, level));
                    break;

                case SHOOTING:
                    enemyArray.add(new ShootingEnemy(platform, level, Enums.EntityType.ENEMY_RED));
                    break;
            }
        }

        lastPlatformTop = currentPlatformTop;
        currentPlatformTop += spaceBetweenPlatforms;

        return platform;
    }

    private Platform spawnNormalPlatform(float left, float nrOfTiles)
    {
        Platform platform;
        if(MathUtils.random() < difficulty.CHANCE_FOR_BROKEN_PLATFORM && enemySpawnedThisFrame == EnemySpawnedThisFrame.NONE)
        {//Spawn broken normal platform
            platform = new BrokenPlatform();
            platform.init(left, currentPlatformTop, (int)nrOfTiles,
                    Enums.PlatformType.PURPLE_METAL, Constants.PLATFORM_FRICTION_VELOCITY_SLOWDOWN,
                    enemySpawnedThisFrame);
        }
        else //Spawn normal basic platform
        {
            platform = new Platform();
            platform.init(left, currentPlatformTop, (int) nrOfTiles,
                    Enums.PlatformType.PURPLE_METAL, Constants.PLATFORM_FRICTION_VELOCITY_SLOWDOWN,
                    enemySpawnedThisFrame);
        }
        return platform;
    }

    private Platform spawnFloatingPlatform(float left, float nrOfTiles)
    {
        Platform platform;
        platform = new FloatingPlatform(level);
        platform.init(left, currentPlatformTop, (int)nrOfTiles,
                Enums.PlatformType.GOLD_METAL, Constants.PLATFORM_FRICTION_VELOCITY_SLOWDOWN,
                enemySpawnedThisFrame);
        return platform;
    }

    private Platform spawnSnowyPlatform(float left, float nrOfTiles)
    {
        Platform platform;
        if(MathUtils.random() < difficulty.CHANCE_FOR_BROKEN_PLATFORM && enemySpawnedThisFrame == EnemySpawnedThisFrame.NONE)
        {//Spawn destroyed snow platform
            platform = new BrokenPlatform();
            platform.init(left, currentPlatformTop, (int)nrOfTiles,
                    Enums.PlatformType.SNOWY, Constants.PLATFORM_SNOW_FRICTION_VELOCITY_SLOWDOWN,
                    enemySpawnedThisFrame);
        }
        else //Spawn normal snow platform
        {
            platform = new Platform();
            platform.init(left, currentPlatformTop, (int) nrOfTiles,
                    Enums.PlatformType.SNOWY, Constants.PLATFORM_SNOW_FRICTION_VELOCITY_SLOWDOWN,
                    enemySpawnedThisFrame);
        }
        return platform;
    }

    private void isPlayerOutOfBounds()
    {
        if(!player.isDead() && player.position.y + player.getHeight() < minHeight)
        {
            level.getChaseCam().setFollowing(false);
            level.gameOver(false, Enums.DeathReason.FALLEN);
        }
    }

    private void setValues()
    {
        level.setPlayer(player);
        level.setEnemies(enemyArray);
        level.setPlatforms(platformArray);
        level.setSplashes(splashArray);
    }

    private enum PlatformType
    {
        NORMAL, SNOWY, FLOATING;

        public static int platformsInRow = 0;
        public static int maxPlatformsInRow = 0;
    }

    public enum EnemySpawnedThisFrame
    {
        NONE, NORMAL, SHOOTING, FLYING
    }
}
