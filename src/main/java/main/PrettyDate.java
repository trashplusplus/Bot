package main;

import java.util.concurrent.TimeUnit;

public class PrettyDate
{
	public static String prettify(long value, TimeUnit unit)
	{
		Localizer localizer = new Localizer();
		boolean printFraction = false;
		long millis = 0;
		int seconds = 0;
		int minutes = 0;
		int hours = 0;
		int days = 0;
		switch (unit)
		{
			case NANOSECONDS:
				value /= 1000;
			case MICROSECONDS:
				value /= 1000;
			case MILLISECONDS:
				millis = value;
				printFraction = true;
				break;
			case SECONDS:
				seconds = (int) value;
				break;
			case MINUTES:
				minutes = (int) value;
				break;
			case HOURS:
				hours = (int) value;
				break;
			case DAYS:
				days = (int) value;
				break;
		}

		seconds += millis / 1000;
		millis %= 1000;

		minutes += seconds / 60;
		seconds %= 60;

		hours += minutes / 60;
		minutes %= 60;

		days += hours / 24;
		hours %= 24;

		StringBuilder sb = new StringBuilder();
		if (days > 0)
		{
			sb.append(localizer.days(days));
		}

		if (hours > 0)
		{
			sb.append(' ');
			sb.append(localizer.hours(hours));
		}

		if (minutes > 0)
		{
			sb.append(' ');
			sb.append(localizer.minutes(minutes));
		}

		if (seconds > 0)
		{
			sb.append(' ');
			sb.append(localizer.seconds(seconds));
		}

		if (sb.length() == 0)
		{
			return "<1 секунды";
		}

		return sb.toString();
	}
}
