package commands;

import database.dao.InventoryDAO;
import database.dao.ItemDAO;
import main.*;

import java.util.concurrent.TimeUnit;

public class Find extends Command
{
	Roller<Item> find_roller;
	ItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public Find(Roller<Item> find_roller, ItemDAO itemDAO, InventoryDAO inventoryDAO)
	{
		this.find_roller = find_roller;
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		int limitSpace;
		Item backpack = itemDAO.getByNameFromCollection("\uD83C\uDF92 Рюкзак");
		if (player.getInventory().getItems().contains(backpack))
		{
			limitSpace = 25;
		}
		else
		{
			limitSpace = 20;
		}

		long player_id = player.getId();
		long now_ts = System.currentTimeMillis();
		if (player.getInventory().getInvSize() < limitSpace)
		{
			if (player.findExpiration != null && player.findExpiration > now_ts)
			{
				host.sendMsg(player_id, String.format("\u231B Вы устали, время ожидания:%s",
						PrettyDate.prettify(player.findExpiration - now_ts, TimeUnit.MILLISECONDS)));
			}
			else
			{
				Item new_item = find_roller.roll();
				inventoryDAO.putItem(player_id, new_item.getId());
				player.getInventory().putItem(new_item);
				host.sendMsg(player_id, String.format("\uD83C\uDF81\t Вы нашли: %s", new_item));
				player.addXp(5);
				player.findExpiration = now_ts + host.findCooldown;
			}
		}
		else
		{
			host.sendMsg(player_id, "⚠ В вашем инвентаре нет места");
		}
	}
}
