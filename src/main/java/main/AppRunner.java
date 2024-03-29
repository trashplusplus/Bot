package main;

import database.SQLExecutor;
import database.SQLSession;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class AppRunner
{
	public static void main(String[] args)

	{
		try
		{
			initDB();
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class); //создание объекта в API
			Bot bot = new Bot(SQLSession.sqlConnection);
			telegramBotsApi.registerBot(bot);
			Runtime.getRuntime().addShutdownHook(new Thread(bot::on_closing));

		}
		catch (TelegramApiException ex)
		{
			ex.printStackTrace();
		}
		catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
			System.out.println("Required file does not exist");
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			System.out.println("Couldn't connect to database (not really true)");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Unknown IO exception");
		}
	}

	private static void initDB() throws SQLException, IOException
	{
		SQLExecutor executor = new SQLExecutor(new File("init.sql"), SQLSession.sqlConnection);
		executor.execute();
	}
}
