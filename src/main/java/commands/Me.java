package commands;

import commands.Command;
import main.Achievements;
import main.Bot;
import main.Player;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class Me extends Command
{
	@Override
	public void consume(Bot host, Player player)
	{
		try
		{
			long player_id = player.getId();
			SendPhoto photo = new SendPhoto();
			photo.setPhoto(new InputFile(new File(".\\pics\\me.jpg")));
			photo.setChatId(player_id);

			String s = "*Информация о персонаже*\n\n" + "Здесь показывается вся Ваша статистика и достижения\n\n" +
					"⭐ Ваш ник: " + player.getUsername() + "\n\n" +
					"\uD83D\uDCB0 Ваш баланс: " + player.getMoney() + "\n\n" +
					"\uD83C\uDF20 Ваш GameID: " + player_id + "\n\n" +
					String.format("\uD83D\uDC7E Ваш уровень: %d (%d XP) \n", player.getLevel(), player.getXp()) +
					"\uD83C\uDF3F Выпито кружек чая: " + player.stats.tea + "\n" +
					"☕️ Выпито кружек кофе: " + player.stats.coffee + "\n" +
					"\uD83C\uDFC6 Победы в монетке: " + player.stats.coinWins + "\n" +
					"\uD83D\uDCC9 Проигрыши в монетке: " + player.stats.coinLosses + "\n" +
					"\uD83C\uDF31 Посажено деревьев: " + player.stats.trees + "\n\n";

			Achievements a = new Achievements(player);

			host.execute(photo);
			host.sendMsg(player_id, s + a.getTeaAch() + a.getCoffeeAch() + a.getTreesAch());
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}
}
