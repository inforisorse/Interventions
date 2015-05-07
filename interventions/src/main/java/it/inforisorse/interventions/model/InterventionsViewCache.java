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
package it.inforisorse.interventions.model;

import it.inforisorse.interventions.ui.R;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class InterventionsViewCache {

	private View baseView;
	private TextView textViewCustomer;
	private TextView textViewElapsed;
	private TextView textViewStart;
	private TextView textViewEnd;
	private TextView textViewNotes;
	private CheckBox checkBoxCall;
	private CheckBox checkBoxBilled;
	private CheckBox checkBoxExtra;

	public InterventionsViewCache(View baseView) {
		this.baseView = baseView;
	}

	public TextView getTextViewCustomer() {
		if (textViewCustomer == null) {
			textViewCustomer = (TextView) baseView.findViewById(R.id.interventions_customer);
		}
		return textViewCustomer;
	}

	public TextView getTextViewElapsed() {
		if (textViewElapsed == null) {
			textViewElapsed = (TextView) baseView.findViewById(R.id.interventions_elapsed);
		}
		return textViewElapsed;
	}

	public TextView getTextViewStart() {
		if (textViewStart == null) {
			textViewStart = (TextView) baseView.findViewById(R.id.interventions_start);
		}
		return textViewStart;
	}
	public TextView getTextViewEnd() {
		if (textViewEnd == null) {
			textViewEnd = (TextView) baseView.findViewById(R.id.interventions_end);
		}
		return textViewEnd;
	}
	public CheckBox getCheckBoxCall() {
		if (checkBoxCall == null) {
			checkBoxCall = (CheckBox) baseView.findViewById(R.id.check_flag_call);
		}
		return checkBoxCall;
	}

	public CheckBox getCheckBoxBilled() {
		if (checkBoxBilled == null) {
			checkBoxBilled = (CheckBox) baseView.findViewById(R.id.check_flag_billed);
		}
		return checkBoxBilled;
	}
	public CheckBox getCheckBoxExtra() {
		if (checkBoxExtra == null) {
			checkBoxExtra = (CheckBox) baseView.findViewById(R.id.check_flag_extra);
		}
		return checkBoxExtra;
	}
	public TextView getTextViewNotes() {
		if (textViewNotes == null) {
			textViewNotes = (TextView) baseView.findViewById(R.id.notes);
		}
		return textViewNotes;
	}
}
