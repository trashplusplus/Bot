package commands;

import com.google.common.collect.Streams;
import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import database.dao.IItemDAO;
import database.dao.PlayerDAO;
import main.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Open extends Command
{
	IItemDAO itemDAO;
	InventoryDAO inventoryDAO;
	IPlayerDAO playerDAO;


	public Open(IItemDAO itemDAO, InventoryDAO inventoryDAO, IPlayerDAO playerDAO)
	{
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
		this.playerDAO = playerDAO;
	}

	@Override
	public void consume(Bot host, Player player){

		long id = player.getId();
		Item _case = itemDAO.get_by_name("Кейс Gift");
		Item _key = itemDAO.get_by_name("Ключ от кейса");

		List<Item> loot;
		Random ran = new Random();
		List<Item> rareItemsWithNotification = new ArrayList<>();

		loot = Streams.concat(itemDAO.get_by_rarity(ItemRarity.Gift).stream(), itemDAO.get_by_rarity(ItemRarity.Rare).stream())
				.collect(Collectors.toList());

		loot.add(itemDAO.get_by_name("Лапки"));
		loot.add(itemDAO.get_by_name("Вояджер-1"));
		loot.add(itemDAO.get_by_name("UFO"));
		loot.add(itemDAO.get_by_name("Nosebleed"));
		loot.add(itemDAO.get_by_name("C4"));
		loot.add(itemDAO.get_by_name("Шина"));
		loot.add(itemDAO.get_by_name("Антидождик"));

		//for compare

		rareItemsWithNotification.add(itemDAO.get_by_name("Лапки"));
		rareItemsWithNotification.add(itemDAO.get_by_name("Вояджер-1"));
		rareItemsWithNotification.add(itemDAO.get_by_name("UFO"));
		rareItemsWithNotification.add(itemDAO.get_by_name("Nosebleed"));
		rareItemsWithNotification.add(itemDAO.get_by_name("C4"));
		rareItemsWithNotification.add(itemDAO.get_by_name("Шина"));
		rareItemsWithNotification.add(itemDAO.get_by_name("Антидождик"));

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
				boolean isLegendaryStatus = rareItemsWithNotification.stream().anyMatch(name -> name.getTitle().equals(prize.getTitle()));
				if(isLegendaryStatus){
					for(Player p: playerDAO.get_all()){
						if(player.getId() != p.getId()){
							if(player.isStatus()){
								host.sendMsg(p.getId(), String.format("❤️\u200D\uD83D\uDD25 Легендарка! " +
										"Игрок `%s` \\[%s] выбил `%s` из кейса", player.getUsername(), player.getStatus(), prize.getEmojiTitle()));
							}else{
								host.sendMsg(p.getId(), String.format("❤️\u200D\uD83D\uDD25 Воу-воу! " +
										"Игрок `%s` выбил `%s` из кейса", player.getUsername(), prize.getEmojiTitle()));
							}

						}
					}
				}

				inventoryDAO.delete(id, _case.getId(), 1);
				inventory.removeItem(case_idx);

				inventoryDAO.delete(id, _key.getId(), 1);
				inventory.removeItem(key_idx);


				inventoryDAO.putItem(id, prize.getId());
				inventory.putItem(prize);
				if(isLegendaryStatus){
					host.sendMsg(player.getId(), String.format("❤️\u200D\uD83D\uDD25 Легендарка! Вам выпал предмет: `%s`", prize.getEmojiTitle()));
				}else{
					host.sendMsg(player.getId(), String.format("\uD83C\uDF89 Ура! Вам выпал предмет: `%s`", prize.getEmojiTitle()));
				}

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
