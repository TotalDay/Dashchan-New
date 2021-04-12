package com.totalday.dashchannew.content.service.webview;

interface IRequestCallback {
	boolean onPageFinished(String uriString, String cookie, String title);
	boolean onLoad(String uriString);

	String onRecaptchaV2(String apiKey, boolean invisible, String referer);
	String onHcaptcha(String apiKey, String referer);
}
