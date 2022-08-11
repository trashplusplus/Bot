package database;

import main.Inventory;
import main.Player;

import java.sql.*;
import java.text.ParseException;
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
			PreparedStatement ps = connection.prepareStatement("insert into players (id, name, balance, state, lastfia) values (?, ?, ?, ?, ?);");
			ps.setLong(1, player.getId());
			ps.setString(2, player.getUsername());
			ps.setInt(3, player.balance);
			ps.setString(4, player.getState().name());
			ps.setString(5, DatabaseDateMediator.ms_to_string(player.last_fia));
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

	public void update(Player player)
	{
		long id = player.getId();
		try
		{
			PreparedStatement ps = connection.prepareStatement("update players set name = ?, balance = ?, state = ?, lastfia = ? where id = ?;");
			ps.setString(1, player.getUsername());
			ps.setInt(2, player.balance);
			ps.setString(3, player.getState().name());
			ps.setString(4, DatabaseDateMediator.ms_to_string(player.last_fia));
			ps.setLong(5, id);
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
		String username = rs.getString(2);
		int balance = rs.getInt(3);
		Player.State state = Player.State.valueOf(rs.getString(4));
		String date_UTC_string = rs.getString(5);
		long last_fia;
		if (date_UTC_string != null)
		{
			try
			{
				last_fia = DatabaseDateMediator.string_to_ms(date_UTC_string);
			}
			catch (ParseException e)
			{
				//throw new SQLException("Error while parsing last find item date from database", e);
				last_fia = 0;
				System.err.printf("Error while parsing last find item date from database, got:\n%s\n", date_UTC_string);
			}
			catch (Exception ex)
			{
				last_fia = 0;
				System.err.println("Unknown exception when reading database" + ex);
				ex.printStackTrace();
			}
		}
		else
		{
			last_fia = 0;
		}

		Inventory inventory = inventoryDAO.get(id);
		return new Player(id, username, balance, state, inventory, last_fia);
	}
}
