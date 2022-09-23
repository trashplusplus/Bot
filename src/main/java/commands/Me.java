package commands;

import commands.Command;
import main.*;
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
			for(Item i : player.getInventory().getItems()){
				if(i.getRarity().equals(ItemRarity.Pet)){
					photo.setPhoto(new InputFile(new File(".\\pics\\petMe.jpg")));
				}else{
					photo.setPhoto(new InputFile(new File(".\\pics\\me.jpg")));
				}
			}

			photo.setChatId(player_id);

			String s = "*Информация о персонаже*\n\n" + "Здесь показывается вся Ваша статистика и достижения\n\n" +
					"⭐ Ваш ник: " + player.getFormattedUsername() + "\n\n" +
					"\uD83D\uDCB0 Ваш баланс: " + player.getMoney() + "\n" +
					"\uD83D\uDC8E Ваши булавки: " + player.needle + "\uD83E\uDDF7 \n\n" +
					"\uD83C\uDF20 Ваш GameID: " + player_id + "\n" +
					String.format("\uD83D\uDC7E Ваш уровень: %d (%d XP) \n\n", player.getLevel(), player.getXp());


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
