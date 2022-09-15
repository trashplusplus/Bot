package main;

public class BackpackException extends Throwable {
    public int backpackID;
    public BackpackException(int backpackID){
        this.backpackID = backpackID;
    }
}
