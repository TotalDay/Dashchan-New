package com.totalday.dashchannew.ui.preference;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.totalday.dashchannew.R;
import com.totalday.dashchannew.content.Preferences;
import com.totalday.dashchannew.ui.FragmentHandler;
import com.totalday.dashchannew.ui.preference.core.Preference;
import com.totalday.dashchannew.ui.preference.core.PreferenceFragment;
import com.totalday.dashchannew.util.SharedPreferences;

import java.util.Collection;

import chan.content.Chan;
import chan.content.ChanManager;

public class ChansFragment extends PreferenceFragment implements FragmentHandler.Callback {
	@Override
	protected SharedPreferences getPreferences() {
		return Preferences.PREFERENCES;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		updateList();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((FragmentHandler) requireActivity()).setTitleSubtitle(getString(R.string.forums), null);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!ChanManager.getInstance().getAvailableChans().iterator().hasNext()) {
			((FragmentHandler) requireActivity()).removeFragment();
		}
	}

	@Override
	public void onChansChanged(Collection<String> changed, Collection<String> removed) {
		removeAllPreferences();
		if (!updateList()) {
			((FragmentHandler) requireActivity()).removeFragment();
		}
	}

	private boolean updateList() {
		boolean hasChans = false;
		ChanManager manager = ChanManager.getInstance();
		for (Chan chan : manager.getAvailableChans()) {
			Preference<?> preference = addCategory(chan.configuration.getTitle(), manager.getIcon(chan));
			preference.setOnClickListener(p -> ((FragmentHandler) requireActivity())
					.pushFragment(new ChanFragment(chan.name)));
			hasChans = true;
		}
		return hasChans;
	}
}
