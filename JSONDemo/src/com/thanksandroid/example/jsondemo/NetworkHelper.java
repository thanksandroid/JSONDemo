package com.thanksandroid.example.jsondemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkHelper {
	// tag for logs
	public static final String TAG = NetworkHelper.class.getSimpleName();
	// Connection and read timeout values in milliseconds.
	public static final int CONNECTION_TIMEOUT = 10000;
	public static final int WAIT_RESPONSE_TIMEOUT = 30000;
	
	/**
	 * Check data connection is available or not.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo(); 
		if (activeNetworkInfo != null) {
			boolean networkAvailable = activeNetworkInfo.isAvailable();
			boolean networkConnected = activeNetworkInfo.isConnected();
			if (networkAvailable && networkConnected) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Executes an HTTP Get request, pass data as query parameters.
	 * 
	 * @param serviceUrl
	 * @param context
	 * @return
	 */
	public static Response doGet(String serviceUrl, Context context) {
		Response response = new Response();
		if (!isOnline(context)) {
			// no network connection
			response.setStatusCode(Constants.CONNECTION_ERROR);
			return response;
		}
		try {
			// set basic http params
			HttpParams httpParams = new BasicHttpParams();
			// set connection timeout
			HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
			// set socket timeout
			HttpConnectionParams.setSoTimeout(httpParams, WAIT_RESPONSE_TIMEOUT);
			// create http client object
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			// build an HttpPost object
			HttpGet httpGet = new HttpGet(serviceUrl);
			// execute get request
			HttpResponse httpReposne = httpClient.execute(httpGet);
			// read response into a string buffer
			String line = null;
			StringBuffer stringBuffer = new StringBuffer();
			InputStreamReader streamReader = new InputStreamReader(httpReposne.getEntity().getContent());
			BufferedReader reader = new BufferedReader(streamReader);
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line);
			}
			reader.close();
			// set response data
			response.setStatusCode(Constants.OK);
			response.setResponseText(stringBuffer.toString());
		}
		catch (SocketTimeoutException ex) {
			response.setStatusCode(Constants.READ_TIMEOUT);
			Log.e(TAG, ex.getMessage(), ex);
		}
		catch (ConnectTimeoutException ex) {
			response.setStatusCode(Constants.CONNECT_TIMEOUT);
			Log.e(TAG, ex.getMessage(), ex);
		}
		catch (ClientProtocolException ex) {
			response.setStatusCode(Constants.CLIENT_PROTOCOL_ERROR);
			Log.e(TAG, ex.getMessage(), ex);
		}
		catch (IOException ex) {
			response.setStatusCode(Constants.IO_ERROR);
			Log.e(TAG, ex.getMessage(), ex);
		}
		catch (Exception ex) {
			response.setStatusCode(Constants.SYSTEM_ERROR);
			Log.e(TAG, ex.getMessage(), ex);
		}
		return response;
	}
}
