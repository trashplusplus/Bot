package database.dao;

import database.DatabaseDateMediator;
import main.Bot;
import main.Inventory;
import main.Player;
import main.Stats;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {
	private final Connection connection;
	private final InventoryDAO inventoryDAO;
	private final StatsDAO statsDAO;
	private final AbilityDAO abilityDAO;
	Bot host;

	private static final long findCooldown = 20L * 60L * 1000L;
	private static final long pocketsCooldown = 30L * 1000L;

	public PlayerDAO(Connection connection, Bot host) {
		this.connection = connection;
		inventoryDAO = new InventoryDAO(connection);
		statsDAO = new StatsDAO(connection);
		abilityDAO = new AbilityDAO(connection, host);
		this.host = host;
	}

	public void put(Player player) {
		try {
			PreparedStatement ps = connection.prepareStatement("insert into players (id, xp, 'level', name, balance, registered) values (?, ?, ?, ?, ?, ?);");
			ps.setLong(1, player.getId());
			ps.setInt(2, player.getXp());
			ps.setInt(3, player.getLevel());
			ps.setString(4, player.getUsername());
			ps.setInt(5, player.balance);
			ps.setInt(6, player.getState() == Player.State.awaitingNickname ? 0 : 1);
			ps.execute();
			inventoryDAO.put(player.getId(), player.getInventory());
			statsDAO.put(player.getStats(), player.getId());
		} catch (SQLException e) {
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
	}

	public Player get_by_id(long id) {
		try {
			PreparedStatement ps = connection.prepareStatement("select * from players, ability_cooldowns on players.id = ability_cooldowns.player_id where id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			Player player = null;
			if (rs.next()) {
				player = form(rs);
			}
			rs.close();
			return player;
		} catch (SQLException e) {
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
	}

	public Player get_by_name(String name) {
		String query = "select * from players, ability_cooldowns on players.id = ability_cooldowns.player_id where name = ?;";
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			Player player = null;
			if (rs.next()) {
				player = form(rs);
			}
			return player;
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException("SQL Exception", ex);
		}
	}

	public List<Player> getAll() {

		List<Player> result = new ArrayList<>();

		try {
			PreparedStatement ps = connection.prepareStatement("select * from players, ability_cooldowns on players.id = ability_cooldowns.player_id;");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(form(rs));

			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);
		}
	}

	public List<Player> getTopN(String field_name, boolean ascending, int limit) {
		try {
			List<Player> players = new ArrayList<>();
			String query = String.format("select * from players, ability_cooldowns on players.id = ability_cooldowns.player_id where registered = 1 order by %s %s limit ?;", field_name, ascending ? "asc" : "desc");
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, limit);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				players.add(form(rs));
			}
			return players;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);
		}
	}

	public int size() {
		String query = "select count(*) from players;";
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);
		}
	}

	public void update(Player player)  // TODO extract exception to signature
	{

		long id = player.getId();
		long now_t = System.currentTimeMillis();
		try {
			PreparedStatement ps = connection.prepareStatement("update players set xp = ?, 'level' = ?, name = ?, balance = ?, registered = ? where id = ?;");
			ps.setInt(1, player.getXp());
			ps.setInt(2, player.getLevel());
			ps.setString(3, player.getUsername());
			ps.setInt(4, player.balance);
			ps.setInt(5, player.getState() == Player.State.awaitingNickname ? 0 : 1);
			//ps.setString(6, DatabaseDateMediator.ms_to_string(player.last_fia));
			//ps.setString(7, DatabaseDateMediator.ms_to_string(player.last_pockets));
			ps.setLong(6, id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Username is already used");
		}
	}

	public void delete(long id) {
		try {
			PreparedStatement ps = connection.prepareStatement("delete from players where id = ?;");
			ps.setLong(1, id);
			ps.execute();
		} catch (SQLException e) {
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
	}

	private Player form(ResultSet rs) throws SQLException {
		long id = rs.getLong("id");
		int xp = rs.getInt("xp");
		int level = rs.getInt("level");
		String username = rs.getString("name");
		int balance = rs.getInt("balance");
		Player.State state = rs.getInt("registered") == 1 ? Player.State.awaitingCommands : Player.State.awaitingNickname;

		Inventory inventory = inventoryDAO.get(id);
		Stats stats = statsDAO.get(id);
		Player player = new Player(id, xp, level, username, balance, state, inventory, stats, host);
		long[] expirations = abilityDAO.get(id);
		player.findExpiration = expirations[0];
		player.pocketsExpiration = expirations[1];
		return player;
	}

	private long read_ts(ResultSet rs, String column_name) throws SQLException {
		long result = 0;
		String date_UTC_string = rs.getString(column_name);
		if (date_UTC_string != null) {
			try {
				result = DatabaseDateMediator.string_to_ms(date_UTC_string);
			} catch (ParseException e) {
				//throw new SQLException("Error while parsing last find item date from database", e);
				System.err.printf("Error while parsing last find item date from database, got:\n%s\n", date_UTC_string);
			} catch (Exception ex) {
				System.err.println("Unknown exception when reading database" + ex);
				ex.printStackTrace();
			}
		}

		return result;
	}
}
