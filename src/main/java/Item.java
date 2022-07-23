
public class Item
{
	private int cost;
	private char rarity;
	private String title;

	Item(String title, char rarity, int cost)
	{
		this.title = title;
		this.rarity = rarity;
		this.cost = cost;
	}

	public char getRarity()
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
		return String.format("|%s|%s|$%d|", title, rarity, cost);
	}
}
