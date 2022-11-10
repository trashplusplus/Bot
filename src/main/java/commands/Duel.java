package commands;

import database.dao.IItemDAO;
import database.dao.IPlayerDAO;
import main.ActiveDuelPairs;
import main.Bot;
import main.Money;
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
        if(player.getLevel() <= 5){
            host.sendMsg(id, "☠ Для участия в дуэлях нужен 5 уровень");
            return;
        }
        if(player.getInventory().getInvSize() >= 5) {
            if (player.getMoney().value >= 1500) {
                player.state = player.state.base;
                host.sendMsg(id, "☠ ($1,500) Введите ник игрока, с которым вы хотите сразиться: ");
                player.state = new DuelState(player, itemDAO, playerDAO, host, activeDuelPairs);
                try {
                    player.getMoney().transfer(-1500);
                } catch (Money.MoneyException e) {
                    e.printStackTrace();
                }

            } else {
                host.sendMsg(id, "☠ Недостаточно средств для участия в дуэлях");
            }
        }else{
            host.sendMsg(id, "☠ В инвентаре должно быть как минимум 5 предметов");
        }
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
            if(playerAcceptor != null){
                if(playerAcceptor.getUsername() != player.getUsername()) {

                    if (playerAcceptor.getInventory().getInvSize() >= 5) {
                        if (activeDuelPairs.getByKey(player) != null &&
                                activeDuelPairs.getKeyByValue(activeDuelPairs.getAllPairs(), playerAcceptor) == null) {
                            player.state = player.state.base;
                            host.sendMsg(player.getId(), "\uD83C\uDFF9 Вы уже отправили приглашение игроку " + activeDuelPairs.getByKey(player).getFormattedUsernameWithTelegramFormatting() + ", чтобы отменить введите /decline");

                        } else {


                            activeDuelPairs.putNewDuelPair(player, playerAcceptor);
                            player.state = player.state.base;
                            host.sendMsg(playerAcceptor.getId(), String.format("\uD83C\uDFF9 Игрок %s приглашает вас на дуэль\n\n✅ Принять - /accept\n❌ Отклонить - /decline", player.getFormattedUsernameWithTelegramFormatting()));
                            host.sendMsg(player.getId(), String.format("\uD83C\uDFF9 Вы отправили приглашение игроку %s", playerAcceptor.getFormattedUsernameWithTelegramFormatting()));
                        }
                    } else {
                        player.state = player.state.base;
                        host.sendMsg(player.getId(), "\uD83C\uDFF9 В инвентаре у игрока `" + playerAcceptor.getUsername() + "` должно быть как минимум 5 предметов");
                    }
                }else{
                    host.sendMsg(player.getId(), "\uD83C\uDFF9 Вы не можете вызвать на дуэль самого себя");
                }
            }

             else {
                host.sendMsg(player.getId(), "\uD83C\uDFF9 Такого игрока не существует");
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
