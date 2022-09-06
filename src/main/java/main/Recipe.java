package main;

import java.util.ArrayList;
import java.util.List;

import static main.RollerFactory.itemDAO;

public class Recipe {
    List<Item> energyRecipe = new ArrayList<>();
    List<Item> caseRecipe = new ArrayList<>();

    List<Item> allRecipes = new ArrayList<>();

    Recipe(){
        energyRecipe.add(itemDAO.getByNameFromCollection("Стиральный порошок"));
        energyRecipe.add(itemDAO.getByNameFromCollection("Бутылка"));
        energyRecipe.add(itemDAO.getByNameFromCollection("Банан"));

        caseRecipe.add(itemDAO.getByNameFromCollection("Отвертка"));
        caseRecipe.add(itemDAO.getByNameFromCollection("Подшипник"));


        allRecipes.add(itemDAO.getByNameFromCollection("Энергетик"));
        allRecipes.add(itemDAO.getByNameFromCollection("\uD83D\uDCE6 Кейс Gift"));
    }


    public boolean hasRecipe(Inventory inventory, List<Item> ingredients){
        if(inventory.getItems().containsAll(ingredients)){
            return true;
        }
        return false;
    }
}
