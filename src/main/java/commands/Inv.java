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

		long player_id = player.getId();
		Inventory inventory = player.getInventory();
		if (inventory.getInvSize() != 0)
		{
			StringBuilder sb = new StringBuilder("\uD83C\uDF81\t Ваш инвентарь: ");
			sb.append("\n");
			sb.append("========================\n");
			for (int i = 0; i < inventory.getInvSize(); i++)
			{
				sb.append(String.format("Предмет |%d| : %s\n", i, inventory.getItem(i).toString()));
			}
			sb.append("========================\n");
			//sendMsg(message, "\u26BD");
			sb.append("\uD83C\uDF81\t Всего предметов: ").append(inventory.getInvSize()).append("/").append(limitSpace);
			host.sendMsg(player_id, sb.toString());
		}
		else
		{
			host.sendMsg(player_id, "\uD83C\uDF81\t Ваш инвентарь пуст ");
		}
	}
}
