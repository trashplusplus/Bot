package commands;

import database.dao.InventoryDAO;
import database.dao.ItemDAO;
import main.*;

import java.util.Random;

public class Forest extends Command
{
	ItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public Forest(ItemDAO itemDAO, InventoryDAO inventoryDAO)
	{
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		Random r = new Random();
		boolean success = r.nextBoolean();
		long fee = r.nextInt(3500);
		try
		{
			Item flashlight = itemDAO.getByNameFromCollection("Поисковый фонарь");
			Item seedling = itemDAO.getByNameFromCollection("Саженец");
			int seedling_index = player.getInventory().getItems().indexOf(seedling);
			//Achievements a = new Achievements(player);
			if (player.getInventory().getItems().contains(flashlight))
			{
				if (seedling_index != -1)  // player has seedling
				{
					if (success)
					{
						host.sendMsg(player.getId(), "\uD83C\uDF33 Вы посадили саженец, природа это оценила | +$" + fee);
						player.getMoney().transfer(fee);
						player.addXp(1);
					}
					else
					{
						host.sendMsg(player.getId(), "\uD83C\uDF33 Вы посадили саженец");
						player.addXp(2);
					}
					player.stats.trees++;  // todo this is shit ??
					if (player.stats.trees == 50)
					{
						player.ach_treeHard();
					}
					inventoryDAO.delete(player.getId(), seedling.getId(), 1);
					player.getInventory().removeItem(seedling_index);
				}
				else
				{
					host.sendMsg(player.getId(), "У вас нет саженцев");
				}
			}
			else
			{
				host.sendMsg(player.getId(), String.format("Для похода в лес вам нужен предмет `%s` \n\uD83D\uDED2 Его можно купить у других игроков в магазине", flashlight.getTitle()));
			}
		}
		catch (RuntimeException | Money.MoneyException e)
		{
			e.printStackTrace();
		}
	}
}