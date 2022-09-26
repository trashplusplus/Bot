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
		player.state = new CheckState(host, player, playerDAO, player.state.base);
		host.sendMsg(player.getId(), player.state.hint);
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
			player.state = base;
			if (inventory.getInvSize() != 0)
			{
				if(anotherPlayer.stats.hideInv <= 0){

					StringBuilder sb = new StringBuilder();

					String sb_text = anotherPlayer.isStatus() ? "\uD83D\uDC41 Инвентарь игрока `" + anotherPlayer.getUsername()
							+ "` \\[" + anotherPlayer.getStatus() + "]"
							:"\uD83D\uDC41 Инвентарь игрока `" + anotherPlayer.getUsername() + "`";

					sb.append(sb_text);
					sb.append("\n").append(inventory.repr());
					sb.append("\n\uD83D\uDC41 Всего предметов: ").append(inventory.getInvSize());

					host.sendMsg(player_id, sb.toString());
				}else{
					host.sendMsg(player_id, "\uD83D\uDD12 Игрок скрыл свой инвентарь");
				}
			}
			else
			{
				host.sendMsg(player_id, "\uD83C\uDF81\t Инвентарь `" + anotherPlayer.getFormattedUsername() + "` пуст\n");
			}
		}
		else
		{
			host.sendMsg(player_id, "Такого игрока не существует");
		}
	}
}
