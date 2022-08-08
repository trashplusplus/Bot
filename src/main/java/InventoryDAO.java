import java.sql.*;

public class InventoryDAO
{
	private Connection connection;
	private ItemDAO itemDAO;

	public InventoryDAO(Connection connection)
	{
		this.connection = connection;
		itemDAO = new ItemDAO(connection);
	}

	public InventoryDAO()
	{
		//this(DriverManager.getConnection("jdbc:sqlite:data.db"));
	}

	public void put(long player_id, Inventory inventory)
	{
		for (int i = 0; i < inventory.getInvSize(); i++)
		{
			Item item = inventory.getItem(i);
			try
			{
				PreparedStatement ps = connection.prepareStatement("insert into inventory (player_id, item_id) values (?, ?);");
				ps.setLong(1, player_id);
				ps.setLong(2, item.getId());
			}
			catch (SQLException throwables)
			{
				throwables.printStackTrace();
			}
		}
	}

	public Inventory get(long player_id)
	{
		Inventory inventory = new Inventory();
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from inventory where player_id = ?;");
			ps.setLong(1, player_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				long item_id = rs.getLong("item_id");
				inventory.putItem(itemDAO.get(item_id));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return inventory;
	}
}
