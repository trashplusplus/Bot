package main;

import java.util.Objects;

public class Money
{
	public long value;

	public Money(long value)
	{
		this.value = value;
	}

	public void transfer(long amount) throws MoneyException
	{
		try
		{
			long new_value = Math.addExact(value, amount);
			if (new_value < 0)
				throw new MoneyException("Not enough money for transfer");
			else
				value = new_value;
		}
		catch (ArithmeticException ex)
		{
			throw new MoneyException("Numbers are too big, can't store the result of transfer");
		}
	}

	public static void transaction(Money sender, Money receiver, long amount)
	{
		// todo
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		long value = this.value;
		int digitNo = 0;
		while (value > 0)
		{
			digitNo++;
			sb.append(value % 10);
			value /= 10;
			if (digitNo % 3 == 0 && value > 0)
			{
				sb.append(",");
			}
		}
		return sb.append("$").reverse().toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Money money = (Money) o;
		return value == money.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(value);
	}

	static class MoneyException extends Exception
	{
		public MoneyException()
		{
		}

		public MoneyException(String message)
		{
			super(message);
		}
	}
}
