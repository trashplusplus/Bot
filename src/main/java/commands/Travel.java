package commands;

import database.dao.IItemDAO;
import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import main.*;

import java.util.List;
import java.util.Random;

public class Travel extends Command{
    IItemDAO itemDAO;
    IPlayerDAO playerDAO;
    InventoryDAO inventoryDAO;
    String phrases[] = {"Вы проехали мимо", "Неудача", "Почти догнали", "Вы ничего не нашли"};
    Random ran = new Random();

    public Travel(IItemDAO itemDAO, IPlayerDAO playerDAO, InventoryDAO inventoryDAO){
        this.itemDAO = itemDAO;
        this.playerDAO = playerDAO;
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public void consume(Bot host, Player player) throws Money.MoneyException {
        Item auto = itemDAO.get_by_name("Обычная машинка");
        Item superCar = itemDAO.get_by_name("Легендарный спорткар");
        int x = ran.nextInt(35000);
        List<Item> ranPet = itemDAO.get_by_rarity(ItemRarity.Pet);


        Item xPet = ranPet.get(new Random().nextInt(ranPet.size()));

        if(player.getInventory().getItems().contains(superCar) || player.getInventory().getItems().contains(auto)){
            if(x == 19877){
                host.sendMsg(player.getId(), String.format("%s Ого! Вы нашли питомца '%s'", superCar.getEmoji(), xPet));
                player.addXp(100);
                player.inventory.putItem(xPet);
                inventoryDAO.putItem(player.getId(), xPet.getId());
            }else{
                host.sendMsg(player.getId(), String.format("%s %s", superCar.getEmoji(), phrases[new Random().nextInt(phrases.length)]));

            }
        }else{
            host.sendMsg(player.getId(), String.format("Для пушетествия и поиска питомцев вам нужен предмет `%s` или `%s` \n\uD83D\uDED2 Его можно купить у других игроков в маркете или Магазине 24/7", auto.getEmojiTitle(), superCar.getEmojiTitle()));
        }

    }
}
