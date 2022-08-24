package database.dao;

import database.DatabaseDateMediator;
import main.Bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AbilityDAO {
	private final Connection connection;
	private final Bot host;

	public AbilityDAO(Connection connection, Bot host) {
		this.connection = connection;
		this.host = host;
	}

	public void put(long player_id) {
		try {
			PreparedStatement ps = connection.prepareStatement("insert into ability_cooldowns values (?, null, null);");
			ps.setLong(1, player_id);
			ps.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public Long[] get(long player_id) {
		try {
			PreparedStatement ps = connection.prepareStatement("select * from ability_cooldowns where player_id = ?;");
			ps.setLong(1, player_id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new Long[]{read_ts(rs, "find_expiration"), read_ts(rs, "pockets_expiration")};
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return new Long[2];
	}

	public List<Long> expireFind() {
		List<Long> expires = new ArrayList<>();
		long nowTs = System.currentTimeMillis();
		String select_query = "select player_id from ability_cooldowns where find_expiration < ?;";
		String update_query = "update ability_cooldowns set find_expiration = null where find_expiration < ?;";
		try {
			synchronized (connection) {
				connection.createStatement().execute("begin transaction;");

				PreparedStatement ps = connection.prepareStatement(select_query);
				ps.setString(1, DatabaseDateMediator.ms_to_string(nowTs));
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					expires.add(rs.getLong("player_id"));
				}

				ps = connection.prepareStatement(update_query);
				ps.setString(1, DatabaseDateMediator.ms_to_string(nowTs));
				ps.execute();

				connection.createStatement().execute("commit transaction;");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return expires;
	}

	public void updateFind(long player_id, long expiration_ts) {
		try {
			PreparedStatement ps = connection.prepareStatement("update ability_cooldowns set find_expiration = ? where player_id = ?;");
			ps.setString(1, DatabaseDateMediator.ms_to_string(expiration_ts));
			ps.setLong(2, player_id);
			ps.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public List<Long> expirePockets() {
		List<Long> expires = new ArrayList<>();
		long nowTs = System.currentTimeMillis();
		String update_query = "update ability_cooldowns set pockets_expiration = null where pockets_expiration < ?;";
		try {
			PreparedStatement ps = connection.prepareStatement(update_query);
			ps.setString(1, DatabaseDateMediator.ms_to_string(nowTs));
			ps.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return expires;
	}

	public void updatePockets(long player_id, long expiration_ts) {
		try {
			PreparedStatement ps = connection.prepareStatement("update ability_cooldowns set pockets_expiration = ? where player_id = ?;");
			ps.setString(1, DatabaseDateMediator.ms_to_string(expiration_ts));
			ps.setLong(2, player_id);
			ps.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private Long read_ts(ResultSet rs, String column_name) throws SQLException {
		Long result = null;
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
