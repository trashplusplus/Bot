package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Inventory
{
	private List<Item> inventory = new ArrayList<>();
	private static Random ran = new Random();


	public Inventory()
	{
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

	public Item getItem(int index)
	{
		return inventory.get(index);
	}

	public void removeItem(int index)
	{
		inventory.remove(index);
	}





	public boolean putItem(Item item)
	{
		inventory.add(item);

		return true;
	}
}
