package commands;

import database.dao.IPlayerDAO;
import main.Bot;
import main.Player;

public class Broadcast extends Command{
    IPlayerDAO playerDAO;

    public Broadcast(IPlayerDAO playerDAO){
        this.playerDAO = playerDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        if(player.getId() == 501446180){
            player.state = new Broadcast1(playerDAO, host, player);
            host.sendMsg(player.getId(), player.state.hint);
        }else{
            host.sendMsg(player.getId(), "\uD83D\uDEAB У вас недостаточно прав для этой команды");
        }
    }
}

class Broadcast1 extends State{

    IPlayerDAO playerDAO;
    Bot host;
    Player player;

    public Broadcast1(IPlayerDAO playerDAO, Bot host, Player player){
        this.playerDAO = playerDAO;
        this.host = host;
        this.player = player;
        hint = "\uD83E\uDDF7 Введите сообщение для всех игроков: ";
    }

    @Override
    public void process(String arg) {
        for(Player p: playerDAO.get_all()){
            player.state = base;
            host.sendMsg(p.getId(), arg);

        }
    }
}
