package com.totalday.dashchannew.text;

import android.text.Spanned;

import java.util.Comparator;

public class SpanComparator implements Comparator<Object> {
	private final Spanned spanned;
	private final Property property;
	public SpanComparator(Spanned spanned, Property property) {
		this.spanned = spanned;
		this.property = property;
	}

	@Override
	public int compare(Object o1, Object o2) {
		int i1 = property.extract.get(spanned, o1);
		int i2 = property.extract.get(spanned, o2);
		return Integer.compare(i1, i2);
	}

	public enum Property {
		START(Spanned::getSpanStart),
		END(Spanned::getSpanEnd);

		private final Extract extract;

		Property(Extract extract) {
			this.extract = extract;
		}

		private interface Extract {
			int get(Spanned spanned, Object span);
		}
	}
}
