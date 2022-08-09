package database;

import database.InventoryDAO;
import main.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO
{
	private Connection connection;
	private InventoryDAO inventoryDAO;

	public PlayerDAO(Connection connection)
	{
		this.connection = connection;
		inventoryDAO = new InventoryDAO(connection);
	}

	public void put(Player player)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("insert into players (id, name, balance, state) values (?, ?, ?, ?);");
			ps.setLong(1, player.getId());
			ps.setString(2, player.getUsername());
			ps.setInt(3, player.balance);
			ps.setString(4, player.getState().name());
			ps.execute();
			inventoryDAO.put(player.getId(), player.getInventory());
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
	}

	public Player get(long id)
	{
		Player player = null;
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from players where id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				player = new Player(
						rs.getLong("id"),
						rs.getString("name"),
						rs.getInt("balance"),
						Player.State.valueOf(rs.getString("state")),
						inventoryDAO.get(id)
				);
			}
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
		return player;
	}

	public List<Player> getAll()
	{
		List<Player> result = new ArrayList<>();
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from players order by balance desc;");
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				long id = rs.getLong("id");
				result.add(new Player(
						id,
						rs.getString("name"),
						rs.getInt("balance"),
						Player.State.valueOf(rs.getString("state")),
						inventoryDAO.get(id)
				));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public int size()
	{
		String query = "select count(*) from players;";
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

	public void update(Player player)
	{
		long id = player.getId();
		try
		{
			PreparedStatement ps = connection.prepareStatement("update players set name = ?, balance = ?, state = ? where id = ?;");
			ps.setString(1, player.getUsername());
			ps.setInt(2, player.balance);
			ps.setString(3, player.getState().name());
			ps.setLong(4, id);
			ps.execute();
			//inventoryDAO.put(id, player.getInventory());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void delete(long id)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("delete from players where id = ?;");
			ps.setLong(1, id);
			ps.execute();
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
	}
}
