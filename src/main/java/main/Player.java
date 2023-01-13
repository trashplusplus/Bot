package main;

import commands.BaseState;
import database.dao.IItemDAO;
import database.dao.InventoryDAO;

import java.awt.event.ContainerAdapter;
import java.util.Objects;

public class Player
{
	private final long id;
	public final Inventory inventory;
	public final Container container;
	public Money balance;
	public Long needle;
	private String username;
	private int level;
	private int xp;
	public Long findExpiration = null;
	public Long pocketsExpiration = null;
	public int donateRandomer = 0;
	public Stats stats;
	public int page = 0;
	public commands.State state;
	public final Bot host;
	public Item status;

	IItemDAO itemDAO;
	InventoryDAO inventoryDAO;
	public Event<Integer> level_reached = new Event<>(this);

	public Player(long id, Bot host, String name)
	{
		this(id, 0, 1, name, 0L, 0L, new Inventory(), new Container(), new Stats(), null, host);
	}

	public Player(long id, int xp, int level, String username, long balance, long needle, Inventory inventory, Container container, Stats stats, Item status, Bot host)
	{
		this.id = id;
		this.username = username;
		this.balance = new Money(balance);
		this.stats = stats;
		this.inventory = inventory;
		this.xp = xp;
		this.level = level;
		this.host = host;
		this.needle = needle;
		state = new BaseState(host, this);
		this.status = status;
		this.container = container;

	}

	public long getId(){
		return id;
	}


	public String getUsername()
	{
		return username;
	}
	public String getFormattedUsername(){
		if(inventory.getItems().contains(status)){
			if(status != null){
				return username + " \\[" + status.getEmoji() + "]";
			}
		}else{
			status = null;
		}

		return username;
	}

	public String getFormattedUsernameWithTelegramFormatting(){
		if(inventory.getItems().contains(status)){
			if(status != null){
				return "`" + username + "`" + " \\[" + status.getEmoji() + "]";
			}
		}else{
			status = null;
		}

		return getUsernameWithWithTelegramFormatting();
	}

	public String getUsernameWithWithTelegramFormatting(){
		return String.format("`%s`", username);
	}

	public boolean isStatus(){
		if(status != null)
			return true;

		return false;
	}


	public void setUsername(String username)
	{
		this.username = username;
	}

	public Inventory getInventory()
	{
		return inventory;
	}

	public Money getMoney()
	{
		return balance;
	}

	public Stats getStats()
	{
		return stats;
	}

	public String getStatus(){

		return status.getEmoji();
	}

	public Container getContainer() {
		return container;
	}

	public String getStatusOrPassedEmoji(String emoji){
		//если не обнаружит статус то вернет заданное эмоджи
		if(status != null) {return status.getEmoji();}else{ return emoji; }
	}

	public Item getStatusItem(){
		return status;
	}

	public void levelUp()
	{
		while (xp > 10 * level)
		{
			xp -= 10 * level++;
			on_level_reached(level);
		}
		//host.level_up_notification(this);
	}

	public void ach_treeHard()
	{

		host.achievement_notification(this, "Поздравляем! Вы заработали достижение *Форрест Гамп*\nДобавлен новый предмет в инвентарь", 15000);

	}

	public void ach_coffee()
	{
		host.achievement_notification(this, "Поздравляем! Вы заработали достижение *Джон Коффи*\nДобавлен новый предмет в инвентарь", 7500);
	}

	public void ach_tea()
	{
		host.achievement_notification(this, "Поздравляем! Вы заработали достижение *Гринфилд*\nДобавлен новый предмет в инвентарь", 7500);
	}

	public void addXp(int xp)
	{
		this.xp += xp;
		if (this.xp >= level * 10)
			levelUp();
	}

	public int getLevel()
	{
		return level;
	}

	public int getXp()
	{
		return xp;
	}


	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Player player = (Player) o;
		return id == player.id;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	void on_level_reached(int level)
	{
		level_reached.raise(level);
	}
}
