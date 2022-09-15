package commands;

import database.dao.InventoryDAO;
import database.dao.ItemDAO;
import database.dao.ShopDAO;
import main.*;

public class Shopbuy extends Command
{
	ShopDAO shopDAO;
	ItemDAO itemDAO;
	InventoryDAO inventoryDAO;

	public Shopbuy(ShopDAO shopDAO, ItemDAO itemDAO, InventoryDAO inventoryDAO)
	{
		this.shopDAO = shopDAO;
		this.itemDAO = itemDAO;
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		long player_id = player.getId();
		int limitSpace;
		Item backpack = itemDAO.getByNameFromCollection("\uD83C\uDF92 Рюкзак");
		if (player.getInventory().getItems().contains(backpack))
		{
			limitSpace = 25;
		}
		else
		{
			limitSpace = 20;
		}

		if (player.getInventory().getInvSize() < limitSpace)
		{
			if (shopDAO.getAll().isEmpty())
			{
				host.sendMsg(player_id, "\uD83D\uDC40 В магазине пока нет товаров, чтобы разместить введите /shopplace\n");
			}
			else
			{
				StringBuilder sb = new StringBuilder("\uD83D\uDC5C Все предметы в магазине:\n\n");
				for (ShopItem i : shopDAO.getAll())
				{
					sb.append(String.format("\uD83C\uDFA9 Товар |# %d| `%s` | Цена: %s | Продавец: `%s` \n", i.getId(), i.getItem().getTitle(), i.getCost(), i.getSeller().getUsername()));
				}
				sb.append("\n");

				host.sendMsg(player_id, sb.toString());
				player.state = new ShopbuyState(host, player, shopDAO, inventoryDAO);
				host.sendMsg(player_id, player.state.hint);
			}
		}
		else
		{
			host.sendMsg(player.getId(), "⚠ В вашем инвентаре нет места");
		}
	}
}

class ShopbuyState extends State
{
	Bot host;
	Player player;
	final ShopDAO shopDAO;
	InventoryDAO inventoryDAO;

	public ShopbuyState(Bot host, Player player, ShopDAO shopDAO, InventoryDAO inventoryDAO)
	{
		this.host = host;
		this.player = player;
		this.shopDAO = shopDAO;
		this.inventoryDAO = inventoryDAO;
		hint = "Введите ID товара, который вы хотите купить: ";
	}

	@Override
	public void process(String arg)
	{
		try
		{
			int userInput = Integer.parseInt(arg);

			synchronized (shopDAO)
			{
				ShopItem wanted_item = shopDAO.getByID(userInput);
				Item item = wanted_item.getItem();
				long itemCost = wanted_item.getCost().value;
				Player seller = wanted_item.getSeller();

				if (player.equals(seller))
				{
					inventoryDAO.putItem(player.getId(), item.getId());
					player.getInventory().putItem(item);
					host.sendMsg(player.getId(), String.format("Ваш товар %s снят с продажи", item));
					shopDAO.delete(userInput);
				}
				else if (player.balance.value >= itemCost)
				{
					player.balance.transfer(-itemCost);
					seller.balance.transfer(itemCost);

					inventoryDAO.putItem(player.getId(), item.getId());
					player.getInventory().putItem(item);

					player.state = player.state.base;

					host.sendMsg(player.getId(), String.format("\uD83C\uDF6D Предмет `%s` успешно куплен", item));
					host.sendMsg(seller.getId(), String.format("\uD83D\uDCC8 Ваш предмет `%s` купил игрок `%s` | + %s", item.getTitle(), player.getUsername(), new Money(itemCost)));
					seller.addXp(3);

					shopDAO.delete(userInput);
				}
				else
				{
					host.sendMsg(player.getId(), "Недостаточно средств");
				}
			}
		}
		catch (NumberFormatException ex)
		{
			ex.printStackTrace();
			host.sendMsg(player.getId(), "Введите целое число");
		}
		catch (IndexOutOfBoundsException ex)
		{
			ex.printStackTrace();
			host.sendMsg(player.getId(), "Неверный ID");
		}
		catch (Money.MoneyException ex)
		{
			ex.printStackTrace();
			host.sendMsg(player.getId(), ex.getMessage());
		}
	}
}
