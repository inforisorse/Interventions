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
package it.inforisorse.interventions.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;

import it.inforisorse.interventions.Utils;
import it.inforisorse.interventions.model.Interventions;

public class DatabaseManager {
	public static final int FLAG_BILLED_UNBILLED = 0;
	public static final int FLAG_BILLED_BILLED = 1;
	public static final int FLAG_BILLED_ALL = 2;
	
	static final String TAG = "DatabaseManager";
	static private DatabaseManager instance;
	private static Context mCtx;

	static public void init(Context ctx) {
		if (null == instance) {
			instance = new DatabaseManager(ctx);
		}
	}

	static public DatabaseManager getInstance() {
		return instance;
	}

	private DatabaseHelper helper;

	private DatabaseManager(Context ctx) {
		helper = new DatabaseHelper(ctx);
		mCtx = ctx;
	}

	private DatabaseHelper getHelper() {
		return helper;
	}

	public void addInterventions(Interventions interventions) {
		try {
			getHelper().getInterventionsDao().create(interventions);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteInterventions(Interventions interventions) {
		try {
			getHelper().getInterventionsDao().delete(interventions);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteBilledInterventions(Boolean billed) {
		try {
			Dao<Interventions, Integer> interventionsDao = getHelper().getInterventionsDao();
			DeleteBuilder<Interventions, Integer> deleteBuilder = interventionsDao.deleteBuilder();
			deleteBuilder.where().eq(Interventions.FIELD_FLAG_BILLED, "1");
			interventionsDao.delete(deleteBuilder.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Interventions getInterventionWithId(int interventionsId) {
		Interventions intervention = null;
		try {
			intervention = getHelper().getInterventionsDao().queryForId(interventionsId);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return intervention;
	}

	public List<Interventions> getAllInterventions() {
		List<Interventions> interventions = null;
		try {
			interventions = getHelper().getInterventionsDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return interventions;
	}

	public List<Interventions> getFilteredInterventions(String customer) {
		List<Interventions> interventions = null;
		try {
			Dao<Interventions, Integer> interventionsDao = getHelper().getInterventionsDao();
			QueryBuilder<Interventions, Integer> queryBuilder = interventionsDao.queryBuilder();
			queryBuilder.orderBy(Interventions.FIELD_TIME_START, false);
			queryBuilder.where().like(Interventions.FIELD_CUSTOMER, '%' + customer + '%');
			PreparedQuery<Interventions> preparedQuery = queryBuilder.prepare();
			interventions = getHelper().getInterventionsDao().query(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return interventions;
	}

	public List<Interventions> getSortedInterventions() {
		return getSortedInterventions(FLAG_BILLED_ALL,"");
	}

	public List<Interventions> getSortedInterventions(String customer) {
		return getSortedInterventions(FLAG_BILLED_ALL,customer);
	}

	public List<Interventions> getSortedInterventions(int billed, String customer) {

		boolean hasFlag = false;
		List<Interventions> interventions = null;
		try {
			Dao<Interventions, Integer> interventionsDao = getHelper().getInterventionsDao();
			QueryBuilder<Interventions, Integer> queryBuilder = interventionsDao.queryBuilder();
			queryBuilder.orderBy(Interventions.FIELD_TIME_START, false);

			Where<Interventions, Integer> where;
			where = queryBuilder.where();

			switch (billed) {
				case FLAG_BILLED_BILLED:

					where.eq(Interventions.FIELD_FLAG_BILLED, "1");
					hasFlag = true;
					break;
				case FLAG_BILLED_UNBILLED:
					where.eq(Interventions.FIELD_FLAG_BILLED, "0");
					hasFlag = true;
					break;
				default:
					break;
			}

			if (customer != "") {
				if (hasFlag) {
					where.and();
				}
				where.like(Interventions.FIELD_CUSTOMER, '%' + customer + '%');
			} else {
				if (!hasFlag) {
					where.like(Interventions.FIELD_CUSTOMER, '%');
				}
			}

			PreparedQuery<Interventions> preparedQuery = queryBuilder.prepare();
			interventions = getHelper().getInterventionsDao().query(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return interventions;
	}

	public void refreshInterventions(Interventions interventions) {
		try {
			getHelper().getInterventionsDao().refresh(interventions);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateInterventions(Interventions interventions) {
		try {
			getHelper().getInterventionsDao().update(interventions);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void exportXml(String filename, boolean createZip, boolean emailZip) {
		SQLiteDatabase mDb = helper.getWritableDatabase();
		final DbExporterXml dba = new DbExporterXml(mCtx, mDb, filename);
		dba.exportData();
		if (createZip) {
			dba.createDbExportZip();
			if (emailZip) {
				dba.emailDbExportZip();
			}
		}
	}

	public void exportCsv(String filename, boolean createZip, boolean emailZip) {
		SQLiteDatabase mDb = helper.getWritableDatabase();
		final DbExporterCsv dba = new DbExporterCsv(mCtx, mDb, filename);
		dba.exportData();
		if (createZip) {
			dba.createDbExportZip();
			if (emailZip) {
				dba.emailDbExportZip();
			}
		}
	}

	public void importXml(String filePath) {
		Interventions interventions = null;
		String curCol;
		String curText;

		XmlPullParser parser = Xml.newPullParser();
		try {
			File inputFile = new File(filePath);
			FileReader reader = new FileReader(inputFile);

			// auto-detect the encoding from the stream
			parser.setInput(reader);
			int eventType = parser.getEventType();
			curCol = "";
			curText = "";

			boolean done = false;
			while (eventType != XmlPullParser.END_DOCUMENT && !done) {
				String name = null;
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						name = parser.getName();
						if (name.equalsIgnoreCase("row")) {
							// new row, initialize DB buffer
							interventions = new Interventions();
						} else if (name.equalsIgnoreCase("col")) {
							// column, get column name
							curCol = parser.getAttributeValue(0);
						}
						break;
					case XmlPullParser.TEXT:
						// get column value and update record buffer
						curText = URLDecoder.decode(parser.getText(),"UTF-8");
						if (curCol.equalsIgnoreCase(Interventions.FIELD_CUSTOMER)) {
							interventions.setCustomer(curText);

						} else if (curCol.equalsIgnoreCase(Interventions.FIELD_TIME_START)) {
							interventions.setStart(curText);

						} else if (curCol.equalsIgnoreCase(Interventions.FIELD_TIME_END)) {
							interventions.setEnd(curText);

						} else if (curCol.equalsIgnoreCase(Interventions.FIELD_NOTES)) {
							interventions.setNotes(curText);

						} else if (curCol.equalsIgnoreCase(Interventions.FIELD_FLAG_CALL)) {
							interventions.setChargeCall(Utils.str2bool(curText));

						} else if (curCol.equalsIgnoreCase(Interventions.FIELD_FLAG_BILLED)) {
							interventions.setBilled(Utils.str2bool(curText));

						} else if (curCol.equalsIgnoreCase(Interventions.FIELD_FLAG_EXTRA)) {
							interventions.setChargeExtra(Utils.str2bool(curText));
						}
						break;

					case XmlPullParser.END_TAG:
						name = parser.getName();
						if (name.equalsIgnoreCase("row")) {
							// end row, insert record
							addInterventions(interventions);
						} else if (name.equalsIgnoreCase("table")) {
							done = true;
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException Parsing XML", e);
		} catch (IOException e) {
			Log.e(TAG, "IOException Parsing XML", e);
		} catch (Exception e) {
			Log.e(TAG, "Parsing XML", e);
		}
	}
}
