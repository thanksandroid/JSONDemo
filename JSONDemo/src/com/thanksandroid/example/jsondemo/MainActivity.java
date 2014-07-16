package com.thanksandroid.example.jsondemo;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView textView;
	protected ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textView = (TextView) findViewById(R.id.response);
	}

	// called on click of Send Request button
	public void sendRequest(View view) {
		// create an async task
		HttpGetTask task = new HttpGetTask();
		// url for http get
		String url = Constants.SITE_HOST + Constants.HTTP_GET;
		// execute async task
		task.execute(url);
	}

	// async task to make network requests in separate thread
	private class HttpGetTask extends AsyncTask<String, Void, Response> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// display a ProgressDialog with message
			showProgressDialog("", getString(R.string.please_wait));
		}

		@Override
		protected Response doInBackground(String... params) {
			// call method to initiate HTTP request
			return NetworkHelper.doGet(params[0], getApplicationContext());
		}

		@Override
		protected void onPostExecute(Response result) {
			hideProgressDialog();
			super.onPostExecute(result);
			// show the result
			if (result.getStatusCode() == Constants.OK) {
				String json = result.getResponseText();
				try {
					JSONObject jsonObject = new JSONObject(json);
					parseDetails(jsonObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				showToast(getString(result.getMessageId()));
			}
		}
	}

	private void parseDetails(JSONObject jsonObject) {

		if (jsonObject == null)
			return;

		String text = "";

		try {
			// Read String type value "status"
			String status = jsonObject.getString("status");
			text += " Status: " + status;

			// Read JSONObject type value "details"
			JSONObject detailsObject = jsonObject.getJSONObject("details");
			// Read int type value "count"
			int count = detailsObject.getInt("count");
			// Read String type value "time"
			String dateTime = detailsObject.getString("time");

			text += "\n Count: " + count;
			text += "\n Date Time: " + dateTime;

			// Read JSONArray type value "users"
			JSONArray usersArray = jsonObject.getJSONArray("users");

			JSONObject userObject;
			String email;
			String dob;
			// Loop through JSON array
			for (int i = 0; i < usersArray.length(); i++) {
				// get JSONObject a index i of JSONArray
				userObject = usersArray.getJSONObject(i);
				// Read values
				email = userObject.getString("email");
				dob = userObject.getString("dob");
				text += "\n\n User " + (i+1) + " Email: " + email;
				text += "\n User " + (i+1) + " DOB: " + dob;
			}

			textView.setText(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private JSONObject writeJSON() {
		JSONObject jsonObject = new JSONObject();
		try {

			// create details object
			JSONObject detailsObject = new JSONObject();
			detailsObject.put("count", 3);
			detailsObject.put("time", "Jul 15 2014 21:41:39");

			// create users array
			JSONArray usersArray = new JSONArray();
			// create first user object and add it to users array
			JSONObject userObject = new JSONObject();
			userObject.put("email", "user1@thanksandroid.com");
			userObject.put("dob", "03-21-1988");
			usersArray.put(userObject);

			// create second user object and add it to users array
			userObject = new JSONObject();
			userObject.put("email", "user2@thanksandroid.com");
			userObject.put("dob", "05-24-1987");
			usersArray.put(userObject);

			// create third user object and add it to users array
			userObject = new JSONObject();
			userObject.put("email", "user3@thanksandroid.com");
			userObject.put("dob", "01-14-1987");
			usersArray.put(userObject);

			// Put all the values in the main object
			jsonObject.put("status", "OK");
			jsonObject.put("details", detailsObject);
			jsonObject.put("users", usersArray);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	protected void showProgressDialog(String title, String message) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = ProgressDialog.show(this, title, message);
	}

	protected void hideProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = null;
	}

	protected void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPause() {
		hideProgressDialog();
		super.onPause();
	}
}
