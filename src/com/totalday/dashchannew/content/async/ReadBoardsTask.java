package com.totalday.dashchannew.content.async;

import com.totalday.dashchannew.content.database.ChanDatabase;
import com.totalday.dashchannew.content.model.ErrorItem;

import chan.content.Chan;
import chan.content.ChanPerformer;
import chan.content.ExtensionException;
import chan.content.InvalidResponseException;
import chan.content.model.BoardCategory;
import chan.http.HttpException;
import chan.http.HttpHolder;

public class ReadBoardsTask extends HttpHolderTask<Void, ErrorItem> {
	private final Callback callback;
	private final Chan chan;

	public ReadBoardsTask(Callback callback, Chan chan) {
		super(chan);
		this.callback = callback;
		this.chan = chan;
	}

	@Override
	protected ErrorItem run(HttpHolder holder) {
		try {
			ChanPerformer.ReadBoardsResult result = chan.performer.safe()
					.onReadBoards(new ChanPerformer.ReadBoardsData(holder));
			BoardCategory[] boardCategories = result != null ? result.boardCategories : null;
			if (boardCategories != null && boardCategories.length == 0) {
				boardCategories = null;
			}
			if (boardCategories != null) {
				chan.configuration.updateFromBoards(boardCategories);
			}
			if (!ChanDatabase.getInstance().setBoards(chan.name, boardCategories)) {
				return new ErrorItem(ErrorItem.Type.EMPTY_RESPONSE);
			}
			return null;
		} catch (ExtensionException | HttpException | InvalidResponseException e) {
			return e.getErrorItemAndHandle();
		} finally {
			chan.configuration.commit();
		}
	}

	@Override
	protected void onComplete(ErrorItem result) {
		if (result == null) {
			callback.onReadBoardsSuccess();
		} else {
			callback.onReadBoardsFail(result);
		}
	}

	public interface Callback {
		void onReadBoardsSuccess();
		void onReadBoardsFail(ErrorItem errorItem);
	}
}
