package main;

import database.dao.ItemDAO;
import database.SQLSession;

import java.util.Random;

public class ItemFactory
{
	static ItemDAO itemDAO = new ItemDAO(SQLSession.sqlConnection);
	static Random random = new Random();

	public static Item getRandomItem()
	{
		long random_id = random.nextInt(itemDAO.size()) + 1;
		return itemDAO.get(random_id);
	}

	public static Item getRandomWeighted()
	{
		Item[] items = itemDAO.getAll().toArray(new Item[0]);
		int[] weights = new int[items.length];
		for (int i = 0; i < items.length; i++)
		{
			weights[i] = items[i].getRarity().toWeight();
		}
		Roller<Item> roller = new Roller<>(items, weights, new Random());  // TODO make static
		return roller.roll();
	}
}
