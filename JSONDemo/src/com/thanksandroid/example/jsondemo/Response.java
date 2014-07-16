package com.thanksandroid.example.jsondemo;

public class Response {

	private int statusCode;
	private String responseText;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public int getMessageId() {
		int resId = 0;
		switch (statusCode) {
		case Constants.CONNECTION_ERROR:
			resId = R.string.err_no_internet;
			break;
		case Constants.CONNECT_TIMEOUT:
			resId = R.string.err_timeout;
			break;
		case Constants.READ_TIMEOUT:
			resId = R.string.err_timeout;
			break;
		case Constants.IO_ERROR:
			resId = R.string.err_io;
			break;
		case Constants.CLIENT_PROTOCOL_ERROR:
			resId = R.string.err_protocol;
			break;
		default:
			resId = R.string.err_unknown;
			break;
		}
		return resId;
	}
}
