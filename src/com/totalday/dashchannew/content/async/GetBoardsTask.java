package com.totalday.dashchannew.content.async;

import android.os.CancellationSignal;
import android.os.OperationCanceledException;

import com.totalday.dashchannew.content.database.ChanDatabase;

import java.util.List;

import chan.content.Chan;

public class GetBoardsTask extends ExecutorTask<Void, ChanDatabase.BoardCursor> {
	private final Callback callback;
	private final Chan chan;
	private final List<String> userBoardNames;
	private final String searchQuery;
	private final CancellationSignal signal = new CancellationSignal();
	public GetBoardsTask(Callback callback, Chan chan, List<String> userBoardNames, String searchQuery) {
		this.callback = callback;
		this.chan = chan;
		this.userBoardNames = userBoardNames;
		this.searchQuery = searchQuery;
	}

	@Override
	protected ChanDatabase.BoardCursor run() {
		try {
			if (userBoardNames != null) {
				return chan.configuration.getUserBoards(userBoardNames, searchQuery, signal);
			} else {
				return chan.configuration.getBoards(searchQuery, signal);
			}
		} catch (OperationCanceledException e) {
			return null;
		}
	}

	@Override
	public void cancel() {
		super.cancel();
		try {
			signal.cancel();
		} catch (Exception e) {
			// Ignore
		}
	}

	@Override
	protected void onCancel(ChanDatabase.BoardCursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	protected void onComplete(ChanDatabase.BoardCursor cursor) {
		callback.onGetBoardsResult(cursor);
	}

	public interface Callback {
		void onGetBoardsResult(ChanDatabase.BoardCursor cursor);
	}
}
