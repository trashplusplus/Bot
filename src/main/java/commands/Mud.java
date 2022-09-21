package commands;

import database.dao.InventoryDAO;
import database.dao.IItemDAO;
import main.Bot;
import main.Item;
import main.Player;
import main.Roller;

public class Mud extends Command
{
	Roller<Item> mud_roller;
	IItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public Mud(Roller<Item> mud_roller, IItemDAO itemDAO, InventoryDAO inventoryDAO)
	{
		this.mud_roller = mud_roller;
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		int limitSpace;
		Item backpack = itemDAO.get_by_name("\uD83C\uDF92 Рюкзак");
		if (player.getInventory().getItems().contains(backpack))
		{
			limitSpace = 25;
		}
		else
		{
			limitSpace = 20;
		}


		long id = player.getId();
		if (player.getInventory().getInvSize() < limitSpace)
		{
			Item item = mud_roller.roll();
			if (item != null)
			{
				inventoryDAO.putItem(id, item.getId());
				player.getInventory().putItem(item);
				host.sendMsg(id, String.format("Вы нашли в грязи %s", item));
				player.addXp(1);
			}
			else
			{
				host.sendMsg(id, "Вы ничего не нашли");
			}
		}
		else
		{
			host.sendMsg(id, "⚠ В вашем инвентаре нет места");
		}
	}
}
