package commands;

import database.dao.InventoryDAO;
import database.dao.IItemDAO;
import main.*;

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
		Inventory inventory = player.inventory;
		int limitSpace = inventory.inventory_capacity;

		long id = player.getId();
		if (inventory.getInvSize() < limitSpace)
		{
			Item item = mud_roller.roll();
			if (item != null)
			{
				inventoryDAO.putItem(id, item.getId());
				inventory.putItem(item);
				host.sendMsg(id, String.format("Вы нашли в грязи %s", item));
				player.stats.mudCounter++;
				player.addXp(1);
			}
			else
			{
				host.sendMsg(id, "Вы ничего не нашли");
				player.stats.totalMud++;
			}
		}
		else
		{
			host.sendMsg(id, "⚠ В вашем инвентаре нет места");
		}
	}
}
