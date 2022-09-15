package commands;

import database.dao.InventoryDAO;
import main.Bot;
import main.Item;
import main.Money;
import main.Player;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sellfish extends Command
{
	InventoryDAO inventoryDAO;

	public Sellfish(InventoryDAO inventoryDAO)
	{
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		long id = player.getId();

		LocalTime open = LocalTime.of(10, 0);
		LocalTime close = LocalTime.of(15, 0);

		LocalTime currentTime = LocalTime.now();

		if (currentTime.isBefore(open) || currentTime.isAfter(close))  // wtf?
		{
			host.sendMsg(id, "\uD83E\uDD88 Рыбная лавка работает с 10:00 до 15:00\n\nСдавая рыбу в лавке, Вы можете получить " +
					"в несколько раз больше выручки, чем если бы сдавали ее \uD83D\uDCDE Скупщику");
		}
		else
		{
			List<String> fish_titles = new ArrayList<>();
			fish_titles.add("Горбуша");
			fish_titles.add("Бычок");
			fish_titles.add("Карась");
			long fee = 0L;

			Iterator<Item> iter = player.getInventory().getItems().iterator();
			while (iter.hasNext())
			{
				Item item = iter.next();
				if (fish_titles.contains(item.getTitle()))
				{
					fee += item.getCost().value * 7;
					inventoryDAO.delete(id, item.getId(), 1);
					iter.remove();
				}
			}

			if (fee > 0)
			{
				host.sendMsg(id, String.format("\uD83E\uDD88 Покупатель выложил за всю рыбу %s", new Money(fee)));
				try
				{
					player.balance.transfer(fee);
				}
				catch (Money.MoneyException e)
				{
					e.printStackTrace();
					host.sendMsg(id, e.getMessage());
				}
			}
			else
			{
				host.sendMsg(id, "\uD83E\uDD88У вас нет рыбы\nЧтобы ловить рыбу, введите /fish");
			}
		}
	}
}
