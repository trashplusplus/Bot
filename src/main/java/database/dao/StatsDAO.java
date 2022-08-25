package database.dao;

import main.Stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsDAO {
	private final Connection connection;

	public StatsDAO(Connection connection) {
		this.connection = connection;
	}

	public void put(Stats stats, long id) {
		try {
			PreparedStatement ps = connection.prepareStatement("insert into stats(player_id, bonus, coinWins, coinLosses, coffee, tea) values (?,?,?,?,?,?);");
			ps.setLong(1, id);
			ps.setInt(2, stats.bonus);
			ps.setInt(3, stats.coinWins);
			ps.setInt(4, stats.coinLosses);
			ps.setInt(5, stats.coffee);
			ps.setInt(6, stats.tea);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Exception in StatsDAO");
		}
	}

	public void update(Stats stats, long id) {

		try {
			PreparedStatement ps = connection.prepareStatement("update stats set bonus = ?, coinWins = ?, coinLosses = ?, coffee = ?, tea = ? where player_id = ?;");
			ps.setInt(1, stats.bonus);
			ps.setInt(2, stats.coinWins);
			ps.setInt(3, stats.coinLosses);
			ps.setInt(4, stats.coffee);
			ps.setInt(5, stats.tea);
			ps.setLong(6, id);
			ps.execute();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Exception in StatsDAO");
		}
	}

	public Stats get(long id) {
		Stats stats = null;
		try {
			PreparedStatement ps = connection.prepareStatement("select * from stats where player_id = ?");
			ps.setLong(1, id);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				stats = new Stats(rs.getInt("bonus"),
						rs.getInt("coinWins"),
						rs.getInt("coinLosses"),
						rs.getInt("coffee"),
						rs.getInt("tea")
				);
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stats;

	}


}
