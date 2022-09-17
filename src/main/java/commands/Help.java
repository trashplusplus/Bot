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
			"\uD83D\uDD0D /find - искать редкие предметы \n" +
			"\uD83D\uDD0D /pockets - проверить карманы \n" +
			"\uD83D\uDD0D /mud - рыться в грязи \n\n" +
			" \\[Команды магазина] \n" +
			"\uD83D\uDD0D /shopshow - посмотреть магазин \n" +
			"\uD83D\uDD0D /shopplace - продать предмет \n" +
			"\uD83D\uDD0D /shopbuy - купить предмет \n\n" +
			" \\[Команды игрока] \n" +
			"\uD83D\uDCC3 /inv - открыть инвентарь \n" +
			"\uD83D\uDCB0 /sell - продать скупщику\n" +
			"\uD83D\uDCB3 /balance - проверить баланс  \n" +
			"\uD83D\uDCB0 /pay - переслать деньги \n" +
			"\uD83D\uDC80 /changenickname - сменить никнейм \n" +
			"⭐ /me - ифнормация о персонаже \n\n" +
			" \\[Общие команы] \n" +
			"\uD83D\uDCE9 /help - список всех команд \n" +
			"ℹ /info - информация об игре \n" +
			"\uD83C\uDF80 /top - посмотреть рейтинг игроков \n" +
			"\uD83D\uDCB9 /stats - онлайн игроков \n\n" +
			" \\[Развлечения] \n" +
			"\uD83C\uDFB0 /coin - сыграть в Монетку \n" +
			"\uD83C\uDF3F /tea - отправить чай \n" +
			"☕️ /coffee - отправить кофе \n" +
			"\uD83D\uDD11 /case - открывать кейсы \n\n" +
			" \\[Локации] \n" +
			"\uD83C\uDF33 /forest - посетить Лес \n" +
			"\uD83D\uDC21 /fish - пойти на рыбалку \n";
}
