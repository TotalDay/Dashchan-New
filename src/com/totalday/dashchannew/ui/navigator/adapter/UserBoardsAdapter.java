package com.totalday.dashchannew.ui.navigator.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.totalday.dashchannew.content.database.ChanDatabase;
import com.totalday.dashchannew.util.ListViewUtils;
import com.totalday.dashchannew.widget.CursorAdapter;
import com.totalday.dashchannew.widget.SimpleViewHolder;
import com.totalday.dashchannew.widget.ViewFactory;

import chan.util.StringUtils;

public class UserBoardsAdapter extends CursorAdapter<ChanDatabase.BoardCursor, RecyclerView.ViewHolder> {
	private final Callback callback;
	private final ChanDatabase.BoardItem boardItem = new ChanDatabase.BoardItem();
	public UserBoardsAdapter(Callback callback) {
		this.callback = callback;
	}

	private ChanDatabase.BoardItem copyItem(int position) {
		return boardItem.update(moveTo(position)).copy();
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return ListViewUtils.bind(new SimpleViewHolder(ViewFactory.makeTwoLinesListItem(parent, 0).view),
				true, this::copyItem, callback);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		ChanDatabase.BoardItem boardItem = this.boardItem.update(moveTo(position));
		ViewFactory.TwoLinesViewHolder viewHolder = (ViewFactory.TwoLinesViewHolder) holder.itemView.getTag();
		viewHolder.text1.setText(StringUtils.formatBoardTitle("", boardItem.boardName, boardItem.extra1));
		if (!StringUtils.isEmpty(boardItem.extra2)) {
			viewHolder.text2.setVisibility(View.VISIBLE);
			viewHolder.text2.setText(boardItem.extra2);
		} else {
			viewHolder.text2.setVisibility(View.GONE);
		}
	}

	public interface Callback extends ListViewUtils.SimpleCallback<ChanDatabase.BoardItem> {}
}
