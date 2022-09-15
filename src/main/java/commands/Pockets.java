package commands;

import main.*;

import java.util.concurrent.TimeUnit;

public class Pockets extends Command
{
	Roller<Integer> pockets_roller;

	public Pockets(Roller<Integer> pockets_roller)
	{
		this.pockets_roller = pockets_roller;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		long player_id = player.getId();
		long now_ts = System.currentTimeMillis();
		long cooldownMs = host.pocketsCooldown;
		if (player.pocketsExpiration != null && player.pocketsExpiration > now_ts)
		{
			host.sendMsg(player_id, String.format("\u231B Вы проверяете карманы, время ожидания:%s",
					PrettyDate.prettify(player.pocketsExpiration - now_ts, TimeUnit.MILLISECONDS)));
		}
		else
		{
			long money = pockets_roller.roll();
			if (money > 0L)
			{
				host.sendMsg(player_id, String.format("Вы пошарили в карманах и нашли %s", new Money(money)));
				try
				{
					player.balance.transfer(money);
				}
				catch (Money.MoneyException e)
				{
					e.printStackTrace();
					host.sendMsg(player_id, e.getMessage());
				}
			}
			else if (money == 0L)
			{
				host.sendMsg(player.getId(), "Вы ничего не нашли в своих карманах");
			}
			else
			{
				throw new Error("WTF?");
			}
			player.pocketsExpiration = now_ts + cooldownMs;
		}
	}
}
