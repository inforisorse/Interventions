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

package it.inforisorse.interventions.ui;

import it.inforisorse.interventions.Constants;
import it.inforisorse.interventions.Utils;
import it.inforisorse.interventions.db.DatabaseManager;
import it.inforisorse.interventions.model.Interventions;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class InterventionsEditActivity extends Activity {
	private Interventions interventions;

	private EditText mEdit_customer;		// edit form customer
	private TextView mEdit_time_start;		// edit form start time
	private TextView mEdit_time_end;		// edit form end time
	private TextView mLabel_time_tot;		// edit form elapsed time
	private CheckBox mCheck_flag_call;		// edit form call flag
	private CheckBox mCheck_flag_billed;	// edit form billed flag
	private CheckBox mCheck_flag_extra;		// edit form billed flag
	private EditText mEdit_notes;			// edit form notes

	private Button mBtn_save;				// edit form save button
	private Button mBtn_cancel;				// edit form cancel button
	private Button mBtn_select_customer;	// edit form select customer button
	private Button mBtn_time_dec;			// edit form dec time button
	private Button mBtn_time_inc;			// edit form inc time button
	
	
	private String mString_time_start;
	private String mString_time_end;

	private static DatePicker mDatePicker; // Datetime dialog datepicker
	private static TimePicker mTimePicker; // Datetime dialog timepicker

	private int mDateTimeDlgInstance;

	private boolean autoDateStart;			// Preferences: auto update Start datetime dialog with now()
	private boolean autoDateEnd;			// Preferences: auto update End datetime dialog with now()
	private boolean autoTimeEnd;			// Preferences: auto set 1 hour elapsed on creation
	private boolean defaultCheckCall;		// Preferences: Call flag default
	private boolean defaultCheckExtra;		// Preferences: Call flag default
	private boolean defaultCheckBilled;		// Preferences: Call flag default
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		autoDateStart = prefs.getBoolean("autoDateStart", true);
		autoDateEnd = prefs.getBoolean("autoDateEnd", true);
		autoTimeEnd = prefs.getBoolean("autoTimeEnd", false);
		defaultCheckCall = prefs.getBoolean("defaultCheckCall", true);
		defaultCheckExtra = prefs.getBoolean("defaultCheckExtra", false);
		defaultCheckBilled = prefs.getBoolean("defaultCheckBilled", false);
		//
		ViewGroup contentView = (ViewGroup) getLayoutInflater().inflate(R.layout.interventions_edit, null);
		setupControls(contentView);
		setupControlsEvents();
		setupInterventions();
		setContentView(contentView);
	}

	/**
	 * Events handling
	 */
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
			case (Constants.PICK_CONTACT):
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = data.getData();
					Cursor c = managedQuery(contactData, null, null, null, null);
					if (c.moveToFirst()) {
						String customer = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
						mEdit_customer.setText(customer);
					}
				}
				break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case Constants.DLG_ID_DATETIME:
				return createDateTimeDialog();
			default:
				return null;
		}
	}

	/**
     * 
     */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
			case Constants.DLG_ID_DATETIME:
				switch (mDateTimeDlgInstance) {
					case Constants.DLG_INSTANCE_TIMESTART:
						if (autoDateStart) {
							Utils.updateDateTimePickers(mDatePicker, mTimePicker, Utils.nowToString());
						} else {
							Utils.updateDateTimePickers(mDatePicker, mTimePicker, mString_time_start);
						}
						break;
					case Constants.DLG_INSTANCE_TIMEEND:
						if (autoDateEnd) {
							Utils.updateDateTimePickers(mDatePicker, mTimePicker, Utils.nowToString());
						} else {
							Utils.updateDateTimePickers(mDatePicker, mTimePicker, mString_time_end);								
						}
						break;
				}
				break;
			default:
				break;
		}
	}

	private void setupControls(ViewGroup contentView) {
		mEdit_customer = (EditText) contentView.findViewById(R.id.edit_customer);
		mEdit_time_start = (EditText) contentView.findViewById(R.id.edit_time_start);
		mEdit_time_end = (EditText) contentView.findViewById(R.id.edit_time_end);
		mLabel_time_tot = (TextView) contentView.findViewById(R.id.label_time_tot);
		mCheck_flag_call = (CheckBox) contentView.findViewById(R.id.check_flag_call);
		mCheck_flag_billed = (CheckBox) contentView.findViewById(R.id.check_flag_billed);
		mCheck_flag_extra = (CheckBox) contentView.findViewById(R.id.check_flag_extra);
		mEdit_notes = (EditText) contentView.findViewById(R.id.edit_notes);

		mBtn_save = (Button) contentView.findViewById(R.id.btn_save);
		mBtn_cancel = (Button) contentView.findViewById(R.id.btn_cancel);
		mBtn_select_customer = (Button) contentView.findViewById(R.id.btn_select_customer);
		mBtn_time_dec = (Button) contentView.findViewById(R.id.btn_time_dec);
		mBtn_time_inc = (Button) contentView.findViewById(R.id.btn_time_inc);
	}

	private void fillInterventions(Interventions interventions) {
		interventions.setCustomer(mEdit_customer.getText().toString());
		interventions.setChargeCall(mCheck_flag_call.isChecked());
		interventions.setBilled(mCheck_flag_billed.isChecked());
		interventions.setChargeExtra(mCheck_flag_extra.isChecked());
		interventions.setNotes(mEdit_notes.getText().toString());

		interventions.setStart(mString_time_start);
		interventions.setEnd(mString_time_end);
	}

	private void setupControlsEvents() {
		final Activity activity = this;
		// SAVE
		mBtn_save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (null != mEdit_customer.getText().toString() && mEdit_customer.getText().toString().length() > 0) {
					if (null != interventions) {
						fillInterventions(interventions);
						updateInterventions(interventions);
					} else {
						interventions = new Interventions();
						fillInterventions(interventions);
						createNewInterventions(interventions);
					}
					finish();
				} else {
					new AlertDialog.Builder(activity).setTitle(getString(R.string.caption_error))
							.setMessage(getString(R.string.notify_invalid_name))
							.setNegativeButton(getString(R.string.caption_ok), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).show();
				}
			}
		});
		// CANCEL
		mBtn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		// SELECT CUSTOMER
		mBtn_select_customer.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, Constants.PICK_CONTACT);
			}
		});
		// EDIT TIME START
		mEdit_time_start.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				mDateTimeDlgInstance = Constants.DLG_INSTANCE_TIMESTART;
				showDialog(Constants.DLG_ID_DATETIME);
				return false;
			}
		});
		// EDIT TIME END
		mEdit_time_end.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				mDateTimeDlgInstance = Constants.DLG_INSTANCE_TIMEEND;
				showDialog(Constants.DLG_ID_DATETIME);
				return false;
			}
		});
		// DEC ELAPSED TIME
		mBtn_time_dec.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mString_time_end = Utils.timeInc(mString_time_end,-1);
				mEdit_time_end.setText(String.format("%s %s", Utils.dateToDow(mString_time_end), Utils.dbDateToString(mString_time_end)));
				mLabel_time_tot.setText(Utils.timeDiff(mString_time_start,mString_time_end));
			}
		});
		// INC ELAPSED TIME
		mBtn_time_inc.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mString_time_end = Utils.timeInc(mString_time_end,+1);
