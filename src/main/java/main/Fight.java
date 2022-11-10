package main;

import database.dao.InventoryDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Fight {
    private Item player1Item;
    private Item player2Item;
    private Player player1;
    private Player player2;
    private InventoryDAO inventoryDAO;
    private List<String> emojis = new ArrayList<>(Arrays.asList("\uD83D\uDC7D",
            "\uD83D\uDC7D", "\uD83E\uDD16", "\uD83E\uDD77", "\uD83E\uDEF5", "\uD83E\uDEF5\uD83C\uDFFF"));

    public Fight(InventoryDAO inventoryDAO, Player player1, Player player2){
        this.player1 = player1;
        this.player2 = player2;
        player1Item = this.player1.getInventory().getItem(new Random().nextInt(player1.getInventory().getInvSize()));
        player2Item = this.player2.getInventory().getItem(new Random().nextInt(player2.getInventory().getInvSize()));

        this.inventoryDAO = inventoryDAO;
    }

    public Item getPlayer1Item(){
        return player1Item;
    }

    public Item getPlayer2Item(){
        return player2Item;
    }

    public void processFight(){

        if(player1Item.getCost().value > player2Item.getCost().value){
            player1.addXp(10);
            player2.addXp(5);
            //add item to winner
            player1.getInventory().putItem(player2Item);
            inventoryDAO.putItem(player1.getId(), player2Item.getId());
            //remove item from loser's inventory
            player2.getInventory().getItems().remove(player2Item);
            inventoryDAO.delete(player2.getId(), player2Item.getId(), 1);
            player2.stats.duelLose++;
            player1.stats.duelWin++;


        }else if (player1Item.getCost().value < player2Item.getCost().value){
            player2.addXp(10);
            player1.addXp(5);
            player2.getInventory().putItem(player1Item);
            inventoryDAO.putItem(player2.getId(), player1Item.getId());

            player1.getInventory().getItems().remove(player1Item);
            inventoryDAO.delete(player1.getId(), player1Item.getId(), 1);
            player1.stats.duelLose++;
            player2.stats.duelWin++;

        }else{

        }
    }

    public String getResultsOfFight(){
        StringBuilder sb = new StringBuilder("\uD83C\uDFF9 Дуэль\n\n");
        sb.append("-----------------\n");
        sb.append(String.format("%s Игроку %s попался %s\n", getRandomEmoji(), player1.getFormattedUsernameWithTelegramFormatting(), player1Item));
        sb.append(String.format("%s Игроку %s попался %s\n", getRandomEmoji(), player2.getFormattedUsernameWithTelegramFormatting(), player2Item));
        sb.append("-----------------\n\n");
        if(player1Item.getCost().value > player2Item.getCost().value){
            sb.append(String.format("%s Победил игрок %s, он забирает себе %s \n", player1.getStatusOrPassedEmoji("\uD83D\uDC8E"),player1.getFormattedUsernameWithTelegramFormatting(), player2Item));
            return sb.toString();
        }else if(player1Item.getCost().value < player2Item.getCost().value){
            sb.append(String.format("%s Победил игрок %s, он забирает себе %s \n", player2.getStatusOrPassedEmoji("\uD83D\uDC8E"), player2.getFormattedUsernameWithTelegramFormatting(), player1Item));
            return sb.toString();
        }else{
            sb.append("❤️\u200D\uD83D\uDD25 Ничья! Вам попались предметы с одинаковой стоимостью!\n");
            return sb.toString();
        }
    }


    private String getRandomEmoji(){
        return emojis.get(new Random().nextInt(emojis.size()));
    }


}
