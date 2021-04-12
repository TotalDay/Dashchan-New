package com.totalday.dashchannew.text.style;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

import com.totalday.dashchannew.util.ResourceUtils;

public class MediumSpan extends MetricAffectingSpan {
	@Override
	public void updateDrawState(@NonNull TextPaint paint) {
		paint.setTypeface(ResourceUtils.TYPEFACE_MEDIUM);
	}

	@Override
	public void updateMeasureState(@NonNull TextPaint paint) {
		updateDrawState(paint);
	}
}
