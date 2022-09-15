package commands;

import database.dao.InventoryDAO;
import main.*;

import java.util.Iterator;

public class Drop extends Command
{
	InventoryDAO inventoryDAO;

	public Drop(InventoryDAO inventoryDAO)
	{
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		long id = player.getId();
		long fee = 0L;

		Iterator<Item> iter = player.getInventory().getItems().iterator();
		while (iter.hasNext())
		{
			Item item = iter.next();
			if (item.getRarity() == ItemRarity.Cheap && !item.getTitle().equals("Саженец"))
			{
				fee += item.getCost().value;
				inventoryDAO.delete(id, item.getId(), 1);
				iter.remove();
			}
		}

		if (fee > 0L)
		{
			host.sendMsg(id, String.format("\uD83D\uDCB3 Вы продали все дешевые вещи за %s", new Money(fee)));
			try
			{
				player.balance.transfer(fee);
			}
			catch (Money.MoneyException e)
			{
				e.printStackTrace();
				host.sendMsg(id, e.getMessage());
			}
		}
		else
		{
			host.sendMsg(id, "У вас нет дешевых вещей");
		}
	}
}
