package commands;

import database.dao.IPlayerDAO;
import database.dao.IItemDAO;
import main.Bot;
import main.Player;

public class Top extends Command
{
	IPlayerDAO playerDAO;
	IItemDAO itemDAO;
	int top1;

	public Top(IPlayerDAO playerDAO, IItemDAO itemDAO)
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
					if(pl.status != null){
						players_list.append(String.format("Игрок \uD83D\uDC51`%s` \\[%s] | %s | %d Ур.", pl.getUsername(), pl.getStatus(), pl.balance, pl.getLevel()));
						players_list.append("\n");
						players_list.append("========================");
						players_list.append("\n");
					}else{
						players_list.append(String.format("Игрок \uD83D\uDC51`%s` | %s | %d Ур.", pl.getUsername(), pl.balance, pl.getLevel()));
						players_list.append("\n");
						players_list.append("========================");
						players_list.append("\n");
					}
				}else{
					if(pl.status != null){
						players_list.append(String.format("Игрок `%s` \\[%s] | %s | %d Ур.", pl.getUsername(), pl.getStatus(), pl.balance, pl.getLevel()));
						players_list.append("\n");
						players_list.append("========================");
						players_list.append("\n");
					}else{
						players_list.append(String.format("Игрок `%s` | %s | %d Ур.", pl.getUsername(), pl.balance, pl.getLevel()));
						players_list.append("\n");
						players_list.append("========================");
						players_list.append("\n");
					}

				}
			top1++;

		}
		players_list.append("\n");
		players_list.append("\uD83D\uDCBB Всего игроков: ").append(playerDAO.get_all().size());
		host.sendMsg(player.getId(), players_list.toString());
	}
}
