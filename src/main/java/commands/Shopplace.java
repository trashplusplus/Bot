package commands;

import database.dao.InventoryDAO;
import database.dao.IItemDAO;
import database.dao.ShopDAO;
import main.*;

public class Shopplace extends Command
{
	IItemDAO itemDAO;
	InventoryDAO inventoryDAO;
	ShopDAO shopDAO;

	public Shopplace(IItemDAO itemDAO, InventoryDAO inventoryDAO, ShopDAO shopDAO)
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
		}else if(shopDAO.getAll().size() > 35){
			host.sendMsg(player.getId(), "\uD83D\uDCE1 В магазине нет доступных слотов, подождите пока один из них освободится \\[`" + shopDAO.getAll().size() + "/35`]");
		}
		else
		{
			player.state = new ShopplaceState1(host, player, itemDAO, inventoryDAO, shopDAO, player.state.base);
			Inventory inventory = player.getInventory();

			StringBuilder sb = new StringBuilder("Предметы, доступные для продажи \n").append(inventory.repr()).append('\n').append(player.state.hint);
			host.sendMsg(player_id, sb.toString());
		}
	}
}

class ShopplaceState1 extends State
{
	Bot host;
	Player player;
	IItemDAO itemDAO;
	InventoryDAO inventoryDAO;
	ShopDAO shopDAO;

	public ShopplaceState1(Bot host, Player player, IItemDAO itemDAO, InventoryDAO inventoryDAO, ShopDAO shopDAO, BaseState base)
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


			int itemID = Integer.parseInt(arg) - 1;
			Item i = player.getInventory().getItem(itemID);
			if (i.getTitle().equals("Рюкзак")
					&& player.inventory.getInvSize() >= 20
					&& player.inventory.getItems().stream().filter(e -> e.getTitle().equals("Рюкзак")).count() < 2)
			{
				host.sendMsg(id, String.format("Избавьтесь от дополнительных слотов, прежде чем продать `%s`", "\uD83C\uDF92 Рюкзак"));
			}
			else
			{
				player.state = new ShopplaceState2(host, player, inventoryDAO, shopDAO, itemID, this, base);
				host.sendMsg(id, player.state.hint);
			}

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
