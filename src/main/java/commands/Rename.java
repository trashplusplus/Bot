package commands;

import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import database.dao.ItemDAO;
import main.Bot;
import main.Item;
import main.Player;

public class Rename extends Command
{
	IPlayerDAO playerDAO;
	ItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public Rename(IPlayerDAO playerDAO, ItemDAO itemDAO, InventoryDAO inventoryDAO)
	{
		this.playerDAO = playerDAO;
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		long id = player.getId();
		Item i = itemDAO.getByNameFromCollection("\uD83D\uDCDD Тег");
		if (player.getInventory().getItems().contains(i))
		{
			player.state = new RenameState(playerDAO, itemDAO, inventoryDAO, player, host);
			host.sendMsg(id, player.state.hint);
		}
		else
		{
			host.sendMsg(id, String.format("Для смены ника нужен предмет `%s`\n\uD83D\uDED2 Его можно купить у других игроков в магазине или найти", i.getTitle()));
		}
	}
}

class RenameState extends State
{
	IPlayerDAO playerDAO;
	ItemDAO itemDAO;
	InventoryDAO inventoryDAO;
	Player player;
	Bot host;

	public RenameState(IPlayerDAO playerDAO, ItemDAO itemDAO, InventoryDAO inventoryDAO, Player player, Bot host)
	{
		this.playerDAO = playerDAO;
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
		this.player = player;
		this.host = host;
		hint = "Введите желаемое имя:";
	}

	@Override
	public void process(String arg)
	{
		long player_id = player.getId();
		String nickname = arg;
		Item tag = itemDAO.getByNameFromCollection("\uD83D\uDCDD Тег");
		int tag_idx = player.getInventory().getItems().indexOf(tag);
		//regex для ника
		String usernameTemplate = "([А-Яа-яA-Za-z0-9]{3,32})";
		if (nickname.matches(usernameTemplate))
		{
			if (playerDAO.get_by_name(nickname) == null)
			{
				player.state = player.state.base;
				player.setUsername(nickname);
				inventoryDAO.delete(player.getId(), tag.getId(), 1);
				player.getInventory().removeItem(tag_idx);
				host.sendMsg(player_id, "Ваш никнейм успешно изменен на `" + player.getUsername() + "`");
			}
			else
			{
				host.sendMsg(player_id, "Игрок с таким ником уже есть");
			}
		}
		else
		{
			host.sendMsg(player_id, "Пожалуйста, введите корректный ник");
		}
	}
}