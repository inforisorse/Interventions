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
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.List;

import it.inforisorse.interventions.Constants;
import it.inforisorse.interventions.db.DatabaseManager;
import it.inforisorse.interventions.GlobalVariables;

import it.inforisorse.interventions.model.Interventions;
import it.inforisorse.interventions.model.InterventionsAdapter;

public class InterventionsListActivity extends Activity {

	private ListView listView;
	private Button mBtn_interventions_add;
	private Button mBtn_back;

//	private int mFlag_list_filter = DatabaseManager.FLAG_BILLED_ALL;
	private MenuItem mChk_list_all;
	private MenuItem mChk_list_only_billed;
	private MenuItem mChk_list_only_unbilled;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);

		// inflate layout
		ViewGroup contentView = (ViewGroup) getLayoutInflater().inflate(R.layout.interventions_list, null);
		// setup controls for layout
		setupControls(contentView);
		setupControlsEvents();
		setContentView(contentView);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (getIntent() != null) {
			handleIntent(getIntent());
		}

		//setupListView(listView);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			listView.setAdapter(null);
			setupListView(listView,query);
		} else {
			setupListView(listView);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.interventions_list, menu);

		mChk_list_all = menu.findItem(R.id.mnu_item_list_show_all);
		mChk_list_only_billed = menu.findItem(R.id.mnu_item_list_show_only_billed);
		mChk_list_only_unbilled = menu.findItem(R.id.mnu_item_list_show_only_unbilled);

		mChk_list_all.setChecked(false);
		mChk_list_only_billed.setChecked(false);
		mChk_list_only_billed.setChecked(false);

		// instatiate Application class for global variables
		final GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
		switch (globalVariables.getFlagListFilter()) {
			case DatabaseManager.FLAG_BILLED_ALL:
				mChk_list_all.setChecked(true);
				break;
			case DatabaseManager.FLAG_BILLED_BILLED:
				mChk_list_only_billed.setChecked(true);
				break;
			case DatabaseManager.FLAG_BILLED_UNBILLED:
				mChk_list_only_unbilled.setChecked(true);
				break;
		}

		// Associate searchable configuration with the SearchView
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String query = "";
		// instatiate Application class for global variables
		final GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();

		switch (item.getItemId()) {
			case R.id.mnu_interventions_add:
				startActivity(new Intent(this, InterventionsEditActivity.class));
				return true;
			case R.id.mnu_back:
				finish();
				return true;
			case R.id.mnu_item_list_show_all:
				if (!item.isChecked()) {
					mChk_list_all.setChecked(true);
					mChk_list_only_billed.setChecked(false);
					mChk_list_only_unbilled.setChecked(false);
					globalVariables.setFlagListFilter(DatabaseManager.FLAG_BILLED_ALL);
				}
				listView.setAdapter(null);
				setupListView(listView);
				return true;
			case R.id.mnu_item_list_show_only_billed:
				if (!item.isChecked()) {
					mChk_list_all.setChecked(false);
					mChk_list_only_billed.setChecked(true);
					mChk_list_only_unbilled.setChecked(false);
					globalVariables.setFlagListFilter(DatabaseManager.FLAG_BILLED_BILLED);
				}
				listView.setAdapter(null);
				setupListView(listView);
				return true;
			case R.id.mnu_item_list_show_only_unbilled:
				if (!item.isChecked()) {
					mChk_list_all.setChecked(false);
					mChk_list_only_billed.setChecked(false);
					mChk_list_only_unbilled.setChecked(true);
					globalVariables.setFlagListFilter(DatabaseManager.FLAG_BILLED_UNBILLED);
				}
				listView.setAdapter(null);
				setupListView(listView);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info_row;
		final Interventions interventions;
		switch (item.getItemId()) {
			// ADD item
			case R.id.mnu_item_add:
				startInterventionsAddActivity();
				return true;
			// DELETE item
			case R.id.mnu_item_delete:
				info_row = (AdapterContextMenuInfo) item.getMenuInfo();
				interventions = (Interventions) listView.getAdapter().getItem((int) info_row.id);

				new AlertDialog.Builder(this)
						.setMessage(
								String.format(getString(R.string.notify_confirm_delete), interventions.getCustomer(),
										interventions.getStart()))
						.setNegativeButton(getString(R.string.caption_no), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).setPositiveButton(getString(R.string.caption_yes), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								DatabaseManager.getInstance().deleteInterventions(interventions);
								setupListView(listView);
							}
						}).create().show();

				return true;
			// EDIT item
			case R.id.mnu_item_edit:
				final Activity activity = this;
				info_row = (AdapterContextMenuInfo) item.getMenuInfo();
				interventions = (Interventions) listView.getAdapter().getItem((int) info_row.id);
				Intent intent = new Intent(activity, InterventionsEditActivity.class);
				intent.putExtra(Constants.KEY_INTERVENTIONS_ID, interventions.getId());
				startActivity(intent);
				return true;
			// SET BILLED item
			case R.id.mnu_item_set_billed:
				info_row = (AdapterContextMenuInfo) item.getMenuInfo();
				interventions = (Interventions) listView.getAdapter().getItem((int) info_row.id);
				interventions.setBilled(!interventions.getBilled());
				DatabaseManager.getInstance().updateInterventions(interventions);
				setupListView(listView);
				return true;
			// SEND EMAIL with item report
			case R.id.mnu_item_email_intervention:
				info_row = (AdapterContextMenuInfo) item.getMenuInfo();
				interventions = (Interventions) listView.getAdapter().getItem((int) info_row.id);
				String subject = String.format(
						getString(R.string.subject_intervention_report),
						interventions.getStart()
						);
				String text = String.format(
						getString(R.string.report_intervention),
							interventions.getCustomer(),
							interventions.getStart(),
							interventions.getEnd(),
							interventions.getNotes()
							) + 
						"\n\n------------------------------------------------------\n\n" +
						getString(R.string.report_footer) +
						"\n\n------------------------------------------------------\n\n";
							
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[] {interventions.getCustomer()});
				i.putExtra(Intent.EXTRA_SUBJECT, subject);
				i.putExtra(Intent.EXTRA_TEXT, text);
				startActivity(Intent.createChooser(i, getText(R.string.subject_select_email_app)));
				return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * setup
	 * @param contentView
	 */
	private void setupControls(ViewGroup contentView) {
		listView = (ListView) contentView.findViewById(R.id.lv_interventions);
		listView.setEmptyView(contentView.findViewById(android.R.id.empty));
		mBtn_interventions_add = (Button) contentView.findViewById(R.id.btn_interventions_add);
		mBtn_back = (Button) contentView.findViewById(R.id.btn_back);
	}

	/**
	 * setup action buttons
	 */
	public void setupControlsEvents() {
		// ADD
		mBtn_interventions_add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startInterventionsAddActivity();
			}
		});
		// BACK
		mBtn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * start ADD INTERVENTION
	 */
	private void startInterventionsAddActivity() {
		final Activity activity = this;
		Intent intent = new Intent(activity, InterventionsEditActivity.class);
		startActivity(intent);		
	}

	private void setupListView(ListView lv) {
		setupListView(lv,"");
	}

	private void setupListView(ListView lv, String query) {

		// instatiate Application class for global variables
		final GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
		int flagFilter = globalVariables.getFlagListFilter();

		// setup list ADAPTER
		final List<Interventions> items = DatabaseManager.getInstance().getSortedInterventions(flagFilter,query);
		final InterventionsAdapter adapter = new InterventionsAdapter(this, R.layout.interventions_list_item, items);
		lv.setAdapter(adapter);

		// setup list ITEM CLICK
		final Activity activity = this;
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// get item object from adapter
				Interventions interventions = adapter.getItem((int) id);
				Intent intent = new Intent(activity, InterventionsEditActivity.class);
				intent.putExtra(Constants.KEY_INTERVENTIONS_ID, interventions.getId());
				startActivity(intent);
			}
		});

		// setup list CONTEXT MENU
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				// super.onCreateContextMenu(menu, v, menuInfo);
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.interventions_list_context, menu);
			}
		});
	}

}
