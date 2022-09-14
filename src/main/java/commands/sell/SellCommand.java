package commands.sell;

import commands.Command;
import database.dao.InventoryDAO;
import main.Bot;
import main.Player;

public class SellCommand extends Command
{
	InventoryDAO inventoryDAO;

	public SellCommand(InventoryDAO inventoryDAO)
	{
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		player.st = new SellState1(player, player.st.base, host, inventoryDAO);
		host.sendMsg(player.getId(), player.st.hint);
	}
}
