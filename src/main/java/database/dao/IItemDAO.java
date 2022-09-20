package database.dao;

import main.Item;
import main.ItemRarity;

import java.util.List;

public interface IItemDAO
{
	Item get_by_id(long id);
	Item get_by_name(String name);

	List<Item> get_by_name_pattern(String re_pattern);
	List<Item> get_by_rarity(ItemRarity rarity);
	List<Item> get_all();
}
