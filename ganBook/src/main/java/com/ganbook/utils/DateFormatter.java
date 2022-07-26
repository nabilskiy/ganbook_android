package com.ganbook.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

public class DateFormatter {
	private DateFormatter() {}

	public static String getCurrent() {
		GregorianCalendar calendar = new GregorianCalendar(); // now
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy"); 
		formatter.setCalendar(calendar);						
		String dateStr = formatter.format(calendar.getTime());
		return dateStr;
	}

	public static String getCurrent_In_Message_Format() {
		GregorianCalendar calendar = new GregorianCalendar(); // now
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		formatter.setCalendar(calendar);						
		String dateStr = formatter.format(calendar.getTime());
		return dateStr;
	}

	public static String getInAlbumFormat(String rawDate, String format) {
		if (StrUtils.isEmpty(rawDate)) {
			return ""; 
		}
//		2015-04-01 15:08:16

		try {  
			int day = -1;
			int month = -1;
			int year = -1;
			int hour = -1;
			int minute = -1;

			StringTokenizer tok = new StringTokenizer(rawDate, "- ");
			while (tok.hasMoreTokens()) {
				String cur = tok.nextToken();
				if (year == -1) {
					year = fromString(cur);
				} else if (month == -1) {
					month = fromString(cur);
					month--; // make it zero based rather than 1 based  
				} else if (day == -1) {
					day = fromString(cur);
				} else if (hour == -1) {
					String[] arr = cur.split(":");
					hour = fromString(arr[0]);
					minute = fromString(arr[1]);

					break;
				}
			}
			GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute);
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			formatter.setCalendar(calendar);						
			String dateStr = formatter.format(calendar.getTime());
			return dateStr;
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
		return "";
	}

	public static String formatForAlbumList(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat myFormat = new SimpleDateFormat("MMMM dd");

		return myFormat.format(date);
	}

	public static String formatStringDate(String date_str,String in_format,String out_format)
	{
		SimpleDateFormat format = new SimpleDateFormat(in_format);
		SimpleDateFormat myFormat = new SimpleDateFormat(out_format);

		String reformattedStr = "";
		try {
			Date date = format.parse(date_str);
			reformattedStr = myFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return reformattedStr;
	}

	private static int fromString(String str) {
		try { 
			int val = Integer.parseInt(str);
			return  val;
		}
		catch (Exception e) {
			int jj=234;
			jj++;
			throw new RuntimeException();
		}
	}

}
