package commands;

import database.dao.IPlayerDAO;
import main.ActiveDuelPairs;
import main.Bot;
import main.Player;
//TODO DUEEEEEEEEEEEELS
public class Accept extends Command{
    IPlayerDAO playerDAO;
    ActiveDuelPairs activeDuelPairs;

    Accept(IPlayerDAO playerDAO, ActiveDuelPairs activeDuelPairs){
        this.playerDAO = playerDAO;
        this.activeDuelPairs = activeDuelPairs;
    }

    @Override
    public void consume(Bot host, Player player) {
        Player playerInviter = activeDuelPairs.getKeyByValue(this.activeDuelPairs.getAllPairs(), player);

        if(playerInviter != null){
            host.sendMsg(player.getId(), "Вы сразились с " + playerInviter.getUsername());
            host.sendMsg(playerInviter.getId(), "Вы сразились с " + player.getUsername());

            activeDuelPairs.removePair(playerInviter, player);
        }else{
            host.sendMsg(player.getId(), "Вам никто не бросал вызов");
        }
    }
}
