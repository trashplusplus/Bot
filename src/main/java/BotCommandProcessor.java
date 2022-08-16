import main.Player;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BotCommandProcessor
{
	public static Map<String, Consumer<Player>> get_map(Bot bot)
	{
		Map<String, Consumer<Player>> res = new HashMap<>();

		res.put("/help", bot::command_help);
		res.put("\uD83C\uDF3A Помощь", bot::command_help);

		res.put("/inv", bot::command_inv);
		res.put("/find", bot::command_find);
		res.put("/balance", bot::command_balance);
		res.put("/stats", bot::command_stats);
		res.put("/top", bot::command_top);
		res.put("/info", bot::command_info);
		res.put("/sell", bot::command_sell);
		res.put("/changenickname", bot::command_changeNickname);
		res.put("/coin", bot::command_coin);

		res.put("/me", bot::command_me);
		res.put("⭐️ Персонаж", bot::command_me);

		res.put("/shop", bot::command_shop);
		res.put("⭐ Начать", bot::command_shop);

		res.put("/start", bot::command_start_already_registered);
		res.put("/pay", bot::command_pay);


		return res;
	}
}
