package commands;

import main.Bot;
import main.Item;
import main.Player;
import main.Recipe;

import java.util.List;
import java.util.Map;

public class Recipes extends Command  // big todo
{
	@Override
	public void consume(Bot host, Player player)
	{
		long id = player.getId();
		StringBuilder sb = new StringBuilder("*Рецепты*\n" +
						"Здесь можно скрафтить полезные вещи, используя менее ценные предметы\n\n" +
						"Предметы, доступные для крафта: \n");
		Recipe recipe = new Recipe();

		if (recipe.recipes != null)
		{
			sb.append("\n========================\n");
			int i = 0;

			for (Map.Entry<Item, List<Item>> entry : recipe.recipes.entrySet())
			{
				String craftName = entry.getKey().getTitle();
				sb.append(String.format("Рецепт |%d|: %s\n", i, craftName));
				i++;
			}

			sb.append("========================\n");
			sb.append("Чтобы скрафтить предмет введите его ID: ");
			player.setState(Player.State.craftAwaitingID);
			host.sendMsg(id, sb.toString());
		}
		else
		{
			host.sendMsg(id, "\uD83C\uDF81\t Список рецептов пуст ");
		}
	}
}
