package com.paypal.example.android.mpl;

import com.paypal.android.MEP.PayPal;

public class Configuration {
	public static final String	PREF_PROFILE		= "PROFILE";
	public static final String	CURRENCY			= "EUR";
	public static final String	SAMPLE_RECIPIENT	= "foo@bar.com";

	/*
	 * //////////////////////////////////////////////// Your credentials -
	 * change for live environment
	 */// /////////////////////////////////////////////
	public static final String	APP_ID				= "APP-80W284485P519543T";
	public static final int		SERVER				= PayPal.ENV_SANDBOX;

	// Get these at devportal.x.com
	public static final String	CLIENT_ID			= "YOUR_ID";
	public static final String	CLIENT_SECRET		= "YOUR_SECRET";

	public static enum Language {
		DE("de_DE"), EN("en_US");

		private String	countryCode;

		private Language(String language) {
			this.countryCode = language;
		}

		public String getCode() {
			return this.countryCode;
		}
	}
}
