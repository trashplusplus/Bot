
public class Player
{
	private long id;
	private Inventory inventory;

	Player(long id)
	{
		this.id = id;
		inventory = new Inventory();
	}

	public void check()
	{

	}

	public Inventory getInventory()
	{
		return inventory;
	}
}
