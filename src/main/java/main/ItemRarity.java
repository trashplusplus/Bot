package main;

public enum ItemRarity
{
	Cheap,
	Common,
	Rare,
	Gift;

	@Override
	public String toString()
	{
		return super.toString();
	}

	int toWeight()  // TODO remove from this class
	{
		switch (this)
		{
			case Cheap:
				return 10;
			case Common:
				return 8;
			case Rare:
				return 3;
			case Gift:
				return 1;
		}

		throw new RuntimeException("WTF?");
	}
}
