package commands;

import main.Bot;
import main.Money;
import main.Player;

public class Carousel extends Command{
    @Override
    public void consume(Bot host, Player player) throws Money.MoneyException {
        host.sendMsg(player.getId(), "⭕ Как фонари снова гаснут слова, словно последние строчки *Fireal*");
    }
}
