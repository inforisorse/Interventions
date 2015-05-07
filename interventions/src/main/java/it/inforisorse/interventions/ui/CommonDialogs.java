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
package it.inforisorse.interventions.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class CommonDialogs {
    /**
     * @param parent Activity calling activity
     * @param title int dialog title id
     * @param message int dialog message id
     * @return
     */
    public static AlertDialog NotificationDialog(Activity parent, int title, int message) {
    	AlertDialog.Builder builder=new AlertDialog.Builder(parent);
    	builder.setTitle(title);
    	builder.setMessage(message);
    	builder.setCancelable(true);
    	builder.setPositiveButton(R.string.caption_ok,new OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                    dialog.dismiss();
                    }
            });
    	AlertDialog dialog=builder.create();
    	return dialog;
    }

    public static AlertDialog ConfirmDialog(Activity parent, int title, int message) {
    	AlertDialog.Builder builder=new AlertDialog.Builder(parent);
    	builder.setTitle(title);
    	builder.setMessage(message);
    	builder.setCancelable(true);
    	builder.setPositiveButton(R.string.caption_ok,new OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                    dialog.dismiss();
                    }
            });
    	builder.setNegativeButton(R.string.caption_no, new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
    	AlertDialog dialog=builder.create();
    	return dialog;
    }

}