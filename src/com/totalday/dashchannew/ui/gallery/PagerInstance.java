package com.totalday.dashchannew.ui.gallery;

import android.view.View;
import android.widget.FrameLayout;

import com.totalday.dashchannew.content.ImageLoader;
import com.totalday.dashchannew.content.model.GalleryItem;
import com.totalday.dashchannew.graphics.DecoderDrawable;
import com.totalday.dashchannew.graphics.SimpleBitmapDrawable;
import com.totalday.dashchannew.media.AnimatedPngDecoder;
import com.totalday.dashchannew.media.GifDecoder;
import com.totalday.dashchannew.media.JpegData;
import com.totalday.dashchannew.widget.CircularProgressBar;
import com.totalday.dashchannew.widget.PhotoView;
import com.totalday.dashchannew.widget.ViewFactory;

public class PagerInstance {
	public final GalleryInstance galleryInstance;
	public final Callback callback;
	public boolean scrollingLeft;
	public ViewHolder leftHolder;
	public ViewHolder currentHolder;
	public ViewHolder rightHolder;
	public PagerInstance(GalleryInstance galleryInstance, Callback callback) {
		this.galleryInstance = galleryInstance;
		this.callback = callback;
	}
	public enum LoadState {PREVIEW_OR_LOADING, COMPLETE, ERROR}

	public interface Callback {
		void showError(ViewHolder holder, String message);
	}

	public static class MediaSummary {
		public int width;
		public int height;
		public long size;

		public MediaSummary(int width, int height, long size) {
			this.width = width;
			this.height = height;
			this.size = size;
		}

		public MediaSummary(GalleryItem galleryItem) {
			this(galleryItem.width, galleryItem.height, galleryItem.size);
		}

		public boolean updateDimensions(int width, int height) {
			if (width > 0 && height > 0 && (this.width != width || this.height != height)) {
				this.width = width;
				this.height = height;
				return true;
			}
			return false;
		}

		public boolean updateSize(long size) {
			if (size > 0 && this.size != size) {
				this.size = size;
				return true;
			}
			return false;
		}
	}

	public static class ViewHolder {
		public GalleryItem galleryItem;
		public MediaSummary mediaSummary;
		public PhotoView photoView;
		public FrameLayout surfaceParent;
		public CircularProgressBar progressBar;
		public View playButton;
		public ViewFactory.ErrorHolder errorHolder;

		public SimpleBitmapDrawable simpleBitmapDrawable;
		public DecoderDrawable decoderDrawable;
		public AnimatedPngDecoder animatedPngDecoder;
		public GifDecoder gifDecoder;
		public JpegData jpegData;
		public boolean photoViewThumbnail;
		public ImageLoader.Target thumbnailTarget;

		public LoadState loadState = LoadState.PREVIEW_OR_LOADING;
		public Object decodeBitmapTask;

		public void recyclePhotoView() {
			photoView.recycle();
			if (simpleBitmapDrawable != null) {
				simpleBitmapDrawable.recycle();
				simpleBitmapDrawable = null;
			}
			if (decoderDrawable != null) {
				decoderDrawable.recycle();
				decoderDrawable = null;
			}
			if (animatedPngDecoder != null) {
				animatedPngDecoder.recycle();
				animatedPngDecoder = null;
			}
			if (gifDecoder != null) {
				gifDecoder.recycle();
				gifDecoder = null;
			}
			jpegData = null;
			photoViewThumbnail = false;
		}
	}
}
