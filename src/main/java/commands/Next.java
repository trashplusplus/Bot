package commands;

import main.Bot;
import main.Player;

public class Next extends Command
{
	@Override
	public void consume(Bot host, Player player)
	{
		if (player.page < host.paginator.size - 1)
		{
			player.page++;
		}
		host.sendMsg(player.getId(), String.format("Страница команд №%d", player.page + 1));
	}
}
