package main;

import java.util.*;

import static main.RollerFactory.itemDAO;

public class Recipe {

    Map<Item, List<Item>> recipes = new HashMap<>();


    Recipe(){

        createRecipe("Энергетик", "Стиральный порошок", "Банан", "Бутылка");
        createRecipe("\uD83D\uDCE6 Кейс Gift", "Отвертка", "Подшипник");

        /*
        recipes.put(itemDAO.getByNameFromCollection("Энергетик"),new ArrayList<Item>(Arrays.asList(
                itemDAO.getByNameFromCollection("Стиральный порошок"),
                itemDAO.getByNameFromCollection("Банан"),
                itemDAO.getByNameFromCollection("Бутылка"))));
        recipes.put(itemDAO.getByNameFromCollection("\uD83D\uDCE6 Кейс Gift"), new ArrayList<Item>(Arrays.asList(
                itemDAO.getByNameFromCollection("Отвертка"),
                itemDAO.getByNameFromCollection("Подшипник")
        )));
        */
    }

    public void createRecipe(String resultProduct, String...ingredients){

        Item resultProductToItem = itemDAO.getByNameFromCollection(resultProduct);
        List<Item> ingredientsToItem = new ArrayList<>();

        for(int i = 0; i < ingredients.length; i++){
            ingredientsToItem.add(itemDAO.getByNameFromCollection(ingredients[i]));
        }

        recipes.put(resultProductToItem, ingredientsToItem);
    }

    public Item choice(int index){
        List<Item> ingredientsToItem = new ArrayList<>();

        for(Map.Entry<Item, List<Item>> entry : recipes.entrySet()){
                Item key = entry.getKey();
                ingredientsToItem.add(key);
            }
        return ingredientsToItem.get(index);
    }


    public boolean hasRecipe(Inventory inventory, List<Item> ingredients){
        if(inventory.getItems().containsAll(ingredients)){
            return true;
        }
        return false;
    }
}
