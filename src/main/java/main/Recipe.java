package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.RollerFactory.itemDAO;

public class Recipe
{

	public Map<Item, List<Item>> recipes = new HashMap<>();

	public Recipe()
	{
		createRecipe("Энергетик", "Стиральный порошок", "Банан", "Бутылка");
		createRecipe("Кейс Gift", "Отвертка", "Подшипник");
	}

	public void createRecipe(String resultProduct, String... ingredients)
	{
		Item resultProductToItem = itemDAO.getByNameFromCollection(resultProduct);
		List<Item> ingredientsToItem = new ArrayList<>();

		for (int i = 0; i < ingredients.length; i++)
		{
			ingredientsToItem.add(itemDAO.getByNameFromCollection(ingredients[i]));
		}

		recipes.put(resultProductToItem, ingredientsToItem);
	}

	public Item choice(int index)
	{
		List<Item> ingredientsToItem = new ArrayList<>();

		for (Map.Entry<Item, List<Item>> entry : recipes.entrySet())
		{
			Item key = entry.getKey();
			ingredientsToItem.add(key);
		}
		return ingredientsToItem.get(index);
	}


	public boolean hasRecipe(Inventory inventory, List<Item> ingredients)
	{
		if (inventory.getItems().containsAll(ingredients))
		{
			return true;
		}
		return false;
	}
}
