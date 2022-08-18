package database.dao;

import database.DatabaseDateMediator;
import main.Inventory;
import main.Player;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO
{
	private final Connection connection;
	private final InventoryDAO inventoryDAO;

	public PlayerDAO(Connection connection)
	{
		this.connection = connection;
		inventoryDAO = new InventoryDAO(connection);
	}

	public void put(Player player)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("insert into players (id, xp, 'level', name, balance, state, lastfia, lastpockets) values (?, ?, ?, ?, ?, ?, ?, ?);");
			ps.setLong(1, player.getId());
			ps.setInt(2, player.getXp());
			ps.setInt(3, player.getLevel());
			ps.setString(4, player.getUsername());
			ps.setInt(5, player.balance);
			ps.setString(6, player.getState().name());
			ps.setString(7, DatabaseDateMediator.ms_to_string(player.last_fia));
			ps.setString(8, DatabaseDateMediator.ms_to_string(player.last_pockets));
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

	public Player get_by_id(long id)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from players where id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			Player player = null;
			if (rs.next())
			{
				player = form(rs);
			}
			rs.close();
			return player;
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
	}

	public Player get_by_name(String name) throws SQLException
	{
		String query = "select * from players where name = ?;";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, name);
		ResultSet rs = ps.executeQuery();
		Player player = null;
		if (rs.next())
		{
			player = form(rs);
		}
		return player;
	}

	public List<Player> getAll()
	{

		List<Player> result = new ArrayList<>();

		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from players;");
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
			throw new RuntimeException("SQL Exception", e);
		}
	}

	public List<Player> getTopN(String field_name, boolean ascending, int limit)
	{
		try
		{
			List<Player> players = new ArrayList<>();
			String query = String.format("select * from players order by %s %s limit ?;", field_name, ascending ? "asc" : "desc");
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, limit);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				players.add(form(rs));
			}
			return players;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);
		}
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

	public void update(Player player)  // TODO extract exception to signature
	{
		long id = player.getId();
		try
		{
			PreparedStatement ps = connection.prepareStatement("update players set xp = ?, 'level' = ?, name = ?, balance = ?, state = ?, lastfia = ?, lastpockets = ? where id = ?;");
			ps.setInt(1, player.getXp());
			ps.setInt(2, player.getLevel());
			ps.setString(3, player.getUsername());

			ps.setInt(4, player.balance);
			ps.setString(5, player.getState().name());
			ps.setString(6, DatabaseDateMediator.ms_to_string(player.last_fia));
			ps.setString(7, DatabaseDateMediator.ms_to_string(player.last_pockets));
			ps.setLong(8, id);
			ps.execute();
			//inventoryDAO.put(id, player.getInventory());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Username is already used");
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

	private Player form(ResultSet rs) throws SQLException
	{
		long id = rs.getLong(1);
		int xp = rs.getInt(2);
		int level = rs.getInt(3);
		String username = rs.getString(4);
		int balance = rs.getInt(5);
		Player.State state = Player.State.valueOf(rs.getString(6));
		//long last_fia;
		//long last_pockets;
		long[] timestamps = new long[2];
		for (int i = 0; i < 2; i++)
		{
			String date_UTC_string = rs.getString(7 + i);
			if (date_UTC_string != null)
			{
				try
				{
					timestamps[i] = DatabaseDateMediator.string_to_ms(date_UTC_string);
				}
				catch (ParseException e)
				{
					//throw new SQLException("Error while parsing last find item date from database", e);
					timestamps[i] = 0;
					System.err.printf("Error while parsing last find item date from database, got:\n%s\n", date_UTC_string);
				}
				catch (Exception ex)
				{
					timestamps[i] = 0;
					System.err.println("Unknown exception when reading database" + ex);
					ex.printStackTrace();
				}
			}
			else
			{
				timestamps[i] = 0;
			}
		}

		Inventory inventory = inventoryDAO.get(id);
		return new Player(id, xp, level, username, balance, state, inventory, timestamps[0], timestamps[1]);
	}
}