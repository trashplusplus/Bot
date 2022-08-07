import javax.swing.*;
import java.sql.*;

public class PlayerDAO implements DAO<Player>
{
	private Connection connection;
	private Statement statement;

	public PlayerDAO(Connection connection) throws SQLException
	{
		this.connection = connection;
		this.statement = connection.createStatement();
	}

	@Override
	public void put(Player item)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("insert into players values (?, ?, ?)");
			ps.setLong(1, item.getId());
			ps.setString(2, item.getUsername());
			ps.setInt(3, item.getState().ordinal());
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
			PreparedStatement ps = connection.prepareStatement("select * from players where id = ?");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			player = new Player(rs.getInt("id"), rs.getString("name"));
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception");
		}
		return player;
	}

	@Override
	public void update(long id, Player new_item)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("update players set id = ?, name = ? where id = ?");
			ps.setLong(1, new_item.getId());
			ps.setString(2, new_item.getUsername());
			ps.setLong(3, id);
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
			PreparedStatement ps = connection.prepareStatement("delete from players where id = ?");
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
