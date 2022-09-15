package commands;

import database.dao.IPlayerDAO;
import database.dao.ItemDAO;
import main.Bot;
import main.Item;
import main.Money;
import main.Player;

public class Tea extends Command
{
	ItemDAO itemDAO;
	IPlayerDAO playerDAO;

	public Tea(ItemDAO itemDAO, IPlayerDAO playerDAO)
	{
		this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		Item cup = itemDAO.getByNameFromCollection("☕ Чашка 'Египет'");
		long cost = player.getInventory().getItems().contains(cup) ? 200L : 500L;

		if (player.getMoney().value < cost)
		{
			host.sendMsg(player.getId(), "\uD83C\uDF3F Не хватает деняк на чай :'(");
		}
		else
		{
			player.state = new TeaState1(host, player, cost, itemDAO, playerDAO, player.state.base);
			host.sendMsg(player.getId(), player.state.hint);
		}
	}
}

class TeaState1 extends State
{
	Bot host;
	Player sender;
	long cost;
	ItemDAO itemDAO;
	IPlayerDAO playerDAO;

	public TeaState1(Bot host, Player sender, long cost, ItemDAO itemDAO, IPlayerDAO playerDAO, BaseState base)
	{
		this.host = host;
		this.sender = sender;
		this.cost = cost;
		this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
		this.base = base;
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
				sender.state = new TeaState2(host, sender, receiver, cost, itemDAO, this, base);
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
	ItemDAO itemDAO;

	public TeaState2(Bot host, Player sender, Player receiver, long cost, ItemDAO itemDAO, State previous, BaseState base)
	{
		this.host = host;
		this.sender = sender;
		this.receiver = receiver;
		this.cost = cost;
		this.itemDAO = itemDAO;
		this.previous = previous;
		this.base = base;
		hint = "Введите сообщение для игрока (48 символов):";
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

				receiver.stats.tea++;
				sender.state = base;
				host.sendMsg(sender_id, "\uD83C\uDF3F Чай отправлен");
				if (receiver.getStats().tea == 75)
				{
					receiver.ach_tea();
				}
				sender.addXp(1);
				host.sendMsg(receiver_id, String.format("\uD83C\uDF3F Игрок `%s` угостил вас кружечкой чая с сообщением: `%s`", sender.getUsername(), note));
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
