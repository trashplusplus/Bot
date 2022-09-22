package main;

import com.google.common.cache.Cache;
import database.dao.CachedItemDAO;
import database.dao.IItemDAO;

import java.util.ArrayList;
import java.util.List;

public class AllDayShop{

    IItemDAO itemDAO;
    private List<Item> goods = new ArrayList<>();

    public AllDayShop(IItemDAO itemDAO){
        this.itemDAO = itemDAO;

        createGood("Сияющий Ригель");
        createGood("Слеза Зевса");
        createGood("Скарлетт");
        createGood("Апельсиновый сон");
        createGood("Плантера");
        createGood("Сапфировый клубок");
        createGood("Страсть");
        createGood("Ангельская пыль");
        createGood("Мишк Фреде");
        createGood("Сатурн");
        createGood("Мыльные пузыри");


    }

    public Item getById(int id){
        return goods.get(id);
    }

    public List<Item> getGoods(){
        return goods;
    }

    public void createGood(String item){
        goods.add(itemDAO.get_by_name(item));
    }

}