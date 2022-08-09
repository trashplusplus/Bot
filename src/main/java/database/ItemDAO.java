package database;

import main.Item;
import main.ItemRarity;

import java.sql.*;

public class ItemDAO
{
	private Connection connection;

	public ItemDAO(Connection connection)
	{
		this.connection = connection;
	}

	public Item get(long id)
	{
		Item item = null;
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from items where id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			item = new Item(
					id,
					rs.getString("name"),
					ItemRarity.valueOf(rs.getString("rarity")),
					rs.getInt("cost"));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return item;
	}

	public int size()
	{
		try
		{
			String query = "select count(*) from items;";
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			return rs.getInt(1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);
		}
	}
}
