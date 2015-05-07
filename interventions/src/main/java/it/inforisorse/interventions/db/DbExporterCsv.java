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

import it.inforisorse.interventions.ui.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbExporterCsv extends DbExporter {
	protected String TAG = "DbExporterCSV";

	protected Exporter _exporter;

	public DbExporterCsv(Context ctx, SQLiteDatabase db, String filename) {
		super(ctx, db, filename);
		_zipTag = "CSV";
	}
	protected String _getOutputDirname() {
		return _getOutputFilename();
	}

	protected void exportTable(String tableName) {
		// save each table in a separate file in the selected path
		
		if (isValidDir(_getOutputFilename())) {
		
			File f = new File(_getOutputFilename());
			String tableFile = f.getPath() + "/" + tableName + ".csv";
			_tableFiles.add(tableFile);
			_exporter = new Exporter(openOutputFile(tableFile));
	
			// get everything from the table
			String sql = "SELECT * FROM " + tableName;
			Cursor cur = _getDb().rawQuery(sql, new String[0]);
			int numcols = cur.getColumnCount();
			cur.moveToFirst();
	
			log(String.format(_getContext().getString(R.string.dbexporter_log_exporting_table, tableName)));
	
			// insert column names in first row
			_exporter.addFirstColumn(cur.getColumnName(0));
			for (int idx = 1; idx < numcols; idx++) {
				_exporter.addColumn(cur.getColumnName(idx));
			}
			_exporter.endRow();
	
			// insert rows
			while (cur.getPosition() < cur.getCount()) {
				_exporter.addFirstColumn(cur.getString(0));
				for (int idx = 1; idx < numcols; idx++) {
					_exporter.addColumn(cur.getString(idx));
				}
	
				_exporter.endRow();
				cur.moveToNext();
			}
	
			cur.close();
			_exporter.close();
		} else {
			// NO VALID OUTPUT DIR
		}
	}

	/**
	 * 
	 * @author amedeo
	 * 
	 */
	private class Exporter {

		private final static String FORMAT_FIRST_COLUMN = "\"%s\"";
		private final static String FORMAT_COLUMN = ",\"%s\"";
		private final static String FORMAT_END_ROW = "\n";

		private BufferedOutputStream _bos;

		public Exporter(BufferedOutputStream bos) {
			_bos = bos;
		}

		public void close() {
			if (_bos != null) {
				try {
					_bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void endRow() {
			try {
				_bos.write(FORMAT_END_ROW.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void addFirstColumn(String name) {
			String stg = String.format(FORMAT_FIRST_COLUMN, name);
			try {
				_bos.write(stg.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void addColumn(String name) {
			String stg = String.format(FORMAT_COLUMN, name);
			try {
				_bos.write(stg.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
