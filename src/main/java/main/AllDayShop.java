package main;

import com.google.common.cache.Cache;
import database.dao.CachedItemDAO;
import database.dao.IItemDAO;

import java.util.ArrayList;
import java.util.List;

public class AllDayShop{

    IItemDAO itemDAO;
    private List<Item> statusList = new ArrayList<>();


    public AllDayShop(IItemDAO itemDAO){
        this.itemDAO = itemDAO;

        createStatus("Сияющий Ригель");
        createStatus("Слеза Зевса");
        createStatus("Скарлетт");
        createStatus("Апельсиновый сон");
        createStatus("Плантера");
        createStatus("Сапфировый клубок");
        createStatus("Страсть");
        createStatus("Ангельская пыль");
        createStatus("Мишк Фреде");
        createStatus("Сатурн");
        createStatus("Мыльные пузыри");


    }

    public Item getStatusById(int id){
        return statusList.get(id);
    }

    public List<Item> getStatusList(){
        return statusList;
    }

    public void createStatus(String item){
        statusList.add(itemDAO.get_by_name(item));
    }

}
