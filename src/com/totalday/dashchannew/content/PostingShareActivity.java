package com.totalday.dashchannew.content;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.totalday.dashchannew.R;
import com.totalday.dashchannew.content.model.FileHolder;
import com.totalday.dashchannew.content.storage.DraftsStorage;
import com.totalday.dashchannew.ui.MainActivity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chan.util.StringUtils;

public class PostingShareActivity extends Activity {
	private static final Pattern PATTERN_HREF = Pattern.compile("<a .*href=([\"'])(.*?)\\1.*>");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DraftsStorage draftsStorage = DraftsStorage.getInstance();
		ArrayList<Uri> uris = null;
		Uri contentUri = null;
		Intent intent = getIntent();

		if (Intent.ACTION_SEND.equals(intent.getAction())) {
			Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
			if (uri != null) {
				uris = new ArrayList<>(1);
				uris.add(uri);
			} else {
				String text = StringUtils.emptyIfNull(intent.getStringExtra(Intent.EXTRA_SUBJECT)) + '\n'
						+ StringUtils.emptyIfNull(intent.getStringExtra(Intent.EXTRA_TEXT));
				Matcher matcher = PATTERN_HREF.matcher(StringUtils.linkify(text));
				if (matcher.find()) {
					contentUri = Uri.parse(matcher.group(2));
				}
			}
		} else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())) {
			uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		}

		int success = 0;
		if (uris != null && !uris.isEmpty()) {
			for (Uri uri : uris) {
				FileHolder fileHolder = FileHolder.obtain(uri);
				if (fileHolder != null && draftsStorage.storeFuture(fileHolder)) {
					success++;
				}
			}
		}

		if (success > 0) {
			Toast.makeText(this, R.string.draft_saved, Toast.LENGTH_SHORT).show();
		} else if (contentUri != null) {
			startActivity(new Intent(this, MainActivity.class).setData(contentUri));
		} else {
			Toast.makeText(this, R.string.unknown_address, Toast.LENGTH_SHORT).show();
		}
		finish();
	}
}
