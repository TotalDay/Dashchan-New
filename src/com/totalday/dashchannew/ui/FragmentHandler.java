package com.totalday.dashchannew.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.totalday.dashchannew.content.model.PostNumber;
import com.totalday.dashchannew.content.service.DownloadService;
import com.totalday.dashchannew.util.ResourceUtils;

import java.util.Collection;

import chan.content.ChanLocator;

public interface FragmentHandler {
	void setTitleSubtitle(CharSequence title, CharSequence subtitle);

	ViewGroup getToolbarView();

	FrameLayout getToolbarExtra();

	Context getToolbarContext();

	default Drawable getActionBarIcon(int attr) {
		return ResourceUtils.getActionBarIcon(getToolbarContext(), attr);
	}

	void pushFragment(ContentFragment fragment);

	void removeFragment();

	DownloadService.Binder getDownloadBinder();

	boolean requestStorage();

	void navigateTargetAllowReturn(String chanName, ChanLocator.NavigationData navigationData);

	void scrollToPost(String chanName, String boardName, String threadNumber, PostNumber postNumber);

	Collection<DrawerForm.Page> obtainDrawerPages();

	void setActionBarLocked(String locker, boolean locked);

	void setNavigationAreaLocked(String locker, boolean locked);
	interface Callback {
		default void onChansChanged(Collection<String> changed, Collection<String> removed) {}
		default void onStorageRequestResult() {}
	}
}
