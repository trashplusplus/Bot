package commands;

import database.dao.IPlayerDAO;
import main.Bot;
import main.Money;
import main.Player;

public class Pay extends Command
{
	IPlayerDAO playerDAO;

	public Pay(IPlayerDAO playerDAO)
	{
		this.playerDAO = playerDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		if (player.balance.value <= 0)
		{
			host.sendMsg(player.getId(), "У вас нет денег для перевода");
		}
		else
		{
			player.state = new PayState1(player, player.state.base, playerDAO, host);
			host.sendMsg(player.getId(), player.state.hint);
		}
	}
}

class PayState1 extends State
{
	Player invoker;
	IPlayerDAO playerDAO;
	Bot host;

	public PayState1(Player invoker, BaseState base, IPlayerDAO playerDAO, Bot host)
	{
		this.invoker = invoker;
		this.base = base;
		this.playerDAO = playerDAO;
		this.host = host;
		hint = "Введите ник получателя:";
	}

	@Override
	public void process(String arg)
	{
		long player_id = invoker.getId();

		if (!arg.equals(invoker.getUsername()))
		{
			Player acceptor = playerDAO.get_by_name(arg);
			if (acceptor != null)
			{
				invoker.state = new PayState2(invoker, host, acceptor, this, base);
				host.sendMsg(player_id, invoker.state.hint);
			}
			else
			{
				host.sendMsg(player_id, "Такого игрока не существует");
			}
		}
		else
		{
			host.sendMsg(player_id, String.format("\uD83C\uDF38 Игрок `%s` очень богат и не нуждается в Ваших копейках", invoker.getUsername()));
		}
	}
}

class PayState2 extends State
{
	Player invoker;
	Bot host;
	Player acceptor;

	public PayState2(Player invoker, Bot host, Player acceptor, State previous, BaseState base)
	{
		this.invoker = invoker;
		this.host = host;
		this.acceptor = acceptor;
		this.previous = previous;
		this.base = base;
		hint = "\uD83D\uDCB3 Введите сумму: ";
	}

	@Override
	public void process(String arg)
	{
		try
		{
			long cost = Long.parseLong(arg);
			if (cost > invoker.balance.value || cost <= 0)
			{
				host.sendMsg(invoker.getId(), "⚠\t Некорректная сумма");
			}
			else
			{
				invoker.balance.transfer(-cost);
				acceptor.balance.transfer(cost);
				invoker.state = invoker.state.base;
				if(invoker.isStatus()){
					host.sendMsg(acceptor.getId(), String.format("\uD83D\uDCB3 Вам начислено %s | Отправитель: `%s` \\[%s] ", new Money(cost), invoker.getUsername(), invoker.getStatus()));
				}else{
					host.sendMsg(acceptor.getId(), String.format("\uD83D\uDCB3 Вам начислено %s | Отправитель: `%s` ", new Money(cost), invoker.getUsername()));
				}

				host.sendMsg(invoker.getId(), "✅ Деньги отправлены");
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			host.sendMsg(invoker.getId(), "⚠\t Вы ввели некорректную сумму");
		}
		catch (Money.MoneyException ex)
		{
			ex.printStackTrace();
			host.sendMsg(invoker.getId(), ex.getMessage());
		}
	}
}
