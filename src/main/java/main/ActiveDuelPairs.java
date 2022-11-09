package main;

import java.util.*;

public final class ActiveDuelPairs {
        private Map<Player, Player> duelPairs;

        public ActiveDuelPairs(){
            duelPairs = new HashMap<>();
        }

        public void putNewDuelPair(Player playerInvoker, Player playerAcceptor){
            duelPairs.put(playerInvoker, playerAcceptor);
        }

        public void dropAllPairs(){
            duelPairs.clear();
        }

        public Map<Player, Player> getAllPairs(){
            return duelPairs;
        }

    public <T, E> Player getKeyByValue(Map<Player, Player> map, Player value) {
        for (Map.Entry<Player, Player> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void removePair(Player player1, Player player2){
            duelPairs.remove(player1, player2);
    }

    public Player getByKey(Player player){
            return duelPairs.get(player);
    }


}
