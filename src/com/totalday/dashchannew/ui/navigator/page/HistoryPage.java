package com.totalday.dashchannew.ui.navigator.page;

import android.app.AlertDialog;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.totalday.dashchannew.C;
import com.totalday.dashchannew.R;
import com.totalday.dashchannew.content.Preferences;
import com.totalday.dashchannew.content.async.GetHistoryTask;
import com.totalday.dashchannew.content.database.CommonDatabase;
import com.totalday.dashchannew.content.database.HistoryDatabase;
import com.totalday.dashchannew.content.storage.FavoritesStorage;
import com.totalday.dashchannew.ui.DialogMenu;
import com.totalday.dashchannew.ui.InstanceDialog;
import com.totalday.dashchannew.ui.navigator.adapter.HistoryAdapter;
import com.totalday.dashchannew.util.ConcurrentUtils;
import com.totalday.dashchannew.util.ResourceUtils;
import com.totalday.dashchannew.util.ViewUtils;
import com.totalday.dashchannew.widget.DividerItemDecoration;
import com.totalday.dashchannew.widget.HeaderItemDecoration;
import com.totalday.dashchannew.widget.ListPosition;
import com.totalday.dashchannew.widget.PaddedRecyclerView;

import chan.content.Chan;
import chan.util.StringUtils;

public class HistoryPage extends ListPage implements HistoryAdapter.Callback, GetHistoryTask.Callback {
	private String chanName;
	private String searchQuery;

	private GetHistoryTask task;
	private final Runnable updateHistoryRunnable = this::updateHistory;
	private boolean firstLoad = true;

	private static void showClearHistoryDialog(FragmentManager fragmentManager, String chanName) {
		new InstanceDialog(fragmentManager, null, provider -> new AlertDialog
				.Builder(provider.getContext())
				.setMessage(R.string.clear_history__sentence)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(android.R.string.ok, (d, w) -> CommonDatabase
						.getInstance().getHistory().clearHistory(chanName))
				.create());
	}

	private HistoryAdapter getAdapter() {
		return (HistoryAdapter) getRecyclerView().getAdapter();
	}

	@Override
	protected void onCreate() {
		PaddedRecyclerView recyclerView = getRecyclerView();
		recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
		if (!C.API_LOLLIPOP) {
			float density = ResourceUtils.obtainDensity(recyclerView);
			ViewUtils.setNewPadding(recyclerView, (int) (16f * density), null, (int) (16f * density), null);
		}
		chanName = Preferences.isMergeChans() ? null : getPage().chanName;
		searchQuery = getInitSearch().currentQuery;
		CommonDatabase.getInstance().getHistory().registerObserver(updateHistoryRunnable);
		HistoryAdapter adapter = new HistoryAdapter(getContext(), this, chanName);
		recyclerView.setAdapter(adapter);
		recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
				adapter::configureDivider));
		recyclerView.addItemDecoration(new HeaderItemDecoration(adapter::getItemHeader));
		recyclerView.setItemAnimator(null);
		switchProgress();
		updateHistory();
	}

	@Override
	protected void onDestroy() {
		CommonDatabase.getInstance().getHistory().unregisterObserver(updateHistoryRunnable);
		getAdapter().setCursor(null);
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	@Override
	public String obtainTitle() {
		return getString(R.string.history);
	}

	@Override
	public void onItemClick(HistoryDatabase.HistoryItem historyItem) {
		getUiManager().navigator().navigatePosts(historyItem.chanName, historyItem.boardName,
				historyItem.threadNumber, null, null);
	}

	@Override
	public boolean onItemLongClick(HistoryDatabase.HistoryItem historyItem) {
		showItemPopupMenu(getFragmentManager(), historyItem);
		return true;
	}

	private void showItemPopupMenu(FragmentManager fragmentManager, HistoryDatabase.HistoryItem historyItem) {
		new InstanceDialog(fragmentManager, null, provider -> {
			DialogMenu dialogMenu = new DialogMenu(provider.getContext());
			dialogMenu.add(R.string.copy_link, () -> {
				Uri uri = Chan.get(historyItem.chanName).locator.safe(true)
						.createThreadUri(historyItem.boardName, historyItem.threadNumber);
				if (uri != null) {
					StringUtils.copyToClipboard(getContext(), uri.toString());
				}
			});
			if (!FavoritesStorage.getInstance().hasFavorite(historyItem.chanName,
					historyItem.boardName, historyItem.threadNumber)) {
				dialogMenu.add(R.string.add_to_favorites, () -> FavoritesStorage.getInstance()
						.add(historyItem.chanName, historyItem.boardName, historyItem.threadNumber,
								historyItem.title, true));
			}
			dialogMenu.add(R.string.remove_from_history, () -> CommonDatabase.getInstance().getHistory()
					.remove(historyItem.chanName, historyItem.boardName, historyItem.threadNumber));
			return dialogMenu.create();
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.menu_search, 0, R.string.filter)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		menu.add(0, R.id.menu_clear, 0, R.string.clear_history);
		menu.addSubMenu(0, R.id.menu_appearance, 0, R.string.appearance);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_clear: {
				showClearHistoryDialog(getFragmentManager(), chanName);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onSearchQueryChange(String query) {
		searchQuery = query;
		updateHistory();
	}

	private void updateHistory() {
		if (task != null) {
			task.cancel();
		}
		task = new GetHistoryTask(this, chanName, searchQuery);
		task.execute(ConcurrentUtils.PARALLEL_EXECUTOR);
	}

	@Override
	public void onGetHistoryResult(HistoryDatabase.HistoryCursor cursor) {
		task = null;
		boolean firstLoad = this.firstLoad;
		this.firstLoad = false;
		getAdapter().setCursor(cursor);
		ListPosition listPosition = takeListPosition();
		if (cursor.hasItems) {
			switchList();
			if (firstLoad && listPosition != null) {
				listPosition.apply(getRecyclerView());
			}
		} else {
			switchError(R.string.history_is_empty);
		}
	}
}
