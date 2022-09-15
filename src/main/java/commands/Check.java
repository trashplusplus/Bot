package commands;

import database.dao.IPlayerDAO;
import main.Bot;
import main.Inventory;
import main.Player;

public class Check extends Command
{
	IPlayerDAO playerDAO;

	public Check(IPlayerDAO playerDAO)
	{
		this.playerDAO = playerDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		player.st = new CheckState(host, player, playerDAO, player.st.base);
	}
}

class CheckState extends State
{
	Bot host;
	Player player;
	IPlayerDAO playerDAO;

	public CheckState(Bot host, Player player, IPlayerDAO playerDAO, BaseState base)
	{
		this.host = host;
		this.player = player;
		this.playerDAO = playerDAO;
		this.base = base;
		hint = "\uD83D\uDC41 Введите ник игрока, чей инвентарь Вы хотите просмотреть: ";
	}

	@Override
	public void process(String name)
	{
		long player_id = player.getId();

		Player anotherPlayer = playerDAO.get_by_name(name);

		if (anotherPlayer != null)
		{
			Inventory inventory = anotherPlayer.getInventory();
			player.st = base;
			if (inventory.getInvSize() != 0)
			{
				StringBuilder sb = new StringBuilder("\uD83D\uDC41 Инвентарь игрока `" + anotherPlayer.getUsername() + "`");
				sb.append("\n");
				sb.append("========================\n");
				for (int i = 0; i < inventory.getInvSize(); i++)
				{
					sb.append(String.format("Предмет |%d| : %s\n", i, inventory.getItem(i).toString()));
				}
				sb.append("========================\n");
				//sendMsg(message, "\u26BD");
				sb.append("\uD83D\uDC41 Всего предметов: ").append(inventory.getInvSize());
				host.sendMsg(player_id, sb.toString());
			}
			else
			{
				host.sendMsg(player_id, "\uD83C\uDF81\t Инвентарь `" + name + "` пуст\n");
			}
		}
		else
		{
			host.sendMsg(player_id, "Такого игрока не существует");
		}
	}
}
