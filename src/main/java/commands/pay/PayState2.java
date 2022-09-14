package commands.pay;

import commands.BaseState;
import commands.State;
import main.Bot;
import main.Money;
import main.Player;

public class PayState2 extends State
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
				invoker.st = invoker.st.base;
				host.sendMsg(acceptor.getId(), String.format("\uD83D\uDCB3 Вам начислено %s | Отправитель: `%s` ", new Money(cost), invoker.getUsername()));
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
