package main;

import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Achievements {
    Player player;

    Map<Integer, Boolean> teaAchs = new HashMap<>();
    Map<Integer, Boolean> coffeeAchs = new HashMap<>();
    Map<Integer, Boolean> treesAchs = new HashMap<>();


    public Achievements(Player player){

        teaAchs.put(75, false);
        teaAchs.put(50, false);
        teaAchs.put(15, false);

        coffeeAchs.put(75, false);
        coffeeAchs.put(50, false);
        coffeeAchs.put(15, false);

        treesAchs.put(50, false);
        treesAchs.put(20, false);
        treesAchs.put(5, false);



        this.player = player;



    }

    public String getTeaAch(){
        StringBuilder sb = new StringBuilder("Ваши достижения\n\n");
        for(Map.Entry<Integer, Boolean> entry : teaAchs.entrySet()) {
            Integer key = entry.getKey();
            Boolean value = entry.getValue();
            if(player.stats.tea >= key){
                value = true;
                sb.append("\uD83C\uDF3F");
            }else{
                sb.append("❌");
            }
            sb.append(String.format("Выпить %d кружек чая\n", key));
        }
        return sb.toString();
    }

    public String getCoffeeAch(){
        StringBuilder sb = new StringBuilder("\n");
        for(Map.Entry<Integer, Boolean> entry : coffeeAchs.entrySet()) {
            Integer key = entry.getKey();
            Boolean value = entry.getValue();
            if(player.stats.coffee >= key){
                value = true;
                sb.append("☕️");
            }else{
                sb.append("❌");
            }
            sb.append(String.format("Выпить %d кружек кофе\n", key));
        }
        return sb.toString();
    }

    public String getTreesAch(){
        StringBuilder sb = new StringBuilder("\n");
        for(Map.Entry<Integer, Boolean> entry : treesAchs.entrySet()) {
            Integer key = entry.getKey();
            Boolean value = entry.getValue();
            if(player.stats.trees >= key){
                value = true;
                sb.append("\uD83C\uDF33");
            }else{
                sb.append("❌");
            }
            sb.append(String.format("Посадить %d деревьев\n", key));
        }
        return sb.toString();
    }

    public boolean checkTrees(int goal){
        if (player.stats.trees == goal)
            return true;
        return false;
    }



}
