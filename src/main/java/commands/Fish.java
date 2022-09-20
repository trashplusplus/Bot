package commands;

import database.dao.InventoryDAO;
import database.dao.CachedItemDAO;
import main.Bot;
import main.Item;
import main.Player;
import main.Roller;

public class Fish extends Command
{
	Roller<Item> fish_roller;
	CachedItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public Fish(Roller<Item> fish_roller, CachedItemDAO itemDAO, InventoryDAO inventoryDAO)
	{
		this.fish_roller = fish_roller;
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		Item i = itemDAO.getByNameFromCollection("Удочка");
		int limitSpace;
		Item backpack = itemDAO.getByNameFromCollection("Рюкзак");

		if (player.getInventory().getItems().contains(backpack))
		{
			limitSpace = 25;
		}
		else
		{
			limitSpace = 20;
		}

		if (player.getLevel() >= 5)
		{
			if (player.getInventory().getItems().contains(i))
			{
				if (player.getInventory().getInvSize() < limitSpace)
				{
					Item item = fish_roller.roll();

					if (item != null)
					{
						inventoryDAO.putItem(player.getId(), item.getId());
						player.getInventory().putItem(item);
						host.sendMsg(player.getId(), String.format("Вы поймали %s", item));
						player.addXp(1);
					}
					else
					{
						host.sendMsg(player.getId(), "Не клюет");
					}
				}
				else
				{
					host.sendMsg(player.getId(), "⚠ В вашем инвентаре нет места");
				}
			}
			else
			{
				host.sendMsg(player.getId(), String.format("Для похода на рыбалку вам нужен предмет `%s` \n\uD83D\uDED2 Его можно купить у других игроков в магазине или найти", i.getTitle()));
			}
		}
		else
		{
			host.sendMsg(player.getId(), "\uD83D\uDC7E Для похода на рыбалку вам нужен 5 уровень");
		}
	}
}
