package com.example.e_canteenorderingapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class FormatUtils {
	private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

	private FormatUtils() {}

	public static String formatBdt(long amountInCents) {
		double amount = amountInCents / 100.0;
		return "৳" + String.format(Locale.getDefault(), "%,.2f", amount);
	}

	public static String formatDate(long millis) {
		return DATE_FMT.format(new Date(millis));
	}

	public static String formatRange(long startMillis, long endMillis) {
		return formatDate(startMillis) + " to " + formatDate(endMillis);
	}
}



