package main;

import database.ItemDAO;
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
}
