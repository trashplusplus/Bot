package commands;

import com.google.common.collect.Streams;
import database.dao.InventoryDAO;
import database.dao.IItemDAO;
import main.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Open extends Command
{
	IItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public Open(IItemDAO itemDAO, InventoryDAO inventoryDAO)
	{
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player){

		long id = player.getId();
		Item _case = itemDAO.get_by_name("Кейс Gift");
		Item _key = itemDAO.get_by_name("Ключ от кейса");

		List<Item> loot;
		Random ran = new Random();

		loot = Streams.concat(itemDAO.get_by_rarity(ItemRarity.Gift).stream(), itemDAO.get_by_rarity(ItemRarity.Rare).stream())
				.collect(Collectors.toList());
		int ranIndex = ran.nextInt(loot.size());

		Inventory inventory = player.getInventory();
		List<Item> player_items = inventory.getItems();
		int case_idx = player_items.indexOf(_case);
		int key_idx = player_items.indexOf(_key);

		if (case_idx != -1)  // player has case
		{
			if (key_idx != -1)  // player has key
			{
				Item prize = loot.get(ranIndex);
				host.sendMsg(player.getId(), String.format("\uD83C\uDF89 Ура! Вам выпал предмет: `%s`", prize.getEmojiTitle()));
				inventoryDAO.putItem(id, prize.getId());
				inventory.putItem(prize);

				inventoryDAO.delete(id, _key.getId(), 1);
				inventory.removeItem(key_idx);

				inventoryDAO.delete(id, _case.getId(), 1);
				inventory.removeItem(case_idx);
			}
			else
			{
				host.sendMsg(id, "У вас нет ключей");
			}
		}
		else
		{
			host.sendMsg(id, "У вас нет кейсов");
		}

	}

}
