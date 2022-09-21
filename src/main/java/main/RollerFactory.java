package main;

import database.SQLSession;
import database.dao.CachedItemDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RollerFactory
{
	static final CachedItemDAO itemDAO = new CachedItemDAO(SQLSession.sqlConnection);

	public static Roller<Item> getMudRoller(Random random)
	{
		List<Item> cheap_items = itemDAO.get_by_rarity(ItemRarity.Cheap);
		List<Item> common_items = itemDAO.get_by_rarity(ItemRarity.Common);  // ?

		cheap_items.add(null);
		common_items.add(null);

		Item[] items_cheap = cheap_items.toArray(new Item[0]);
		Item[] items_common = common_items.toArray(new Item[0]);

		int[] weights_cheap = new int[items_cheap.length];
		int[] weights_common = new int[items_common.length];

		Arrays.fill(weights_cheap, 1);
		Arrays.fill(weights_common, 1);
		weights_cheap[weights_cheap.length - 1] = 4 * (weights_cheap.length - 1);
		weights_common[weights_common.length - 1] = 17 * (weights_common.length - 1);

		return new Roller<>(items_cheap, weights_cheap, random);
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
		Item[] items = itemDAO.get_all().toArray(new Item[0]);
		int[] weights = new int[items.length];

		for (int i = 0; i < items.length; i++)
		{
			switch (items[i].getRarity())
			{
				case Cheap:
					weights[i] = 2;
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
				case Status:
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
		for (Item item : itemDAO.get_all())
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
