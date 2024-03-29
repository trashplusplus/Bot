package database.dao;

import main.Item;
import main.ItemRarity;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CachedItemDAO implements IItemDAO
{
	private final Connection connection;
	private final List<Item> allItems = new ArrayList<>();

	public CachedItemDAO(Connection connection)
	{
		this.connection = connection;
		loadItems();
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

	Item form(ResultSet rs) throws SQLException
	{
		long id = rs.getLong(1);
		String title = rs.getString(2);
		ItemRarity rarity = ItemRarity.valueOf(rs.getString(3));
		long cost = rs.getLong(4);
		String emoji = rs.getString(5);

		return new Item(id, title, rarity, cost, emoji);
	}

	public void loadItems()
	{
		try
		{
			String query = "select * from items;";
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next())
			{
				Item item = form(rs);
				allItems.add(item);
				items_id_index.put(item.getId(), item);
				items_name_index.put(item.getTitle(), item);
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public Item get_by_id(long id)
	{
		return items_id_index.get(id);
		//return allItems.stream().filter(i -> i.getId() == id).findAny().orElse(null);
	}

	@Override
	public Item get_by_name(String name)
	{
		return items_name_index.get(name);
		//return allItems.stream().filter(i -> i.getTitle().equals(name)).findAny().orElse(null);
	}

	@Override
	public List<Item> get_by_name_pattern(String re_pattern)
	{
		return allItems.stream().filter(i -> i.getTitle().matches(re_pattern)).collect(Collectors.toList());
	}

	@Override
	public List<Item> get_by_rarity(ItemRarity rarity)
	{
		return allItems.stream().filter(i -> i.getRarity() == rarity).collect(Collectors.toList());
	}

	@Override
	public List<Item> get_all()
	{
		return allItems;
	}

	Map<Long, Item> items_id_index = new HashMap<>();
	Map<String, Item> items_name_index = new HashMap<>();
}

