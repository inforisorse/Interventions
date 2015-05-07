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
 * Thanks to:
 *    http://logic-explained.blogspot.com/2011/12/using-ormlite-in-android-projects.html
 */
package it.inforisorse.interventions.ui;

import it.inforisorse.interventions.Constants;
import it.inforisorse.interventions.db.DatabaseManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.marcoduff.util.aboutactivity.R;

public class InterventionsExportActivity extends Activity {

	private static final int PICK_FOLDERTOOPEN = 1;

	private EditText mEdit_filename;
	private EditText mEdit_foldername;
	private Button mBtn_select_file;
	private Button mBtn_export_file;
	private Button mBtn_cancel;
	private RadioGroup mRg_export_type;
	private RadioButton mRb_export_xml;
	private RadioButton mRb_export_csv;
	private TextView mTv_desc_export;
	private TextView mTv_caption_filename;
	private CheckBox mCheck_create_zip;
	private CheckBox mCheck_email_zip;
	
	private boolean default_create_zip;
	private boolean default_email_zip;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		default_create_zip = prefs.getBoolean("exportCreateZip", false);
		default_email_zip = prefs.getBoolean("exportEmailZip", false);
		DatabaseManager.init(this);
		setContentView(R.layout.interventions_export);
		setupControls();
		setupControlsEvents();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case Constants.PICK_FILETOSAVE: {
				if (resultCode == RESULT_OK && data != null && data.getData() != null) {
					String filePath = data.getData().getPath();
					mEdit_filename.setText(filePath);
				}
				break;
			}
			case PICK_FOLDERTOOPEN:
				if (resultCode == RESULT_OK) {
					String filePath = data.getData().getPath();
					mEdit_foldername.setText(filePath);
				}
				break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case Constants.DLG_ID_EXPORT_XML_DONE:
				return CommonDialogs.NotificationDialog(this, R.string.caption_info, R.string.notify_export_xml_done);
			default:
				return null;
		}
	}

	private void setupControls() {
		mEdit_filename = (EditText) findViewById(R.id.edit_filename);
		mEdit_filename.setText("interventions.xml");
		mEdit_foldername = (EditText) findViewById(R.id.edit_foldername);
		mEdit_foldername.setText("/sdcard/Interventions");
		mBtn_select_file = (Button) findViewById(R.id.btn_select_file);
		mBtn_export_file = (Button) findViewById(R.id.btn_export_file);
		mBtn_cancel = (Button) findViewById(R.id.btn_cancel);
		mRg_export_type = (RadioGroup) findViewById(R.id.rg_export_type);
		mRb_export_xml = (RadioButton) findViewById(R.id.rb_export_xml);
		mRb_export_csv = (RadioButton) findViewById(R.id.rb_export_csv);
		mTv_desc_export = (TextView) findViewById(R.id.tv_desc_export);
		mTv_caption_filename = (TextView) findViewById(R.id.tv_caption_filename);
		mCheck_create_zip = (CheckBox) findViewById(R.id.check_create_zip);
		mCheck_email_zip = (CheckBox) findViewById(R.id.check_email_zip);
		
		mCheck_create_zip.setChecked(default_create_zip);
		mCheck_email_zip.setChecked(default_email_zip);
	}

	private void setupControlsEvents() {
		mBtn_select_file.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startPickFolderActivity(mEdit_foldername.getText().toString());
			}
		});

		mBtn_export_file.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startFileExport();
			}
		});
		mBtn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		mRb_export_xml.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mTv_desc_export.setText(getString(R.string.desc_export_xml));
				mEdit_filename.setVisibility(View.VISIBLE);
				mTv_caption_filename.setVisibility(View.VISIBLE);
			}
		});
		mRb_export_csv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mTv_desc_export.setText(getString(R.string.desc_export_csv));
				mEdit_filename.setVisibility(View.INVISIBLE);
				mTv_caption_filename.setVisibility(View.INVISIBLE);
			}
		});
	}

	/**
	 * 
	 * @param defaultFile
	 */
	private void startPickFolderActivity(String defaultFile) {
		Intent intent = new Intent("org.openintents.action.PICK_DIRECTORY");
		intent.setData(Uri.parse("file://" + defaultFile));
		intent.putExtra("org.openintents.extra.TITLE", getString(R.string.caption_select_folder));
		intent.putExtra("org.openintents.extra.BUTTON_TEXT", getString(R.string.caption_use_this_dir));
		startActivityForResult(intent, PICK_FOLDERTOOPEN);
	}

	private void startFileExport() {
		Toast.makeText(this, getString(R.string.notify_export_start), Toast.LENGTH_SHORT).show();
		int checkedRadioButton = mRg_export_type.getCheckedRadioButtonId();

		boolean createZip = mCheck_create_zip.isChecked(); // create zip ?
		boolean emailZip = mCheck_email_zip.isChecked(); // create zip ?
		
		String outputPath = "";
		switch (checkedRadioButton) {
			case R.id.rb_export_csv:
				outputPath = mEdit_foldername.getText().toString();
				DatabaseManager.getInstance().exportCsv(outputPath,createZip,emailZip);
				break;
			case R.id.rb_export_xml:
				outputPath = mEdit_foldername.getText().toString() + '/' + mEdit_filename.getText().toString();
				DatabaseManager.getInstance().exportXml(outputPath,createZip,emailZip);
				break;
		}
		Toast.makeText(this, getString(R.string.notify_export_end), Toast.LENGTH_LONG).show();
	}

	
}
