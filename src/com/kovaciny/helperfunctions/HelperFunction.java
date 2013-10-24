package com.kovaciny.helperfunctions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.math.Fraction;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

final public class HelperFunction {
	private HelperFunction() {
		//class can't be instantiated
	}
	public static final long ONE_HOUR_IN_MILLIS=3600000; //millisecs
	public static final long ONE_MINUTE_IN_MILLIS=60000; //millisecs
	public static final long ONE_SECOND_IN_MILLIS=1000;
	public static final long MINUTES_PER_HOUR = 60;
	public static final long SECONDS_PER_MINUTE = 60;
	public static final double INCHES_PER_FOOT = 12.0;
	public static final double GRAMS_PER_POUND = 453.592;
	
	public static final double EPSILON = 0.0001; //for comparing doubles

	public static final float RELATIVE_SIZE_SUPERSCRIPT = 0.66f;
	public static final float RELATIVE_SIZE_SUBSCRIPT = 0.66f;
	public static final float RELATIVE_SIZE_SMALLER = 0.75f;

	/*
	 * Time functions
	 */
	
	
	public static String formatMinutesAsHours(long minutes) {
        long hours = minutes/60;
        long remainingMinutes = minutes % 60;
        return String.format("%d:%02d", hours, remainingMinutes);
    }
	
	public static String formatSecondsAsMinutes(long seconds) {
        long minutes = seconds/60;
        long remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

	public static Date toNearestWholeMinute(Date d) {
	    Calendar c = new GregorianCalendar();
	    c.setTime(d);

	    if (c.get(Calendar.SECOND) >= 30)
	        c.add(Calendar.MINUTE, 1);

	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);

	    return c.getTime();
	}
	
	/*
	 * String functions
	 */
	
	public static String capitalizeFirstChar(String target) {
		StringBuilder capitalized = new StringBuilder(target);
		capitalized.setCharAt(0, Character.toUpperCase(capitalized.charAt(0)));
		return capitalized.toString();
	}
	
	public static Spannable formatDecimalAsProperFraction(double decimal, double maxDenominator) {
	    Fraction fraction;
	    if (maxDenominator == 0){
	        fraction = Fraction.getFraction(decimal);
	    } else {
	        double roundedDecimal = Math.round(decimal * maxDenominator) / maxDenominator;
	        fraction = Fraction.getFraction(roundedDecimal);
	    }
		String whole = String.valueOf(fraction.getProperWhole());
		if (fraction.getProperNumerator() == 0) return new SpannableString(whole);
		
		SpannableString numerator = new SpannableString(String.valueOf(fraction.getProperNumerator()));
		numerator.setSpan(new SuperscriptSpan(), 0, numerator.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		SpannableString denominator = new SpannableString(String.valueOf(fraction.getDenominator()));
		denominator.setSpan(new SubscriptSpan(), 0, denominator.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		Spanned slash = new SpannedString(Html.fromHtml("&frasl;"));
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		ssb.append(numerator);
		ssb.append(slash);
		ssb.append(denominator);
		ssb.setSpan(new RelativeSizeSpan(RELATIVE_SIZE_SUPERSCRIPT), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.insert(0, new SpannableString(whole + " "));
		
		return ssb;
	}
	
	/*
	 * View functions
	 */
	
	
	
	/*
	 * Can handle both double and integer formats
	 */
	public static void incrementEditText(EditText et) {
		String current = et.getText().toString();
		if (current.length() == 0) {
			et.setText("1");
		}
		double doubleValue = Double.valueOf(et.getText().toString());
		int intValue = (int) doubleValue;
		if (doubleValue == intValue) {
			String incremented = String.valueOf(intValue + 1);
			et.setText(incremented);
		} else {
			String incremented = String. valueOf(doubleValue + 1d);
			et.setText(incremented);
		}		
		et.clearFocus();
	}
	
	public static void hideKeyboard(FragmentActivity fragmentActivity) {
        InputMethodManager inputMethodManager = (InputMethodManager) fragmentActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = fragmentActivity.getCurrentFocus();
        if (focus != null) {
            inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(),
                    0);
        }
    }
	
	public static <E> E getOnlyElement(Iterable<E> iterable) {
	    Iterator<E> iterator = iterable.iterator();

	    if (!iterator.hasNext()) {
	        throw new RuntimeException("Collection is empty");
	    }

	    E element = iterator.next();

	    if (iterator.hasNext()) {
	        throw new RuntimeException("Collection contains more than one item");
	    }

	    return element;
	}
	
	public static List<EditText> getChildEditTexts (ViewGroup container) {
	    List<EditText> children = new ArrayList<EditText>();
	    for (int i = 0, n = container.getChildCount(); i < n; i++) {
	        View nextChild = container.getChildAt(i);
	        if (nextChild instanceof ViewGroup) {
	            children.addAll(getChildEditTexts((ViewGroup)nextChild));
	        } else if (nextChild instanceof EditText) {
	            children.add((EditText)nextChild); 
	        }
	    } 
	    return children;
	}
}
