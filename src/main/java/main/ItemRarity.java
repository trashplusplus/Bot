package main;

public enum ItemRarity
{
	Common,
	Rare,
	Gift,
	Special;

	@Override
	public String toString()
	{
		return super.toString();
	}

	int toWeight()
	{
		switch (this)
		{
			case Common:
				return 10;
			case Rare:
				return 5;
			case Gift:
				return 2;
			case Special:
				return 1;
		}

		throw new RuntimeException("WTF?");
	}
}
