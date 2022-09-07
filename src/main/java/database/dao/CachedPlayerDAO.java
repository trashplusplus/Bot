package database.dao;

import main.Bot;
import main.Player;

import java.sql.Connection;
import java.util.*;

public class CachedPlayerDAO implements IPlayerDAO
{
	Map<Player, Boolean> players;
	IPlayerDAO databaseDAO;

	public CachedPlayerDAO(Connection connection, Bot host)
	{
		players = new HashMap<>();
		databaseDAO = new PlayerDAO(connection, host);
	}

	public void dump()
	{
		players.forEach((p, v) -> databaseDAO.update(p));
		players.entrySet().removeIf(e -> !e.getValue());
		players.replaceAll((p, v) -> false);
	}

	public Set<Player> cached_players()
	{
		return players.keySet();
	}

	@Override
	public void put(Player player)
	{
		players.put(player, true);
		databaseDAO.put(player);
	}

	@Override
	public Player get_by_id(long id)
	{
		Player player = players.keySet().stream().filter(p -> p.getId() == id).findAny().orElse(null);
		if (player == null)
		{
			player = databaseDAO.get_by_id(id);
		}
		if (player != null)
		{
			players.put(player, true);
		}
		return player;
	}

	@Override
	public Player get_by_name(String name)
	{
		Player player = players.keySet().stream().filter(p -> p.getUsername().equals(name)).findAny().orElse(null);
		if (player == null)
		{
			player = databaseDAO.get_by_name(name);
		}
		if (player != null)
		{
			players.put(player, true);
		}
		return player;
	}

	@Override
	public List<Player> get_all()
	{
		return databaseDAO.get_all();
	}

	@Override
	public List<Player> get_top(String field_name, boolean ascending, long limit)
	{
		return databaseDAO.get_top(field_name, ascending, limit);
	}

	@Override
	public void update(Player player)
	{
		players.put(player, true);
	}

	@Override
	public void delete(long id)
	{
		players.keySet().stream().filter(p -> p.getId() == id).findAny().ifPresent(player -> players.remove(player));
		databaseDAO.delete(id);
	}
}
