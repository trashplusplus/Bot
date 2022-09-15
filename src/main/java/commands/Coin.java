package commands;

import main.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Coin extends Command
{
	@Override
	public void consume(Bot host, Player player)
	{
		long player_id = player.getId();
		if (player.getLevel() >= 4)
		{
			if (player.balance.value > 0)
			{
				host.sendMsg(player_id, "\uD83D\uDCB0 Ваш баланс: " + player.getMoney());
				player.state = new CoinState(player, host);
				host.sendMsg(player_id, player.state.hint);
			}
			else
			{
				host.sendMsg(player_id, "\uD83C\uDFB0 У вас недостаточно денег");
			}
		}
		else
		{
			host.sendMsg(player_id, "\uD83D\uDC7E Для игры в монетку нужен 4 уровень \n⚡ Повысить уровень можно за поиск предметов");
		}
	}
}

class CoinState extends State
{
	Player player;
	Bot host;

	public CoinState(Player player, Bot host)
	{
		this.player = player;
		this.host = host;
		hint = "\uD83C\uDFB0 Введите ставку: ";
	}

	@Override
	public void process(String dash)
	{
		long player_id = player.getId();
		try
		{
			int i_dash = Integer.parseInt(dash);
			Random r = new Random();
			int ran = r.nextInt(text.size());

			if (i_dash > 0 && i_dash <= player.balance.value)
			{

				host.sendMsg(player_id, "\uD83C\uDFB0 Ваша ставка: " + new Money(i_dash));

				host.sendMsg(player_id, text.get(ran));

				STPE.stpe.schedule(() -> coin_dash_callback(player, i_dash), 2L, TimeUnit.SECONDS);
			}
			else
			{
				host.sendMsg(player_id, "⚠\t У вас нет такой суммы");
			}
		}
		catch (NumberFormatException e)
		{
			host.sendMsg(player_id, "⚠\tВаша ставка должна быть целым числом");
			e.printStackTrace();
		}
	}

	void coin_dash_callback(Player player, long bid)
	{
		long player_id = player.getId();
		CoinGame coinGame = new CoinGame(bid);
		if (coinGame.roll())
		{
			host.sendMsg(player_id, "\uD83D\uDCB0 Вы выиграли " + new Money(bid));
			coinGame.coinWin(player, bid);
			player.addXp(1);
			player.stats.coinWins++;
		}
		else
		{
			host.sendMsg(player_id, "❌ Вы проиграли " + new Money(bid));
			coinGame.coinLose(player, bid);

			player.stats.coinLosses++;
		}

		player.state = player.state.base;
		host.sendMsg(player_id, "Ваш баланс: " + player.balance + " \uD83D\uDCB2");
	}

	static List<String> text = new ArrayList<>();
	static
	{
		text.add("Подбрасываем монетку...");
		text.add("Молим удачу...");
		text.add("Скрещиваем пальцы...");
		text.add("Не не надеемся не на проигрыш...");
		text.add("Закрываем глаза...");
		text.add("Держим кулачки...");
		text.add("Затаиваем дыхание...");
		text.add("Верим в Бога...");
		text.add("Надеемся на выигрыш...");
		text.add("Загадываем желание...");
		text.add("Думаем о победе...");
		text.add("Надеемся на решку...");
		text.add("Надеемся на орла...");
		text.add("И выпадает...");
	}
}
