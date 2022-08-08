import java.sql.Connection;
import java.util.Random;

public class ItemFactory
{
	static ItemDAO itemDAO = new ItemDAO(SQLSession.sqlConnection);
	static Random random = new Random();

	public static Item getRandomItem()
	{
		long random_id = random.nextInt(itemDAO.size());
		return itemDAO.get(random_id);
	}
}
