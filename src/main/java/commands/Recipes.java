package commands;

import database.dao.InventoryDAO;
import main.*;

import java.util.List;
import java.util.Map;

public class Recipes extends Command
{
	InventoryDAO inventoryDAO;

	public Recipes(InventoryDAO inventoryDAO)
	{
		this.inventoryDAO = inventoryDAO;
	}

	@Override
	public void consume(Bot host, Player player)
	{
		long id = player.getId();
		Recipe recipe = new Recipe();

		if (recipe.recipes != null && !recipe.recipes.isEmpty())
		{
			StringBuilder sb = new StringBuilder("*Рецепты*\n" +
					"Здесь можно скрафтить полезные вещи, используя менее ценные предметы\n\n" +
					"Предметы, доступные для крафта: \n\n" +
					"========================\n");
			int i = 0;

			for (Map.Entry<Item, List<Item>> entry : recipe.recipes.entrySet())
			{
				String craftName = entry.getKey().getEmojiTitle();
				sb.append(String.format("Рецепт |`%d`|: %s\n", i++, craftName));
			}

			sb.append("========================\n" +
					"Чтобы скрафтить предмет введите его ID:");
			player.state = new RecipesState(host, player, inventoryDAO, player.state.base);
			host.sendMsg(id, sb.toString());
		}
		else
		{
			host.sendMsg(id, "\uD83C\uDF81\t Список рецептов пуст ");
		}
	}
}

class RecipesState extends State
{
	Bot host;
	Player player;
	InventoryDAO inventoryDAO;

	public RecipesState(Bot host, Player player, InventoryDAO inventoryDAO, BaseState base)
	{
		this.host = host;
		this.player = player;
		this.inventoryDAO = inventoryDAO;
		this.base = base;
		hint = "Введите номер рецепта:";
	}

	@Override
	public void process(String arg)
	{
		//улучшить код, добавить поощрение опытом
		long player_id = player.getId();
		try
		{
			Recipe recipe = new Recipe();
			Inventory inventory = player.getInventory();
			int craft_id = Integer.parseInt(arg);
			Item craftName = recipe.choice(craft_id);
			List<Item> ingredients = recipe.recipes.get(craftName);

			player.state = base;
			if (recipe.hasRecipe(inventory, ingredients))
			{
				for (Item i : ingredients)
				{
					inventoryDAO.delete(player_id, i.getId(), 1);
					inventory.getItems().remove(i);  // todo test
				}
				inventoryDAO.putItem(player_id, craftName.getId());
				inventory.putItem(craftName);

				player.addXp(4);
				host.sendMsg(player_id, "\uD83D\uDD27 Предмет изготовлен " + craftName.getEmojiTitle());
			}
			else
			{
				host.sendMsg(player_id, "\uD83E\uDE93 Для крафта нужно иметь: \n " + recipe.recipes.get(craftName).toString());
			}
		}
		catch (NumberFormatException nfe)
		{
			nfe.printStackTrace();
			host.sendMsg(player_id, "⚠\t Пожалуйста, введите целое число");
		}
		catch (IndexOutOfBoundsException ioobe)
		{
			ioobe.printStackTrace();
			host.sendMsg(player_id, "⚠\t Указан неверный ID");
		}
	}
}
