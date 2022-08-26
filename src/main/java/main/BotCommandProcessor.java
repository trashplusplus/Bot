package main;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BotCommandProcessor
{
	public static Map<String, Consumer<Player>> get_map(Bot bot)
	{
		Map<String, Consumer<Player>> res = new HashMap<>();

		res.put("/help", bot::command_help);
		res.put("\uD83C\uDF3A Помощь", bot::command_help);

		res.put("/inv", bot::command_inv);
		res.put("\uD83C\uDF92 Инвентарь", bot::command_inv);

		res.put("/find", bot::command_find);
		res.put("\uD83D\uDC8E Искать редкие предметы", bot::command_find);

		res.put("/mud", bot::command_mud);
		res.put("\uD83D\uDD26 Рыться в грязи", bot::command_mud);


		res.put("\uD83E\uDDF6 Проверить карманы", bot::command_pockets);
		res.put("/pockets", bot::command_pockets);
		res.put("/balance", bot::command_balance);
		res.put("/stats", bot::command_stats);

		res.put("/top", bot::command_top);
		res.put("\uD83C\uDF80 Топ 10", bot::command_top);

		res.put("/info", bot::command_info);
		res.put("/sell", bot::command_sell);
		res.put("\uD83D\uDCDE Скупщик", bot::command_sell);
		res.put("/changenickname", bot::command_changeNickname);

		res.put("/coin", bot::command_coin);
		res.put("\uD83D\uDCB0 Монетка", bot::command_coin);

		res.put("/me", bot::command_me);
		res.put("⭐️ Персонаж", bot::command_me);

		//res.put("⭐ Начать", bot::command_shop);

		res.put("/start", bot::command_start_already_registered);
		res.put("⭐ Начать", bot::command_start_already_registered);
		res.put("/pay", bot::command_pay);
		res.put("/shopbuy", bot::command_shopbuy);
		res.put("/shopshow", bot::command_shopshow);
		res.put("\uD83D\uDED2 Магазин", bot::command_shopshow);
		res.put("/shopplace", bot::command_shopplace);

		res.put("/tea", bot::command_tea);
		res.put("/coffee", bot::command_coffee);

		res.put("/forest", bot::command_forest);
		res.put("/fish", bot::command_fish);
		res.put("/sellfish", bot::command_sellfish);
		res.put("\uD83E\uDD88 Сдать рыбу", bot::command_sellfish);
		res.put("🐡 Рыбачить", bot::command_fish);
		res.put("/drop", bot::command_drop);
		res.put("\uD83D\uDEE0 Продать Cheap", bot::command_drop);
		res.put("/ach", bot::command_achievements);

		res.put("Пирожок", bot::command_bonus);
		res.put("/case", bot::command_case);

		res.put("/open", bot::command_open);
		res.put("/touch", bot::command_touch);



		return res;
	}
}
