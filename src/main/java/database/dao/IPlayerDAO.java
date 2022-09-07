package database.dao;

import main.Player;

import java.util.List;

public interface IPlayerDAO
{
	void put(Player player);


	Player get_by_id(long id);
	Player get_by_name(String name);

	List<Player> get_all();
	List<Player> get_top(String field_name, boolean ascending, long limit);


	void update(Player player);


	void delete(long id);
}
