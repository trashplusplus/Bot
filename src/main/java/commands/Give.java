package commands;

import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import database.dao.ItemDAO;
import main.Bot;
import main.Inventory;
import main.Item;
import main.Player;

public class Give extends Command{

    ItemDAO itemDAO;
    InventoryDAO inventoryDAO;

    public Give(ItemDAO itemDAO, InventoryDAO inventoryDAO){
        this.itemDAO = itemDAO;
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        if (player.getId() == 501446180 || player.getId() == 684744711){
            host.sendMsg(player.getId(), "Введите название предмета, который вы хотите получить: ");
            player.state = new Give1(player, player.state.base, host, itemDAO, inventoryDAO);

        }else{
            host.sendMsg(player.getId(), "У вас недостаточно прав для этой комманды");
        }
    }

}

class Give1 extends State{

    Player invoker;
    IPlayerDAO playerDAO;
    Bot host;
    ItemDAO itemDAO;
    InventoryDAO inventoryDAO;

    public Give1(Player invoker, BaseState base, Bot host, ItemDAO itemDAO, InventoryDAO inventoryDAO){
        this.invoker = invoker;
        this.base = base;
        this.itemDAO = itemDAO;
        this.host = host;
        this.inventoryDAO = inventoryDAO;
        hint = "";
    }

    @Override
    public void process(String arg) {
        long player_id = invoker.getId();
        try{
            Item item = itemDAO.getByNameFromCollection(arg);
            host.sendMsg(player_id, String.format("Предмет `%s` добавлен в Ваш инвентарь", item.getEmojiTitle()));
            inventoryDAO.putItem(player_id, item.getId());
            invoker.getInventory().putItem(item);
        }catch (RuntimeException e){
            e.printStackTrace();
            host.sendMsg(player_id, "Такого предмета не существует");
        }
    }
}
