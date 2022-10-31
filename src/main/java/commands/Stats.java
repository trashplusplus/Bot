package commands;

import main.Bot;
import main.Money;
import main.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Stats extends Command{

    @Override
    public void consume(Bot host, Player player) {

        //easter egg
        List<String> randomCapitalEmoji = new ArrayList<>(Arrays.asList(
                "\uD83C\uDDFA\uD83C\uDDE6", "\uD83C\uDDF3\uD83C\uDDF4",
                "\uD83C\uDDF5\uD83C\uDDF1", "\uD83C\uDDF1\uD83C\uDDF9",
                "\uD83C\uDDF1\uD83C\uDDFB", "\uD83C\uDDFA\uD83C\uDDF8",
                "\uD83C\uDDE9\uD83C\uDDF0", "\uD83C\uDDE6\uD83C\uDDF9",
                "\uD83C\uDDEB\uD83C\uDDEE"));
        String randomEmoji = randomCapitalEmoji.get(new Random().nextInt(randomCapitalEmoji.size()));

        //create right declension depend on value of variables 31.10.22
        StringBuilder sb = new StringBuilder("\uD83C\uDF38\uD83C\uDF38\uD83C\uDF38\n\n");
        sb.append("*Общая статистика*\n");
        sb.append("\uD83C\uDF3F Выпито кружек чая: *" + player.stats.tea + "* раз\n");
        sb.append(   "☕️ Выпито кружек кофе: *" + player.stats.coffee + "* раз\n");
        sb.append("\uD83C\uDF31 Посажено деревьев: *" + player.stats.trees + "* шт.\n");
        sb.append("\uD83D\uDD2E Прочитано журналов: *" + player.stats.magazines + "* шт.\n");
        sb.append(randomEmoji + " Отгадано столиц: *" + player.stats.capitals + "* раз\n\n");

        sb.append("*Предметы*\n");
        sb.append("\uD83D\uDC8E Редкие предметы: *" + player.stats.findCounter + "* шт.\n");
        sb.append("\uD83D\uDC5E Найдено в грязи: *" + player.stats.mudCounter + "*  шт.\n");
        sb.append("\uD83D\uDD26 Всего поисков в грязи: *" + player.stats.totalMud + "* раз\n");
        sb.append("\uD83E\uDD65 Изготовлено предметов: *" + player.stats.craftCounter + "* раз\n\n");

        sb.append("*Монетка*\n");
        sb.append("\uD83C\uDFC6 Победы в монетке: *" + player.stats.coinWins + "* раз\n");
        sb.append("\uD83D\uDCC9 Проигрыши в монетке: *" + player.stats.coinLosses + "* раз\n");
        sb.append("\uD83D\uDCB0 Общий выигрыш: *" + new Money(player.stats.totalWonMoney) + "*\n");
        sb.append("❌ Общий проигрыш: *" + new Money(player.stats.totalLostMoney) + "*\n");
        host.sendMsg(player.getId(), sb.toString());
    }
}
