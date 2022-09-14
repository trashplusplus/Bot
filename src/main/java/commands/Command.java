package commands;

import main.Bot;
import main.Player;

public abstract class Command
{
	public abstract void consume(Bot host, Player player);
}
