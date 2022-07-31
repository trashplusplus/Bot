
public class Casino {

    private int dash;
    private long coefficientX;

    Casino(){
        this.dash = 0;
    }

    public void newDash(int playerBid, long coefficientX){
        this.dash = dash;
        this.coefficientX = coefficientX;
    }

    public String youLose(Player player){
        return player.getUsername().toString() + " проиграл со ставкой " + dash * coefficientX;
    }

    public String youWon(Player player){
        return player.getUsername().toString() + " выиграл со ставкой " + dash * coefficientX;
    }
}
