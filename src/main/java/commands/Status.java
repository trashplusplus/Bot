package commands;

import database.dao.InventoryDAO;
import database.dao.IItemDAO;
import main.*;

public class Status extends Command{

    InventoryDAO invDAO;
    IItemDAO itemDAO;
    Status(InventoryDAO invDAO, IItemDAO itemDAO){
        this.invDAO = invDAO;
        this.itemDAO = itemDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        long id = player.getId();

        StringBuilder sb = new StringBuilder();


        if(player.getInventory().getItems().stream().anyMatch(item -> item.getRarity().equals(ItemRarity.Status) ||
                item.getRarity().equals(ItemRarity.Pet))){
            sb.append("\uD83C\uDF38 Ваши статусы: \n\n");

            for(int i = 0; i < player.getInventory().getInvSize(); i++){
                if(player.getInventory().getItem(i).getRarity() == ItemRarity.Status || player.getInventory().getItem(i).getRarity() ==ItemRarity.Pet){
                    sb.append("Статус |#" + i + player.getInventory().getItem(i) + "\n");
                }
            }
            sb.append("\nВаш ник: " + player.getFormattedUsername() + "\n");
            sb.append("\nВведите номер статуса, чтобы его установить\n");
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
    IItemDAO itemDAO;
    InventoryDAO inventoryDAO;


    StatusID(Bot host, Player player, IItemDAO itemDAO, InventoryDAO inventoryDAO, BaseState base){
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
            Item statusItem = itemDAO.get_by_name(player.getInventory().getItem(itemID).getTitle());

            if(statusItem.getRarity().equals(ItemRarity.Status) || statusItem.getRarity().equals(ItemRarity.Pet)){
                if(player.status != statusItem){
                    player.status = statusItem;
                    host.sendMsg(id, String.format("Статус `%s` успешно установлен", statusItem.getEmojiTitle()));

                }else{
                    player.status = null;
                    host.sendMsg(id, String.format("Статус `%s` деактивирован", statusItem.getEmojiTitle()));
                }

                //player.state = base;

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
