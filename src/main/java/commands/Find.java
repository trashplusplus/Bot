package commands;

import database.dao.IItemDAO;
import database.dao.InventoryDAO;
import main.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Find extends Command
{
	Roller<Item> find_roller;
	IItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public Find(Roller<Item> find_roller, IItemDAO itemDAO, InventoryDAO inventoryDAO)
	{
		this.find_roller = find_roller;
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		Inventory inventory = player.inventory;
		int limitSpace = inventory.inventory_capacity;

		long player_id = player.getId();
		long now_ts = System.currentTimeMillis();
		if (inventory.getInvSize() < limitSpace)
		{
			if (player.findExpiration != null && player.findExpiration > now_ts)
			{
				StringBuilder sb = new StringBuilder(String.format("\u231B Вы устали, время ожидания:%s",
						PrettyDate.prettify(player.findExpiration - now_ts, TimeUnit.MILLISECONDS)));
				player.donateRandomer = (player.donateRandomer + 1) % 3;
				if (player.donateRandomer == 0) {
					sb.append("\n Вы можете сбросить время ожидания за 2\uD83E\uDDF7 командой /boost");
				}
				host.sendMsg(player_id, sb.toString());
			}
			else
			{
				if(player.getStatusItem() != null && player.getInventory().getInvSize() > 0 && player.getStatusItem().getRarity().equals(ItemRarity.Pet)){
					List<Item> all_gifts = itemDAO.get_by_rarity(ItemRarity.Gift);
					Item randomItem = all_gifts.get(new Random().nextInt(all_gifts.size()));
					inventoryDAO.putItem(player_id, randomItem.getId());
					inventory.putItem(randomItem);
					host.sendMsg(player_id, String.format("%s Ваш питомец откопал: %s", player.getStatus(), randomItem));
					player.addXp(50);
					player.stats.findCounter++;
					player.findExpiration = now_ts + host.findCooldown;
				}else{
					Item new_item = find_roller.roll();
					inventoryDAO.putItem(player_id, new_item.getId());
					inventory.putItem(new_item);
					host.sendMsg(player_id, String.format("\uD83C\uDF81\t Вы нашли: %s", new_item));
					player.addXp(10);
					player.stats.findCounter++;
					player.findExpiration = now_ts + host.findCooldown;
				}
			}
		}
		else
		{
			host.sendMsg(player_id, "⚠ В вашем инвентаре нет места");
		}
	}
}
