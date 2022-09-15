package commands;

import main.Bot;
import main.Money;
import main.Player;

public class Bonus extends Command
{
	@Override
	public void consume(Bot host, Player player)
	{
		long id = player.getId();
		if (player.getStats().bonus == 0)
		{
			host.sendMsg(id, "\uD83C\uDF3A Вы получили бонус | +" + new Money(15000L));
			try
			{
				player.balance.transfer(15000L);
			}
			catch (Money.MoneyException e)
			{
				e.printStackTrace();
				host.sendMsg(id, e.getMessage());
			}
			player.stats.bonus++;
		}
		else
		{
			host.sendMsg(id, "Вы уже получили свой бонус");
		}
	}
}
