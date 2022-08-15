package database;

import main.Inventory;
import main.Item;
import main.Player;

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
				ps.execute();
			}
			catch (SQLException throwables)
			{
				throwables.printStackTrace();
			}
		}
	}

	public void putItem(long player_id, long item_id)
	{
		String query = "insert into inventory (player_id, item_id) values (?, ?)";
		try
		{
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setLong(1, player_id);
			ps.setLong(2, item_id);
			ps.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
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
			rs.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return inventory;
	}

	public int size()
	{
		String query = "select count(*) from inventory;";
		try
		{
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			rs.next();
			return rs.getInt(1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);
		}
	}

	public void delete(long player_id, long item_id, int count)
	{
		String query = "delete from inventory where id in (select id from inventory where player_id = ? and item_id = ? limit ?);";
		try
		{
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setLong(1, player_id);
			ps.setLong(2, item_id);
			ps.setInt(3, count);
			int res = ps.executeUpdate();
			System.out.printf("InventoryDAO.delete() fired, res = %d\n", res);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	public void delete(Player player, long item_id, int count)
	{
		String query = "delete from inventory where id in (select id from inventory where player_id = ? and item_id = ? limit ?);";
		try
		{
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setLong(1, player.getId());
			ps.setLong(2, item_id);
			ps.setInt(3, count);
			int res = ps.executeUpdate();
			System.out.printf("Предмет | %s | удален  из инвентаря", player.getInventory().getItem((int)item_id).getTitle());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
