package main;

import java.util.Objects;

public class Item
{
	private final long id;
	private final String title;
	private final ItemRarity rarity;
	private final Money cost;
	private String emoji;
	private int needleCost;

	public Item(long id, String title, ItemRarity rarity, long cost)
	{
		this.id = id;
		this.title = title;
		this.rarity = rarity;
		this.cost = new Money(cost);
		this.emoji = "";
		needleCost = 0;
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

	public boolean isNeedleCost(){
		if(needleCost != 0){return true;}
		return false;
	}

	public String getEmoji(){
		return emoji;
	}
	@Override
	public String toString()
	{
		return String.format("|%s|%s|%s|", emoji + title, rarity.toString(), cost);
	}
	public String getNeedleCostFormat(){
		return String.format("|%s|%s|\uD83E\uDDF7%d|", emoji + title, rarity.toString(), needleCost);
	}

	public void setNeedleCost(int needleCost){
		this.needleCost = needleCost;
	}

	public int getNeedleCost(){
		return needleCost;
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
