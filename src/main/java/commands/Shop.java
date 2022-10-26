package commands;

import database.dao.CachedItemDAO;
import database.dao.IItemDAO;
import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import main.*;

public class Shop extends Command{

    IItemDAO itemDAO;
    InventoryDAO inventoryDAO;
	IPlayerDAO playerDAO;

    Shop(IItemDAO itemDAO, InventoryDAO inventoryDAO, IPlayerDAO playerDAO){
        this.itemDAO = itemDAO;
        this.inventoryDAO = inventoryDAO;
		this.playerDAO = playerDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        AllDayShop shop = new AllDayShop(itemDAO);
        StringBuilder sb = new StringBuilder("\uD83C\uDFEA Добро пожаловать в Магазин 24/7\n\n");
        sb.append("Здесь можно купить разные товары по интересным ценам\n\n");
        sb.append("========================\n");
        sb.append("*Статусы*\n\n");

        String prefix;
        for(int i = 0; i < shop.getGoodsList().size(); i++){
            prefix = shop.getGoodById(i).getRarity() == ItemRarity.Pet ? "\uD83D\uDC36 Питомец" : "\uD83D\uDECD Товар";
            if(shop.getGoodById(i).isNeedleCost()){
                sb.append(String.format("%s |`%d`| %s \n", prefix, i, shop.getGoodById(i).getNeedleCostFormat()));
            }else{
                sb.append(String.format("%s |`%d`| %s \n", prefix, i, shop.getGoodById(i)));
            }

            if(i == 11){
                sb.append("\n*Питомцы*\n\n");
            }else if (i == 20){
                sb.append("\n*Предметы*\n\n");
            }

        }

        sb.append("========================\n\n");

        sb.append("Выберите номер предмета, который вы хотите купить: \n");
        player.state = new Shop1(player, player.state.base, host, inventoryDAO, itemDAO, playerDAO);
        host.sendMsg(player.getId(), sb.toString());

    }
}

 class Shop1 extends State{

    Player player;
    Bot host;
    InventoryDAO inventoryDAO;
    IItemDAO itemDAO;
	IPlayerDAO playerDAO;

    Shop1(Player player, BaseState base, Bot host, InventoryDAO inventoryDAO, IItemDAO itemDAO, IPlayerDAO playerDAO){
        this.player = player;
        this.host = host;
        this.inventoryDAO = inventoryDAO;
        this.itemDAO = itemDAO;
		this.playerDAO = playerDAO;
    }

     @Override
     public void process(String arg) {
        long id = player.getId();
         AllDayShop shop = new AllDayShop(itemDAO);

        try{
				
            int thisItemID = Integer.parseInt(arg);
            Item good = shop.getGoodById(thisItemID);
            if(good.isNeedleCost()){
                if(player.needle >= good.getNeedleCost()){
                    player.needle -= good.getNeedleCost();
                    inventoryDAO.putItem(id, good.getId());
                    player.getInventory().putItem(good);
                    player.state = base;
                    host.sendMsg(id, String.format("\uD83C\uDFEA Ура! Вы успешно приобрели `%s` Хорошего дня", good.getEmojiTitle()));

					//all players notification
                    for(Player p : playerDAO.get_all()){
                        if(p.getId() != player.getId()) {
                            if (player.isStatus()) {
                                host.sendMsg(p.getId(), String.format("\uD83C\uDFEA Вау! Игрок `%s` \\[%s] приобрел себе новый товар %s в Магазине 24/7", player.getUsername(), player.getStatus(), good.getEmojiTitle()));
                            } else {
                                host.sendMsg(p.getId(), String.format("\uD83C\uDFEA Ого! Игрок `%s` приобрел себе новый товар %s в Магазине 24/7", player.getUsername(), good.getEmojiTitle()));
                            }
                        }
                    }


                }else{
                    throw new Money.MoneyException();
                }
            }else{
                if(player.getMoney().value >= good.getCost().value){
                    player.getMoney().transfer(-good.getCost().value);
                    inventoryDAO.putItem(id, good.getId());
                    player.getInventory().putItem(good);
                    player.state = base;
                    host.sendMsg(id, String.format("\uD83C\uDFEA Ура! Вы успешно приобрели `%s` Хорошего дня", good.getEmojiTitle()));

                    //notification for all players
                    for(Player p : playerDAO.get_all()){
                        if(p.getId() != player.getId()) {
                            if (player.isStatus()) {
                                host.sendMsg(p.getId(), String.format("\uD83C\uDFEA Вау! Игрок `%s` \\[%s] приобрел себе новый товар %s в Магазине 24/7", player.getUsername(), player.getStatus(), good.getEmojiTitle()));
                            } else {
                                host.sendMsg(p.getId(), String.format("\uD83C\uDFEA Ого! Игрок `%s` приобрел себе новый товар %s в Магазине 24/7", player.getUsername(), good.getEmojiTitle()));
                            }
                        }
                    }

                }else{
                    throw new Money.MoneyException();
                }
            }


        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            host.sendMsg(id, "Неверный ID");
        }catch (Money.MoneyException ee){
            ee.printStackTrace();
            host.sendMsg(id, "\uD83E\uDDE2 Упс! Недостаточно средств");
        }catch (NumberFormatException e){
            host.sendMsg(id, "Укажите число");
            e.printStackTrace();
        }
     }
 }
