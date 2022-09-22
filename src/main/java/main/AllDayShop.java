package main;

import com.google.common.cache.Cache;
import database.dao.CachedItemDAO;
import database.dao.IItemDAO;

import java.util.ArrayList;
import java.util.List;

public class AllDayShop{

    IItemDAO itemDAO;
    private List<Item> statusList = new ArrayList<>();
    private List<Item> petList = new ArrayList<>();


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
        createStatus("Фея");

        createStatus("Пчелка");
        createStatus("Корова Бога");
        createStatus("Вамп");
        createStatus("Стелла");
        createStatus("Поня");
        createStatus("Дино");
        createStatus("Кит");
        createStatus("Бабочка");
        createStatus("Бог Смерти");

    }

    public Item getStatusById(int id){
        return statusList.get(id);
    }
    public Item getPetById(int id){
        return petList.get(id);
    }

    public List<Item> getStatusList(){
        return statusList;
    }
    public List<Item> getPetList(){
        return petList;
    }

    public void createStatus(String item){
        statusList.add(itemDAO.get_by_name(item));
    }
    public void createPet(String pet){petList.add(itemDAO.get_by_name(pet));}

}
