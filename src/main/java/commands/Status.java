package commands;

import database.dao.InventoryDAO;
import database.dao.ItemDAO;
import main.Bot;
import main.Player;

public class Status extends Command{

    InventoryDAO invDAO;
    ItemDAO itemDAO;
    Status(InventoryDAO invDAO, ItemDAO itemDAO){
        this.invDAO = invDAO;
        this.itemDAO = itemDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        long id = player.getId();
        host.sendMsg(id, "");
    }
}
