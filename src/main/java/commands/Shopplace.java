package commands;

import database.dao.InventoryDAO;
import database.dao.ItemDAO;
import database.dao.ShopDAO;
import main.*;

public class Shopplace extends Command
{
	ItemDAO itemDAO;
	InventoryDAO inventoryDAO;
	ShopDAO shopDAO;

	public Shopplace(ItemDAO itemDAO, InventoryDAO inventoryDAO, ShopDAO shopDAO)
	{
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
		this.shopDAO = shopDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		long player_id = player.getId();

		if (player.getInventory().getInvSize() == 0)
		{
			host.sendMsg(player_id, "Вы не можете ничего продать, так как Ваш инвентарь пуст");
		}
		else
		{
			player.state = new ShopplaceState1(host, player, itemDAO, inventoryDAO, shopDAO, player.state.base);
			Inventory inventory = player.getInventory();

			StringBuilder sb = new StringBuilder("Предметы, доступные для продажи \n");
			sb.append("=====================\n");
			for (int i = 0; i < inventory.getInvSize(); i++)
			{
				sb.append(String.format("Предмет | %d |: ", i)).append(inventory.getItem(i)).append("\n");
			}
			sb.append("=====================\n");
			host.sendMsg(player_id, sb.toString());
			host.sendMsg(player_id, player.state.hint);
		}
	}
}

class ShopplaceState1 extends State
{
	Bot host;
	Player player;
	ItemDAO itemDAO;
	InventoryDAO inventoryDAO;
	ShopDAO shopDAO;

	public ShopplaceState1(Bot host, Player player, ItemDAO itemDAO, InventoryDAO inventoryDAO, ShopDAO shopDAO, BaseState base)
	{
		this.host = host;
		this.player = player;
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
		this.shopDAO = shopDAO;
		this.base = base;
		hint = "Введите ID предмета, который хотите продать:";
	}

	@Override
	public void process(String arg)
	{
		long id = player.getId();
		try
		{
			int itemID = Integer.parseInt(arg);
			if (itemID >= player.getInventory().getInvSize())
			{
				throw new IndexOutOfBoundsException();
			}
			else if (player.getInventory().getInvSize() > 20)
			{
				throw new BackpackException(itemID);
			}
			player.state = new ShopplaceState2(host, player, inventoryDAO,shopDAO, itemID, this, base);
			host.sendMsg(id, player.state.hint);
		}
		catch (NumberFormatException ex)
		{
			ex.printStackTrace();
			host.sendMsg(id, "Введите целое число");
		}
		catch (IndexOutOfBoundsException ex)
		{
			ex.printStackTrace();
			host.sendMsg(id, "Неверный ID");
		}
		catch (BackpackException ex)
		{
			Item ii = player.getInventory().getItem(ex.backpackID);
			Item backpack = itemDAO.getByNameFromCollection("Рюкзак");
			if (ii.equals(backpack))
			{
				host.sendMsg(id, String.format("Избавьтесь от дополнительных слотов, прежде чем продать `%s`", backpack.getTitle()));
				//player.setState(Player.State.awaitingCommands); todo
			}
			else
			{
				player.state = new ShopplaceState2(host, player, inventoryDAO, shopDAO, ex.backpackID, this, base);
				host.sendMsg(player.getId(), player.state.hint);
			}
		}
	}
}

class ShopplaceState2 extends State
{
	Bot host;
	Player player;
	InventoryDAO inventoryDAO;
	ShopDAO shopDAO;
	int item_id;

	public ShopplaceState2(Bot host, Player player, InventoryDAO inventoryDAO, ShopDAO shopDAO, int item_id, State previous, BaseState base)
	{
		this.host = host;
		this.player = player;
		this.inventoryDAO = inventoryDAO;
		this.shopDAO = shopDAO;
		this.item_id = item_id;
		this.previous = previous;
		this.base = base;
		hint = "Введите стоимость товара:";
	}

	@Override
	public void process(String arg)
	{
		long player_id = player.getId();
		try
		{
			long cost = Long.parseLong(arg);
			if (cost > 0L)
			{
				Inventory inventory = player.getInventory();
				ShopItem shopItem = new ShopItem(inventory.getItem(item_id), cost, player);
				shopDAO.put(shopItem);

				player.state = base;
				host.sendMsg(player_id, String.format("Товар `%s` выставлен на продажу", inventory.getItem(item_id).getEmojiTitle()));
				inventory.removeItem(item_id);

				inventoryDAO.delete(player_id, shopItem.getItem().getId(), 1);
			}
			else
			{
				host.sendMsg(player_id, "Некорректная сумма");
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			host.sendMsg(player_id, "⚠\t Пожалуйста, введите целое число");
		}
	}
}
