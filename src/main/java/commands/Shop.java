package commands;

import database.dao.CachedItemDAO;
import database.dao.IItemDAO;
import database.dao.InventoryDAO;
import main.*;

public class Shop extends Command{

    IItemDAO itemDAO;
    InventoryDAO inventoryDAO;

    Shop(IItemDAO itemDAO, InventoryDAO inventoryDAO){
        this.itemDAO = itemDAO;
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        AllDayShop shop = new AllDayShop(itemDAO);
        StringBuilder sb = new StringBuilder("\uD83C\uDFEA Добро пожаловать в Магазин 24/7\n\n");
        sb.append("Здесь вы можете купить разные товары по интересным ценам\n");
        sb.append("*Статусы*\n");
        sb.append("========================\n");
        for(int i = 0; i < shop.getStatusList().size(); i++){
            sb.append(String.format("\uD83D\uDECD Товар |`%d`| %s \n", i, shop.getStatusById(i)));
        }
        sb.append("========================\n\n");
        sb.append("Выберите ID предмета, который вы хотите купить: \n");
        player.state = new Shop1(player, player.state.base, host, inventoryDAO, itemDAO);
        host.sendMsg(player.getId(), sb.toString());

    }
}

 class Shop1 extends State{

    Player player;
    Bot host;
    InventoryDAO inventoryDAO;
    IItemDAO itemDAO;

    Shop1(Player player, BaseState base, Bot host, InventoryDAO inventoryDAO, IItemDAO itemDAO){
        this.player = player;
        this.host = host;
        this.inventoryDAO = inventoryDAO;
        this.itemDAO = itemDAO;
    }

     @Override
     public void process(String arg) {
        long id = player.getId();
         AllDayShop shop = new AllDayShop(itemDAO);

        try{

            int thisItemID = Integer.parseInt(arg);
            Item good = shop.getStatusById(thisItemID);

            if(player.getMoney().value >= good.getCost().value){
                player.getMoney().transfer(-good.getCost().value);
                inventoryDAO.putItem(id, good.getId());
                player.getInventory().putItem(good);
                host.sendMsg(id, String.format("\uD83C\uDFEA Ура! Вы успешно приобрели `%s` Хорошего дня", good.getEmojiTitle()));
                player.state = base;
            }

        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            host.sendMsg(id, "Неверный ID");
        }catch (Money.MoneyException ee){
            ee.printStackTrace();
            host.sendMsg(id, "Недостаточно средств");
        }catch (NumberFormatException e){
            host.sendMsg(id, "Укажите число");
            e.printStackTrace();
        }
     }
 }
