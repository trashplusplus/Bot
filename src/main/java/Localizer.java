public class Localizer
{
	public String days(int value)
	{
		int two_digits = value % 100;
		String form = "дней";
		if (two_digits < 11 || two_digits > 14)
		{
			switch (two_digits % 10)
			{
				case 1:
					form = "день";
					break;
				case 2:
				case 3:
				case 4:
					form = "дня";
					break;
				default:
					break;
			}
		}
		return String.format("%d %s", value, form);
	}

	public String hours(int value)
	{
		int two_digits = value % 100;
		String form = "часов";
		if (two_digits < 11 || two_digits > 14)
		{
			switch (two_digits % 10)
			{
				case 1:
					form = "час";
					break;
				case 2:
				case 3:
				case 4:
					form = "часа";
					break;
				default:
					break;
			}
		}
		return String.format("%d %s", value, form);
	}

	public String minutes(int value)
	{
		int two_digits = value % 100;
		String form = "минут";
		if (two_digits < 11 || two_digits > 14)
		{
			switch (two_digits % 10)
			{
				case 1:
					form = "минута";
					break;
				case 2:
				case 3:
				case 4:
					form = "минуты";
					break;
				default:
					break;
			}
		}
		return String.format("%d %s", value, form);
	}

	public String seconds(int value)
	{
		int two_digits = value % 100;
		String form = "секунд";
		if (two_digits < 11 || two_digits > 14)
		{
			switch (two_digits % 10)
			{
				case 1:
					form = "секунда";
					break;
				case 2:
				case 3:
				case 4:
					form = "секунды";
					break;
				default:
					break;
			}
		}
		return String.format("%d %s", value, form);
	}
}
