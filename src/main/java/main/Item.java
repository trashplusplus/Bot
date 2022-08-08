package main;

public class Item
{
	private long id;
	private int cost;
	private ItemRarity rarity;
	private String title;

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
		//return "|" + title + "|" + rarity + "|" + cost + "$" + "|";
		return String.format("|%s|%s|$%d|", title, rarity.toString(), cost);
	}
}
