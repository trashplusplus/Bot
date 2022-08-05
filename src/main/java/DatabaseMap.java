import java.sql.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DatabaseMap<Long, V> implements Map<Long, V>
{
	private Connection conn;
	private String tableName;

	public DatabaseMap(String databaseName, String tableName) throws SQLException
	{
		DriverManager.getConnection(databaseName);
		this.tableName = tableName;
	}

	@Override
	public int size()
	{
		try
		{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from " + tableName + ";");
			rs.next();
			int size = rs.getInt(1);
			rs.close();
			stmt.close();

			return size;
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);
		}
	}

	@Override
	public boolean isEmpty()
	{
		return size() != 0;
	}

	@Override
	public boolean containsKey(Object key)
	{
		try
		{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from " + tableName + " where id = " + key);
			if (rs.next())
			{
				rs.close();
				stmt.close();
				return true;
			}
			rs.close();
			stmt.close();
			return false;
		}
		catch (SQLException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
			throw new RuntimeException("SQL Exception", e);
		}
	}

	@Override
	public boolean containsValue(Object value)
	{
		return false;
	}

	@Override
	public V get(Object key)
	{
		return null;
	}

	@Override
	public V put(Long key, V value)
	{
		return null;
	}

	@Override
	public V remove(Object key)
	{
		return null;
	}

	@Override
	public void putAll(Map<? extends Long, ? extends V> m)
	{

	}

	@Override
	public void clear()
	{

	}

	@Override
	public Set<Long> keySet()
	{
		return null;
	}

	@Override
	public Collection<V> values()
	{
		return null;
	}

	@Override
	public Set<Entry<Long, V>> entrySet()
	{
		return null;
	}
}
