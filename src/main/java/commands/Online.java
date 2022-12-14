package commands;

import database.dao.IPlayerDAO;
import main.Bot;
import main.Player;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Online extends Command{
    IPlayerDAO playerDAO;

    public Online(IPlayerDAO playerDAO){
        this.playerDAO = playerDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        if(player.getId() == 501446180){
            StringBuilder sb = new StringBuilder("Все зарегистрированные игроки: \n\n");
            for(Player p: playerDAO.get_all()){
                sb.append(String.format("\uD83D\uDC64 Игрок `%s` | `%d` \n", p.getUsername(), p.getId()));
            }
            host.sendMsg(player.getId(), sb.toString());
        }else{
            host.sendMsg(player.getId(), "\uD83D\uDEAB У вас недостаточно прав для этой команды");
        }
    }
}
