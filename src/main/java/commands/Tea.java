package commands;

import database.dao.IPlayerDAO;
import database.dao.IItemDAO;
import database.dao.InventoryDAO;
import main.Bot;
import main.Item;
import main.Money;
import main.Player;

public class Tea extends Command
{
	IItemDAO itemDAO;
	IPlayerDAO playerDAO;
	InventoryDAO inventoryDAO;

	public Tea(IItemDAO itemDAO, IPlayerDAO playerDAO, InventoryDAO inventoryDAO)
	{
		this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		Item cup = itemDAO.get_by_name("Чашка 'Египет'");
		long cost = player.getInventory().getItems().contains(cup) ? 700L : 1200L;

		if (player.getMoney().value < cost)
		{
			host.sendMsg(player.getId(), "\uD83C\uDF3F Не хватает деняк на чай :'(");
		}
		else
		{
			player.state = new TeaState1(host, player, cost, itemDAO, playerDAO, player.state.base, inventoryDAO);
			host.sendMsg(player.getId(), player.state.hint);
		}
	}
}

class TeaState1 extends State
{
	Bot host;
	Player sender;
	long cost;
	IItemDAO itemDAO;
	IPlayerDAO playerDAO;
	InventoryDAO inventoryDAO;

	public TeaState1(Bot host, Player sender, long cost, IItemDAO itemDAO, IPlayerDAO playerDAO, BaseState base, InventoryDAO inventoryDAO)
	{
		this.host = host;
		this.sender = sender;
		this.cost = cost;
		this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
		this.base = base;
		this.inventoryDAO = inventoryDAO;
		hint = String.format("\uD83C\uDF3F(%s) Введите ник игрока: ", new Money(cost));
	}

	@Override
	public void process(String name)
	{
		long player_id = sender.getId();

		if (!name.equals(sender.getUsername()))
		{
			Player receiver = playerDAO.get_by_name(name);
			if (receiver != null)
			{
				sender.state = new TeaState2(host, sender, receiver, cost, itemDAO, this, base, inventoryDAO);
				host.sendMsg(player_id, sender.state.hint);
			}
			else
			{
				host.sendMsg(player_id, "Такого игрока не существует");
			}
		}
		else
		{
			host.sendMsg(player_id, "\uD83C\uDF38 Чай можно отправлять только другим игрокам");
		}
	}
}

class TeaState2 extends State
{
	Bot host;
	Player sender;
	Player receiver;
	long cost;
	IItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public TeaState2(Bot host, Player sender, Player receiver, long cost, IItemDAO itemDAO, State previous, BaseState base, InventoryDAO inventoryDAO)
	{
		this.host = host;
		this.sender = sender;
		this.receiver = receiver;
		this.cost = cost;
		this.itemDAO = itemDAO;
		this.previous = previous;
		this.base = base;
		this.inventoryDAO = inventoryDAO;
		hint = "Введите сообщение для игрока (256 символов):";
	}

	@Override
	public void process(String note)
	{
		long sender_id = sender.getId();
		long receiver_id = receiver.getId();
		try
		{
			if (note.length() <= 256)
			{
				sender.balance.transfer(-cost);

				receiver.stats.tea++;
				sender.state = base;
				host.sendMsg(sender_id, "\uD83C\uDF3F Чай отправлен");
				if (receiver.getStats().tea == 75)
				{
					receiver.ach_tea();
					Item johnCoffi = itemDAO.get_by_name("Гринфилд");
					receiver.getInventory().putItem(johnCoffi);
					inventoryDAO.putItem(receiver.getId(), johnCoffi.getId());
				}
				sender.addXp(1);
				if(sender.status != null){
					host.sendMsg(receiver_id, String.format("\uD83C\uDF3F Игрок `%s` \\[%s] лично принес Вам кружечку зеленого чая со стиком сахара и сказал: `%s`", sender.getUsername(), sender.getStatus(), note));
				}else{
					host.sendMsg(receiver_id, String.format("\uD83C\uDF3F Игрок `%s` угостил Вас кружечкой чая с сообщением: `%s`", sender.getUsername(), note));
				}

			}
			else
			{
				host.sendMsg(sender_id, "Сообщение больше, чем 256 символов");
			}
		}
		catch (Money.MoneyException ex)
		{
			ex.printStackTrace();
			host.sendMsg(sender_id, ex.getMessage());
		}
	}
}
