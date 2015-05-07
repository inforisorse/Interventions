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
 * Original idea from:
 *    http://mgmblog.com/2009/02/06/export-an-android-sqlite-db-to-an-xml-file-on-the-sd-card/
 *
 */
package it.inforisorse.interventions.db;

import it.inforisorse.interventions.Compress;
import it.inforisorse.interventions.Utils;
import it.inforisorse.interventions.ui.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DbExporter {
	protected String TAG = "DbExporter";
	protected String _zipTag = "please set ZIPTAG";
	protected ArrayList<String> _tableFiles = new ArrayList<String>();
	
	private Context _ctx;
	private SQLiteDatabase _db;
	private String _outputFilename;
	
	protected SQLiteDatabase _getDb() {
		return this._db;
	}

	protected Context _getContext() {
		return this._ctx;
	}

	protected String _getOutputFilename() {
		return this._outputFilename;
	}

	protected String _getOutputDirname() {
		File f = new File(_getOutputFilename());
		return f.getParent();
	}

	/**
	 * 
	 * @param ctx
	 * @param db
	 */
	public DbExporter(Context ctx, SQLiteDatabase db, String filename) {
		_ctx = ctx;
		_db = db;
		_outputFilename = filename;
	}

	protected boolean isValidDir(String dirname) {
		File dir = new File(dirname);
		if (dir.exists()) {
			return dir.isDirectory();
		} else {
			return dir.mkdirs();
		}
	}

	protected BufferedOutputStream openOutputFile(String filename) {
		try {
			File myFile = new File(filename);
			myFile.createNewFile();

			FileOutputStream fOut = new FileOutputStream(myFile);
			BufferedOutputStream bos = new BufferedOutputStream(fOut);
			return bos;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG, _ctx.getString(R.string.dbexporter_error_opening), e);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, _ctx.getString(R.string.dbexporter_error_opening), e);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, _ctx.getString(R.string.dbexporter_error_opening), e);
			return null;
		}
	}

	/**
	 * 
	 */
	public void exportData() {
		log("Exporting Data");

		try {
			startDbExport(_db.getPath());

			// get the tables out of the given sqlite database
			String sql = "SELECT * FROM sqlite_master";

			Cursor cur = _db.rawQuery(sql, new String[0]);
			Log.d("db", "show tables, cur size " + cur.getCount());
			cur.moveToFirst();

			String tableName;
			while (cur.getPosition() < cur.getCount()) {
				tableName = cur.getString(cur.getColumnIndex("name"));
				log("table name " + tableName);

				// don't process these two tables since they are used
				// for metadata
				if (!tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence")) {
					exportTable(tableName);
				}

				cur.moveToNext();
			}
			endDbExport();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, _ctx.getString(R.string.dbexporter_error_exporting), e);
		}
	}

	/**
	 * 
	 * @param msg
	 */
	protected void log(String msg) {
		Log.w(TAG, msg);
	}

	/**
	 * 
	 * @param tableName
	 */
	protected void exportTable(String tableName) {
		log("Exporting table ABSTRACT METHOD! Method exportTable() must be redefined in DbExporter subclasses!");
	}

	/**
	 * 
	 * @param path
	 *            database path
	 * 
	 *            Start export. This method is to be redefined in subclasses
	 */
	protected void startDbExport(String path) {
		// _exporter.startDbExport(path);
	}

	/**
	 * 
	 * End export. This method is to be redefined in subclasses
	 */
	protected void endDbExport() {
		// _exporter.endDbExport();
	}
	/**
	 * create a Zip file containing all exported table files
	 */
	protected void createDbExportZip() {
		File f = new File(_getOutputDirname());
		String zipFile = f.getPath() + "/" + "interventions_" + _zipTag + "_" + Utils.nowToFilename() + ".zip";
		Compress compress = new Compress(_tableFiles, zipFile);
		compress.zip();
	}
	/**
	 * send email with backup Zip file as attachment
	 */
	protected void emailDbExportZip() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_ctx);
		String exportZipMailbox = prefs.getString("exportZipMailbox", "");

		File f = new File(_getOutputDirname());
		String zipFile = f.getPath() + "/" + "interventions_" + _zipTag + "_" + Utils.nowToFilename() + ".zip";
		String subject = _ctx.getString(R.string.app_name) + " - "  + _ctx.getString(R.string.subject_export) + " " + _zipTag;
		
		Uri attachmentUri = Uri.parse("file://" + zipFile);
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { exportZipMailbox });
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		i.putExtra(Intent.EXTRA_STREAM, attachmentUri);

		i.putExtra(Intent.EXTRA_TEXT, subject + ":\n" +zipFile);
		try {
//			_ctx.startActivity(Intent.createChooser(i, "Send " + _zipTag + " Export data via email..."));
			_ctx.startActivity(i);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(_ctx, _ctx.getString(R.string.notify_no_email_client), Toast.LENGTH_SHORT).show();
		}
	}

}
