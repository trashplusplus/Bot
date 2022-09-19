package main;

import database.dao.ItemDAO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StatusList {
    ItemDAO itemDAO;
    Set<Item> status = new HashSet<>();

    public StatusList(ItemDAO itemDAO){
        this.itemDAO = itemDAO;

        status.add(get("\uD83E\uDDDA\u200D♀ Фея"));
        status.add(get("⚡Молния"));
        status.add(get("\uD83D\uDCAB Звезда"));
    }

    public Set<Item> getStatusList(){
        return status;
    }


    private Item get(String name){
        return itemDAO.getByNameFromCollection(name);
    }

}
