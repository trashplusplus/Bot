package commands;

import database.dao.CachedItemDAO;
import database.dao.IItemDAO;
import main.AllDayShop;
import main.Bot;
import main.Player;

public class Shop extends Command{

    IItemDAO itemDAO;

    Shop(IItemDAO itemDAO){
        this.itemDAO = itemDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        AllDayShop shop = new AllDayShop(itemDAO);
        StringBuilder sb = new StringBuilder("\uD83C\uDFEA Добро пожаловать в Магазин 24/7\n\n");
        sb.append("Здесь вы можете купить разные товары по интересным ценам\n");
        sb.append("========================\n");
        for(int i = 0; i < shop.getGoods().size(); i++){
            sb.append(String.format("\uD83E\uDDF2 Товар |`%d`| %s \n", i, shop.getById(i)));
        }
        sb.append("========================\n");
        host.sendMsg(player.getId(), sb.toString());

    }


}
