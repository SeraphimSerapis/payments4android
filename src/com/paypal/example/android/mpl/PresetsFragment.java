package com.paypal.example.android.mpl;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PresetsFragment extends ListFragment {
	private static PresetsHandler		handler;
	private View						view;
	private Context						context;
	private ListView					presetsList;
	private ArrayAdapter<CharSequence>	presetsAdapter;

	public PresetsFragment() {
	}

	public PresetsFragment(PresetsHandler handler) {
		PresetsFragment.handler = handler;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_presets, container, false);
		context = getActivity();
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		presetsList = getListView();
		presetsAdapter = ArrayAdapter.createFromResource(context,
				R.array.presets, android.R.layout.simple_list_item_1);
		presetsList.setAdapter(presetsAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		handler.onPresetSelected(presetsAdapter.getItem(position).toString());
	}

	public interface PresetsHandler {
		public void onPresetSelected(String preset);
	}
}
