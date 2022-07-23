public class Cost {

    private int cost;

    Cost(){
        cost = 0;
    }

    @Override
    public String toString(){
        return String.format("%c%d", '$', cost);
    }
}
