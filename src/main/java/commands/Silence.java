package commands;

import main.Bot;
import main.Money;
import main.Player;

public class Silence extends Command{
    @Override
    public void consume(Bot host, Player player) throws Money.MoneyException {
        host.sendMsg(player.getId(), "❔ Ветер колеблет листву деревьев и молча шепчет *Карусель*");
    }
}
