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
package it.inforisorse.interventions.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Interventions {

	public static String FIELD_ID 			= "id";
	public static String FIELD_CUSTOMER 	= "customer";
	public static String FIELD_TIME_START 	= "time_start";
	public static String FIELD_TIME_END 	= "time_end";
	public static String FIELD_NOTES 		= "notes";
	public static String FIELD_FLAG_CALL 	= "flag_call";
	public static String FIELD_FLAG_BILLED 	= "flag_billed";
	public static String FIELD_FLAG_EXTRA 	= "flag_extra";
	
	@DatabaseField(generatedId=true)
    private int id;
	
	@DatabaseField
    private String customer;
	
	@DatabaseField
    private String time_start;

	@DatabaseField
    private String time_end;

	@DatabaseField
    private String notes;

	@DatabaseField
    private int flag_call;

	@DatabaseField
    private int flag_billed;

	@DatabaseField
    private int flag_extra;

	public void setId(int value) {
		id = value;
	}

	public int getId() {
		return id;
	}
	
	public void setCustomer(String value) {
		customer = value;
	}

	public String getCustomer() {
		return customer;
	}

	public void setStart(String value) {
		time_start = value;
	}

	public String getStart() {
		return time_start;
	}

	public void setEnd(String value) {
		time_end = value;
	}

	public String getEnd() {
		return time_end;
	}
	
	public void setNotes(String value) {
		notes = value;
	}

	public String getNotes() {
		return notes;
	}
	public void setBilled(boolean value) {
		if (value) {
			flag_billed = 1;
		} else {
			flag_billed = 0;
		}
	}

	public boolean getBilled() {
		return (flag_billed > 0);
	}

	public void setChargeCall(boolean value) {
		if (value) {
			flag_call = 1;
		} else {
			flag_call = 0;
		}
	}

	public boolean getChargeCall() {
		return (flag_call > 0);
	}

	public void setChargeExtra(boolean value) {
		if (value) {
			flag_extra = 1;
		} else {
			flag_extra = 0;
		}
	}

	public boolean getChargeExtra() {
		return (flag_extra > 0);
	}
}
