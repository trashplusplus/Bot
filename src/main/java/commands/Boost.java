package commands;

import main.Bot;
import main.Player;

public class Boost extends Command
{
	@Override
	public void consume(Bot host, Player player)
	{
		if (player.needle >= 2){
			host.sendMsg(player.getId(), "\uD83D\uDC8E Ура! Вы сбросили время ожидания");
			player.needle -= 2;
			player.findExpiration = 0L;
		}
		else{
			host.sendMsg(player.getId(), "\uD83D\uDC8E Недостаточно булавок. Чтобы купить их введите команду /donate");
		}
	}
}
