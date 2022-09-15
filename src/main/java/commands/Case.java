package commands;

import database.dao.ItemDAO;
import main.Bot;
import main.Item;
import main.Player;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class Case extends Command
{
	ItemDAO itemDAO;

	public Case(ItemDAO itemDAO)
	{
		this.itemDAO = itemDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		try
		{
			SendPhoto photo = new SendPhoto();
			photo.setPhoto(new InputFile(new File(".\\pics\\case.jpg")));
			photo.setChatId(player.getId());

			int casesCounter = 0;
			int keysCounter = 0;
			long id = player.getId();

			StringBuilder sb = new StringBuilder("*Открытие кейсов*\n\n");

			Item _case = itemDAO.getByNameFromCollection("\uD83D\uDCE6 Кейс Gift");
			Item _key = itemDAO.getByNameFromCollection("\uD83D\uDD11 Ключ от кейса");

			for (Item item : player.getInventory().getItems())
			{
				if (item.equals(_case))
				{
					casesCounter++;
				}
				if (item.equals(_key))
				{
					keysCounter++;
				}
			}

			sb.append("В кейсах могут выпадать различные предметы редкости `Gift` и `Rare`\n");
			sb.append("Кейсы можно скрафтить из `Отвертки` и `Подшипника` в разделе `\uD83E\uDD65 Рецепты`\n");
			sb.append("Чтобы открыть кейс нужен предмет `\uD83D\uDD11 Ключ от кейса`\n\n");

			sb.append(String.format("\uD83D\uDCE6 Кейсы: %d\n", casesCounter));
			sb.append(String.format("\uD83D\uDD11 Ключи: %d\n", keysCounter));
			sb.append("\n\n");

			sb.append("Введите /open чтобы открыть кейс: \n");
			host.execute(photo);
			host.sendMsg(id, sb.toString());
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}
}
