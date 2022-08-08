import java.util.concurrent.Callable;

public class FindItemAction implements Callable<Item>
{
	private Player player;

	public FindItemAction(Player player)
	{
		this.player = player;
	}

	@Override
	public Item call() throws Exception
	{
		Item new_item = ItemFactory.getRandomItem();
		player.getInventory().putItem(new_item);
		return new_item;
	}
}
