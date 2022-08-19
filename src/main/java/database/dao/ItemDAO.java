package database.dao;

import main.Item;
import main.ItemRarity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO
{
	private final Connection connection;

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
			if (rs.next())
			{
				item = new Item(
						id,
						rs.getString("name"),
						ItemRarity.valueOf(rs.getString("rarity")),
						rs.getInt("cost"));
			}
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

	public Item getByName(String name)
	{
		Item item = null;
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from items where name = ?;");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				item = new Item(rs.getLong("id"),
						rs.getString("name"),
						ItemRarity.valueOf(rs.getString("rarity")),
						rs.getInt("cost")
				);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return item;
	}

	public List<Item> getAll()
	{
		List<Item> res = new ArrayList<>();
		try
		{
			String query = "select * from items;";
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next())
			{
				res.add(form(rs));
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}

		return res;
	}

	Item form(ResultSet rs) throws SQLException
	{
		long id = rs.getLong(1);
		String title = rs.getString(2);
		ItemRarity rarity = ItemRarity.valueOf(rs.getString(3));
		int cost = rs.getInt(4);

		return new Item(id, title, rarity, cost);
	}
}