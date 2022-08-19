package main;

import java.util.Objects;

public class Item
{
	private final long id;
	private final int cost;
	private final ItemRarity rarity;
	private final String title;

	public Item(long id, String title, ItemRarity rarity, int cost)
	{
		this.id = id;
		this.title = title;
		this.rarity = rarity;
		this.cost = cost;
	}

	public ItemRarity getRarity()
	{
		return rarity;
	}

	public int getCost()
	{
		return cost;
	}

	public String getTitle()
	{
		return title;
	}

	public long getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return String.format("|%s|%s|$%d|", title, rarity.toString(), cost);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Item item = (Item) o;
		return id == item.id;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}
}
