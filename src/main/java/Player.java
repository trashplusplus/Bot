import ability.Ability;
import ability.Cooldown;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Player
{
	private long id;
	private String username;
	private Inventory inventory;
	private Ability<Item> findItemAbility;

	public static enum State{
		awaitingNickname,
		awaitingSellArguments,
		awaitingCommands,
		awaitingChangeNickname,
		casinoDash;
	}
	private State state;

	Player(long id, String username)
	{
		this(id, username, State.awaitingNickname);
	}

	Player(long id, String username, State state)
	{
		this(id, username, state, new Inventory());
	}

	public Player(long id, String username, State state, Inventory inventory)
	{
		this.id = id;
		this.username = username;
		this.state = state;
		this.inventory = inventory;
		findItemAbility = new Ability<>(new Cooldown(10L), new FindItemAction(this));
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
		return inventory.getBalance();
	}
}
