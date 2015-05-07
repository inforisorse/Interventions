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

import it.inforisorse.interventions.Constants;
import it.inforisorse.interventions.db.DatabaseManager;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InterventionsImportActivity extends Activity {

	private EditText mEdit_filename;
	private Button mBtn_select_file;
	private Button mBtn_import_file;
	private Button mBtn_cancel;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);
		setContentView(R.layout.interventions_import);
		setupControls();
		setupControlsEvents();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
			case Constants.PICK_FILETOOPEN:
				if (resultCode == RESULT_OK) {
					String filePath = data.getData().getPath();
					mEdit_filename.setText(filePath);
				}
				break;
		}
	}

	private void setupControls() {
		mEdit_filename = (EditText) findViewById(R.id.edit_filename);
		mBtn_select_file = (Button) findViewById(R.id.btn_select_file);
		mBtn_import_file = (Button) findViewById(R.id.btn_import_file);
		mBtn_cancel = (Button) findViewById(R.id.btn_cancel);
		mEdit_filename.setText("/sdcard/Interventions/intervenions.xml");
	}

	private void setupControlsEvents() {
		mBtn_select_file.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startPickFileActivity("file://mnt/sdcard/Interventions/import.xml");
			}
		});
		mBtn_import_file.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startFileImport();
			}
		});
		mBtn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void startPickFileActivity(String defaultFile) {
		Uri startDir = Uri.fromFile(new File("/sdcard/Interventions"));
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setDataAndType(startDir, "file://");
		intent.putExtra("explorer_title", "Select a file");
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    try {
	        startActivityForResult(intent,Constants.PICK_FILETOOPEN);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private void startFileImport() {
		Toast.makeText(this, getString(R.string.notify_import_start), Toast.LENGTH_SHORT).show();
		DatabaseManager.getInstance().importXml(mEdit_filename.getText().toString());
		Toast.makeText(this, getString(R.string.notify_import_end), Toast.LENGTH_LONG).show();
	}
}
