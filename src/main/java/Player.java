import ability.Ability;
import ability.Cooldown;

import java.util.LinkedList;
import java.util.List;

public class Player
{
	private long id;
	private Inventory inventory;
	private String username;
	private String state;
	private Ability<Item> findItemAbility;

	Player(long id, String username)
	{
		this.username = username;
		this.id = id;
		inventory = new Inventory();
		state = "start";
		findItemAbility = new Ability(new Cooldown(10L), new FindItemAction(this));
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
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
