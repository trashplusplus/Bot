package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Inventory
{
	private final List<Item> inventory = new ArrayList<>();

	public Event<Item> inventory_updated = new Event<>(this);

	public Inventory()
	{
	}

	public List<Item> getItems()
	{
		return inventory;
		//return new ArrayList<>(this.inventory);
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
		Item removed_item = inventory.remove(index);
		on_inventory_updated(removed_item);
	}


	public boolean putItem(Item item)
	{
		inventory.add(item);
		on_inventory_updated(item);

		return true;
	}

	void on_inventory_updated(Item item)
	{
		inventory_updated.raise(item);
	}
}
