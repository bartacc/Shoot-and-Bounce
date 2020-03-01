package com.artec.jumpGame.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.artec.jumpgame.Achievements;
import com.artec.jumpgame.AndroidHandler;
import com.artec.jumpgame.GameMain;
import com.artec.jumpgame.android.R;
import com.artec.jumpgame.utils.Enums;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
/*
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;*/

import java.io.File;

public class AndroidLauncher extends AndroidApplication implements AndroidHandler{
	private static final String TAG = "AndroidLauncher";

	//private GameHelper gameHelper;
	private final static int requestCode = 1;
	private final int SHARE = 1;

	//Handler handler = new Handler()
	//{
	//	@Override
	//	public void handleMessage(Message msg)
	//	{
	//		switch(msg.what)
	//		{
	//			case SHARE:
	//				Intent shareIntent = new Intent();
	//				shareIntent.setAction(Intent.ACTION_SEND);
	//				shareIntent.putExtra(Intent.EXTRA_TEXT, "I scored "+msg.arg1+
	//						" points in Shoot and Bounce. Check it out: https://play.google.com/store/apps/details?id=com.artec.jumpgame.android");
	//
	//				File file = new File(getCachePath()+"/screenshot.png");
	//				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
	//
	//				shareIntent.setType("*/*");
	//				startActivity(Intent.createChooser(shareIntent, "Share score to: "));
	//				break;
	//		}
	//	}
	//};

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);

		/*gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);

		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
		{
			@Override
			public void onSignInFailed(){ }

			@Override
			public void onSignInSucceeded(){ }
		};

		gameHelper.setup(gameHelperListener);*/

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useImmersiveMode = true;

		initialize(new GameMain(this), config);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		//gameHelper.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		//gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		//gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	public String getCachePath() {return getExternalFilesDir(null).toString();}

	@Override
	public void signIn()
	{
		/*try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
		}*/
	}

	@Override
	public void signOut()
	{
		/*try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.signOut();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
		}*/
	}

	@Override
	public void showLeaderboards()
	{
		/*if(isSignedIn())
		{
			startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(
					gameHelper.getApiClient()), requestCode);
		}
		else signIn();*/
	}

	@Override
	public void submitScore(int score, int gameChanger)
	{
	/*	if (isSignedIn() == true)
		{
			String leaderboardName;
			switch(gameChanger)
			{
				case NORMAL_GAME_CHANGER:
					leaderboardName = getString(R.string.leaderboard_basic_blob_high_score);
					break;
				case BOUNCY_BULLET_GAME_CHANGER:
					leaderboardName = getString(R.string.leaderboard_bouncy_bullet_high_score);
					break;
				case BIGGER_BLOB_GAME_CHANGER:
					leaderboardName = getString(R.string.leaderboard_bigger_blob_high_score);
					break;
				case LOW_GRAVITY_GAME_CHANGER:
					leaderboardName = getString(R.string.leaderboard_low_gravity_high_score);
					break;
				case SMALLER_BLOB_GAME_CHANGER:
					leaderboardName = getString(R.string.leaderboard_smaller_blob_high_score);
					break;
				case REVERSED_GRAVITY_GAME_CHANGER:
					leaderboardName = getString(R.string.leaderboard_reversed_gravity_high_score);
					break;
				default: leaderboardName = null;

			}
			if(leaderboardName != null)
			{
				Games.Leaderboards.submitScore(gameHelper.getApiClient(),
						leaderboardName, score);
			}
		}*/
	}

	@Override
	public void showAchievements()
	{
	/*	if(isSignedIn())
		{
			startActivityForResult(Games.Achievements.getAchievementsIntent(
					gameHelper.getApiClient()), requestCode);
		}
		else signIn();*/
	}

	@Override
	public void unlockAchievement(Achievements achievement)
	{
		/*if(isSignedIn())
		{
			String achievementID;
			switch (achievement)
			{
				case GETTING_SOMEWHERE:
					achievementID = getString(R.string.achievement_getting_somewhere___);
					break;
				case ABSOLUTE_MASTERY:
					achievementID = getString(R.string.achievement_absolute_mastery);
					break;
				case BASIC_SURVIVAL:
					achievementID = getString(R.string.achievement_basic_survival);
					break;
				case BULLSEYE:
					achievementID = getString(R.string.achievement_bullseye);
					break;
				case PACIFIST:
					achievementID = getString(R.string.achievement_pacifist);
					break;
				case I_CAN_FLY:
					achievementID = getString(R.string.achievement_i_can_fly);
					break;
				case GREEDY:
					achievementID = getString(R.string.achievement_greedy);
					break;
				case FAST_RIDE:
					achievementID = getString(R.string.achievement_fast_ride);
					break;
				default:
					achievementID = null;
			}
			if (achievementID != null)
				Games.Achievements.unlock(gameHelper.getApiClient(), achievementID);
		}*/
	}

	@Override
	public boolean isSignedIn()
	{
		return false;
		//return gameHelper.isSignedIn();
	}

	public void share(int score)
	{
		/*
		Message message = new Message();
		message.what = SHARE;
		message.arg1 = score;
		message.arg2 = score;
		handler.sendMessage(message);
		*/
	}

	public void boughtGameChanger()
	{
		for(Enums.Upgrade upgrade : Enums.Upgrade.values())
		{
			String bought;
			if(upgrade.bought) bought = "true";
			else bought = "false";
		}
	}

	@Override
	public void showAds(boolean show) {
		//handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}
}
