package com.ganbook.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.ganbook.app.MyApp;

public class LocaleGetter {
	private LocaleGetter() {}
	
	private static String countryCode;
	private static String langCode;
	
	public static String get() {
		// see http://en.wikipedia.org/wiki/ISO_3166-1   and use Alpha-2 codes
		
		if (StrUtils.isEmpty(countryCode)) {
			countryCode = getCountryCode().toUpperCase();
		}
		
		if (StrUtils.isEmpty(langCode)) {
			langCode = getLangCode().toLowerCase();
		}
		
		return langCode + "_" + countryCode; 
	}
	

	private static String getCountryCode() {
		// test by CONFIGURED locale (i.e. set by user for device)
		final Context context = MyApp.context;
		String code_userConfiguredLocale = context.getResources().getConfiguration().locale.getCountry();
		if (StrUtils.notEmpty(code_userConfiguredLocale)) { // US
			return code_userConfiguredLocale.toLowerCase();
		}

		
		// test by device CURRENT locale
		String code_deviceLocale = Locale.getDefault().getCountry(); // US
		if (StrUtils.notEmpty(code_deviceLocale)) {
			return code_deviceLocale.toLowerCase();
		}


		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		// test by local network
		String code_localNetwork = tm.getNetworkCountryIso(); // il
		if (StrUtils.notEmpty(code_localNetwork)) {
			return code_localNetwork.toLowerCase();
		}
		// test by SIM
		String code_SimCard = tm.getSimCountryIso(); // il
		if (StrUtils.notEmpty(code_SimCard)) {
			return code_SimCard.toLowerCase();
		}

		return ""; // not found
	}
	
	private static String getLangCode() {
		// test by CONFIGURED locale (i.e. set by user for device)
		final Context context = MyApp.context;
		String code_userConfiguredLocale = context.getResources().getConfiguration().locale.getLanguage();
		if (StrUtils.notEmpty(code_userConfiguredLocale)) { // US
			return code_userConfiguredLocale.toLowerCase();
		}
		
		// test by device CURRENT locale
		String code_deviceLocale = Locale.getDefault().getLanguage();
		if (StrUtils.notEmpty(code_deviceLocale)) {
			return code_deviceLocale.toLowerCase();
		}
		return ""; // not found
	}


	public static String getGmt() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
		Date currentLocalTime = calendar.getTime();
		SimpleDateFormat date = new SimpleDateFormat("Z");
		String localTime = date.format(currentLocalTime);
		String bagBegin = localTime.substring(0,3);
		String bagEnd = localTime.substring(3);
		localTime = bagBegin + ":" + bagEnd;
		return localTime;
	}

	public static boolean isHebrew(Configuration config) {
		try {
			Locale currentLocale = null;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				currentLocale = config.getLocales().get(0);
			} else {
				//noinspection deprecation
				currentLocale = config.locale;
			}

			if (currentLocale.getLanguage().equals(new Locale("he").getLanguage()) || currentLocale.getLanguage().equals(new Locale("iw").getLanguage())) {
				return true;
			}

			return false;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
