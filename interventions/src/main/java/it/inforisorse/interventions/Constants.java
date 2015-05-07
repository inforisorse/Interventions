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

import android.app.Activity;

public class Constants {
	public static final String KEY_INTERVENTIONS_ID = "id";
	
	public static final int ACTIVITY_EDIT = Activity.RESULT_FIRST_USER + 1;
	public static final int PICK_CONTACT = Activity.RESULT_FIRST_USER + 2;
	public static final int PICK_FILETOOPEN = Activity.RESULT_FIRST_USER + 3;
	public static final int PICK_FILETOSAVE = Activity.RESULT_FIRST_USER + 4;
	
	public static final int DLG_ID_DATETIME = 0;
	public static final int DLG_ID_EXPORT_XML_DONE = 1;

	public static final int DLG_INSTANCE_TIMESTART = 0;
	public static final int DLG_INSTANCE_TIMEEND = 1;
	
	public static final String DATETIME_STORE_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String DATETIME_FILENAME_FORMAT = "yyyyMMdd-HHmm";
	public static final String DATETIME_PRINT_FORMAT = "%04d-%02d-%02d %02d:%02d";
}
