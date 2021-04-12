package com.totalday.dashchannew.ui.posting;

import android.util.Pair;

import java.util.List;

import chan.content.ChanConfiguration;

public interface PostingDialogCallback {
	AttachmentHolder getAttachmentHolder(int index);
	List<Pair<String, String>> getAttachmentRatingItems();
	ChanConfiguration.Posting getPostingConfiguration();
}
