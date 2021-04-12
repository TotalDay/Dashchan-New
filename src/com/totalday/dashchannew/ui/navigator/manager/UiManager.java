package com.totalday.dashchannew.ui.navigator.manager;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.totalday.dashchannew.content.model.AttachmentItem;
import com.totalday.dashchannew.content.model.GalleryItem;
import com.totalday.dashchannew.content.model.PostItem;
import com.totalday.dashchannew.content.model.PostNumber;
import com.totalday.dashchannew.content.service.DownloadService;
import com.totalday.dashchannew.content.service.WatcherService;
import com.totalday.dashchannew.ui.InstanceDialog;
import com.totalday.dashchannew.ui.gallery.GalleryOverlay;
import com.totalday.dashchannew.ui.posting.Replyable;
import com.totalday.dashchannew.util.ListViewUtils;
import com.totalday.dashchannew.util.WeakObservable;
import com.totalday.dashchannew.widget.CommentTextView;
import com.totalday.dashchannew.widget.ThemeEngine;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import chan.content.ChanLocator;

public class UiManager {
	private final Context context;
	private final ViewUnit viewUnit;
	private final DialogUnit dialogUnit;
	private final InteractionUnit interactionUnit;
	private final WeakObservable<Observer> observable = new WeakObservable<>();

	private final Callback callback;
	private final LocalNavigator localNavigator;

	public UiManager(Context context, Callback callback, LocalNavigator localNavigator) {
		this.context = context;
		viewUnit = new ViewUnit(this);
		dialogUnit = new DialogUnit(this);
		interactionUnit = new InteractionUnit(this);
		this.callback = callback;
		this.localNavigator = localNavigator;
	}

	public static UiManager extract(InstanceDialog.Provider provider) {
		FragmentActivity activity = provider.getActivity();
		UiManagerViewModel viewModel = new ViewModelProvider(activity).get(UiManagerViewModel.class);
		UiManager uiManager = viewModel.uiManager != null ? viewModel.uiManager.get() : null;
		return uiManager != null && uiManager.context == activity ? uiManager : null;
	}

	Context getContext() {
		return context;
	}

	public ViewUnit view() {
		return viewUnit;
	}

	public DialogUnit dialog() {
		return dialogUnit;
	}

	public InteractionUnit interaction() {
		return interactionUnit;
	}

	public Callback callback() {
		return callback;
	}

	public LocalNavigator navigator() {
		return localNavigator;
	}

	public void sendPostItemMessage(View view, Message message) {
		Holder holder = ListViewUtils.getViewHolder(view, Holder.class);
		sendPostItemMessage(holder.getPostItem(), message);
	}

	public void sendPostItemMessage(PostItem postItem, Message message) {
		for (Observer observer : observable) {
			observer.onPostItemMessage(postItem, message);
		}
	}

	public void reloadAttachmentItem(AttachmentItem attachmentItem) {
		Iterator<Observer> iterator = observable.iterator();
		Observer observer = null;
		while (iterator.hasNext()) {
			observer = iterator.next();
		}
		if (observer != null) {
			observer.onReloadAttachmentItem(attachmentItem);
		}
	}

	public WeakObservable<Observer> observable() {
		return observable;
	}

	public void attach(FragmentActivity activity) {
		UiManagerViewModel viewModel = new ViewModelProvider(activity).get(UiManagerViewModel.class);
		viewModel.uiManager = new WeakReference<>(this);
	}

	public enum Message {
		POST_INVALIDATE_ALL_VIEWS,
		INVALIDATE_COMMENT_VIEW,
		PERFORM_SWITCH_USER_MARK,
		PERFORM_SWITCH_HIDE,
		PERFORM_HIDE_REPLIES,
		PERFORM_HIDE_NAME,
		PERFORM_HIDE_SIMILAR,
		PERFORM_GO_TO_POST
	}

	public enum Selection {DISABLED, NOT_SELECTED, SELECTED, THREADSHOT}

	public interface Observer {
		default void onPostItemMessage(PostItem postItem, Message message) {}
		default void onReloadAttachmentItem(AttachmentItem attachmentItem) {}
	}

	public interface PostsProvider extends Iterable<PostItem> {
		PostItem findPostItem(PostNumber postNumber);
	}

	public interface PostStateProvider {
		PostStateProvider DEFAULT = new PostStateProvider() {};

		default boolean isHiddenResolve(PostItem postItem) {
			return postItem.getHideState().hidden;
		}

		default boolean isUserPost(PostNumber postNumber) {
			return false;
		}

