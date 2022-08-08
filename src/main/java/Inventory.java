import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Inventory
{
	private List<Item> inventory = new ArrayList<>();

	//static
	//{
	//	allItems = new ArrayList<>(List.of(
	//			new Item("Лопата", ItemRarity.Common, 200),
	//			new Item("Поисковый фонарь", ItemRarity.Rare, 7000),
	//			new Item("Подвеска \"Nosebleed\"", ItemRarity.Rare, 30000),
	//			new Item("Струны", ItemRarity.Common, 500),
	//			new Item("Футболка \"Drain\"", ItemRarity.Common, 500),
	//			new Item("Банан", ItemRarity.Common, 100),
	//			new Item("Чашка \"Египет\"", ItemRarity.Rare, 1000),
	//			new Item("Носки", ItemRarity.Common, 100),
	//			new Item("Ручка", ItemRarity.Common, 100),
	//			new Item("Баллончик с краской", ItemRarity.Common, 750),
	//			new Item("Платок", ItemRarity.Common, 150),
	//			new Item("Пачка сигарет", ItemRarity.Common, 50),
	//			new Item("Синий браслет", ItemRarity.Common, 300),
	//			new Item("Красный браслет", ItemRarity.Common, 300),
	//			new Item("Желтый браслет", ItemRarity.Common, 300),
	//			new Item("Зеленый браслет", ItemRarity.Common, 300),
	//			new Item("Браслет \"Орион\"", ItemRarity.Common, 1000),
	//			new Item("Браслет \"Сириус\"", ItemRarity.Common, 900),
	//			new Item("Зубная щетка", ItemRarity.Common, 50),
	//			new Item("Шоколадка", ItemRarity.Common, 200),
	//			new Item("Рюкзак", ItemRarity.Rare, 700),
	//			new Item("Синий фонарик", ItemRarity.Gift, 25000)
	//	));
	//}

	private int balance;
	private static Random ran = new Random();

	public Inventory()
	{
		balance = 0;
	}

	public List<Item> getItems()
	{
		return new ArrayList<>(this.inventory);
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

	public Item getItem(int index)
	{
		return inventory.get(index);
	}

	public void sellItem(int index)
	{
		balance += inventory.get((index)).getCost();
		inventory.remove(index);
	}

	public boolean putItem(Item item)
	{
		inventory.add(item);

		return true;
	}
}
