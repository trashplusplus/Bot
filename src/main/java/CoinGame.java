import java.util.Random;

public class CoinGame {

    private int dash;
    private Random randomCoin;
    //true = орел
    //false = решка

    CoinGame(int dash){
        this.dash = dash;
    }

    public boolean roll(){
        boolean side;
        randomCoin = new Random();
        side = randomCoin.nextBoolean();
        return side;
    }


}
