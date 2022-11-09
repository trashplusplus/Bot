package commands;

import database.dao.IItemDAO;
import database.dao.IPlayerDAO;
import main.ActiveDuelPairs;
import main.Bot;
import main.Player;

public class Duel extends Command{
    IPlayerDAO playerDAO;
    IItemDAO itemDAO;
    ActiveDuelPairs activeDuelPairs;

    Duel(IItemDAO itemDAO, IPlayerDAO playerDAO, ActiveDuelPairs activeDuelPairs){
        this.itemDAO = itemDAO;
        this.playerDAO = playerDAO;
        this.activeDuelPairs = activeDuelPairs;
    }

    @Override
    public void consume(Bot host, Player player) {
        long id = player.getId();
        host.sendMsg(id, "Введите ник игрока, с которым вы хотите сразиться: ");
        player.state = new DuelState(player, itemDAO, playerDAO, host, activeDuelPairs);
    }
}

class DuelState extends State{
    IItemDAO itemDAO;
    IPlayerDAO playerDAO;
    Bot host;
    Player player;
    ActiveDuelPairs activeDuelPairs;


    DuelState(Player player, IItemDAO itemDAO, IPlayerDAO playerDAO, Bot host, ActiveDuelPairs activeDuelPairs){
        this.itemDAO = itemDAO;
        this.playerDAO = playerDAO;
        this.host = host;
        this.player = player;
        this.activeDuelPairs = activeDuelPairs;
    }

    @Override
    public void process(String arg) {
        try{
            Player playerAcceptor = playerDAO.get_by_name(arg);
            if(playerAcceptor != null && playerAcceptor.getUsername() != player.getUsername()){

                if(playerAcceptor.getInventory().getInvSize() != 0){
                    if(activeDuelPairs.getByKey(player) != null){
                        player.state = player.state.base;
                        host.sendMsg(player.getId(), "\uD83C\uDFF9 Вы уже отправили приглашение игроку `" + activeDuelPairs.getByKey(player).getUsername() + "`");

                    }else{


                   activeDuelPairs.putNewDuelPair(player, playerAcceptor);
                    player.state = player.state.base;
                   host.sendMsg(playerAcceptor.getId(), String.format("\uD83C\uDFF9 Игрок `%s` приглашает вас на дуэль", player.getUsername()));
                   host.sendMsg(player.getId(), String.format("\uD83C\uDFF9 Вы отправили приглашение игроку `%s`", playerAcceptor.getUsername()));
                    }
                }else{
                    player.state = player.state.base;
                    host.sendMsg(player.getId(), "\uD83C\uDFF9 Инвентарь игрока `" + playerAcceptor.getUsername() + "` пуст");
                }

            }else{
                host.sendMsg(player.getId(), "\uD83C\uDFF9 Такого игрока не существует");
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
