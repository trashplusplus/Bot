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
		createRecipe("Ключ от кейса", "Ожерелье", "Брелок с бабочкой", "Урановый стержень");
		createRecipe("Микросхема", "Урановый стержень", "USB провод", "Литий");
		createRecipe("Аккумулятор", "USB провод", "Литий", "Пакет");
		createRecipe("Связь", "USB провод", "Веревка");
		createRecipe("Антенна", "Связь", "Литий");
		createRecipe("Скейтборд", "Дека", "Граффити", "Джинсы", "Энергетик", "Гитара Fender", "Курточка", "Футболка 'Drain'");
		createRecipe("Гитара Fender", "Струны", "Дека", "Медиатор", "Whirr - Feels Like You");
		createRecipe("Розовый телефон", "Часы", "Микросхема", "Аккумулятор", "Антенна", "Сим-карта 777");
		createRecipe("Дисперсия", "Цветная резинка для волос", "Браслет 'Орион'", "Браслет 'Сириус'", "Красный браслет", "Зеленый браслет");
		createRecipe("Редкий кубок", "Золото", "Подшипник", "Ожерелье");
		createRecipe("Кулон удачи", "Сердце в золотой корочке", "Четырехлистный клевер", "Редкий кубок");
		createRecipe("Ангельская пыль", "Deftones - Adrenaline", "Облако в бутылке", "Слеза Зевса");
		createRecipe("Ключ от контейнера", "Брелок с бабочкой", "Слеза Зевса", "Золото", "Титан");
		createRecipe("Шифр", "Якорь", "Каска");
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
