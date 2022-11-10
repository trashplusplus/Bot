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
        sb.append("\uD83C\uDF3F Выпито кружек чая: *" + declension(player.stats.tea) + "*\n");
        sb.append(   "☕️ Выпито кружек кофе: *" + declension(player.stats.coffee) + "*\n");
        sb.append("\uD83C\uDF31 Посажено деревьев: *" + declension(player.stats.trees) + "*\n");
        sb.append("\uD83D\uDD2E Прочитано журналов: *" + declension(player.stats.magazines) + "*\n");
        sb.append(randomEmoji + " Отгадано столиц: *" + declension(player.stats.capitals) + "*\n\n");

        sb.append("*Предметы*\n");
        sb.append("\uD83D\uDC8E Найдено редких предметов: *" + declension(player.stats.findCounter) + "*\n");
        sb.append("\uD83D\uDC5E Найдено в грязи: *" + declension(player.stats.mudCounter) + "*\n");
        sb.append("\uD83D\uDD26 Всего поисков в грязи: *" + declension(player.stats.totalMud) + "*\n");
        sb.append("\uD83E\uDD65 Изготовлено предметов: *" + declension(player.stats.craftCounter) + "*\n\n");

        sb.append("*Монетка*\n");
        sb.append("\uD83C\uDFC6 Победы в монетке: *" + declension(player.stats.coinWins) + "*\n");
        sb.append("\uD83D\uDCC9 Проигрыши в монетке: *" + declension(player.stats.coinLosses) + "*\n");
        sb.append("\uD83D\uDCB0 Общий выигрыш: *" + new Money(player.stats.totalWonMoney) + "*\n");
        sb.append("❌ Общий проигрыш: *" + new Money(player.stats.totalLostMoney) + "*\n\n");

        sb.append("*Дуэли*\n");
        sb.append("\uD83C\uDFF9 Побед в дуэлях: *" + declension(player.stats.duelWin) + "*\n");
        sb.append("\uD83D\uDC80 Проигрышей в дуэлях: *" + declension(player.stats.duelLose) + "*\n\n");


        host.sendMsg(player.getId(), sb.toString());
    }

    private static String declension(int num){
        boolean bool1 = num % 10 == 1 && num % 100 != 11 || num == 0;
        boolean bool2 = num % 10 >= 2 && num % 10 <= 4 && (num % 100 < 10 || num % 100 >= 20);
        return bool1 || !bool2 ? num + " раз" : num + " раза";
    }


}
