package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLExecutor
{
	File file;
	Connection connection;
	Scanner scanner;

	public SQLExecutor(File file, Connection connection) throws FileNotFoundException
	{
		this.file = file;
		this.connection = connection;
		scanner = new Scanner(this.file).useDelimiter(";");
	}

	public void execute() throws SQLException
	{
		while (scanner.hasNext())
		{
			String sql_statement = scanner.next();
			Statement statement = connection.createStatement();
			statement.execute(sql_statement);
		}
	}
}
