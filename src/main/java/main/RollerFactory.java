package main;

import database.dao.ItemDAO;
import database.SQLSession;

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
				default:
					throw new RuntimeException("WTF?");
			}
		}

		return new Roller<>(items, weights, random);
	}
}