//				mEdit_time_start.setText(String.format("%s %s", Utils.dateToDow(mString_time_start),
//						Utils.dbDateToString(mString_time_start)));
				mEdit_time_end.setText(String.format("%s %s", Utils.dateToDow(mString_time_end), Utils.dbDateToString(mString_time_end)));
				mLabel_time_tot.setText(Utils.timeDiff(mString_time_start,mString_time_end));
			}
		});

	}
	/**
	 * 
	 */
	private void setupInterventions() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle && bundle.containsKey(Constants.KEY_INTERVENTIONS_ID)) {
			//
			// edit intervention
			//
			int interventionsId = bundle.getInt(Constants.KEY_INTERVENTIONS_ID);
			interventions = DatabaseManager.getInstance().getInterventionWithId(interventionsId);
			mEdit_customer.setText(interventions.getCustomer());

			mString_time_start = interventions.getStart();
			mString_time_end = interventions.getEnd();

			mCheck_flag_call.setChecked(interventions.getChargeCall());
			mCheck_flag_billed.setChecked(interventions.getBilled());
			mCheck_flag_extra.setChecked(interventions.getChargeExtra());
			mEdit_notes.setText(interventions.getNotes());
		} else {
			//
			// create intervention
			//
			mCheck_flag_call.setChecked(defaultCheckCall);
			mCheck_flag_extra.setChecked(defaultCheckExtra);
			mCheck_flag_billed.setChecked(defaultCheckBilled);

			mString_time_start = Utils.nowToString();
			if (autoTimeEnd) {
				mString_time_end = Utils.nextHourToString();
			} else {
				mString_time_end = Utils.nowToString();
			}
		}
		mEdit_time_start.setText(String.format("%s %s", Utils.dateToDow(mString_time_start),
				Utils.dbDateToString(mString_time_start)));
		mEdit_time_end.setText(String.format("%s %s", Utils.dateToDow(mString_time_end), Utils.dbDateToString(mString_time_end)));
		mLabel_time_tot.setText(Utils.timeDiff(mString_time_start,mString_time_end));
	}
	/**
	 * update record in database
	 * @param interventions
	 */
	private void updateInterventions(Interventions interventions) {
		if (null != interventions) {
			DatabaseManager.getInstance().updateInterventions(interventions);
		}
	}
	/**
	 * Insert new record in database
	 * @param interventions
	 */
	private void createNewInterventions(Interventions interventions) {
		if (null != interventions) {
			DatabaseManager.getInstance().addInterventions(interventions);
		}
	}
	/**
	 * 
	 * @return datetime dialog
	 */
	private final Dialog createDateTimeDialog() {

		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.datetime_dialog, (ViewGroup) findViewById(R.id.dateTimeDialogRoot));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.caption_datetime_dialog);
		builder.setIcon(R.drawable.ic_dialog_time);
		builder.setView(layout);
		builder.setPositiveButton(R.string.caption_ok, new DialogInterface.OnClickListener() {
			// OK
			public void onClick(DialogInterface dialog, int id) {
				DatePicker datePicker = (DatePicker) layout.findViewById(R.id.DlgDatePicker);
				TimePicker timePicker = (TimePicker) layout.findViewById(R.id.DlgTimePicker);
				String dateString = String.format(Constants.DATETIME_PRINT_FORMAT, datePicker.getYear(), datePicker.getMonth() + 1,
						datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
				Toast.makeText(InterventionsEditActivity.this, dateString, Toast.LENGTH_SHORT).show();
				switch (mDateTimeDlgInstance) {
					case Constants.DLG_INSTANCE_TIMESTART:

						mString_time_start = dateString;
						// display as "dow datetime"
						mEdit_time_start.setText(String.format("%s %s", Utils.dateToDow(mString_time_start),
								Utils.dbDateToString(mString_time_start)));
						break;
					case Constants.DLG_INSTANCE_TIMEEND:
						mString_time_end = dateString;
						// display as "dow datetime"
						mEdit_time_end.setText(String.format("%s %s", Utils.dateToDow(mString_time_end),
								Utils.dbDateToString(mString_time_end)));
						break;
				}
				// update total time display
				mLabel_time_tot.setText(Utils.timeDiff(mString_time_start,mString_time_end));
			}
		});
		builder.setNegativeButton(R.string.caption_cancel, new DialogInterface.OnClickListener() {
			// CANCEL
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(InterventionsEditActivity.this, R.string.caption_cancel, Toast.LENGTH_SHORT).show();
			}
		});
		mDatePicker = (DatePicker) layout.findViewById(R.id.DlgDatePicker);
		mTimePicker = (TimePicker) layout.findViewById(R.id.DlgTimePicker);
		mTimePicker.setIs24HourView(DateFormat.is24HourFormat(this));
		return builder.create();
	}

}