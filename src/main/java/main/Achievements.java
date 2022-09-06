package main;

import javax.swing.text.StyledEditorKit;
import java.util.*;

public class Achievements {
    Player player;
    
    Set<Integer> teaAchs = new LinkedHashSet<>();
    Set<Integer> coffeeAchs = new LinkedHashSet<>();
    Set<Integer> treesAchs = new LinkedHashSet<>();


    public Achievements(Player player){

        teaAchs.add(15);
        teaAchs.add(50);
        teaAchs.add(75);

        coffeeAchs.add(15);
        coffeeAchs.add(50);
        coffeeAchs.add(75);

        treesAchs.add(5);
        treesAchs.add(20);
        treesAchs.add(50);

        this.player = player;
    }

    public String getTeaAch(){
        StringBuilder sb = new StringBuilder("Ваши достижения\n\n");
        for(int i: teaAchs) {
            if(player.stats.tea >= i){
               // value = true;
                sb.append("\uD83C\uDF3F");
            }else{
                sb.append("❌");
            }
            sb.append(String.format("Выпить %d кружек чая\n", i));
        }
        return sb.toString();
    }

    public String getCoffeeAch(){
        StringBuilder sb = new StringBuilder("\n");
        for(int i: coffeeAchs) {
            if(player.stats.coffee >= i){
                sb.append("☕️");
            }else{
                sb.append("❌");
            }
            sb.append(String.format("Выпить %d кружек кофе\n", i));
        }
        return sb.toString();
    }

    public String getTreesAch(){
        StringBuilder sb = new StringBuilder("\n");
        for(int i: treesAchs) {
            if(player.stats.trees >= i){
                sb.append("\uD83C\uDF33");
            }else{
                sb.append("❌");
            }
            sb.append(String.format("Посадить %d деревьев\n", i));
        }
        return sb.toString();
    }

    public boolean checkTrees(int goal){
        if (player.stats.trees == goal)
            return true;
        return false;
    }




}
