package database.dao;

import main.Item;
import main.ItemRarity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CachedItemDAO implements IItemDAO
{
	private final Connection connection;
	private final List<Item> allItems = new ArrayList<>();

	public CachedItemDAO(Connection connection) {
		this.connection = connection;
		loadItems();
	}

	public Item get(long id) {
		Item item = null;
		try {
			PreparedStatement ps = connection.prepareStatement("select * from items where id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				item = form(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return item;
	}

	public int size() {
		try {
			String query = "select count(*) from items;";
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);
		}
	}

	public Item getByName(String name) {
		Item item = null;
		try {
			PreparedStatement ps = connection.prepareStatement("select * from items where name = ?;");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				item = form(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return item;
	}

	public List<Item> getAll() {
		List<Item> res = new ArrayList<>();
		try {
			String query = "select * from items;";
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				res.add(form(rs));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return res;
	}

	Item form(ResultSet rs) throws SQLException {
		long id = rs.getLong(1);
		String title = rs.getString(2);
		ItemRarity rarity = ItemRarity.valueOf(rs.getString(3));
		long cost = rs.getLong(4);
		String emoji = rs.getString(5);

		return new Item(id, title, rarity, cost, emoji);
	}

	public void loadItems(){
		try {
			String query = "select * from items;";
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				allItems.add(form(rs));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	public Item getByNameFromCollection(String title){
		Item item = null;
		for(Item i: allItems){
			if(title.equals(i.getTitle()))
			item = i;
		}
		return item;
	}

	public List<Item> getAllFromCollection(){
		return allItems;
	}

	@Override
	public Item get_by_id(long id)
	{
		return allItems.stream().filter(i -> i.getId() == id).findAny().orElse(null);
	}

	@Override
	public Item get_by_name(String name)
	{
		return allItems.stream().filter(i -> i.getTitle().equals(name)).findAny().orElse(null);
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
}

