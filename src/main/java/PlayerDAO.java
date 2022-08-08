import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO implements DAO<Player>
{
	private Connection connection;

	public PlayerDAO(Connection connection) throws SQLException
	{
		this.connection = connection;
	}

	@Override
	public void put(Player item)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("insert into players (id, name, state) values (?, ?, ?);");
			ps.setLong(1, item.getId());
			ps.setString(2, item.getUsername());
			ps.setString(3, item.getState().name());
			ps.execute();
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
	}

	@Override
	public Player get(long id)
	{
		Player player = null;
		try
		{
			PreparedStatement ps = connection.prepareStatement("select * from players where id = ?;");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			player = new Player(
					rs.getInt("id"),
					rs.getString("name"),
					Player.State.valueOf(rs.getString("state"))
			);
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
			PreparedStatement ps = connection.prepareStatement("select * from players;");
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				result.add(new Player(
						rs.getLong("id"),
						rs.getString("name"),
						Player.State.valueOf(rs.getString("state"))
				));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void update(long id, Player new_item)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("update players set id = ?, name = ?, state = ? where id = ?;");
			ps.setLong(1, new_item.getId());
			ps.setString(2, new_item.getUsername());
			ps.setString(3, new_item.getState().name());
			ps.setLong(4, id);
			ps.setInt(5, new_item.getInventory().getBalance());
			ps.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
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
