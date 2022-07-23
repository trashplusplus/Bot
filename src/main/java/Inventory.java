import java.util.*;
import java.util.function.Consumer;

public class Inventory
{
	private List<Item> inventory = new ArrayList<>();

	private static List<Item> allItems = new ArrayList<>(List.of(new Item("Лопата", 'О', 20),
			new Item("Поисковый фонарь", 'Р', 300),
			new Item("Подвеска \"Nosebleed\"", 'Р', 1000),
			new Item("Струны", 'О', 35),
			new Item("Футболка \"Drain\"", 'О', 55),
			new Item("Банан", 'О', 5),
			new Item("Чашка \"Египет\"", 'Р', 75),
			new Item("Носки", 'О', 5),
			new Item("Ручка", 'О', 5),
			new Item("Баллончик с краской", 'О', 25),
			new Item("Платок", 'О', 10),
			new Item("Пачка сигарет", 'О', 5),
			new Item("Синий браслет", 'О', 10),
			new Item("Красный браслет", 'О', 10),
			new Item("Желтый браслет", 'О', 10),
			new Item("Зеленый браслет", 'О', 10),
			new Item("Браслет \"Орион\"", 'О', 60),
			new Item("Браслет \"Сириус\"", 'О', 65),
			new Item("Зубная щетка", 'О', 5),
			new Item("Шоколадка", 'О', 10),
			new Item("Рюкзак", 'Р', 700))
	);

	private int balance;
	private static Random ran = new Random();

	public Inventory()
	{
		balance = 0;
	}

	public Item findItem()
	{
		int randomNumber = ran.nextInt(allItems.size());
		Item currentItem = allItems.get(randomNumber);
		inventory.add(currentItem);
		return currentItem;
	}

	public String showInventory()
	{
		return inventory.toString();
	}

	public int getInvSize()
	{
		return inventory.size();
	}

	public int getBalance()
	{
		return balance;
	}
}
