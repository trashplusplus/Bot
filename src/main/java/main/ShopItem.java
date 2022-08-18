package main;

public class ShopItem
{
	int id;
	Item item;
	int cost;
	Player seller;

	public ShopItem(int id, Item item, int cost, Player seller)
	{
		this.id = id;
		this.item = item;
		this.cost = cost;
		this.seller = seller;
	}

	public ShopItem(Item item, int cost, Player seller)
	{
		this(-1, item, cost, seller);
	}

	public int getCost()
	{
		return cost;
	}

	public Player getSeller()
	{
		return seller;
	}

	public Item getItem()
	{
		return item;
	}

	public int getId()
	{
		return id;
	}
}
