package chan.content;

import androidx.annotation.NonNull;

import com.totalday.dashchannew.content.model.ErrorItem;
import com.totalday.dashchannew.widget.ClickableToast;

public class ExtensionException extends Exception implements ErrorItem.Holder {
	public ExtensionException(Throwable throwable) {
		super(throwable);
	}

	@NonNull
	@Override
	public ErrorItem getErrorItemAndHandle() {
		logException(getCause(), false);
		return new ErrorItem(ErrorItem.Type.EXTENSION);
	}

	public static void logException(Throwable t, boolean showToast) {
		if (t instanceof LinkageError || t instanceof RuntimeException) {
			t.printStackTrace();
			if (showToast) {
				ClickableToast.show(new ErrorItem(ErrorItem.Type.EXTENSION));
			}
		}
	}
}
