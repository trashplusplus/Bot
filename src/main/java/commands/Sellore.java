package commands;

import database.dao.InventoryDAO;
import main.Bot;
import main.Item;
import main.Money;
import main.Player;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Sellore extends Command{
    InventoryDAO inventoryDAO;

    Sellore(InventoryDAO inventoryDAO){
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        long id = player.getId();

        LocalTime open = LocalTime.of(0, 0);
        LocalTime close = LocalTime.of(7, 0);

        LocalTime currentTime = LocalTime.now();

        if (currentTime.isBefore(open) || currentTime.isAfter(close))  // wtf?
        {
            host.sendMsg(id, "\uD83E\uDEA8 Прием руды работает с 0:00 до 7:00\n\nСдавая руду, Вы можете получить " +
                    "в несколько раз больше выручки, чем если бы сдавали ее \uD83D\uDCDE Скупщику");
        }
        else
        {
            List<String> ore_titles = new ArrayList<>();
            ore_titles.add("Золото");
            ore_titles.add("Каменный уголь");
            ore_titles.add("Титан");
            long fee = 0L;

            Iterator<Item> iter = player.getInventory().getItems().iterator();
            while (iter.hasNext())
            {
                Item item = iter.next();
                if (ore_titles.contains(item.getTitle()))
                {
                    fee += item.getCost().value * new Random().nextInt(12);
                    inventoryDAO.delete(id, item.getId(), 1);
                    iter.remove();
                }
            }

            if (fee > 0)
            {
                host.sendMsg(id, String.format("\uD83E\uDEA8 Покупатель выложил за всю руду %s", new Money(fee)));
                try
                {
                    player.balance.transfer(fee);
                }
                catch (Money.MoneyException e)
                {
                    e.printStackTrace();
                    host.sendMsg(id, e.getMessage());
                }
            }
            else
            {
                host.sendMsg(id, "\uD83E\uDD88У вас нет полезных ископаемых\nЧтобы начать копать, введите /mine");
            }
        }
    }
}
