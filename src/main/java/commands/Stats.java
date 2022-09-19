package commands;

import main.Bot;
import main.Player;

public class Stats extends Command{

    @Override
    public void consume(Bot host, Player player) {
        StringBuilder sb = new StringBuilder("\uD83C\uDF38 Ваша статистика: \n\n");
        sb.append("\uD83C\uDF3F Выпито кружек чая: " + player.stats.tea + "\n");
        sb.append(   "☕️ Выпито кружек кофе: " + player.stats.coffee + "\n");
        sb.append("\uD83C\uDFC6 Победы в монетке: " + player.stats.coinWins + "\n");
        sb.append("\uD83D\uDCC9 Проигрыши в монетке: " + player.stats.coinLosses + "\n");
        sb.append("\uD83C\uDF31 Посажено деревьев: " + player.stats.trees + "\n");
        sb.append("\uD83C\uDDFA\uD83C\uDDE6 Отгадано столиц: " + player.stats.capitals + "\n");
        host.sendMsg(player.getId(), sb.toString());
    }
}
