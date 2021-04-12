package com.totalday.dashchannew.ui.posting.text;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.totalday.dashchannew.C;
import com.totalday.dashchannew.R;
import com.totalday.dashchannew.util.ResourceUtils;
import com.totalday.dashchannew.widget.ErrorEditTextSetter;
import com.totalday.dashchannew.widget.ThemeEngine;

public class NameEditWatcher implements TextWatcher {
	private final boolean watchTripcodeWarning;
	private final EditText nameView;
	private final TextView tripcodeWarning;

	private final Runnable layoutCallback;
	private boolean error = false;
	private ForegroundColorSpan tripcodeSpan;
	private ErrorEditTextSetter errorSetter;
	public NameEditWatcher(boolean watchTripcodeWarning, EditText nameView, TextView tripcodeWarning,
			Runnable layoutCallback) {
		this.watchTripcodeWarning = watchTripcodeWarning;
		this.nameView = nameView;
		this.tripcodeWarning = tripcodeWarning;
		this.layoutCallback = layoutCallback;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void afterTextChanged(Editable s) {
		int index = s.toString().indexOf('#');
		if (watchTripcodeWarning) {
			boolean error = index >= 0;
			if (this.error != error) {
				if (C.API_LOLLIPOP) {
					if (errorSetter == null) {
						errorSetter = new ErrorEditTextSetter(nameView);
					}
					errorSetter.setError(error);
				}
				tripcodeWarning.setVisibility(error ? View.VISIBLE : View.GONE);
				layoutCallback.run();
				this.error = error;
			}
		}
		if (tripcodeSpan != null) {
			s.removeSpan(tripcodeSpan);
		}
		if (index >= 0) {
			if (tripcodeSpan == null) {
				tripcodeSpan = new ForegroundColorSpan(watchTripcodeWarning
						? ResourceUtils.getColor(nameView.getContext(), R.attr.colorTextError)
						: ThemeEngine.getTheme(nameView.getContext()).tripcode);
			}
			s.setSpan(tripcodeSpan, index, s.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
}
