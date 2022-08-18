package main;

public class ShopItem {
    Item item;
    int cost;
    String seller;
    int id;

    public ShopItem(Item item, int cost, String seller, int id){
        this.item = item;
        this.cost = cost;
        this.seller = seller;
        this.id = id;

    }

    public ShopItem(Item item, int cost, String seller){
        this.item = item;
        this.cost = cost;
        this.seller = seller;


    }
    public int getCost() {
        return cost;
    }

    public String getSeller() {
        return seller;
    }

    public Item getItem() {
        return item;
    }

    public int getId(){
        return id;
    }



}
