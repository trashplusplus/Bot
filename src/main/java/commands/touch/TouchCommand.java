package commands.touch;

import commands.Command;
import commands.State;
import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import main.Bot;
import main.Player;

public class TouchCommand extends Command
{
	InventoryDAO inventoryDAO;
	IPlayerDAO playerDAO;

	public TouchCommand(InventoryDAO inventoryDAO, IPlayerDAO playerDAO)
	{
		this.inventoryDAO = inventoryDAO;
		this.playerDAO = playerDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		player.st = new TouchState(inventoryDAO, playerDAO);
	}
}

class TouchState extends State
{
	InventoryDAO inventoryDAO;
	IPlayerDAO playerDAO;

	public TouchState(InventoryDAO inventoryDAO, IPlayerDAO playerDAO)
	{
		this.inventoryDAO = inventoryDAO;
		this.playerDAO = playerDAO;
	}

	@Override
	public void process(String arg)
	{

	}
}
