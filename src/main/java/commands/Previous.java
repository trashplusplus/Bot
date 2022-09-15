package commands;

import main.Bot;
import main.Player;

public class Previous extends Command
{
	@Override
	public void consume(Bot host, Player player)
	{
		if (player.page > 0)
		{
			player.page--;
		}
		host.sendMsg(player.getId(), String.format("Страница команд №%d", player.page + 1));
	}
}
