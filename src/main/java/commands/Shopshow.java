package commands;

import database.dao.ShopDAO;
import main.Bot;
import main.Player;
import main.ShopItem;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class Shopshow extends Command
{
	ShopDAO shopDAO;

	public Shopshow(ShopDAO shopDAO)
	{
		this.shopDAO = shopDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		try
		{
			SendPhoto photo = new SendPhoto();
			photo.setPhoto(new InputFile(new File(".\\pics\\shop.jpg")));
			photo.setChatId(player.getId());

			long player_id = player.getId();

			if (shopDAO.getAll().isEmpty())
			{
				host.sendMsg(player_id, "\uD83D\uDC40 В магазине пока нет товаров, чтобы разместить введите /shopplace\n");
			}
			else
			{
				StringBuilder sb = new StringBuilder("\uD83D\uDCE1 Новости\n\nОфициальный телеграм канал: *@needlechat*\n\n");
				sb.append("\uD83D\uDC5C Все предметы в магазине:\n\n");
				for (ShopItem i : shopDAO.getAll())
				{
					sb.append(i);
				}
				sb.append("\n");
				sb.append("\uD83D\uDCB3 Чтобы купить, введите /shopbuy \n");
				sb.append("\uD83D\uDED2 Чтобы разместить свой товар, введите /shopplace \n");

				host.execute(photo);
				host.sendMsg(player_id, sb.toString());
			}
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}
}
