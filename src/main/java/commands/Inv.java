package commands;

import database.dao.CachedItemDAO;
import main.Bot;
import main.Inventory;
import main.Item;
import main.Player;

public class Inv extends Command
{
	CachedItemDAO itemDAO;

	public Inv(CachedItemDAO itemDAO)
	{
		this.itemDAO = itemDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		long player_id = player.getId();
		Inventory inventory = player.getInventory();
		int limitSpace = inventory.inventory_capacity;
		if (inventory.getInvSize() != 0)
		{
			StringBuilder sb = new StringBuilder("\uD83C\uDF81\t Ваш инвентарь: \n").append(inventory.repr());
			sb.append("\n\uD83C\uDF81\t Всего предметов: ").append(inventory.getInvSize()).append("/").append(limitSpace);
			host.sendMsg(player_id, sb.toString());
		}
		else
		{
			host.sendMsg(player_id, "\uD83C\uDF81\t Ваш инвентарь пуст ");
		}
	}
}
