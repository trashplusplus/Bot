package commands;

import database.dao.ContainerDAO;
import database.dao.IItemDAO;
import database.dao.InventoryDAO;
import main.*;

public class Contget extends Command{
    IItemDAO itemDAO;
    ContainerDAO containerDAO;
    InventoryDAO inventoryDAO;

    public Contget(ContainerDAO containerDAO, IItemDAO itemDAO, InventoryDAO inventoryDAO){
        this.itemDAO = itemDAO;
        this.containerDAO = containerDAO;
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public void consume(Bot host, Player player) throws Money.MoneyException {
        boolean isKey = player.getInventory().getItems().contains(itemDAO.get_by_name("Ключ от контейнера"));

        Inventory inventory = player.inventory;
        int limitSpace = inventory.inventory_capacity;

        if(!isKey) {
            host.sendMsg(player.getId(), "\uD83D\uDDDD Для доступа к контейнеру нужен \uD83D\uDDDD `Ключ от контейнера`");
            return;
        }

        if(player.getInventory().getItems().size() > 0 && player.getInventory().getItems().size() < limitSpace){
            StringBuilder sb = new StringBuilder("Введите ID предмета, который Вы хотите забрать из контейнера \n");


            sb.append("========================\n");
            for (int i = 0; i < player.getContainer().getContainerSize(); i++)
            {
                sb.append(String.format("Предмет |`%2d`| : %s\n", i + 1, player.getContainer().getItem(i)));
            }
            sb.append("========================");


            host.sendMsg(player.getId(), sb.toString());
            player.state = new ContgetState(player, itemDAO, containerDAO, host, inventoryDAO);
        }else{
            host.sendMsg(player.getId(),"В вашем инвентаре нет места");
        }

    }

    class ContgetState extends State{

        IItemDAO itemDAO;
        ContainerDAO containerDAO;
        Player player;
        Bot host;
        InventoryDAO inventoryDAO;

        public ContgetState(Player player, IItemDAO itemDAO, ContainerDAO containerDAO, Bot host, InventoryDAO inventoryDAO){
            this.itemDAO = itemDAO;
            this.player = player;
            this.containerDAO = containerDAO;
            this.host = host;
            this.inventoryDAO = inventoryDAO;
        }

        @Override
        public void process(String arg) {
            try{


            long player_id = player.getId();
            int item_id = Integer.parseInt(arg) - 1;

            Item item = player.getContainer().getItem(item_id);
            //player.state = base;
            inventoryDAO.putItem(player_id, item.getId());
            player.getInventory().putItem(item);
            containerDAO.delete(player.getId(), item.getId(), 1);
            player.getContainer().removeItem(item_id);





            host.sendMsg(player_id, String.format("Предмет \\[%s] успешно добавлен в инвентарь", item.getEmojiTitle()));



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
