package commands;

import main.ActiveDuelPairs;
import main.Bot;
import main.Player;

public class Decline extends Command{
    ActiveDuelPairs activeDuelPairs;

    Decline(ActiveDuelPairs activeDuelPairs){
        this.activeDuelPairs = activeDuelPairs;
    }


    @Override
    public void consume(Bot host, Player player) {
        //DECLINE FOR ACCEPTOR
        Player anotherPlayerForAcceptor = activeDuelPairs.getByKey(player);
        Player anotherPlayerForInviter = activeDuelPairs.getKeyByValue(activeDuelPairs.getAllPairs(), player);
        if(activeDuelPairs.getByKey(player) != null){
            host.sendMsg(player.getId(), "Вы отменили дуэль");
            host.sendMsg(anotherPlayerForAcceptor.getId(), String.format("Игрок `%s` отменил дуэль", player.getUsername()));
            activeDuelPairs.removePair(player, anotherPlayerForAcceptor);
        }else if(activeDuelPairs.getKeyByValue(activeDuelPairs.getAllPairs(), player) != null){
            host.sendMsg(player.getId(), "Вы отказались от дуэля");
            host.sendMsg(anotherPlayerForInviter.getId(), String.format("Игрок `%s` отказался дуэль", player.getUsername()));
            activeDuelPairs.removePair(anotherPlayerForInviter, player);
        }
            else{
            host.sendMsg(player.getId(), "У вас нет активных дуэлей");
        }

    }
}
