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

import it.inforisorse.interventions.Utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InterventionsAdapter extends ArrayAdapter<Interventions> {
	private int resource;
	private LayoutInflater inflater;

	public InterventionsAdapter(Context context, int resourceId, List<Interventions> objects) {
		super(context, resourceId, objects);
		resource = resourceId;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Interventions interventions = getItem(position);

		InterventionsViewCache viewCache;

		if (convertView == null) {
			convertView = (RelativeLayout) inflater.inflate(resource, null);
			viewCache = new InterventionsViewCache(convertView);
			convertView.setTag(viewCache);
		} else {
			convertView = (RelativeLayout) convertView;
			viewCache = (InterventionsViewCache) convertView.getTag();
		}

		TextView customer = viewCache.getTextViewCustomer();
		customer.setText(interventions.getCustomer());

		TextView elapsed = viewCache.getTextViewElapsed();
		elapsed.setText(Utils.timeDiff(interventions.getStart(), interventions.getEnd()));

		TextView start = viewCache.getTextViewStart();
		start.setText(String.format("%s %s", Utils.dateToDow(interventions.getStart()),Utils.dbDateToString(interventions.getStart())));

		TextView end = viewCache.getTextViewEnd();
		end.setText(String.format("%s %s", Utils.dateToDow(interventions.getEnd()),Utils.dbDateToString(interventions.getEnd())));

		CheckBox call = viewCache.getCheckBoxCall();
		call.setChecked(interventions.getChargeCall());

		CheckBox billed = viewCache.getCheckBoxBilled();
		billed.setChecked(interventions.getBilled());

		CheckBox extra = viewCache.getCheckBoxExtra();
		extra.setChecked(interventions.getChargeExtra());

		TextView notes = viewCache.getTextViewNotes();
		String txt = interventions.getNotes();
		if (txt != null) {
			notes.setText(txt.substring(0, Math.min(30, txt.length())));
		} else {
			notes.setText("");
		}
		
		return convertView;

	}
}
