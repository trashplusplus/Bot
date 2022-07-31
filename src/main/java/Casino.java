
public class Casino {

    private int bid;
    private long coefficientX;

    Casino(){
        this.bid = 0;
    }

    public void newBid(int playerBid, long coefficientX){
        this.bid = bid;
        this.coefficientX = coefficientX;
    }


    public String youLose(Player player){
        return player.getUsername().toString() + " проиграл со ставкой " + bid * coefficientX;
    }

    public String youWon(Player player){
        return player.getUsername().toString() + " выиграл со ставкой " + bid * coefficientX;
    }
}