		default boolean isExpanded(PostNumber postNumber) {
			return true;
		}

		default void setExpanded(PostNumber postNumber) {}

		default boolean isRead(PostNumber postNumber) {
			return true;
		}

		default void setRead(PostNumber postNumber) {}
	}

	public interface Callback {
		void onDialogStackOpen();
		DownloadService.Binder getDownloadBinder();
		WatcherService.Client getWatcherClient();
	}

	public interface LocalNavigator {
		void navigateBoardsOrThreads(String chanName, String boardName);
		void navigatePosts(String chanName, String boardName, String threadNumber,
				PostNumber postNumber, String threadTitle);
		void navigateSearch(String chanName, String boardName, String searchQuery);
		void navigateArchive(String chanName, String boardName);
		void navigateTargetAllowReturn(String chanName, ChanLocator.NavigationData data);
		void navigatePosting(String chanName, String boardName, String threadNumber,
				Replyable.ReplyData... data);
		void navigateGallery(String chanName, GalleryItem.Set gallerySet, int imageIndex,
				View view, GalleryOverlay.NavigatePostMode navigatePostMode, boolean galleryMode);
		void navigateSetTheme(ThemeEngine.Theme theme);
	}

	public interface ThumbnailClickListener extends View.OnClickListener {
		void update(int index, boolean mayShowDialog, GalleryOverlay.NavigatePostMode navigatePostMode);
	}

	public interface ThumbnailLongClickListener extends View.OnLongClickListener {
		void update(AttachmentItem attachmentItem);
	}

	public interface Holder {
		PostItem getPostItem();
		ConfigurationSet getConfigurationSet();

		default GalleryItem.Set getGallerySet() {
			return getConfigurationSet().galleryProvider.getGallerySet(getPostItem());
		}
	}

	public static class DemandSet {
		public boolean lastInList = false;
		public Selection selection = Selection.DISABLED;
		public boolean showOpenThreadButton = false;
		public Collection<String> highlightText = Collections.emptyList();
	}

	public static class ConfigurationSet {
		public final String chanName;
		public final Replyable replyable;
		public final PostsProvider postsProvider;
		public final PostStateProvider postStateProvider;
		public final GalleryItem.Provider galleryProvider;
		public final FragmentManager fragmentManager;
		public final DialogUnit.StackInstance stackInstance;
		public final CommentTextView.LinkListener linkListener;
		public final ListViewUtils.ClickCallback<PostItem, RecyclerView.ViewHolder> clickCallback;

		public final boolean mayCollapse;
		public final boolean isDialog;
		public final boolean allowMyMarkEdit;
		public final boolean allowHiding;
		public final boolean allowGoToPost;
		public final PostNumber repliesToPost;

		public ConfigurationSet(String chanName, Replyable replyable,
				PostsProvider postsProvider, PostStateProvider postStateProvider,
				GalleryItem.Provider galleryProvider, FragmentManager fragmentManager,
				DialogUnit.StackInstance stackInstance, CommentTextView.LinkListener linkListener,
				ListViewUtils.ClickCallback<PostItem, RecyclerView.ViewHolder> clickCallback,
				boolean mayCollapse, boolean isDialog, boolean allowMyMarkEdit,
				boolean allowHiding, boolean allowGoToPost, PostNumber repliesToPost) {
			this.chanName = chanName;
			this.replyable = replyable;
			this.postsProvider = postsProvider;
			this.postStateProvider = postStateProvider;
			this.galleryProvider = galleryProvider;
			this.stackInstance = stackInstance;
			this.fragmentManager = fragmentManager;
			this.linkListener = linkListener;
			this.clickCallback = clickCallback;

			this.mayCollapse = mayCollapse;
			this.isDialog = isDialog;
			this.allowMyMarkEdit = allowMyMarkEdit;
			this.allowHiding = allowHiding;
			this.allowGoToPost = allowGoToPost;
			this.repliesToPost = repliesToPost;
		}

		public ConfigurationSet copy(ListViewUtils.ClickCallback<PostItem, RecyclerView.ViewHolder> clickCallback,
				boolean mayCollapse, boolean isDialog, PostNumber repliesToPost) {
			return new ConfigurationSet(chanName, replyable, postsProvider, postStateProvider,
					galleryProvider, fragmentManager, stackInstance, linkListener, clickCallback,
					mayCollapse, isDialog, allowMyMarkEdit, allowHiding, allowGoToPost, repliesToPost);
		}
	}

	public static class UiManagerViewModel extends ViewModel {
		private WeakReference<UiManager> uiManager;
	}
}
