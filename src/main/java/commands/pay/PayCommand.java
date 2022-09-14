package commands.pay;

import commands.Command;
import database.dao.IPlayerDAO;
import main.Bot;
import main.Player;

public class PayCommand extends Command
{
	IPlayerDAO playerDAO;

	public PayCommand(IPlayerDAO playerDAO)
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
			player.st = new PayState1(player, player.st.base, playerDAO, host);
			host.sendMsg(player.getId(), player.st.hint);
		}
	}
}
