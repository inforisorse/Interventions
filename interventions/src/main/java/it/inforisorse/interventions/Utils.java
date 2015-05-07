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
 */
package it.inforisorse.interventions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class Utils {
	
	private static final String TAG = "Utils";

	/**
	 * Return boolean value from string format value
	 * @param value
	 * @return
	 */
	public static boolean str2bool (String value) {
		return (value != null && value.length() == 1 && !value.contentEquals("0"));
	}

	/**
	 * Returns SimpledateFormat with date-time format used in database
	 * @return
	 */
    public static SimpleDateFormat dateTimeStoreFormat() {
    	SimpleDateFormat format = new SimpleDateFormat(Constants.DATETIME_STORE_FORMAT);
    	return format;
    }
	/**
	 * Returns SimpledateFormat with date-time format used for filename
	 * @return
	 */
    public static SimpleDateFormat dateTimeFilenameFormat() {
    	SimpleDateFormat format = new SimpleDateFormat(Constants.DATETIME_FILENAME_FORMAT);
    	return format;
    }

    /**
     * Converts date-time string as stored in database to Date
     * @param String dbDate
     * @return date
     */
    public static Date dbDateToDate(String dbDate) {
    	Date date = null;
    	try {
    		date = dateTimeStoreFormat().parse(dbDate);
   		} catch (Exception e) {
			Log.e(TAG, "Parsing ISO8601 datetime failed: " + dbDate, e);
   		}
    	return date;
    }

    /**
     * Returns the day of week for specified date
     * @param date
     * @return
     */
    public static String dateToDow(Date date) {
    	return DateFormat.format("E",date).toString();
    }

    /**
     * Returns the day of week for specified date string in the format used 
     * in database
     * @param dbDate
     * @return
     */
    public static String dateToDow(String dbDate) {
    	return DateFormat.format("E",dbDateToDate(dbDate)).toString();
    }

    /**
     * Returns the specified date as string
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
    	return SimpleDateFormat.getDateTimeInstance().format(date);
    }

    /**
     * Returns the specified date string in database format as string
     * @param dbDate
     * @return
     */
    public static String dbDateToString(String dbDate) {
    	return dateToString(dbDateToDate(dbDate));
    }
    
    /**
     * Returns the current date & time as string in the format used in
     * the database
     * @return
     */
    public static String nowToString() {
    	Date date = new Date();
    	return dateTimeStoreFormat().format(date);
    }
    /**
     * Returns the current date & time as string in the format used
     * for filenames
     * @return
     */
    public static String nowToFilename() {
    	Date date = new Date();
    	return dateTimeFilenameFormat().format(date);
    }
    /**
     * Returns the current date & time as string in the format used in
     * the database
     * @return
     */
    public static String nextHourToString() {
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.HOUR, 1);
    	return dateTimeStoreFormat().format(calendar.getTime());
    }

    /**
     * Update the specified DatePicker and TimePicker with the specified date-time
     * string in the format used in the database
     * @param datePicker
     * @param timePicker
     * @param dateString
     */
    public static void updateDateTimePickers(DatePicker datePicker, TimePicker timePicker, String dateString) {
		Calendar cal = Calendar.getInstance();
		if (dateString != null && dateString.length() == Constants.DATETIME_STORE_FORMAT.length()) {
			Date d = dbDateToDate(dateString);
			cal.setTime(d);
		} else {
			cal.setTime(new Date());
		}
		datePicker.updateDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
		// must add AM_PM (0/1) * 12 to hours since get(Calendar.HOUR) return tima in 12 Hour format
		timePicker.setCurrentHour(cal.get(Calendar.HOUR) +  cal.get(Calendar.AM_PM) * 12);
		timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
    }

    /**
     * Compute time difference between the two specified date-time strings
     * in the format used in the database.
     * @param sDate1
     * @param sDate2
     * @return
     */
    public static String timeDiff(String sDate1, String sDate2) {
    	Date date1 = dbDateToDate(sDate1);
    	Date date2 = dbDateToDate(sDate2);
    	long diffInMis = (date2.getTime() - date1.getTime());
    	long diffInMin = TimeUnit.MILLISECONDS.toSeconds(diffInMis) /60;
    	
    	long diffH = diffInMin / 60;
    	long diffM = diffInMin % 60;
    	
    	String dateString = String.format("%02d:%02d",diffH,diffM); 
    	return dateString;
    }
    /**
     * Compute time + 1H
     * in the format used in the database.
     * @param sDate
     * @return
     */
    public static String timeInc(String sDate, int iHours) {
    	// Date date = dbDateToDate(sDate);
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(dbDateToDate(sDate));
    	calendar.add(Calendar.HOUR, iHours);
    	return dateTimeStoreFormat().format(calendar.getTime());
/*
    	String dateString = String.format(Constants.DATETIME_PRINT_FORMAT, 
    				calendar.get(Calendar.YEAR),
    				calendar.get(Calendar.MONTH),
    				calendar.get(Calendar.DAY_OF_MONTH),
    				calendar.get(Calendar.HOUR),
    				calendar.get(Calendar.MINUTE),
    				calendar.get(Calendar.SECOND));
    	String retValue = String.format("%s %s", Utils.dateToDow(dateString),
				Utils.dbDateToString(dateString));
    	return retValue;
*/
    }

}
