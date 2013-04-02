package com.paypal.example.android.mpl.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

/**
 * This {@link AsyncTask} was designed to handle POST and GET operations and
 * return the results to a defined listener.
 * 
 * @author tmesserschmidt@paypal.com
 * 
 */
public class AsyncConnection extends AsyncTask<String, Void, String> {
	public static final String		METHOD_GET	= "GET";
	public static final String		METHOD_POST	= "POST";

	private AsyncConnectionListener	listener;

	public AsyncConnection(AsyncConnectionListener listener) {
		this.listener = listener;
	}

	@Override
	public String doInBackground(String... params) {
		final String method = params[0];
		final String urlString = params[1];

		final StringBuilder builder = new StringBuilder();

		try {
			final URL url = new URL(urlString);

			final HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			conn.setRequestMethod(method);

			if (method.equals(METHOD_POST)) {
				final String urlParams = params[2];
				conn.setRequestProperty(HTTP.CONTENT_LEN,
						"" + Integer.toString(urlParams.getBytes().length));
				System.out.println(urlParams);
				// Send request
				final DataOutputStream wr = new DataOutputStream(
						conn.getOutputStream());
				wr.writeBytes(urlParams);
				wr.flush();
				wr.close();
			}

			// Get Response
			final InputStream is = conn.getInputStream();
			final BufferedReader rd = new BufferedReader(new InputStreamReader(
					is));

			String line;
			while ((line = rd.readLine()) != null) {
				builder.append(line);
			}
			rd.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return builder.toString();
	}

	@Override
	public void onPostExecute(String result) {
		listener.connectionDone(result);
	}

	public interface AsyncConnectionListener {
		public void connectionDone(String result);
	}
}
