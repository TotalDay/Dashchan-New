package com.totalday.dashchannew.ui.preference.core;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.totalday.dashchannew.util.ConcurrentUtils;
import com.totalday.dashchannew.util.SharedPreferences;

import java.util.List;

import chan.util.CommonUtils;

public class ListPreference extends DialogPreference<String> {
	public final List<CharSequence> entries;
	public final List<String> values;

	public ListPreference(Context context, String key, String defaultValue, CharSequence title,
			List<CharSequence> entries, List<String> values) {
		super(context, key, defaultValue, title, p -> entries.get(getIndex((ListPreference) p)));
		if (entries.size() != values.size()) {
			throw new IllegalArgumentException();
		}
		this.entries = entries;
		this.values = values;
	}

	private static int getIndex(ListPreference preference) {
		int index = preference.values.indexOf(preference.getValue());
		if (index < 0) {
			index = preference.values.indexOf(preference.defaultValue);
		}
		return index;
	}

	@Override
	protected void extract(SharedPreferences preferences) {
		String value = preferences.getString(key, defaultValue);
		if (!values.contains(value)) {
			value = defaultValue;
		}
		setValue(value);
	}

	@Override
	protected void persist(SharedPreferences preferences) {
		preferences.edit().put(key, getValue()).close();
	}

	@Override
	protected AlertDialog.Builder configureDialog(Bundle savedInstanceState, AlertDialog.Builder builder) {
		return super.configureDialog(savedInstanceState, builder)
				.setSingleChoiceItems(CommonUtils.toArray(entries, CharSequence.class), getIndex(this), (d, which) -> {
					d.dismiss();
					ConcurrentUtils.HANDLER.post(() -> setValue(values.get(which)));
				});
	}
}
