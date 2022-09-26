package commands;

import main.Bot;
import main.Money;
import main.Player;

import java.util.Random;

public class Capitalgame extends Command
{
	main.Capitalgame capitalgame;

	public Capitalgame(main.Capitalgame capitalgame)
	{
		this.capitalgame = capitalgame;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		if (player.getLevel() < 7)
		{
			host.sendMsg(player.getId(), "⚡ Для мини-игры *Столицы* нужен 7 уровень");
		}
		else
		{
			Random ran = new Random();
			int random = ran.nextInt(capitalgame.getCountries().size());

			String country = capitalgame.getCountry(random);

			player.state = new CapitalgameState(host, player, country, player.state.base, capitalgame);
			host.sendMsg(player.getId(), player.state.hint);
		}
	}
}

class CapitalgameState extends State
{
	Bot host;
	Player player;
	String country;
	main.Capitalgame capitalgame;

	public CapitalgameState(Bot host, Player player, String country, BaseState base, main.Capitalgame capitalgame)
	{
		this.host = host;
		this.player = player;
		this.country = country;
		this.capitalgame = capitalgame;
		this.base = base;
		hint = "\uD83E\uDDE9 Столица страны: " + country;
	}

	@Override
	public void process(String arg)
	{
		long id = player.getId();

		player.state = base;
		if (!main.Capitalgame.capitalizeString(arg).equals(capitalgame.getCapital(country)))
		{
			host.sendMsg(id, "❌ Неправильно");
		}
		else
		{
			long money = new Random().nextInt(3000);
			host.sendMsg(id, "\uD83C\uDFC6 Правильно | +" + new Money(money));
			player.stats.capitals++;

			try
			{
				player.getMoney().transfer(money);
			}
			catch (Money.MoneyException e)
			{
				e.printStackTrace();
			}
		}
	}
}
