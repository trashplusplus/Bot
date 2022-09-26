package main;

import com.google.common.cache.Cache;
import database.dao.CachedItemDAO;
import database.dao.IItemDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllDayShop{

    IItemDAO itemDAO;
    private List<Item> statusList = new ArrayList<>();

    public AllDayShop(IItemDAO itemDAO){
        this.itemDAO = itemDAO;

        createGood("Сияющий Ригель");
        createGood("Слеза Зевса");
        createGood("Скарлетт");
        createGood("Полумесяц");
        createGood("Плантера");
        createGood("Дисперсия");
        createGood("Страсть");
        createGood("Ангельская пыль");
        createGood("Сатурн");
        createGood("Мыльные пузыри");
        createDonateGood("Фея", 3);
        createDonateGood("Мишк Фреде", 4);


        createGood("Пчелка");
        createGood("Корова Бога");
        createGood("Бог Смерти");
        createGood("Вамп");
        createGood("Стелла");
        createGood("Поня");
        createDonateGood("Кибо", 5);
        createDonateGood("Кит", 5);
        createDonateGood("Бабочка", 5);

        createDonateGood("Ключ от кейса", 1);

    }



    public Item getGoodById(int id){
        return statusList.get(id);
    }

    public List<Item> getGoodsList(){
        return statusList;
    }


    public void createGood(String item){
        statusList.add(itemDAO.get_by_name(item));
    }
    public void createDonateGood(String item, int needles){
        itemDAO.get_by_name(item).setNeedleCost(needles);
        statusList.add(itemDAO.get_by_name(item));
    }


}
