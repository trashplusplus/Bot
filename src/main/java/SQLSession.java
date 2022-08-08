import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLSession
{
	public static final Connection sqlConnection;

	static
	{
		String db_url = "jdbc:sqlite:data.db";
		try
		{
			sqlConnection = DriverManager.getConnection(db_url);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Can't connect to database " + db_url, e);
		}
	}
}
