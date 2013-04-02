package com.paypal.example.android.mpl.util;

import android.content.Context;

import com.paypal.android.MEP.PayPal;
import com.paypal.example.android.mpl.Configuration;
import com.paypal.example.android.mpl.Configuration.Language;

public class InitLibThread extends Thread {
	private Context					context;
	private InitLibThreadListener	listener;
	private Language				language;

	/**
	 * Used to initialize a new Thread including a {@link Context} and an
	 * {@link InitLibThreadListener}.
	 * 
	 * @param context
	 *            the {@link Context}
	 * @param listener
	 *            the {@link InitLibThreadListener}
	 */
	public InitLibThread(Context context, InitLibThreadListener listener,
			Language language) {
		this.context = context;
		this.listener = listener;
		this.language = language;
	}

	@Override
	public void run() {
		listener.initStart();
		PayPal instance = PayPal.getInstance();
		if (instance == null) {
			instance = PayPal.initWithAppID(context, Configuration.APP_ID,
					Configuration.SERVER);
			instance.setLanguage(language.getCode());

			// Optional parameters
			instance.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);
			instance.setShippingEnabled(true);
			instance.setDynamicAmountCalculationEnabled(false);
		}
		listener.initDone();
	}

	/**
	 * This interface enables to listen for updates during the initialization of
	 * the Mobile Payments Library.
	 * 
	 * @author tmesserschmidt@paypal.com
	 * 
	 */
	public interface InitLibThreadListener {
		/**
		 * Used after the start of the initialization of the Library.
		 */
		public void initStart();

		/**
		 * Used when the initialization is done.
		 */
		public void initDone();
	}
}
