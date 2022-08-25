package database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DatabaseDateMediator
{
	private static final String date_pattern = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final DateFormat df;
	private static final TimeZone tz;
	private static final Date date = new Date();

	static
	{
		tz = TimeZone.getTimeZone("UTC");
		df = new SimpleDateFormat(date_pattern);
		df.setTimeZone(tz);
	}

	public static long string_to_ms(String date_string) throws ParseException
	{
		return df.parse(date_string).getTime();
	}

	public static long parse(String date_string) throws ParseException
	{
		return string_to_ms(date_string);
	}

	public static String ms_to_string(Long ms)
	{
		if (ms == null)
		{
			return "";
		}
		date.setTime(ms);
		return df.format(date);
	}

	public static String format(long ms)
	{
		return ms_to_string(ms);
	}
}
