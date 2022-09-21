package commands;

import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import database.dao.CachedItemDAO;
import main.Bot;
import main.Item;
import main.Player;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Random;

public class Touch extends Command
{
	InventoryDAO inventoryDAO;
	CachedItemDAO itemDAO;
	IPlayerDAO playerDAO;

	public Touch(InventoryDAO inventoryDAO, CachedItemDAO itemDAO, IPlayerDAO playerDAO)
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
	CachedItemDAO itemDAO;
	IPlayerDAO playerDAO;
	Bot host;
	Player player;

	public TouchState(InventoryDAO inventoryDAO, CachedItemDAO itemDAO, IPlayerDAO playerDAO, Bot host, Player player, BaseState base)
	{
		this.inventoryDAO = inventoryDAO;
		this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
		this.host = host;
		this.player = player;
		this.base = base;
		hint = "Введите номер предмета, который хотите осмотреть";
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
			int item_id = Integer.parseInt(arg);
			Item energy = itemDAO.getByNameFromCollection("Энергетик");
			String responseText = touch.getInfo().get(player.getInventory().getItem(item_id));

			player.state = base;
			if (responseText != null && responseText != touch.getInfo().get(energy))
			{
				if (touch.getMagazines().containsKey(responseText))
					host.execute(touch.getMagazinePhoto(responseText));
					player.getInventory().removeItem(item_id);
				host.sendMsg(id, "\uD83E\uDEA1 " + responseText);
			}
			else
			{
				if (player.getInventory().getItem(item_id).getTitle().equals(energy.getTitle()))
				{
					if (player.findExpiration != null)
					{
						inventoryDAO.delete(id, energy.getId(), 1);
						player.getInventory().removeItem(item_id);
						host.sendMsg(id, "⚡ Вы чувствуете прилив сил, время ожидания снижено на 2 минуты");
						player.findExpiration -= 120L * 1000L;
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
