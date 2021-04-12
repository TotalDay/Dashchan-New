package com.totalday.dashchannew.ui.navigator.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.totalday.dashchannew.C;
import com.totalday.dashchannew.content.database.ChanDatabase;
import com.totalday.dashchannew.util.ListViewUtils;
import com.totalday.dashchannew.widget.CursorAdapter;
import com.totalday.dashchannew.widget.DividerItemDecoration;
import com.totalday.dashchannew.widget.SimpleViewHolder;
import com.totalday.dashchannew.widget.ViewFactory;

import chan.util.CommonUtils;
import chan.util.StringUtils;

public class BoardsAdapter extends CursorAdapter<ChanDatabase.BoardCursor, RecyclerView.ViewHolder> {
	private final Callback callback;
	private final ChanDatabase.BoardItem boardItem = new ChanDatabase.BoardItem();
	public BoardsAdapter(Callback callback) {
		this.callback = callback;
	}

	public boolean isRealEmpty() {
		ChanDatabase.BoardCursor cursor = getCursor();
		return cursor == null || !cursor.hasItems;
	}

	private ChanDatabase.BoardItem copyItem(int position) {
		return boardItem.update(moveTo(position)).copy();
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return ListViewUtils.bind(new SimpleViewHolder(ViewFactory.makeSingleLineListItem(parent)),
				true, this::copyItem, callback);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		ChanDatabase.BoardItem boardItem = this.boardItem.update(moveTo(position));
		((TextView) holder.itemView).setText(StringUtils.formatBoardTitle("", boardItem.boardName, boardItem.extra1));
	}

	public DividerItemDecoration.Configuration configureDivider
			(DividerItemDecoration.Configuration configuration, int position) {
		String header = position + 1 < getItemCount() ? getItemHeader(position + 1) : null;
		if (C.API_LOLLIPOP) {
			return configuration.need(header != null);
		} else {
			return configuration.need(header == null);
		}
	}

	public String getItemHeader(int position) {
		ChanDatabase.BoardCursor cursor = getCursor();
		if (cursor != null && cursor.filtered) {
			return null;
		} else if (position == 0) {
			return StringUtils.nullIfEmpty(boardItem.update(moveTo(0)).category);
		} else {
			String previous = boardItem.update(moveTo(position - 1)).category;
			String current = boardItem.update(moveTo(position)).category;
			return CommonUtils.equals(previous, current) ? null : current;
		}
	}

	public interface Callback extends ListViewUtils.SimpleCallback<ChanDatabase.BoardItem> {}
}
