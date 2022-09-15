package commands;

import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import main.Bot;
import main.Player;

public class Touch extends Command
{
	InventoryDAO inventoryDAO;
	IPlayerDAO playerDAO;

	public Touch(InventoryDAO inventoryDAO, IPlayerDAO playerDAO)
	{
		this.inventoryDAO = inventoryDAO;
		this.playerDAO = playerDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		player.state = new TouchState(inventoryDAO, playerDAO, host);
		host.sendMsg(player.getId(), player.state.hint);
	}
}

class TouchState extends State  // todo
{
	InventoryDAO inventoryDAO;
	IPlayerDAO playerDAO;
	Bot host;

	public TouchState(InventoryDAO inventoryDAO, IPlayerDAO playerDAO, Bot host)
	{
		this.inventoryDAO = inventoryDAO;
		this.playerDAO = playerDAO;
		this.host = host;

		hint = "Введите номер предмета, который хотите осмотреть";
	}

	@Override
	public void process(String arg)
	{

	}
}
