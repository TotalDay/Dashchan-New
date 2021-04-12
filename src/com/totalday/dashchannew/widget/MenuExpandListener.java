package com.totalday.dashchannew.widget;

import android.view.MenuItem;

public class MenuExpandListener implements MenuItem.OnActionExpandListener {
	private final Callback callback;

	public MenuExpandListener(Callback callback) {
		this.callback = callback;
	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem menuItem) {
		return callback.onChange(menuItem, true);
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem menuItem) {
		return callback.onChange(menuItem, false);
	}

	public interface Callback {
		boolean onChange(MenuItem menuItem, boolean expand);
	}
}
