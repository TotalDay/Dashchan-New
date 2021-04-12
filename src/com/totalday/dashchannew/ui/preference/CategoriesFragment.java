package com.totalday.dashchannew.ui.preference;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;

import com.totalday.dashchannew.C;
import com.totalday.dashchannew.R;
import com.totalday.dashchannew.content.Preferences;
import com.totalday.dashchannew.ui.FragmentHandler;
import com.totalday.dashchannew.ui.preference.core.Preference;
import com.totalday.dashchannew.ui.preference.core.PreferenceFragment;
import com.totalday.dashchannew.util.ResourceUtils;
import com.totalday.dashchannew.util.SharedPreferences;

import java.util.Iterator;

import chan.content.Chan;
import chan.content.ChanManager;

public class CategoriesFragment extends PreferenceFragment {
	private Preference<Void> compatibilityPreference;

	@Override
	protected SharedPreferences getPreferences() {
		return Preferences.PREFERENCES;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Iterator<Chan> chans = ChanManager.getInstance().getAvailableChans().iterator();
		boolean hasChan = chans.hasNext();
		String singleChanName = hasChan ? chans.next().name : null;
		boolean hasMultipleChans = hasChan && chans.hasNext();
		addCategory(R.string.general, R.drawable.ic_map)
				.setOnClickListener(p -> ((FragmentHandler) requireActivity())
						.pushFragment(new GeneralFragment()));
		if (hasMultipleChans) {
			addCategory(R.string.forums, R.drawable.ic_public)
					.setOnClickListener(p -> ((FragmentHandler) requireActivity())
							.pushFragment(new ChansFragment()));
		} else if (hasChan) {
			addCategory(R.string.forum, R.drawable.ic_public)
					.setOnClickListener(p -> ((FragmentHandler) requireActivity())
							.pushFragment(new ChanFragment(singleChanName)));
		}
		compatibilityPreference = addCategory(R.string.compatibility, R.drawable.ic_verified);
		compatibilityPreference.setOnClickListener(p -> ((FragmentHandler) requireActivity())
				.pushFragment(new CompatibilityFragment()));
		addCategory(R.string.user_interface, R.drawable.ic_color_lens)
				.setOnClickListener(p -> ((FragmentHandler) requireActivity())
						.pushFragment(new InterfaceFragment()));
		addCategory(R.string.contents, R.drawable.ic_local_library)
				.setOnClickListener(p -> ((FragmentHandler) requireActivity())
						.pushFragment(new ContentsFragment()));
		addCategory(R.string.media, R.drawable.ic_save)
				.setOnClickListener(p -> ((FragmentHandler) requireActivity())
						.pushFragment(new MediaFragment()));
		addCategory(R.string.autohide, R.drawable.ic_custom_fork)
				.setOnClickListener(p -> ((FragmentHandler) requireActivity())
						.pushFragment(new AutohideFragment()));
		addCategory(R.string.about, R.drawable.ic_info)
				.setOnClickListener(p -> ((FragmentHandler) requireActivity())
						.pushFragment(new AboutFragment()));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		compatibilityPreference = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((FragmentHandler) requireActivity()).setTitleSubtitle(getString(R.string.preferences), null);
	}

	@Override
	public void onResume() {
		super.onResume();

		boolean hasIssues = C.API_NOUGAT_MR1 && !Settings.canDrawOverlays(requireContext());
		setCategoryTint(compatibilityPreference, hasIssues ? ColorStateList.valueOf(ResourceUtils
				.getColor(requireContext(), R.attr.colorTextError)) : null);
	}
}
