package com.paypal.example.android.mpl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.paypal.example.android.mpl.model.Pizza;
import com.paypal.example.android.mpl.model.Pizza.Preset;
import com.paypal.example.android.mpl.model.Pizza.Size;
import com.paypal.example.android.mpl.model.Pizza.Topping;

public class ConfiguratorFragment extends SherlockListFragment {
	private final Handler				handler	= new Handler();
	private View						view;
	private Spinner						sizeSpinner;
	private ListView					toppingsList;
	private ArrayAdapter<CharSequence>	sizeAdapter;
	private ArrayAdapter<CharSequence>	toppingsAdapter;
	private TextView					priceTextNormal;
	private TextView					priceTextHighlight;
	private TextSwitcher				switcher;
	private Context						context;
	private Pizza						pizza;
	private boolean						next	= true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_configurator, container,
				false);
		context = getActivity();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUi(view);

		if (savedInstanceState != null) {
			pizza = (Pizza) savedInstanceState.getSerializable("pizza");

		}

		if (pizza != null) {
			for (Topping topping : pizza.getToppings()) {
				selectTopping(topping);
			}

			updatePrice(true);
			toppingsAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("pizza", MainActivity.pizza);
	}

	/**
	 * Used to initialize and bind all the XML based views
	 */
	private void initUi(View view) {
		sizeSpinner = (Spinner) view.findViewById(R.id.main_size_spinner);
		sizeAdapter = ArrayAdapter.createFromResource(context, R.array.sizes,
				R.layout.spinner_text);
		sizeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sizeSpinner.setAdapter(sizeAdapter);

		sizeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				MainActivity.pizza.setSize(Size.valueOf(sizeSpinner
						.getSelectedItem().toString()));
				setPrice(false);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Nothing
			}
		});

		toppingsList = getListView();
		toppingsAdapter = ArrayAdapter.createFromResource(context,
				R.array.toppings,
				android.R.layout.simple_list_item_multiple_choice);
		toppingsList.setAdapter(toppingsAdapter);
		toppingsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

		toppingsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				updatePrice(true);
			}
		});

		switcher = (TextSwitcher) view
				.findViewById(R.id.main_configuration_price);
		priceTextNormal = (TextView) switcher
				.findViewById(R.id.main_configuration_price_normal);
		priceTextHighlight = (TextView) switcher
				.findViewById(R.id.main_configuration_price_highlight);
	}

	private void setPrice(boolean animate) {
		// Cancel any animation
		switcher.clearAnimation();

		priceTextNormal.setText(getString(
				R.string.main_configuration_price_string,
				MainActivity.pizza.getPrice()));
		priceTextHighlight.setText(getString(
				R.string.main_configuration_price_string,
				MainActivity.pizza.getPrice()));

		if (animate) {
			toggleSwitcher(0);
			toggleSwitcher(400);
		}
	}

	private void updatePrice(boolean animate) {
		final SparseBooleanArray checkedItemPositions = toppingsList
				.getCheckedItemPositions();

		for (int i = 0; i < toppingsAdapter.getCount(); i++) {
			final Topping tmpTopping = Topping.valueOf(toppingsAdapter.getItem(
					i).toString());
			if (checkedItemPositions.get(i)) {
				MainActivity.pizza.addTopping(tmpTopping);
			} else {
				MainActivity.pizza.removeTopping(tmpTopping);
			}
		}

		setPrice(animate);
	}

	private void toggleSwitcher(int delayInMs) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (next) {
					switcher.showNext();
				} else {
					switcher.showPrevious();
				}
			}
		}, delayInMs);
		next = !next;
	}

	private void selectTopping(Topping topping) {
		for (int i = 0; i < toppingsAdapter.getCount(); i++) {
			final String tmpTopping = toppingsAdapter.getItem(i).toString();
			if (tmpTopping.equals(topping.toString())) {
				getListView().setItemChecked(i, true);
				toppingsAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	public void presetSelected(Preset preset) {
		for (int i = 0; i < toppingsAdapter.getCount(); i++) {
			getListView().setItemChecked(i, false);
		}
		for (Topping topping : preset.getToppings()) {
			selectTopping(topping);
		}

		updatePrice(true);
	}
}
