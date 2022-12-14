package commands;

import main.Bot;
import main.Money;
import main.Player;

public abstract class Command
{
	public abstract void consume(Bot host, Player player) throws Money.MoneyException;
}
