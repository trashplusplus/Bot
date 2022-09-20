package commands;

import database.dao.InventoryDAO;
import database.dao.CachedItemDAO;
import main.*;

public class Status extends Command{

    InventoryDAO invDAO;
    CachedItemDAO itemDAO;
    Status(InventoryDAO invDAO, CachedItemDAO itemDAO){
        this.invDAO = invDAO;
        this.itemDAO = itemDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        long id = player.getId();

        StringBuilder sb = new StringBuilder();


        if(player.getInventory().getItems().stream().anyMatch(item -> item.getRarity().equals(ItemRarity.Status))){
            sb.append("\uD83C\uDF38 Ваши статусы: \n\n");

            for(int i = 0; i < player.getInventory().getInvSize(); i++){
                if(player.getInventory().getItem(i).getRarity() == ItemRarity.Status){
                    sb.append("Статус |#" + i + player.getInventory().getItem(i) + "\n");
                }
            }
            sb.append("\nВаш ник: " + player.getFormattedUsername() + "\n");
            sb.append("\nВведите ID, чтобы установить статус\n");
            player.state = new StatusID(host, player, itemDAO, invDAO, player.state.base);
        }else{
            sb.append("\uD83C\uDF38 У вас пока что нет статусов, но они обязательно когда-то появятся :'(");
        }


        host.sendMsg(id, sb.toString());

    }

}

class StatusID extends State{

    Bot host;
    Player player;
    CachedItemDAO itemDAO;
    InventoryDAO inventoryDAO;


    StatusID(Bot host, Player player, CachedItemDAO itemDAO, InventoryDAO inventoryDAO, BaseState base){
        this.host = host;
        this.player = player;
        this.itemDAO = itemDAO;
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public void process(String arg) {
        long id = player.getId();
        try{
            int itemID = Integer.parseInt(arg);
            Item statusItem = itemDAO.getByNameFromCollection(player.getInventory().getItem(itemID).getTitle());

            if(statusItem.getRarity().equals(ItemRarity.Status)){
                player.status = statusItem;
                host.sendMsg(id, String.format("Статус `%s` успешно установлен", statusItem.getEmojiTitle()));
                player.state = base;
            }

        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            host.sendMsg(id, "Указан неверный ID");
        }catch (NumberFormatException e) {
            e.printStackTrace();
            host.sendMsg(id, "Введите целое число");
        }
    }
}
