package commands;

import database.dao.InventoryDAO;
import main.*;

public class Sell extends Command
{
	InventoryDAO inventoryDAO;

	public Sell(InventoryDAO inventoryDAO)
	{
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		player.state = new SellState(player, player.state.base, host, inventoryDAO);
		host.sendMsg(player.getId(), player.state.hint);
	}
}

class SellState extends State
{
	Player invoker;
	Bot host;
	InventoryDAO inventoryDAO;

	public SellState(Player invoker, BaseState base, Bot host, InventoryDAO inventoryDAO)
	{
		this.invoker = invoker;
		this.base = base;
		this.host = host;
		this.inventoryDAO = inventoryDAO;
		rebuild_hint();
	}

	@Override
	public void process(String arg)
	{
		long player_id = invoker.getId();
		try
		{
			Inventory inventory = invoker.getInventory();
			int sell_id = Integer.parseInt(arg) - 1;
			Item item = inventory.getItem(sell_id);
			if (item.getRarity() != ItemRarity.Limited && item.getRarity() != ItemRarity.Status && item.getRarity() != ItemRarity.Pet)
			{
				if (item.getTitle().equals("Рюкзак")
						&& inventory.getInvSize() >= 20
						&& inventory.getItems().stream().filter(e -> e.getTitle().equals("Рюкзак")).count() < 2)
				{
					host.sendMsg(player_id, String.format("Избавьтесь от дополнительных слотов, прежде чем продать `%s`", "\uD83C\uDF92 Рюкзак"));
				}
				else
				{
					invoker.balance.transfer(item.getCost().value);
					inventory.removeItem(sell_id);
					inventoryDAO.delete(player_id, item.getId(), 1);
					host.sendMsg(player_id, String.format("✅ Предмет %s продан | +%s", item.getTitle(), item.getCost()));
					if (inventory.getInvSize() == 0)
					{
						invoker.state = base;
					}
					rebuild_hint();
					host.sendMsg(player_id, invoker.state.hint);
				}
			}
			else
			{
				long downCost = (item.getCost().value / 100) * 30;
				//при продаже статуса мы будем делить цену на 3
				invoker.balance.transfer(downCost);
				inventory.removeItem(sell_id);
				inventoryDAO.delete(player_id, item.getId(), 1);
				host.sendMsg(player_id, "\uD83D\uDCC9Стоимость предмета была снижена на 70% | +" + new Money(downCost));
				//invoker.st = new SellState(invoker, base, host, inventoryDAO);
				rebuild_hint();
				host.sendMsg(player_id, invoker.state.hint);

			//	host.sendMsg(player_id, "\uD83D\uDC8D Лимитированные вещи нельзя продавать");
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			host.sendMsg(player_id, "⚠\t Пожалуйста, введите целое число");
		}
		catch (IndexOutOfBoundsException ee)
		{
			ee.printStackTrace();
			host.sendMsg(player_id, "⚠\t Указан неверный ID");
		}
		catch (Money.MoneyException e)
		{
			e.printStackTrace();
			host.sendMsg(player_id, e.getMessage());
		}
	}

	private void rebuild_hint()
	{
		Inventory inventory = invoker.getInventory();
		if (inventory.getInvSize() > 0)
		{
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("\uD83E\uDDF6 Предметы, доступные к продаже:\n");
			stringBuilder.append("\n").append(inventory.repr());
			stringBuilder.append("\n");
			stringBuilder.append("Введите номер предмета, который хотите продать:\n");
			hint = stringBuilder.toString();
		}
		else
		{
			hint = "⚠\t Ваш инвентарь пуст. Нет доступных вещей для продажи ";
		}
	}
}

