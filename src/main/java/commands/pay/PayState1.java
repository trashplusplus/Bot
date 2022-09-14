package commands.pay;

import commands.BaseState;
import commands.State;
import database.dao.IPlayerDAO;
import main.Bot;
import main.Player;

public class PayState1 extends State
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
				invoker.st = new PayState2(invoker, host, acceptor, this, base);
				host.sendMsg(player_id, invoker.st.hint);
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
