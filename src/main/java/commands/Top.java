package commands;

import database.dao.IPlayerDAO;
import database.dao.ItemDAO;
import database.dao.PlayerDAO;
import main.Bot;
import main.Player;

public class Top extends Command
{
	IPlayerDAO playerDAO;
	ItemDAO itemDAO;
	int top1;

	public Top(IPlayerDAO playerDAO, ItemDAO itemDAO)
	{
		this.playerDAO = playerDAO;
		this.itemDAO = itemDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		StringBuilder players_list = new StringBuilder("\uD83D\uDCBB Топ 10 самых богатых игроков:\n\n");
		players_list.append("========================");
		players_list.append("\n");

		for (Player pl : playerDAO.get_top("balance", false, 10))
		{
				if(top1 == 0){
					players_list.append(String.format("Игрок ⭐ `%s` | %s | %d Ур.", pl.getFormattedUsername(), pl.balance, pl.getLevel()));
					players_list.append("\n");
					players_list.append("========================");
					players_list.append("\n");

				}else{
					players_list.append(String.format("Игрок `%s` | %s | %d Ур.", pl.getFormattedUsername(), pl.balance, pl.getLevel()));
					players_list.append("\n");
					players_list.append("========================");
					players_list.append("\n");
				}
			top1++;

		}
		players_list.append("\n");
		players_list.append("\uD83D\uDCBB Всего игроков: ").append(playerDAO.get_all().size());
		host.sendMsg(player.getId(), players_list.toString());
	}
}
