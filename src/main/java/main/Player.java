package main;

import ability.Ability;
import ability.Cooldown;

public class Player
{
	private long id;
	private String username;
	public int balance;
	private Inventory inventory;
	private Ability<Item> findItemAbility;
	public long last_fia;
	private int level;
	private int xp;

	public Player(long id)
	{
		this(id, 0, 1, "player" + id, 0, State.awaitingNickname, new Inventory(), 0L);
	}

	public static enum State{
		awaitingNickname,
		awaitingSellArguments,
		awaitingCommands,
		awaitingChangeNickname,
		coinDash,
		shopAwaitingTypeOfShop,
		shopPlaceGood,
		shopPlaceGood_awaitingCost,
		shopBuyGood;

	}
	private State state;

	//public Player(long id, String username)
	//{
	//	this(id, username, 0, State.awaitingNickname, new Inventory());
	//}

	public Player(long id, int xp, int level, String username, int balance, State state, Inventory inventory, long last_fia)
	{
		this.id = id;
		this.username = username;
		this.balance = balance;
		this.state = state;
		this.inventory = inventory;
		findItemAbility = new Ability<>(new Cooldown(10L), new FindItemAction(this));
		this.last_fia = last_fia;
		this.xp = xp;
		this.level = level;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setState(State state){
		this.state = state;
	}

	public State getState(){
		return state;
	}

	public long getId(){
		return id;
	}

	public String getUsername()
	{
		return username;
	}

	public Inventory getInventory()
	{
		return inventory;
	}

	public Item findItem()
	{
		return findItemAbility.use();
	}

	public Ability<Item> getFindItemAbility()
	{
		return findItemAbility;
	}

	public int getMoney()
	{
		return balance;
	}

	public void levelUp(){

			level++;
			xp = 0;

	}

	public void addXp(int xp){
		this.xp += xp;
	}

	public int getLevel(){
		return level;
	}

	public int getXp(){
		return xp;
	}
}
