package chan.content;

import androidx.annotation.NonNull;

import com.totalday.dashchannew.content.model.ErrorItem;

import chan.annotation.Public;

@Public
public final class InvalidResponseException extends Exception implements ErrorItem.Holder {
	@Public
	public InvalidResponseException() {}

	@Public
	public InvalidResponseException(Throwable throwable) {
		super(throwable);
	}

	@NonNull
	@Override
	public ErrorItem getErrorItemAndHandle() {
		printStackTrace();
		return new ErrorItem(ErrorItem.Type.INVALID_RESPONSE);
	}
}
