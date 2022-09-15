package commands;

import main.Bot;
import main.Player;
import main.Recipe;

public class Recipes extends Command  // big todo
{
	@Override
	public void consume(Bot host, Player player)
	{
		long id = player.getId();
		StringBuilder sb = new StringBuilder("*Рецепты*\n");
		sb.append("Здесь можно скрафтить полезные вещи, используя менее ценные предметы\n\n");
		sb.append("Предметы, доступные для крафта: \n");
		Recipe recipe = new Recipe();

		if (recipe.allRecipes != null)
		{
			sb.append("\n");
			sb.append("========================\n");
			for (int i = 0; i < recipe.allRecipes.size(); i++)
			{
				sb.append(String.format("Рецепт |%d|: %s\n", i, recipe.allRecipes.get(i).getTitle()));
			}
			sb.append("========================\n");
			sb.append("Чтобы скрафтить предмет введите его ID: ");
			// todo
			host.sendMsg(id, sb.toString());
		}
		else
		{
			host.sendMsg(id, "\uD83C\uDF81\t Список рецептов пуст ");
		}
	}
}
