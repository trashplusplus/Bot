package commands;

import main.Bot;
import main.Player;

public class Help extends Command
{
	@Override
	public void consume(Bot host, Player player)
	{
		host.sendMsg(player.getId(), HELP_MESSAGE);
	}

	static String HELP_MESSAGE = "\\[`Needle`] Бот содержит следующие команды: \n\n" +
			" \\[Команды поиска] \n" +
			"\uD83D\uDC8E /find - искать редкие предметы \n" +
			"\uD83E\uDDF6 /pockets - проверить карманы \n" +
			"\uD83D\uDD26 /mud - рыться в грязи \n\n" +
			" \\[Команды маркета] \n" +
			"\uD83C\uDFEA /shopshow - посмотреть магазин \n" +
			"\uD83E\uDDF7 /shopplace - продать предмет \n" +
			"\uD83C\uDF81 /shopbuy - купить предмет \n" +
			"\uD83D\uDCB0 /shop - Магазин 24/7 \n\n" +
			" \\[Команды игрока] \n" +
			"\uD83D\uDCC3 /inv - открыть инвентарь \n" +
			"\uD83D\uDCB0 /sell - продать скупщику\n" +
			"\uD83D\uDCB0 /pay - переслать деньги \n" +
			"\uD83D\uDC80 /rename - сменить никнейм \n" +
			"⭐ /me - ифнормация о персонаже \n\n" +
			" \\[Общие команы] \n" +
			"\uD83D\uDCE9 /help - список всех команд \n" +
			"ℹ /info - информация об игре \n" +
			"\uD83C\uDF80 /top - посмотреть топ 10 игроков \n" +
			"\uD83D\uDCB9 /stats - посмотреть статистику \n\n" +
			" \\[Развлечения] \n" +
			"\uD83C\uDFB0 /coin - сыграть в Монетку \n" +
			"\uD83C\uDF3F /tea - отправить чай \n" +
			"☕️ /coffee - отправить кофе \n" +
			"\uD83C\uDDFA\uD83C\uDDE6 /capitalgame - сыграть в Столицы \n" +
			"\uD83D\uDD11 /case - открывать кейсы \n\n" +
			" \\[Локации] \n" +
			"\uD83C\uDF33 /forest - посетить Лес \n" +
			"\uD83D\uDC21 /fish - пойти на рыбалку \n\n" +
			" \\[Дуэли] \n" +
			"\uD83C\uDFF9 /duel - бросить дуэль \n" +
			"\uD83C\uDFF9 /accept - принять дуэль \n" +
			"\uD83C\uDFF9 /decline - отменить дуэль \n\n" +
			" \\[Косметика и крафтинг] \n" +
			"\uD83D\uDC36 /mypet - посмотреть на питомца \n" +
			"✨ /status - установить/снять статус \n" +
			"\uD83D\uDC8E /donate - купить Булавки \n" +
			"\uD83E\uDD65 /recipes - скрафтить предмет \n";

}
