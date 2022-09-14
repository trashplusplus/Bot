package commands.sell;

import commands.BaseState;
import commands.State;
import database.dao.InventoryDAO;
import main.*;

public class SellState1 extends State
{
	Player invoker;
	Bot host;
	InventoryDAO inventoryDAO;

	public SellState1(Player invoker, BaseState base, Bot host, InventoryDAO inventoryDAO)
	{
		this.invoker = invoker;
		this.base = base;
		this.host = host;
		this.inventoryDAO = inventoryDAO;

		Inventory inventory = invoker.getInventory();
		if (inventory.getInvSize() > 0)
		{
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("\uD83E\uDDF6 Предметы, доступные к продаже:\n");
			stringBuilder.append("\n");
			stringBuilder.append("============================\n");
			for (int i = 0; i < inventory.getInvSize(); i++)
			{
				stringBuilder.append(String.format("Предмет |%d| : %s\n", i, inventory.getItem(i).toString()));
			}

			stringBuilder.append("============================\n");
			stringBuilder.append("\n");
			stringBuilder.append("Введите номер предмета, который хотите продать:\n");
			hint = stringBuilder.toString();
		}
		else
		{
			hint = "⚠\t Ваш инвентарь пуст. Нет доступных вещей для продажи ";
		}
	}

	@Override
	public void process(String arg)
	{
		long player_id = invoker.getId();
		try
		{
			Inventory inventory = invoker.getInventory();
			int sell_id = Integer.parseInt(arg);
			Item item = inventory.getItem(sell_id);
			if (item.getRarity() != ItemRarity.Limited)
			{
				invoker.balance.transfer(item.getCost().value);
				inventory.removeItem(sell_id);
				inventoryDAO.delete(player_id, item.getId(), 1);
				host.sendMsg(player_id, "✅ Предмет продан | + " + item.getCost());
				invoker.st = new SellState1(invoker, base, host, inventoryDAO);
				host.sendMsg(player_id, invoker.st.hint);
			}
			else
			{
				host.sendMsg(player_id, "\uD83D\uDC8D Лимитированные вещи нельзя продавать");
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
}
