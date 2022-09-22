package commands;

import database.dao.IPlayerDAO;
import database.dao.IItemDAO;
import main.Bot;
import main.Item;
import main.Money;
import main.Player;

public class Coffee extends Command
{
	IItemDAO itemDAO;
	IPlayerDAO playerDAO;

	public Coffee(IItemDAO itemDAO, IPlayerDAO playerDAO)
	{
		this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		Item cup = itemDAO.get_by_name("Чашка 'Египет'");
		long cost = player.getInventory().getItems().contains(cup) ? 200L : 500L;

		if (player.getMoney().value < cost)
		{
			host.sendMsg(player.getId(), "☕ Не хватает деняк на кофе :'(");
		}
		else
		{
			player.state = new CoffeeState1(host, player, cost, itemDAO, playerDAO, player.state.base);
			host.sendMsg(player.getId(), player.state.hint);
		}
	}
}

class CoffeeState1 extends State
{
	Bot host;
	Player sender;
	long cost;
	IItemDAO itemDAO;
	IPlayerDAO playerDAO;

	public CoffeeState1(Bot host, Player sender, long cost, IItemDAO itemDAO, IPlayerDAO playerDAO, BaseState base)
	{
		this.host = host;
		this.sender = sender;
		this.cost = cost;
		this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
		this.base = base;
		hint = String.format("☕(%s) Введите ник игрока: ", new Money(cost));
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
				sender.state = new CoffeeState2(host, sender, receiver, cost, itemDAO, this, base);
				host.sendMsg(player_id, sender.state.hint);
			}
			else
			{
				host.sendMsg(player_id, "Такого игрока не существует");
			}
		}
		else
		{
			host.sendMsg(player_id, "\uD83C\uDF38 Кофе можно отправлять только другим игрокам");
		}
	}
}

class CoffeeState2 extends State
{
	Bot host;
	Player sender;
	Player receiver;
	long cost;
	IItemDAO itemDAO;

	public CoffeeState2(Bot host, Player sender, Player receiver, long cost, IItemDAO itemDAO, State previous, BaseState base)
	{
		this.host = host;
		this.sender = sender;
		this.receiver = receiver;
		this.cost = cost;
		this.itemDAO = itemDAO;
		this.previous = previous;
		this.base = base;
		hint = "Введите сообщение для игрока (48 символов): ";
	}

	@Override
	public void process(String note)
	{
		long sender_id = sender.getId();
		long receiver_id = receiver.getId();
		try
		{
			if (note.length() <= 48)
			{
				sender.balance.transfer(-cost);

				receiver.stats.coffee++;
				sender.state = base;
				host.sendMsg(sender_id, "☕ Кофе отправлен");
				if (receiver.getStats().coffee == 75)
				{
					receiver.ach_coffee();
				}
				sender.addXp(1);

				if(sender.status != null){
					host.sendMsg(receiver_id, String.format("☕ Игрок `%s` \\[%s] угостил Вас кружечкой ароматного кофе, прошептав на ушко: `%s`", sender.getUsername(), sender.getStatus(), note));
				}else{
					host.sendMsg(receiver_id, String.format("☕ Игрок `%s` угостил Вас кружечкой кофе с сообщением: `%s`", sender.getUsername(), note));
				}

			}
			else
			{
				host.sendMsg(sender_id, "Сообщение больше, чем 48 символов");
			}
		}
		catch (Money.MoneyException ex)
		{
			ex.printStackTrace();
			host.sendMsg(sender_id, ex.getMessage());
		}
	}
}
