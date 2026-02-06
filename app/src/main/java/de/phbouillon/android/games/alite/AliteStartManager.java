package de.phbouillon.android.games.alite;

/* Alite - Discover the Universe on your Favorite Android Device
 * Copyright (C) 2015 Philipp Bouillon
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful and
 * fun, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * http://http://www.gnu.org/licenses/gpl-3.0.txt.
 */

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import de.phbouillon.android.framework.impl.AndroidFileIO;

public class AliteStartManager extends Activity {
	public static final int ALITE_RESULT_CLOSE_ALL = 78615265;
		
	public static final String ALITE_STATE_FILE = "current_state.dat";
		
	private AndroidFileIO fileIO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		if (fileIO == null) {
			fileIO = new AndroidFileIO(this);
		}
		if (!AliteLog.isInitialized()) {
			AliteLog.initialize(fileIO);
		}
		AliteLog.d("AliteStartManager.onCreate", "onCreate begin");
		final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread paramThread, Throwable paramThrowable) {				
	            AliteLog.e("Uncaught Exception (AliteStartManager)", "Message: " + (paramThrowable == null ? "<null>" : paramThrowable.getMessage()), paramThrowable);
				if (oldHandler != null) {
					oldHandler.uncaughtException(paramThread, paramThrowable);
				} else {
					System.exit(2);
				}
			}
		});
		AliteLog.d("Alite Start Manager", "Alite Start Manager has been created.");
		Settings.load(fileIO);

		startGame();
		AliteLog.d("AliteStartManager.onCreate", "onCreate end");
	}

	private void startGame() {
		AliteLog.d("Alite Start Manager", "Loading Alite State");
		loadCurrentGame(fileIO);
	}
	
	private void startAliteIntro() {
		AliteLog.d("Alite Start Manager", "Starting INTRO!");
		Intent intent = new Intent(this, AliteIntro.class);
		intent.putExtra(Alite.LOG_IS_INITIALIZED, true);
		startActivityForResult(intent, 0);	
	}
	
	private void startAlite() {
		AliteLog.d("Alite Start Manager", "Starting Alite.");
		Intent intent = new Intent(this, Alite.class);
		intent.putExtra(Alite.LOG_IS_INITIALIZED, true);
		startActivityForResult(intent, 0);	
	}

	private void loadCurrentGame(AndroidFileIO fileIO) {
		try {
			if (fileIO.exists(ALITE_STATE_FILE)) {
				AliteLog.d("Alite Start Manager", "Alite state file exists. Opening it.");
				// State file exists, so open the first byte to check if the
				// Intro Activity or the Game Activity must be started.
				byte [] b = fileIO.readPartialFileContents(ALITE_STATE_FILE, 1);
				if (b == null || b.length != 1) {
					// Fallback in case of an error
					AliteLog.d("Alite Start Manager", "Reading screen code failed. b == " + (b == null ? "<null>" : b) + " -- " + (b == null ? "<null>" : b.length));
					startAlite();
				} else {
					// We used to parse the first byte here, to determine if we want to resume the intro.
					// However, if the game crashes right after the intro (probably because of the
					// Activity change), we don't have a fallback. Hence, we simply prohibit resuming
					// the intro...
					AliteLog.d("Alite Start Manager", "Saved screen code == " + ((int) b[0]) + " - starting game.");
					startAlite();
				}
			} else {
				AliteLog.d("Alite Start Manager", "No state file present: Starting intro.");
				startAliteIntro();			
			}			
		} catch (IOException e) {
			// Default to Intro...
			AliteLog.e("Alite Start Manager", "Exception occurred. Starting intro.", e);
			startAliteIntro();
		}
	}
	
	@Override
	protected void onPause() {
		AliteLog.d("AliteStartManager.onPause", "onPause begin");
		super.onPause();		
		AliteLog.d("AliteStartManager.onPause", "onPause end");
	}
		
	@Override
	protected void onResume() {
		AliteLog.d("AliteStartManager.onResume", "onResume begin");
		super.onResume();
		AliteLog.d("AliteStartManager.onResume", "onResume end");
	}
	
	@Override
	protected void onStop() {
		AliteLog.d("AliteStartManager.onStop", "onStop begin");
		super.onStop();
		AliteLog.d("AliteStartManager.onStop", "onStop end");
	}

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  if (resultCode == ALITE_RESULT_CLOSE_ALL) {
	    setResult(ALITE_RESULT_CLOSE_ALL);
	    finish();
	  }
	  super.onActivityResult(requestCode, resultCode, data);
	}
}
