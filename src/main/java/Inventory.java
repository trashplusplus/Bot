import java.util.*;
import java.util.function.Consumer;

public class Inventory
{
	private List<Item> inventory = new ArrayList<>();

	private static List<Item> allItems = new ArrayList<>(List.of(
			new Item("Лопата", 'О', 200),
			new Item("Поисковый фонарь", 'Р', 7000),
			new Item("Подвеска \"Nosebleed\"", 'Р', 30000),
			new Item("Струны", 'О', 500),
			new Item("Футболка \"Drain\"", 'О', 500),
			new Item("Банан", 'О', 100),
			new Item("Чашка \"Египет\"", 'Р', 1000),
			new Item("Носки", 'О', 100),
			new Item("Ручка", 'О', 100),
			new Item("Баллончик с краской", 'О', 750),
			new Item("Платок", 'О', 150),
			new Item("Пачка сигарет", 'О', 50),
			new Item("Синий браслет", 'О', 300),
			new Item("Красный браслет", 'О', 300),
			new Item("Желтый браслет", 'О', 300),
			new Item("Зеленый браслет", 'О', 300),
			new Item("Браслет \"Орион\"", 'О', 1000),
			new Item("Браслет \"Сириус\"", 'О', 900),
			new Item("Зубная щетка", 'О', 50),
			new Item("Шоколадка", 'О', 200),
			new Item("Рюкзак", 'Р', 700),
			new Item("Синий фонарик", 'П', 25000)
	));

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

	public Item getItem(int index){
		return inventory.get(index);
	}

	public void sellItem(int index){
		balance += inventory.get((index)).getCost();
		inventory.remove(index);

	}
}
