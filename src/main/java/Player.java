import ability.Ability;
import ability.Cooldown;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Player
{
	private long id;
	private Inventory inventory;
	private String username;
	private Ability<Item> findItemAbility;
	public static enum State{
		awaitingNickname,
		awaitingSellArguments,
		awaitingCommands,
		awaitingChangeNickname,
		coinDash;
	}
	private State state;

	Player(long id, String username)
	{
		this.username = username;
		this.id = id;
		inventory = new Inventory();
		state = State.awaitingNickname;
		findItemAbility = new Ability(new Cooldown(20000L), new FindItemAction(this));
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
}
