package main;

public class BackpackException extends Throwable {
    public int backpackID;
    BackpackException(int backpackID){
        this.backpackID = backpackID;
    }
}
