package com.paypal.example.android.mpl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.paypal.example.android.mpl.util.AccessHelper;
import com.paypal.example.android.mpl.util.AccessHelperConnect;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends SherlockFragmentActivity {
	/**
	 * Helper class for PayPal Access usage
	 */
	private AccessHelper	helper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		helper = AccessHelperConnect.init(Configuration.CLIENT_ID,
				Configuration.CLIENT_SECRET, getString(R.string.callback_url));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	/**
	 * This method is being called whenever the user clicks on the PayPal Access
	 * button to login.
	 * 
	 * @param accessButton
	 *            the ImageView
	 */
	public void onAccessClick(View accessButton) {
		Intent webIntent = new Intent(Intent.ACTION_VIEW);
		webIntent.setData(Uri.parse(helper.getAuthUrl()));
		webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		LoginActivity.this.startActivity(webIntent);
		this.finish();
	}
}
