package main;

import database.SQLSession;
import database.dao.ItemDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RollerFactory
{
	static final ItemDAO itemDAO = new ItemDAO(SQLSession.sqlConnection);

	public static Roller<Item> getMudRoller(Random random)
	{
		List<Item> cheap_items = new ArrayList<>();
		for (Item item : itemDAO.getAll())
		{
			if (item.getRarity() == ItemRarity.Cheap)
			{
				cheap_items.add(item);
			}
		}
		cheap_items.add(null);
		Item[] items = cheap_items.toArray(new Item[0]);
		int[] weights = new int[items.length];
		Arrays.fill(weights, 1);
		weights[weights.length - 1] = 4 * (weights.length - 1);

		return new Roller<>(items, weights, random);
	}

	public static Roller<Integer> getMoneyRoller(Random random)
	{
		int limit = 101;
		Integer[] money = new Integer[limit];
		int[] weights = new int[limit];

		Arrays.fill(weights, 1);

		for (int i = 0; i < limit; i++)
		{
			money[i] = i;
		}

		return new Roller<>(money, weights, random);
	}

	public static Roller<Item> getFindRoller(Random random)
	{
		Item[] items = itemDAO.getAll().toArray(new Item[0]);
		int[] weights = new int[items.length];

		for (int i = 0; i < items.length; i++)
		{
			switch (items[i].getRarity())
			{
				case Cheap:
					weights[i] = 1;
					break;
				case Common:
					weights[i] = 4;
					break;
				case Rare:
					weights[i] = 5;
					break;
				case Gift:
					weights[i] = 2;
					break;
				case Limited:
					weights[i] = 0;
					break;
				default:
					throw new RuntimeException("WTF?");
			}
		}

		return new Roller<>(items, weights, random);
	}

	public static Roller<Item> getFishRoller(Random random)
	{
		List<Item> item_list = new ArrayList<>();
		int rares = 0;
		int cheaps = 0;
		for (Item item : itemDAO.getAll())
		{
			if (item.getRarity() == ItemRarity.Rare)
			{
				item_list.add(item);
				rares++;
			}
			if (item.getRarity() == ItemRarity.Cheap)
			{
				item_list.add(item);
				cheaps++;
			}
			if (is_fish(item))
			{
				item_list.add(item);
			}
		}

		item_list.add(null);

		Item[] items = item_list.toArray(new Item[0]);
		int[] weights = new int[items.length];

		for (int i = 0; i < weights.length; i++)
		{
			Item item = items[i];
			if (item == null)
			{
				weights[i] = 39 * cheaps * rares;
			}
			else
			{
				if (is_fish(item))
				{
					weights[i] = rares * cheaps * 10;
				}
				if (item.getRarity() == ItemRarity.Cheap)
				{
					weights[i] = rares * 30;
				}
				if (item.getRarity() == ItemRarity.Rare)
				{
					weights[i] = cheaps;
				}
			}
		}

		return new Roller<>(items, weights, random);
	}

	private static boolean is_fish(Item item)
	{
		switch (item.getTitle())
		{
			case "Бычок":
			case "Карась":
			case "Горбуша":
				return true;
			default:
				return false;
		}
	}
}
