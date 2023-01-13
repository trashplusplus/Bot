package database.dao;

import database.DatabaseDateMediator;
import main.Bot;
import main.Item;
import main.Player;
import main.ShopItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShopDAO
{
	private final Connection connection;
	CachedItemDAO item;
	IPlayerDAO playerDAO;

	Bot host;

	private static final long shopItemDuration = 12L * 60L * 60L * 1000L;
	//private static final long shopItemDuration = 10000L;

	public ShopDAO(Connection connection, Bot host, IPlayerDAO playerDAO)
	{
		this.connection = connection;
		item = new CachedItemDAO(this.connection);
		this.host = host;
		this.playerDAO = playerDAO;
	}

	public void put(ShopItem shopItem)
	{
		try
		{
			connection.createStatement().execute("begin transaction;");

			PreparedStatement ps = connection.prepareStatement("insert into shop(item_id, cost, seller_id) values (?, ?, ?);");
			ps.setLong(1, shopItem.getItem().getId());
			ps.setLong(2, shopItem.getCost().value);
			ps.setLong(3, shopItem.getSeller().getId());
			ps.execute();

			ps = connection.prepareStatement("insert into shop_expiration (shop_id, exp_date) values (last_insert_rowid(), ?);");
			//ps.setInt(1, shopItem.getId());
			ps.setString(1, DatabaseDateMediator.ms_to_string(System.currentTimeMillis() + shopItemDuration));
			ps.execute();

			connection.createStatement().execute("commit transaction;");
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception in ShopDAO");
		}
	}

	public List<ShopItem> getBySellerName(String sellerName)
	{
		List<ShopItem> result = new ArrayList<>();
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from shop where sellerName = ?;");
			ps.setString(1, sellerName);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				result.add(form(rs));
			}
			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQL Exception in ShopDAO", e);
		}
	}

	public List<ShopItem> getAll()
	{
		List<ShopItem> result = new ArrayList<>();
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from shop");
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				result.add(form(rs));
			}
			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQL Exception in ShopDAO", e);
		}
	}

	public ShopItem getByID(int index)
	{
		ShopItem s = null;
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from shop where id = ?");
			ps.setInt(1, index);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				s = form(rs);
			}
			else
			{
				throw new IndexOutOfBoundsException();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQL Exception in ShopDAO", e);
		}
		return s;
	}

	/*
	public void update(ShopItem shopItem){
		try {
			PreparedStatement ps = connection.prepareStatement("update shop set cost = ?, where id = ?");
			ps.setString(1, shopItem.getItem().getTitle());
			ps.setInt(2, shopItem.getCost());
			ps.setString(3, shopItem.getSeller());


		}catch (SQLException e){
			e.printStackTrace();
		}
	}

	*/
	public void delete(int id)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("delete from shop where id = ?;");
			ps.setInt(1, id);
			ps.execute();
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception in ShopDAO");
		}
	}

	synchronized public List<ShopItem> expire()
	{
		long now_t = System.currentTimeMillis();
		String now_ts = DatabaseDateMediator.ms_to_string(now_t);
		List<ShopItem> shopItems = new ArrayList<>();
		synchronized (connection)
		{
			try
			{
				int deleted = -1;
				//connection.createStatement().execute("begin transaction;");

				PreparedStatement ps = connection.prepareStatement("select * from shop where id in (select shop_id from shop_expiration where exp_date < ?);");
				ps.setString(1, now_ts);
				ResultSet rs = ps.executeQuery();
				while (rs.next())
				{
					shopItems.add(form(rs));
				}

				//rs.close();
				//ps.close();

				ps = connection.prepareStatement("delete from shop where id in (select shop_id from shop_expiration where exp_date < ?);");
				ps.setString(1, now_ts);
				deleted = ps.executeUpdate();

				ps = connection.prepareStatement("delete from shop_expiration where exp_date < ?;");
				ps.setString(1, now_ts);
				ps.execute();

				//connection.createStatement().execute("commit transaction;");

				assert (deleted == shopItems.size());
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}

		return shopItems;
	}

	private ShopItem form(ResultSet rs) throws SQLException
	{
		int id = rs.getInt("id");
		Item item = this.item.get_by_id(rs.getLong("item_id"));
		long cost = rs.getLong("cost");
		Player seller = playerDAO.get_by_id(rs.getLong("seller_id"));
		return new ShopItem(id, item, cost, seller);
	}
}
