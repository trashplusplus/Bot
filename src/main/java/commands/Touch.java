package commands;

import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import database.dao.IItemDAO;
import main.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Random;

public class Touch extends Command
{
	InventoryDAO inventoryDAO;
	IItemDAO itemDAO;
	IPlayerDAO playerDAO;

	public Touch(InventoryDAO inventoryDAO, IItemDAO itemDAO, IPlayerDAO playerDAO)
	{
		this.inventoryDAO = inventoryDAO;
		this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		player.state = new TouchState(inventoryDAO, itemDAO, playerDAO, host, player, player.state.base);
		host.sendMsg(player.getId(), player.state.hint);
	}
}

class TouchState extends State
{
	InventoryDAO inventoryDAO;
	IItemDAO itemDAO;
	IPlayerDAO playerDAO;
	Bot host;
	Player player;

	public TouchState(InventoryDAO inventoryDAO, IItemDAO itemDAO, IPlayerDAO playerDAO, Bot host, Player player, BaseState base)
	{
		this.inventoryDAO = inventoryDAO;
		this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
		this.host = host;
		this.player = player;
		this.base = base;
		hint = player.getInventory().repr() + "\nВведите номер предмета, который хотите осмотреть";
	}

	@Override
	public void process(String arg)
	{
		Random randomPlayer = new Random();
		List<Player> players = playerDAO.get_all();
		int randomIndex = randomPlayer.nextInt(players.size());
		String anotherPlayer = players.get(randomIndex).getUsername();

		main.Touch touch = new main.Touch(player, anotherPlayer);

		long id = player.getId();
		try
		{
			int item_id = Integer.parseInt(arg) - 1;
			Item energy = itemDAO.get_by_name("Энергетик");
			Item energy2 = itemDAO.get_by_name("Белый Monster Energy");
			Item mag = player.getInventory().getItem(item_id);
			String responseText = touch.getInfo().get(player.getInventory().getItem(item_id));

			player.state = base;
			if (responseText != null && !responseText.equals(touch.getInfo().get(energy)))
			{
				//картинка журналов
				if (touch.getMagazines().containsKey(responseText)){
					host.execute(touch.getMagazinePhoto(responseText));
					inventoryDAO.delete(id, mag.getId(), 1);
					player.getInventory().removeItem(item_id);
					host.sendMsg(id, "\uD83E\uDEA1 " + responseText);
					player.stats.magazines++;
					return;
					//нужен return, потому что когда мы удаляем журнал, idшники смещаются
					//и после responseText вылетает IndexOutOfBoundsException

				//fix this
				}else if(touch.getPets().containsKey(responseText)){
					host.execute(touch.getPetPhoto(responseText));
				}



				if(player.getInventory().getItem(item_id).getRarity() == ItemRarity.Pet){
					host.sendMsg(id, player.getInventory().getItem(item_id).getEmoji() + " " + responseText);
				}else{
					host.sendMsg(id, "\uD83E\uDEA1 " + responseText);
				}

			}
			else
			{
				if (player.getInventory().getItem(item_id).getTitle().equals(energy.getTitle()))

				{
					if (player.findExpiration != null)
					{
						player.getInventory().removeItem(item_id);
						inventoryDAO.delete(id, energy.getId(), 1);

						host.sendMsg(id, "⚡ Вы чувствуете прилив сил, время ожидания снижено на 5 минут");
						player.findExpiration -= 300L * 1000L;
					}
					else
					{
						host.sendMsg(id, "Вы и так полны энергии");
					}
				}
				else if( player.getInventory().getItem(item_id).getTitle().equals(energy2.getTitle())){
					if (player.findExpiration != null)
					{
						player.getInventory().removeItem(item_id);
						inventoryDAO.delete(id, energy.getId(), 1);

						host.sendMsg(id, "⚡ Вы чувствуете прилив сил, время ожидания снижено на 15 минут");
						player.findExpiration -= 60L * 15L * 1000L;
					}
					else
					{
						host.sendMsg(id, "Вы и так полны энергии");
					}
				}
				else
				{
					host.sendMsg(id, "\uD83E\uDEA1 " + "Обычный предмет, его не интересно трогать");
				}
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			host.sendMsg(id, "⚠\t Пожалуйста, введите целое число");
		}
		catch (IndexOutOfBoundsException ee)
		{
			ee.printStackTrace();
			host.sendMsg(id, "⚠\t Указан неверный ID");
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
	}
}
