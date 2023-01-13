package main;

import java.util.ArrayList;
import java.util.List;

public class Inventory
{
	private final List<Item> inventory = new ArrayList<>();
	public int inventory_capacity = 20;

	public Event<Item> inventory_updated = new Event<>(this);

	public Inventory()
	{
		inventory_updated.subscribe(Inventory::capacity_check_handler);
	}

	public List<Item> getItems()
	{
		return inventory;
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

	public String repr()
	{
		StringBuilder sb = new StringBuilder(inventory.size() * 20);
		sb.append("========================\n");
		for (int i = 0; i < inventory.size(); i++)
		{
			sb.append(String.format("Предмет |`%2d`| : %s\n", i + 1, inventory.get(i)));
		}
		return sb.append("========================").toString();
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

	public boolean hasPet(){
		for(Item i: inventory){
			if(i.getRarity().equals(ItemRarity.Pet)){
				return true;
			}
		}
		return false;
	}

	void change_capacity()
	{
		inventory_capacity = inventory.stream().anyMatch(i -> i.getTitle().equals("Рюкзак")) ? 30 : 20;
	}

	static void capacity_check_handler(Object o_inventory, Item item)
	{
		if (item.getTitle().equals("Рюкзак"))
		{
			Inventory inventory = (Inventory) o_inventory;
			//System.out.printf("Change capacity called, capacity before change: %d", inventory.inventory_capacity);
			inventory.change_capacity();
			//System.out.printf(", capacity after change: %d \n", inventory.inventory_capacity);
		}
	}



	void on_inventory_updated(Item item)
	{
		inventory_updated.raise(item);
	}
}
