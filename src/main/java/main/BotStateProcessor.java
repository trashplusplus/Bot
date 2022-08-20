package main;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BotStateProcessor
{
	public static Map<Player.State, BiConsumer<Player, Message>> get_map(Bot bot)
	{
		Map<Player.State, BiConsumer<Player, Message>> res = new HashMap<>();

		res.put(Player.State.awaitingNickname, bot::awaitingNickname_processor);
		res.put(Player.State.awaitingSellArguments, bot::awaitingSellArguments_processor);
		res.put(Player.State.awaitingCommands, bot::awaitingCommand_processor);
		res.put(Player.State.awaitingChangeNickname, bot::awaitingChangeNickname_processor);
		res.put(Player.State.coinDash, bot::coinDash_processor);
		res.put(Player.State.shopPlaceGood_awaitingCost, bot::shopPlaceGood_awaitingCost_processor);
		res.put(Player.State.shopPlaceGood_awaitingID, bot::shopPlaceGood_awaitingID_processor);
		res.put(Player.State.payAwaitingNickname, bot::payAwaitingNickname_processor);
		res.put(Player.State.payAwaitingAmount, bot::payAwaitingAmount_processor);
		res.put(Player.State.shopBuy, bot::shopBuy_processor);
		return res;
	}
}
