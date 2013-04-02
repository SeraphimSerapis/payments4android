package com.paypal.example.android.mpl;

import java.math.BigDecimal;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalInvoiceItem;
import com.paypal.android.MEP.PayPalPayment;
import com.paypal.example.android.mpl.Configuration.Language;
import com.paypal.example.android.mpl.PresetsFragment.PresetsHandler;
import com.paypal.example.android.mpl.model.Pizza;
import com.paypal.example.android.mpl.model.Pizza.Preset;
import com.paypal.example.android.mpl.model.Pizza.Topping;
import com.paypal.example.android.mpl.util.AccessHelper;
import com.paypal.example.android.mpl.util.AccessHelperConnect;
import com.paypal.example.android.mpl.util.AsyncConnection;
import com.paypal.example.android.mpl.util.AsyncConnection.AsyncConnectionListener;
import com.paypal.example.android.mpl.util.InitLibThread;
import com.paypal.example.android.mpl.util.InitLibThread.InitLibThreadListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;

public class MainActivity extends SlidingFragmentActivity implements
		InitLibThreadListener, PresetsHandler {
	public static Pizza				pizza				= new Pizza();
	private Handler					handler				= new Handler();
	private PresetsFragment			presetsFragment		= null;
	private ConfiguratorFragment	configFragment		= null;
	private Context					context				= null;
	private ProgressDialog			initProgress		= null;
	private LinearLayout			checkoutContainer	= null;
	private CheckoutButton			button				= null;
	private AccessHelper			helper				= null;
	private JSONObject				profile				= null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		setSlidingActionBarEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState != null) {
			restoreFragments(savedInstanceState);
		}

		initFragments();

		configureMenu();
		initUi();

		if (checkLogin() || handleIntent()) {
			new InitLibThread(context, this, Language.EN).start();
		} else {
			startActivity(new Intent(this, LoginActivity.class));
			this.finish();
		}
	}

	private void restoreFragments(Bundle savedInstanceState) {
		final FragmentManager supportFragmentManager = getSupportFragmentManager();
		if (savedInstanceState.containsKey("presets")) {
			presetsFragment = (PresetsFragment) supportFragmentManager
					.getFragment(savedInstanceState, "presets");
		}

		if (savedInstanceState.containsKey("config")) {
			configFragment = (ConfiguratorFragment) supportFragmentManager
					.getFragment(savedInstanceState, "config");
		}
	}

	private void initFragments() {
		FragmentManager supportFragmentManager = this
				.getSupportFragmentManager();
		FragmentTransaction transaction = null;

		setContentView(R.layout.activity_container);
		transaction = supportFragmentManager.beginTransaction();
		if (configFragment == null) {
			configFragment = new ConfiguratorFragment();
		}
		transaction.replace(R.id.fragment_config, configFragment);
		transaction.commit();

		setBehindContentView(R.layout.activity_behind);
		transaction = supportFragmentManager.beginTransaction();
		if (presetsFragment == null) {
			presetsFragment = new PresetsFragment(this);
		}
		transaction.replace(R.id.menu_frame, presetsFragment);
		transaction.commit();
	}

	private void configureMenu() {
		final SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				Crouton.cancelAllCroutons();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		dismissDialog();
		Crouton.clearCroutonsForActivity(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		moveTaskToBack(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				toggle();
				return true;
			case R.id.action_share:
				final Intent intent = new Intent(
						android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.putExtra(Intent.EXTRA_SUBJECT, "Ordered a Pizza!");
				intent.putExtra(
						Intent.EXTRA_TEXT,
						"I've just ordered a Pizza with this ingredients: "
								+ pizza.getDescription() + " for "
								+ pizza.getPrice() + " Euros");
				startActivity(Intent.createChooser(intent,
						"How do you want to share?"));
				return true;
		}
		return false;
	}

	public boolean handleIntent() {
		Intent intent = getIntent();
		if (intent != null && intent.getAction().equals(Intent.ACTION_VIEW)
				&& intent.getData().getPath().equals("/auth")) {
			helper = AccessHelperConnect.getInstance();

			final Uri uri = (intent.getData());
			setIntent(null);

			final String code = uri.getQueryParameter("code");

			final String urlParams = helper.getTokenServiceParameters(code);
			final String urlString = helper.getTokenServiceUrl();

			new AsyncConnection(new AsyncConnectionListener() {
				@Override
				public void connectionDone(String result) {
					try {
						final JSONObject object = new JSONObject(result);
						final String accessToken = object
								.getString("access_token");

						if (accessToken != null && !accessToken.equals("")) {
							getProfile(accessToken);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).execute(AsyncConnection.METHOD_POST, urlString, urlParams);
			return true;
		}
		return false;
	}

	private boolean checkLogin() {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (prefs.contains(Configuration.PREF_PROFILE)) {
			try {
				profile = new JSONObject(prefs.getString(
						Configuration.PREF_PROFILE, null));
				showCrouton(R.string.main_welcome_back,
						profile.getString("given_name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	private void getProfile(String accessToken) {
		final String urlString = helper.getProfileUrl(accessToken);

		new AsyncConnection(new AsyncConnectionListener() {
			@Override
			public void connectionDone(String result) {
				handleResult(result);
			}
		}).execute(AsyncConnection.METHOD_GET, urlString);
	}

	private void handleResult(String result) {
		try {
			final SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			prefs.edit().putString(Configuration.PREF_PROFILE, result).commit();
			profile = new JSONObject(result);
			showCrouton(R.string.main_hello, profile.getString("given_name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showCrouton(int res) {
		showCrouton(getString(res));
	}

	private void showCrouton(int res, String text) {
		showCrouton(getString(res, text));
	}

	private void showCrouton(String text) {
		Style style = new Style.Builder().setBackgroundColorValue(0xffffb300)
				.setTextColor(R.color.crouton_text)
				.setHeight(LayoutParams.WRAP_CONTENT).setDuration(5000)
				.setTextSize(40).build();
		Crouton.makeText(MainActivity.this, text.toUpperCase(Locale.US), style)
				.show();
	}

	/**
	 * Used to initialize and bind all the XML based views
	 */
	private void initUi() {
		checkoutContainer = (LinearLayout) findViewById(R.id.checkout_button_container);
	}

	private void addCheckoutButton() {
		/*
		 * Quick fix to remove an existing button. This is needed because
		 * sometimes the button cannot be triggered more than once.
		 */
		if (button != null) {
			checkoutContainer.removeView(button);
			button = null;
		}

		// Get the button
		PayPal instance = PayPal.getInstance();
		button = instance.getCheckoutButton(context, PayPal.BUTTON_294x45,
				CheckoutButton.TEXT_PAY);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handlePayment();
			}
		});

		// This code changes the width of the button to fill the screen's width.
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(layoutParams);

		checkoutContainer.addView(button);
	}

	/*
	 * MPL payment processing
	 */

	private void handlePayment() {
		final PayPalPayment payment = createPayment();

		// Start the payment intent and receive the result later
		final Intent checkoutIntent = PayPal.getInstance().checkout(payment,
				this);

		startActivityForResult(checkoutIntent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			switch (resultCode) {
				case Activity.RESULT_OK:
					// The payment succeeded.
					showCrouton(R.string.main_toast_payment_success);
					break;
				case Activity.RESULT_CANCELED:
					// The payment was canceled.
					showCrouton(R.string.main_toast_payment_canceled);
					break;
				case PayPalActivity.RESULT_FAILURE:
					/*
					 * The payment failed -- we get the error from the
					 * EXTRA_ERROR_ID and EXTRA_ERROR_MESSAGE.
					 */
					final String errorId = data
							.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
					final String errorMessage = data
							.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
					showCrouton("Error " + errorId + ", Msg: " + errorMessage);
			}
			addCheckoutButton();
		}
	}

	/*
	 * Listener implementation
	 */

	@Override
	public void initStart() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				initProgress = ProgressDialog.show(context,
						getString(R.string.progress_init_title),
						getString(R.string.progress_init_msg));
			}
		});
	}

	@Override
	public void initDone() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				dismissDialog();
				addCheckoutButton();
			}
		});
	}

	/*
	 * Helper methods
	 */

	private void dismissDialog() {
		if (initProgress != null && initProgress.isShowing()) {
			initProgress.dismiss();
			initProgress = null;
		}
	}

	/**
	 * Helper method to initialize a new {@link PayPalPayment}.
	 * 
	 * @return a new instance of {@link PayPalPayment} including the currently
	 *         selected {@link Pizza}
	 */
	private static PayPalPayment createPayment() {
		final PayPalPayment payment = new PayPalPayment();
		payment.setMerchantName("Tim's Pizza Heaven");
		payment.setCurrencyType(Configuration.CURRENCY);
		payment.setRecipient(Configuration.SAMPLE_RECIPIENT);
		payment.setPaymentType(PayPal.PAYMENT_TYPE_GOODS);
		payment.setSubtotal(new BigDecimal(pizza.getPrice()));

		final PayPalInvoiceData data = new PayPalInvoiceData();

		final PayPalInvoiceItem size = new PayPalInvoiceItem();
		size.setName(pizza.getSize().name());
		size.setTotalPrice(new BigDecimal(pizza.getSize().getPrice()));
		data.add(size);

		for (Topping topping : pizza.getToppings()) {
			final PayPalInvoiceItem item = new PayPalInvoiceItem();
			item.setName(topping.name());
			item.setTotalPrice(new BigDecimal(topping.getPrice()));
			data.add(item);
		}

		payment.setInvoiceData(data);
		return payment;
	}

	@Override
	public void onPresetSelected(String preset) {
		preset = preset.replaceAll(" ", "");
		configFragment.presetSelected(Preset.valueOf(preset));
	}
}
