package commands;

import main.Bot;
import main.Money;
import main.Player;

public class WhileIWatchYou extends Command{
    @Override
    public void consume(Bot host, Player player) throws Money.MoneyException {
        host.sendMsg(player.getId(), "⚜ Чтобы получить ответ, напиши письмо со словом *ДеньБабочек123*");
    }
}
