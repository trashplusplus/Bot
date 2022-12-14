package commands;

import database.dao.IItemDAO;
import main.Bot;
import main.Item;
import main.Money;
import main.Player;

import java.util.Random;

public class Phone extends Command{

    IItemDAO itemDAO;

    String videos[] = {
            "https://www.youtube.com/watch?v=JNE5Tf--5Ec",
            "https://www.youtube.com/watch?v=7LdYl7Qb5Sw&t",
            "https://www.youtube.com/watch?v=8hyCrwdoufI&t",
            "https://www.youtube.com/watch?v=m6pE8psWJXE",
            "https://www.youtube.com/watch?v=A7txKkBkgXg&list=LL&index=11",
            "https://www.youtube.com/watch?v=A7txKkBkgXg&ab_channel=GamedevAki",
            "https://www.youtube.com/watch?v=ee0jNrw-Y6c",
            "https://www.youtube.com/watch?v=NSr2QBjmQCM&list=LL&index=70&t",
            "https://www.youtube.com/watch?v=qvsNyOhYMkQ"

    };

    public Phone(IItemDAO itemDAO){
        this.itemDAO = itemDAO;
    }

    @Override
    public void consume(Bot host, Player player) throws Money.MoneyException {
        Item phone = itemDAO.get_by_name("Розовый телефон");
        if(player.getInventory().getItems().contains(phone)){
            host.sendMsg(player.getId(), videos[new Random().nextInt(videos.length)]);
        }else{
            host.sendMsg(player.getId(), String.format("Для использования этой команды вам нужен предмет `%s` \n\uD83D\uDED2 Его можно купить у других игроков в маркете или скрафтить", phone.getEmojiTitle()));
        }


    }
}
