package main;

import java.util.Objects;

public class Item
{
	private final long id;
	private final String title;
	private final ItemRarity rarity;
	private final Money cost;
	private String emoji;

	public Item(long id, String title, ItemRarity rarity, long cost)
	{
		this.id = id;
		this.title = title;
		this.rarity = rarity;
		this.cost = new Money(cost);
		this.emoji = "";
	}

	public Item(long id, String title, ItemRarity rarity, long cost, String emoji)
	{
		this.id = id;
		this.title = title;
		this.rarity = rarity;
		this.cost = new Money(cost);
		this.emoji = emoji;
	}

	public ItemRarity getRarity()
	{
		return rarity;
	}

	public Money getCost()
	{
		return cost;
	}

	public String getTitle(){
		return title;
	}

	public String getEmojiTitle(){
		return emoji + title;
	}

	public long getId()
	{
		return id;
	}

	public String getEmoji(){
		return emoji;
	}
	@Override
	public String toString()
	{
		return String.format("|%s|%s|%s|", emoji + title, rarity.toString(), cost);
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
