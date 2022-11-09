package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.RollerFactory.itemDAO;

public class Recipe{
	public Map<Item, List<Item>> recipes = new HashMap<>();

	public Recipe(){
		createRecipe("Энергетик", "Стиральный порошок", "Банан", "Бутылка");
		createRecipe("Кейс Gift", "Отвертка", "Подшипник");
		createRecipe("Статусная Аянами", "Плюшевая Аянами Рей", "Whirr - Feels Like You", "Рецепт Статусной Аянами");
		createRecipe("Граффити", "Баллончик с краской", "Текст песни 'FF'");
		createRecipe("Статус поддержки Украины", "Синий браслет", "Желтый браслет");


	}

	public void createRecipe(String resultProduct, String... ingredients){

		Item resultProductToItem = itemDAO.get_by_name(resultProduct);
		List<Item> ingredientsToItem = new ArrayList<>();

		for (int i = 0; i < ingredients.length; i++){
			ingredientsToItem.add(itemDAO.get_by_name(ingredients[i]));
		}
		recipes.put(resultProductToItem, ingredientsToItem);
	}

	public Item choice(int index){

		List<Item> ingredientsToItem = new ArrayList<>();
		for (Map.Entry<Item, List<Item>> entry : recipes.entrySet()){
			Item key = entry.getKey();
			ingredientsToItem.add(key);
		}
		return ingredientsToItem.get(index);
	}


	public boolean hasRecipe(Inventory inventory, List<Item> ingredients){

		if (inventory.getItems().containsAll(ingredients)){
			return true;
		}
		return false;
	}
}
