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
        //createGood("Ёлка 2023");
        createGood("Мыльные пузыри");
        createDonateGood("Фея", 4);
        createDonateGood("Мишк Фреде", 5);


        createGood("Пчелка");
        createGood("Корова Бога");
        createGood("Бог Смерти");
        createGood("Вамп");
        createGood("Стелла");
        createGood("Поня");
        createDonateGood("Кибо", 5);
        createDonateGood("Кит", 10);
        createDonateGood("День бабочек", 15);
        createDonateGood("XJ9", 25);


        createGood("Обычная машинка");
        createDonateGood("Ключ от кейса", 1);
        createDonateGood("Легендарный спорткар", 35);

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
