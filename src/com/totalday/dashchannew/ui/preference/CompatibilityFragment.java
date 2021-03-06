package com.totalday.dashchannew.ui.preference;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;

import com.totalday.dashchannew.C;
import com.totalday.dashchannew.R;
import com.totalday.dashchannew.content.Preferences;
import com.totalday.dashchannew.ui.FragmentHandler;
import com.totalday.dashchannew.ui.preference.core.CheckPreference;
import com.totalday.dashchannew.ui.preference.core.PreferenceFragment;
import com.totalday.dashchannew.util.SharedPreferences;
import com.totalday.dashchannew.widget.ClickableToast;

public class CompatibilityFragment extends PreferenceFragment {
	private CheckPreference drawOverOtherApplicationsPreference;

	@Override
	protected SharedPreferences getPreferences() {
		return Preferences.PREFERENCES;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (C.API_NOUGAT_MR1) {
			drawOverOtherApplicationsPreference = addCheck(false, "draw_over_other_applications", false,
					R.string.draw_over_other_applications, R.string.draw_over_other_applications__summary);
			drawOverOtherApplicationsPreference.setOnClickListener(p -> {
				try {
					startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
							.setData(Uri.parse("package:" + requireContext().getPackageName())));
				} catch (ActivityNotFoundException e) {
					ClickableToast.show(R.string.unknown_address);
				}
			});
		}
		addHeader(R.string.additional);
		addCheck(true, Preferences.KEY_USE_GMS_PROVIDER, Preferences.DEFAULT_USE_GMS_PROVIDER,
				R.string.use_gms_security_provider, R.string.use_gms_security_provider__summary);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		drawOverOtherApplicationsPreference = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((FragmentHandler) requireActivity()).setTitleSubtitle(getString(R.string.compatibility), null);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (drawOverOtherApplicationsPreference != null) {
			drawOverOtherApplicationsPreference.setValue(C.API_NOUGAT_MR1 &&
					Settings.canDrawOverlays(requireContext()));
		}
	}
}
