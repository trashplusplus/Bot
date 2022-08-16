package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLExecutor
{
	File file;
	Connection connection;
	Scanner scanner;

	public SQLExecutor(File file, Connection connection) throws IOException
	{
		this.file = file;
		this.connection = connection;
		scanner = new Scanner(this.file, StandardCharsets.UTF_8).useDelimiter(";");
	}

	public void execute() throws SQLException
	{
		while (scanner.hasNext())
		{
			try
			{
				String sql_statement = scanner.next().strip();
				if (sql_statement.isEmpty())
				{
					break;
				}
				System.out.printf("Executing\n\t%s\n", sql_statement);
				Statement statement = connection.createStatement();
				statement.execute(sql_statement);
			}
			catch (SQLException ex)
			{
				System.out.println("SQL Exception " + ex);
				throw ex;
			}
		}
	}
}
