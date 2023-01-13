package commands;

import database.dao.ContainerDAO;
import database.dao.IItemDAO;
import main.*;

public class Cont extends Command{
    ContainerDAO containerDAO;
    IItemDAO itemDAO;
    public Cont(ContainerDAO containerDAO, IItemDAO itemDAO){
        this.containerDAO = containerDAO;
        this.itemDAO = itemDAO;
    }

    @Override
    public void consume(Bot host, Player player) throws Money.MoneyException {
        long player_id = player.getId();
        Container container = player.getContainer();

        boolean isKey = player.getInventory().getItems().contains(itemDAO.get_by_name("Ключ от контейнера"));

        String answer = "Недоступен";

        if(isKey)
           answer = "Есть доступ";

        if (container.getContainerSize() != 0) {
            StringBuilder sb = new StringBuilder(String.format("\uD83D\uDDDD Ваш контейнер \\[%s]: \n", answer)).append(container.repr());
            sb.append("\n\uD83D\uDDDD Всего предметов: ").append(container.getContainerSize()).append("/").append(15);
            host.sendMsg(player_id, sb.toString());
        }
        else
        {
            host.sendMsg(player_id, "\uD83D\uDDDD Ваш контейнер пуст ");
        }
    }
}
