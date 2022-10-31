package main;

import java.util.*;

public class Stats {
    //these fields must be private and i know this
    public int bonus;

    public int coffee;
    public int tea;

    public int coinWins;
    public int coinLosses;
    public int trees;
    public int capitals;
    public int hideInv;

    public int magazines;
    public int totalWonMoney;
    public int totalLostMoney;
    public int findCounter;
    public int mudCounter;
    public int totalMud;
    public int craftCounter;

    public Stats(){

    }

    public Stats(int bonus, int coinWins, int coinLosses, int coffee, int tea, int trees, int capitals, int hideInv, int magazines,
    int totalWonMoney, int totalLostMoney, int findCounter, int mudCounter, int totalMud, int craftCounter){
        this.coinWins = coinWins;
        this.coinLosses = coinLosses;
        this.coffee = coffee;
        this.tea = tea;
        this.bonus = bonus;
        this.trees = trees;
        this.capitals = capitals;
        this.hideInv = hideInv;
        this.magazines = magazines;
        this.totalWonMoney = totalWonMoney;
        this.totalLostMoney = totalLostMoney;
        this.findCounter = findCounter;
        this.mudCounter = mudCounter;
        this.totalMud = totalMud;
        this.craftCounter = craftCounter;
    }




}
