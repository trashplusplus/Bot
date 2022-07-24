
public class Player
{
	private long id;
	private Inventory inventory;
	private String username;

	Player(long id, String username)
	{
		this.username = username;
		this.id = id;
		inventory = new Inventory();
	}

	public String getUsername()
	{
		return username;
	}

	public Inventory getInventory()
	{
		return inventory;
	}
}
