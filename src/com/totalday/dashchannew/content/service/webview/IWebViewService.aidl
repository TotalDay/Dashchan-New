package com.totalday.dashchannew.content.service.webview;

import com.totalday.dashchannew.content.service.webview.IRequestCallback;
import com.totalday.dashchannew.content.service.webview.WebViewExtra;

interface IWebViewService {
	boolean loadWithCookieResult(String requestId, String uriString, String userAgent,
			boolean proxySocks, String proxyHost, int proxyPort, boolean verifyCertificate, long timeout,
			in WebViewExtra extra, IRequestCallback requestCallback);
	void interrupt(String requestId);
}
