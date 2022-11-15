package commands;

import database.dao.IPlayerDAO;
import database.dao.InventoryDAO;
import main.*;

//TODO DUEEEEEEEEEEEELS
public class Accept extends Command{
    IPlayerDAO playerDAO;
    ActiveDuelPairs activeDuelPairs;
    InventoryDAO inventoryDAO;

    Accept(InventoryDAO inventoryDAO, IPlayerDAO playerDAO, ActiveDuelPairs activeDuelPairs){
        this.playerDAO = playerDAO;
        this.activeDuelPairs = activeDuelPairs;
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public void consume(Bot host, Player player) {
        Player playerInviter = activeDuelPairs.getKeyByValue(this.activeDuelPairs.getAllPairs(), player);

        if(playerInviter != null){

            Fight fight = new Fight(inventoryDAO, player, playerInviter);
            host.sendMsg(player.getId(), fight.getResultsOfFight());
            host.sendMsg(playerInviter.getId(), fight.getResultsOfFight());

            //в процессе выполняются манипуляции над предметами, поэтому нужно сначала вывести,
            // а потом удалить/добавить их в инвентари
            fight.processFight();

            activeDuelPairs.removePair(playerInviter, player);
        }else{
            host.sendMsg(player.getId(), "Вам никто не бросал вызов");
        }
    }
}
