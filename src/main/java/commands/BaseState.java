package commands;

import main.Bot;
import main.BotCommandProcessor;
import main.Player;

import java.util.function.Consumer;

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
	public void process(String arg)
	{
		FI<Bot, Player, String> fi = null;  // get(arg);
		fi.foo(host,player,arg);
	}

	interface FI<A, B, C>
	{
		void foo(A a, B b, C c);
	}
}
