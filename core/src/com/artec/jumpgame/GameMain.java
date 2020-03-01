package com.artec.jumpgame;

import com.artec.jumpgame.screens.GameScreen;
import com.artec.jumpgame.screens.LoadingScreen;
import com.artec.jumpgame.utils.Assets;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameMain extends Game
{
	public final boolean gameOnAndroid;
	public final AndroidHandler androidHandler;

	private ShapeRenderer renderer;
	private SpriteBatch batch;
	private AssetManager assetManager;

	private GameScreen gameScreen;
	private LoadingScreen loadingScreen;

	private boolean loaded;

	public GameMain()
	{
		gameOnAndroid = false;
		androidHandler = null;
	}

	public GameMain(AndroidHandler androidHandler)
	{
		this.androidHandler = androidHandler;
		gameOnAndroid = true;
	}

	@Override
	public void create ()
	{
		loaded = false;

		renderer = new ShapeRenderer();
		renderer.setAutoShapeType(true);
		batch = new SpriteBatch();

		assetManager = new AssetManager();
		Assets.instance.queueLoading(assetManager);
		loadingScreen = new LoadingScreen(this);
		loadingScreen.setDelayAfterCompletion(true);
		this.setScreen(loadingScreen);
	}

	public void initGameScreen()
	{
		if(!loaded)
		{
			loaded = true;
			gameScreen = new GameScreen(this, batch, renderer);
		}
		this.setScreen(gameScreen);
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		renderer.dispose();
		Assets.instance.dispose();
	}

	@Override
	public void pause()
	{
		if(loadingScreen.getLoadingState() == LoadingScreen.LoadingState.FINISHED)
		gameScreen.pause();
	}

	@Override
	public void resume()
	{
		super.resume();
		//assetManager = new AssetManager();
		loadingScreen.setDelayAfterCompletion(false);
		Assets.instance.queueLoading(assetManager);
		this.setScreen(loadingScreen);
	}

	public SpriteBatch getSpriteBatch() {return batch;}
}
