package commands;

import database.dao.InventoryDAO;
import database.dao.ItemDAO;
import main.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Open extends Command
{
	ItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public Open(ItemDAO itemDAO, InventoryDAO inventoryDAO)
	{
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		Random ran = new Random();
		long id = player.getId();
		Item _case = itemDAO.getByNameFromCollection("\uD83D\uDCE6 Кейс Gift");
		Item _key = itemDAO.getByNameFromCollection("\uD83D\uDD11 Ключ от кейса");

		List<Item> loot;

		loot = itemDAO.getAllFromCollection().stream().filter((item -> item.getRarity().equals(ItemRarity.Gift) ||
				item.getRarity().equals(ItemRarity.Rare))).collect(Collectors.toList());

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
				host.sendMsg(id, String.format("\uD83C\uDF89 Ура! Вам выпал предмет: `%s`", prize.getTitle()));
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
