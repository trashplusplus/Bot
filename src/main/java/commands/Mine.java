package commands;

import database.dao.IItemDAO;
import database.dao.InventoryDAO;
import main.*;

import java.util.Random;

public class Mine extends Command{

    IItemDAO itemDAO;
    InventoryDAO inventoryDAO;

    public Mine(IItemDAO itemDAO, InventoryDAO inventoryDAO){
        this.itemDAO = itemDAO;
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public void consume(Bot host, Player player){
        Item pickaxe = itemDAO.get_by_name("Кирка");
        Random random = new Random();
        int x = random.nextInt(2500);
        ItemRarity rareIty = ItemRarity.Rare;
        Item randomRare = itemDAO.get_by_rarity(rareIty).get(new Random().nextInt(itemDAO.get_by_rarity(rareIty).size()));
        Item gold = itemDAO.get_by_name("Золото");
        Item titan = itemDAO.get_by_name("Титан");
        Item coal = itemDAO.get_by_name("Каменный уголь");

        Inventory inventory = player.inventory;
        int limitSpace = inventory.inventory_capacity;

        if (player.inventory.getItems().contains(pickaxe)){
            if(player.getInventory().getInvSize() < limitSpace){


            if((x % 700) == 0){
                host.sendMsg(player.getId(), String.format("⛏ Вы откопали: ", randomRare.getEmojiTitle() ));
                player.addXp(35);
                player.inventory.putItem(randomRare);
                inventoryDAO.putItem(player.getId(), randomRare.getId());
            }else if((x % 200) == 0){
                host.sendMsg(player.getId(), String.format("⛏ Вы нашли: %s", gold.getEmojiTitle()));
                player.addXp(25);
                player.inventory.putItem(gold);
                inventoryDAO.putItem(player.getId(), gold.getId());
            }else if((x % 20) == 0) {
                host.sendMsg(player.getId(), String.format("⛏ Вы нашли: %s", coal.getEmojiTitle()));
                player.addXp(10);
                player.inventory.putItem(coal);
                inventoryDAO.putItem(player.getId(), coal.getId());
            }else if((x % 635) == 0){
                    host.sendMsg(player.getId(), String.format("⛏ Вы нашли: %s", titan.getEmojiTitle()));
                    player.addXp(80);
                    player.inventory.putItem(titan);
                    inventoryDAO.putItem(player.getId(), titan.getId());
            }else{
                host.sendMsg(player.getId(), "⛏ Вы ничего не откопали");
            }
            }else{
                host.sendMsg(player.getId(), "⚠ В вашем инвентаре нет места");
            }
        }else{
            host.sendMsg(player.getId(), String.format("Для работы в шахте вам нужен предмет `%s` \n\uD83D\uDED2 " +
                    "Его можно купить у других игроков в маркете или найти", pickaxe.getEmojiTitle()));
        }
    }
}
