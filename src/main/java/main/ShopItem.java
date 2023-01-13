package main;

public class ShopItem
{
	int id;
	Item item;
	Money cost;
	Player seller;
	String emoji;

	public ShopItem(int id, Item item, long cost, Player seller)
	{
		this.id = id;
		this.item = item;
		this.cost = new Money(cost);
		this.seller = seller;
	}

	public ShopItem(Item item, long cost, Player seller)
	{
		this(-1, item, cost, seller);
	}

	public Money getCost()
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

	@Override
	public String toString(){
		return String.format("\uD83C\uDFA9 Товар `%s` | Цена: %s | Продавец: %s \n", item.getEmojiTitle(), cost, seller.getFormattedUsernameWithTelegramFormatting());
	}
}
