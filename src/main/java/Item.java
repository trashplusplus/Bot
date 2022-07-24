
public class Item
{
	private int cost;
	private ItemRarity rarity;
	private String title;

	Item(String title, ItemRarity rarity, int cost)
	{
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



	@Override
	public String toString()
	{
		//return "|" + title + "|" + rarity + "|" + cost + "$" + "|";
		return String.format("|%s|%s|$%d|", title, rarity.toString(), cost);
	}
}
