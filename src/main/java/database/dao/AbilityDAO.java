package database.dao;

import database.DatabaseDateMediator;
import main.Bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class AbilityDAO {
	private final Connection connection;
	private final Bot host;

	public AbilityDAO(Connection connection, Bot host) {
		this.connection = connection;
		this.host = host;
	}

	public void put(long player_id)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("insert into ability_cooldowns values (?, null, null);");
			ps.setLong(1, player_id);
			ps.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public long[] get(long player_id)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from ability_cooldowns where player_id = ?;");
			ps.setLong(1, player_id);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				return new long[] {read_ts(rs, "find_expiration"), read_ts(rs, "pockets_expiration")};
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
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
