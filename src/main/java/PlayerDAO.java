import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO implements DAO<Player>
{
	private Connection connection;
	private InventoryDAO inventoryDAO;

	public PlayerDAO(Connection connection) throws SQLException
	{
		this.connection = connection;
		inventoryDAO = new InventoryDAO(connection);
	}

	@Override
	public void put(Player player)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("insert into players (id, name, state) values (?, ?, ?);");
			ps.setLong(1, player.getId());
			ps.setString(2, player.getUsername());
			ps.setString(3, player.getState().name());
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
					Player.State.valueOf(rs.getString("state")),
					inventoryDAO.get(id)
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
				long id = rs.getLong("id");
				result.add(new Player(
						id,
						rs.getString("name"),
						Player.State.valueOf(rs.getString("state")),
						inventoryDAO.get(id)
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
	public void update(long id, Player player)
	{
		try
		{
			PreparedStatement ps = connection.prepareStatement("update players set id = ?, name = ?, state = ? where id = ?;");
			ps.setLong(1, player.getId());
			ps.setString(2, player.getUsername());
			ps.setString(3, player.getState().name());
			ps.setLong(4, id);
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
