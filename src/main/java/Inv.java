import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Inv {
	

	private ArrayList<Item> inventory = new ArrayList<Item>();
	private ArrayList<Item> allItems = new ArrayList<Item>();

	private static int balance;
	private static Random ran;
	
	public Inv(){
		balance = 0;
		allItems.add(new Item("Лопата", 'О', 20));
		allItems.add(new Item("Поисковый фонарь", 'Р', 300));
		allItems.add(new Item("Подвеска \"Nosebleed\"", 'Р', 1000));
		allItems.add(new Item("Струны", 'О', 35));
		allItems.add(new Item("Футболка \"Drain\"", 'О', 55));
		allItems.add(new Item("Банан", 'О', 5));
		allItems.add(new Item("Чашка \"Египет\"", 'Р', 75));
		allItems.add(new Item("Носки", 'О', 5));
		allItems.add(new Item("Ручка", 'О', 5));
		allItems.add(new Item("Баллончик с краской", 'О', 25));
		allItems.add(new Item("Платок", 'О', 10));
		allItems.add(new Item("Пачка сигарет", 'О', 5));
		allItems.add(new Item("Синий браслет", 'О', 10));
		allItems.add(new Item("Красный браслет", 'О', 10));
		allItems.add(new Item("Желтый браслет", 'О', 10));
		allItems.add(new Item("Зеленый браслет", 'О', 10));
		allItems.add(new Item("Браслет \"Орион\"", 'О', 60));
		allItems.add(new Item("Браслет \"Сириус\"", 'О', 65));
		allItems.add(new Item("Зубная щетка", 'О', 5));
		allItems.add(new Item("Шоколадка", 'О', 10));
		allItems.add(new Item("Рюкзак", 'Р', 700));
	}
	
	public Item findItem() {
		Random ran = new Random();
		int randomNumber = ran.nextInt(allItems.size());
		Item currentItem = allItems.get(randomNumber);
		inventory.add(currentItem);
		return currentItem;
	}
		
	public String showInventory() {
		
		return inventory.toString();
		
	}
	
	public int getInvSize() {
		return inventory.size();
	}
	
	public int getBalance() {
		return balance;
	}
	
}
