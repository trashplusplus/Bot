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
			PreparedStatement ps = connection.prepareStatement("insert into stats(player_id, bonus, coinWins, " +
					"coinLosses, coffee, tea, trees, capitals, " +
					"hideInv, magazines, totalWonMoney, totalLostMoney, findCounter, mudCounter, totalMud, craftCounter, duelWin, duelLose) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
			ps.setLong(1, id);
			ps.setInt(2, stats.bonus);
			ps.setInt(3, stats.coinWins);
			ps.setInt(4, stats.coinLosses);
			ps.setInt(5, stats.coffee);
			ps.setInt(6, stats.tea);
			ps.setInt(7, stats.trees);
			ps.setInt(8, stats.capitals);
			ps.setInt(9, stats.hideInv);
			ps.setInt(10, stats.magazines);
			ps.setInt(11, stats.totalWonMoney);
			ps.setInt(12, stats.totalLostMoney);
			ps.setInt(13, stats.findCounter);
			ps.setInt(14, stats.mudCounter);
			ps.setInt(15, stats.totalMud);
			ps.setInt(16, stats.craftCounter);
			ps.setInt(17, stats.duelWin);
			ps.setInt(18, stats.duelLose);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Exception in StatsDAO");
		}
	}

	public void update(Stats stats, long id) {

		try {
			PreparedStatement ps = connection.prepareStatement("update stats set bonus = ?, coinWins = ?, coinLosses = ?, coffee = ?, " +
					"tea = ?, trees = ?, capitals = ?, hideInv = ?, magazines = ?," +
					"totalWonMoney = ?, totalLostMoney = ?, findCounter = ?, mudCounter = ?, totalMud = ?, craftCounter = ?, duelWin = ?, duelLose = ? where player_id = ?;");
			ps.setInt(1, stats.bonus);
			ps.setInt(2, stats.coinWins);
			ps.setInt(3, stats.coinLosses);
			ps.setInt(4, stats.coffee);
			ps.setInt(5, stats.tea);
			ps.setInt(6, stats.trees);
			ps.setInt(7, stats.capitals);
			ps.setInt(8, stats.hideInv);
			ps.setInt(9, stats.magazines);
			ps.setInt(10, stats.totalWonMoney);
			ps.setInt(11, stats.totalLostMoney);
			ps.setInt(12, stats.findCounter);
			ps.setInt(13, stats.mudCounter);
			ps.setInt(14, stats.totalMud);
			ps.setInt(15, stats.craftCounter);
			ps.setInt(16, stats.duelWin);
			ps.setInt(17, stats.duelLose);
			ps.setLong(18, id);
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
						rs.getInt("tea"),
						rs.getInt("trees"),
						rs.getInt("capitals"),
						rs.getInt("hideInv"),
						rs.getInt("magazines"),
						rs.getInt("totalWonMoney"),
						rs.getInt("totalLostMoney"),
						rs.getInt("findCounter"),
						rs.getInt("mudCounter"),
						rs.getInt("totalMud"),
						rs.getInt("craftCounter"),
						rs.getInt("duelWin"),
						rs.getInt("duelLose")
				);
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stats;

	}


}
