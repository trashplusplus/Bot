package commands;

import database.dao.IItemDAO;
import database.dao.IPlayerDAO;
import main.Bot;
import main.Item;
import main.Money;
import main.Player;

import java.util.Random;

public class Skate extends Command{

    IPlayerDAO playerDAO;
    IItemDAO itemDAO;
    Random ran = new Random();
    String phrases[] = {"Вы упали", "Неудача", "Почти получилось", "Скейт уехал"};
    public Skate(IPlayerDAO playerDAO, IItemDAO itemDAO){
        this.playerDAO = playerDAO;
        this.itemDAO = itemDAO;
    }

    @Override
    public void consume(Bot host, Player player) throws Money.MoneyException {

        Item skate = itemDAO.get_by_name("Скейтборд");
        if (player.getInventory().getItems().contains(skate)){


        int x = ran.nextInt(100000);

        if(x == 3483){
            for(Player p: playerDAO.get_all()){
                if(p.getId() != player.getId()){
                    host.sendMsg(p.getId(), String.format("❤️\u200D\uD83D\uDD25 Вау! Игрок %s сделал эффектный трюк и получил *1*\uD83E\uDDF7" +
                            " на скейтборде", player.getFormattedUsernameWithTelegramFormatting()));
                }
            }

            host.sendMsg(player.getId(), "❤️\u200D\uD83D\uDD25 Ого! Вы сделали эффектный трюк и получили 1 \uD83E\uDDF7");
            player.needle += 1;

        }else if((x > 100 && x < 2500) || (x > 55000 && x < 56100)){
            int fee = new Random().nextInt(25000);
            host.sendMsg(player.getId(), String.format("❤️\u200D\uD83D\uDD25 Ого! Вы сделали эффектный трюк и получили %s", new Money(new Random().nextInt(fee))));
            player.getMoney().transfer(fee);
            player.addXp(25);
        }

        else{
            host.sendMsg(player.getId(), "\uD83D\uDEF9 " + phrases[new Random().nextInt(phrases.length)]);
        }

        }else{
            host.sendMsg(player.getId(), String.format("Для катания на скейте вам нужен предмет `%s` \n\uD83D\uDED2 Его можно купить у других игроков в маркете или скрафтить", skate.getEmojiTitle()));
        }
    }
}
