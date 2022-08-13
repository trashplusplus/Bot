package main;

public class ShopItem {
    Item item;
    int cost;
    String seller;

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
}
