package main;

import main.Player;

import java.util.Random;

public class CoinGame {

    private final int dash;
    private Random randomCoin;


    CoinGame(int dash){
        this.dash = dash;
    }

    public boolean roll(){
        boolean side;
        randomCoin = new Random();
        side = randomCoin.nextBoolean();
        return side;
    }


    public void coinWin(Player player, int money)
    {
        try {
            player.balance.transfer(money);
        } catch (Money.MoneyException e) {
            e.printStackTrace();
        }

    }

    public void coinLose(Player player, int money)
    {
        try {
            player.balance.transfer(-money);
        } catch (Money.MoneyException e) {
            e.printStackTrace();
        }
    }

}
