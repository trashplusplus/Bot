package commands;

import database.dao.ContainerDAO;
import database.dao.IItemDAO;
import database.dao.InventoryDAO;
import main.*;

public class Contput extends Command{
    InventoryDAO inventoryDAO;
    IItemDAO itemDAO;
    ContainerDAO containerDAO;
    public Contput(InventoryDAO inventoryDAO, IItemDAO itemDAO, ContainerDAO containerDAO){
        this.inventoryDAO = inventoryDAO;
        this.itemDAO = itemDAO;
        this.containerDAO = containerDAO;
    }

    @Override
    public void consume(Bot host, Player player) throws Money.MoneyException {
        Inventory inventory = player.getInventory();
        int limitSpace = inventory.inventory_capacity;
        boolean isKey = inventory.getItems().contains(itemDAO.get_by_name("Ключ от контейнера"));

        if(!isKey) {
            host.sendMsg(player.getId(), "\uD83D\uDDDD Для доступа к контейнеру нужен \uD83D\uDDDD `Ключ от контейнера`");
            return;
        }
        if(player.getInventory().getItems().size() > 0 && player.getContainer().getContainerSize() < 15){
            StringBuilder sb = new StringBuilder("Введите ID предмета, который будет помещен в контейнер \n");


            sb.append("========================\n");
            for (int i = 0; i < inventory.getInvSize(); i++)
            {
                sb.append(String.format("Предмет |`%2d`| : %s\n", i + 1, inventory.getItem(i)));
            }
            sb.append("========================");


            host.sendMsg(player.getId(), sb.toString());
            player.state = new ContputState(player, inventoryDAO, itemDAO, containerDAO, host);
        }else{
            host.sendMsg(player.getId(), "В контейнере нет места");
        }
    }

    class ContputState extends State{

        InventoryDAO inventoryDAO;
        IItemDAO itemDAO;
        Player player;
        Bot host;
        ContainerDAO containerDAO;

        ContputState(Player player, InventoryDAO inventoryDAO, IItemDAO itemDAO, ContainerDAO containerDAO, Bot host){
            this.itemDAO = itemDAO;
            this.inventoryDAO = inventoryDAO;
            this.player = player;
            this.host = host;
            this.containerDAO = containerDAO;
        }


        @Override
        public void process(String arg) {
            try{


            long player_id = player.getId();
            int item_id = Integer.parseInt(arg) - 1;
            Item item = player.getInventory().getItem(item_id);
            containerDAO.putItem(player.getId(), item.getId());
            player.getContainer().putItem(item);
            player.getInventory().removeItem(item_id);
            inventoryDAO.delete(player_id, item.getId(), 1);



            if(!item.getTitle().equals("Ключ от контейнера")) {
                host.sendMsg(player_id, String.format("Предмет \\[%s] перемещен в контейнер, чтобы его достать введите /contget", item.getEmojiTitle()));
                return;
            }

            host.sendMsg(player.getId(), "\uD83D\uDE43 Хахахах, Вы поместили ключ от контейнера в контейнер");
           // host.sendMsg(player_id, String.format("✅ Предмет %s продан | +%s", item.getTitle(), item.getCost()));
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                host.sendMsg(player.getId(), String.format("⚠ Неверный ID"));
            }catch (NumberFormatException e){
                e.printStackTrace();
                host.sendMsg(player.getId(), "⚠\t Пожалуйста, введите целое число");

            }
        }
    }
}
