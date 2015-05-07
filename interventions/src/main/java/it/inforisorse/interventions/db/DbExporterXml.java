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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbExporterXml extends DbExporter {
    protected String TAG = "DbExporterXML";
	protected Exporter _exporter;

    public DbExporterXml(Context ctx, SQLiteDatabase db, String filename) {
		super (ctx,db,filename);
		_zipTag = "XML";
	}

	protected void startDbExport(String path) {
		File f = new File(_getOutputFilename());
		
		if (isValidDir(f.getParent())) {
			_tableFiles.add(_getOutputFilename());
			_exporter = new Exporter(openOutputFile(_getOutputFilename()));
			_exporter.startDbExport(_getDb().getPath());
		} else {
			// NO VALID OUTPUT DIR
		}
	}

	protected void endDbExport() {
		_exporter.endDbExport();		
		_exporter.close();
	}
    
	/**
	 * 
	 * @param tableName
	 * @throws IOException
	 */
	protected void exportTable(String tableName)  {
		_exporter.startTable(tableName);

		// get everything from the table
		String sql = "SELECT * FROM " + tableName;
		Cursor cur = _getDb().rawQuery(sql, new String[0]);
		int numcols = cur.getColumnCount();

		log(String.format(_getContext().getString(R.string.dbexporter_log_exporting_table,tableName)));

		cur.moveToFirst();

		// move through the table, creating rows
		// and adding each column with name and value
		// to the row
		while (cur.getPosition() < cur.getCount()) {
			_exporter.startRow();
			for (int idx = 0; idx < numcols; idx++) {
				_exporter.addColumn(cur.getColumnName(idx), cur.getString(idx));
			}

			_exporter.endRow();
			cur.moveToNext();
		}

		cur.close();

		_exporter.endTable();
	}
	/**
	 * 
	 */
	class Exporter {
		private static final String TAG_DB_START	= "<export-database name='%s'>";
		private static final String TAG_DB_END		= "</export-database>";
		private static final String TAG_TABLE_START = "<table name='%s'>";
		private static final String TAG_TABLE_END	= "</table>";
		private static final String TAG_ROW_START	= "<row>";
		private static final String TAG_ROW_END		= "</row>";
		private static final String TAG_COLUMN		= "<col name='%s'>%s</col>";

		private BufferedOutputStream _bos;

		private String avoidNull(String s) {
			if (s == null) {
				return "";
			} else {
				return s;
			}
		}

		public Exporter(BufferedOutputStream bos) {
			_bos = bos;
		}

		public void close(){
			if (_bos != null) {
				try {
					_bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void startDbExport(String dbName) {
			String stg = String.format(TAG_DB_START,dbName);
			try {
				_bos.write(stg.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void endDbExport()  {
			try {
				_bos.write(TAG_DB_END.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void startTable(String tableName) {
			String stg = String.format(TAG_TABLE_START,tableName);
			try {
				_bos.write(stg.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void endTable() {
			try {
				_bos.write(TAG_TABLE_END.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void startRow() {
			try {
				_bos.write(TAG_ROW_START.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void endRow() {
			try {
				_bos.write(TAG_ROW_END.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void addColumn(String name, String val)  {
			try {
				String stg = String.format(TAG_COLUMN,name,URLEncoder.encode(avoidNull(val),"UTF-8"));
				try {
					_bos.write(stg.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {			
				e.printStackTrace();
			}
		}
	}

}
