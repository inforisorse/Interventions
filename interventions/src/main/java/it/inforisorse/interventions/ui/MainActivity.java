/*
 * Copyright (C) 2011,2012 Consulanza Informatica.
 * info@consulanza.it
 *
 * This file is part of Interventions Tracker.
 *
 *    Interventions Tracker is free software: you can redistribute it and/or
 *    modify it under the terms of the GNU General Public License as published
 *    by the Free Software Foundation, either version 3 of the License, or (at 
 *    your option) any later version.
 *
 *    Interventions Tracker is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General 
 *    Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Interventions Tracker. If not, see http://www.gnu.org/licenses/.
 *
 */
package it.inforisorse.interventions.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.marcoduff.util.aboutactivity.AboutActivity;

import it.inforisorse.interventions.db.DatabaseManager;

public class MainActivity extends Activity implements AboutActivity.OnStartupEulaListener, AboutActivity.OnStartupChangeLogListener {

	private static final String MY_AD_UNIT_ID = "a14f33c2ec77bb3";
	private static final String TEST_DEVICE_ID = "3D16E2F3A204C8861D9DC1748541F4ADC";

	private AdView adView;

	Button mBtn_interventions_add;
	Button mBtn_interventions_list;
	Button mBtn_interventions_export;
	Button mBtn_interventions_import;
	Button mBtn_interventions_delete_billed;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		AboutActivity.showStartupEula(this);
		AboutActivity.showStartupChangeLog(this);
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);
		setContentView(R.layout.main);
		setupControls();
		setupControlsEvents();
		setupAdMob();
	}
	
	@Override
	public void onDestroy() {
		// adView.destroy();
		super.onDestroy();
	}

	/**
	 * Setup - load main menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Setup - create UI controls
	 */
	public void setupControls() {
		mBtn_interventions_add = (Button) findViewById(R.id.btn_interventions_add);
		mBtn_interventions_list = (Button) findViewById(R.id.btn_interventions_list);
		mBtn_interventions_export = (Button) findViewById(R.id.btn_interventions_export);
		mBtn_interventions_import = (Button) findViewById(R.id.btn_interventions_import);
		mBtn_interventions_delete_billed = (Button) findViewById(R.id.btn_interventions_delete_billed);
	}

	/**
	 * Setup - assign UI controls events
	 */
	public void setupControlsEvents() {
		// ADD
		mBtn_interventions_add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startInterventionsAddActivity();
			}
		});
		// LIST
		mBtn_interventions_list.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startInterventionsListActivity();
			}
		});
		// EXPORT
		mBtn_interventions_export.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startInterventionsExportActivity();
			}
		});
		// IMPORT
		mBtn_interventions_import.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startInterventionsImportActivity();
			}
		});
		// DELETE BILLED
		mBtn_interventions_delete_billed.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startInterventionsDeleteBilledActivity();
			}
		});
	}
	/**
	 * Setup - assign main menu items' actions
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mnu_interventions_add:
				startInterventionsAddActivity();
				return true;
			case R.id.mnu_interventions_list:
				startInterventionsListActivity();
				return true;
			case R.id.mnu_interventions_export:
				startInterventionsExportActivity();
				return true;
			case R.id.mnu_interventions_import:
				startInterventionsImportActivity();
				return true;
			case R.id.mnu_interventions_delete_billed:
				startInterventionsDeleteBilledActivity();
				return true;
			case R.id.mnu_preferences:
				startPreferencesActivity();
				return true;
			case R.id.mnu_about:
				startAboutActivity();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startInterventionsAddActivity() {
		final Activity activity = this;
		Intent intent = new Intent(activity, InterventionsEditActivity.class);
		startActivity(intent);		
	}
	private void startInterventionsListActivity() {
		final Activity activity = this;
		Intent intent = new Intent(activity, InterventionsListActivity.class);
		startActivity(intent);
	}
	private void startInterventionsExportActivity() {
		final Activity activity = this;
		Intent intent = new Intent(activity, InterventionsExportActivity.class);
		startActivity(intent);
	}
	private void startInterventionsImportActivity() {
		final Activity activity = this;
		Intent intent = new Intent(activity, InterventionsImportActivity.class);
		startActivity(intent);
	}
	private void startInterventionsDeleteBilledActivity() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.caption_delete_billed)
		.setMessage(R.string.notify_confirm_delete_billed)
		.setNegativeButton(getString(R.string.caption_no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setPositiveButton(getString(R.string.caption_yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DatabaseManager.getInstance().deleteBilledInterventions(true);			}
		}).create().show();
	}
	private void startPreferencesActivity() {
		final Activity activity = this;
		Intent intent = new Intent(activity, PreferencesActivity.class);
		startActivity(intent);		
	}
	
	/**
	 * Simple event/action placeholder
	 *//*
	private void notYetImplemented(String label) {
		Toast.makeText(this, String.format(getString(R.string.notify_function_not_implemented), label), Toast.LENGTH_SHORT).show();
	}
*/
	/*
	 * *************************************************************************
	 * AboutActivity setup
	 * *************************************************************************
	 */
	/**
	 * 
	 */
	@Override
	public void onVersionChanged(String oldVersion, String newVersion) {
		Toast.makeText(this, String.format(this.getString(R.string.notify_version_changed), oldVersion, newVersion),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 */
	@Override
	public void onEulaAction(boolean isAccepted) {
		if (isAccepted)
			Toast.makeText(this, this.getString(R.string.notify_eula_accepted), Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, this.getString(R.string.notify_eula_refused), Toast.LENGTH_SHORT).show();
	}

	/**
	 * Edit res/strings.xml to customize values
	 */
	private void startAboutActivity() {
		Intent intent = AboutActivity.getAboutActivityIntent(
				this,
				this.getString(R.string.about_author),
				this.getString(R.string.about_website),
				this.getString(R.string.about_email),
				null,
				String.format("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=%s",
						this.getString(R.string.about_paypal_id)), false, null, null);
		startActivity(intent);
	}
	/*
	 * *************************************************************************
	 * AdMob setup
	 * *************************************************************************
	 */
	private void setupAdMob() {
		//
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		
	}

}