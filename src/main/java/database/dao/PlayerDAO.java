package database.dao;

import database.DatabaseDateMediator;
import main.Bot;
import main.Inventory;
import main.Player;
import main.Stats;

import java.sql.*;
import java.text.ParseException;
import java.util.*;

public class PlayerDAO implements IPlayerDAO
{
	private final Connection connection;
	private final InventoryDAO inventoryDAO;
	private final StatsDAO statsDAO;
	private final AbilityDAO abilityDAO;
	Bot host;

	public PlayerDAO(Connection connection, Bot host)
	{
		this.connection = connection;
		inventoryDAO = new InventoryDAO(connection);
		statsDAO = new StatsDAO(connection);
		abilityDAO = new AbilityDAO(connection, host);
		this.host = host;
	}

	//region create
	public void put(Player player)
	{
		PreparedStatement ps = null;
		try
		{
			ps = connection.prepareStatement("insert into players (id, xp, 'level', needle, name, balance) values (?, ?, ?, ?, ?, ?);");
			ps.setLong(1, player.getId());
			ps.setInt(2, player.getXp());
			ps.setInt(3, player.getLevel());
			ps.setLong(4, player.needle);
			ps.setString(5, player.getUsername());
			ps.setLong(6, player.balance.value);
			ps.execute();
			statsDAO.put(player.getStats(), player.getId());
			abilityDAO.put(player.getId());
			ps.close();
			ps = null;
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	//endregion

	//region read
	public Player get_by_id(long id)
	{
		Player player = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "select * from player where id = ?;";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			if (rs.next())
			{
				player = form(rs);
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");  // todo
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
		}

		return player;
	}

	public Player get_by_name(String name)
	{
		Player player = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "select * from player where name = ?;";
		try
		{
			ps = connection.prepareStatement(query);
			ps.setString(1, name);
			rs = ps.executeQuery();
			if (rs.next())
			{
				player = form(rs);
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			throw new RuntimeException("SQL Exception", ex);  // todo
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
		}

		return player;
	}

	public List<Player> get_all()
	{
		List<Player> result = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "select * from player;";
		try
		{
			ps = connection.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next())
			{
				result.add(form(rs));
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);  // todo
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
		}

		return result;
	}

	public List<Player> get_top(String field_name, boolean ascending, long limit)
	{
		List<Player> players = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = String.format("select * from player order by %s %s limit ?;", field_name, ascending ? "asc" : "desc");
		try
		{
			ps = connection.prepareStatement(query);
			ps.setLong(1, limit);
			rs = ps.executeQuery();
			while (rs.next())
			{
				players.add(form(rs));
			}
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);  // todo
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
		}

		return players;
	}
	//endregion

	public int size()
	{
		Statement statement;
		ResultSet rs;
		String query = "select count(*) from players;";
		try
		{
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			rs.next();
			int result = rs.getInt(1);
			rs.close();
			statement.close();
			return result;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			throw new RuntimeException("SQL Exception", ex);
		}
	}

	//region update
	public void update(Player player)  // TODO extract exception to signature
	{
		long id = player.getId();
		PreparedStatement ps = null;
		Statement st = null;
		synchronized (connection)
		{
			try
			{
				st = connection.createStatement();
				//st.execute("begin transaction");

				String update_players =
						"update players set xp = ?, 'level' = ?, name = ?, balance = ?, needle = ? where id = ?;";
				ps = connection.prepareStatement(update_players);
				ps.setInt(1, player.getXp());
				ps.setInt(2, player.getLevel());
				ps.setString(3, player.getUsername());
				ps.setLong(4, player.balance.value);
				ps.setLong(5, player.needle);
				ps.setLong(6, id);
				ps.execute();

				String update_stats =
						"insert or replace into stats (player_id, bonus, coinWins, coinLosses, coffee, tea, trees) values (?, ?, ?, ?, ?, ?, ?);";
				ps = connection.prepareStatement(update_stats);
				Stats stats = player.stats;
				ps.setLong(1, id);
				ps.setInt(2, stats.bonus);
				ps.setInt(3, stats.coinWins);
				ps.setInt(4, stats.coinLosses);
				ps.setInt(5, stats.coffee);
				ps.setInt(6, stats.tea);
				ps.setInt(7, stats.trees);
				ps.execute();

				String update_cooldowns =
						"insert or replace into cooldowns (player_id, find_expiration, pockets_expiration) values (?, ?, ?);";
				ps = connection.prepareStatement(update_cooldowns);
				ps.setLong(1, id);
				ps.setString(2, DatabaseDateMediator.ms_to_string(player.findExpiration));
				ps.setString(3, DatabaseDateMediator.ms_to_string(player.pocketsExpiration));
				ps.execute();

				//st.execute("commit transaction;");

				ps.close();
				ps = null;
				st.close();
				st = null;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new RuntimeException("Username is already used");  // todo
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (ps != null)
				{
					try
					{
						ps.close();
					}
					catch (SQLException ex)
					{
						ex.printStackTrace();
					}
				}
				if (st != null)
				{
					try
					{
						st.close();
					}
					catch (SQLException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}
	//endregion

	//region delete
	public void delete(long id)
	{
		PreparedStatement ps = null;
		try
		{
			ps = connection.prepareStatement("delete from players where id = ?;");
			ps.setLong(1, id);
			ps.execute();
			ps.close();
			ps = null;
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");  // todo
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	//endregion

	private Player form(ResultSet rs) throws SQLException
	{
		long id = rs.getLong("id");
		String username = rs.getString("name");
		int xp = rs.getInt("xp");
		int level = rs.getInt("level");
		long balance = rs.getLong("balance");
		long needle = rs.getLong("needle");
		//Long findExpiration = read_ts(rs, "find_expiration");
		Long findExpiration = read_ts(rs, "FIND");
		//Long pocketsExpiration = read_ts(rs, "pockets_expiration");
		Long pocketsExpiration = read_ts(rs, "POCKETS");
		int bonus = rs.getInt("bonus");
		//int coinWins = rs.getInt("coinWins");
		int coinWins = rs.getInt("W");
		//int coinLosses = rs.getInt("coinLosses");
		int coinLosses = rs.getInt("L");
		int coffee = rs.getInt("coffee");
		int tea = rs.getInt("tea");
		int trees = rs.getInt("trees");
		Stats stats = new Stats(bonus, coinWins, coinLosses, coffee, tea, trees);
		Inventory inventory = inventoryDAO.get(id);
		Player player = new Player(id, xp, level, username, balance, needle, inventory, stats, host);
		player.findExpiration = findExpiration;
		player.pocketsExpiration = pocketsExpiration;
		return player;
	}

	private Long read_ts(ResultSet rs, String column_name) throws SQLException
	{
		Long result = null;
		String date_UTC_string = rs.getString(column_name);
		if (date_UTC_string != null)
		{
			try
			{
				result = DatabaseDateMediator.string_to_ms(date_UTC_string);
			}
			catch (ParseException e)
			{
				//throw new SQLException("Error while parsing last find item date from database", e);
				System.err.printf("Error while parsing %s from database, got:\n%s\n", column_name, date_UTC_string);
			}
			catch (Exception ex)
			{
				System.err.println("Unknown exception when reading database" + ex);
				ex.printStackTrace();
			}
		}

		return result;
	}
}
