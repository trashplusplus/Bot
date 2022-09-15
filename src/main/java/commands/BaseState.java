package commands;

import main.Bot;
import main.Player;

public class BaseState extends State
{
	Bot host;
	Player player;

	public BaseState(Bot host, Player player)
	{
		base = this;
		hint = "Выберите команду";

		this.host = host;
		this.player = player;
	}

	@Override
	public void process(String arg)  // todo
	{}
}
